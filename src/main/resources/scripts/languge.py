import os
import json
import csv
import xml.etree.ElementTree as ET
from xml.dom import minidom
from collections import defaultdict
import argparse # argparse 라이브러리 추가

def analyze_and_save(base_dir):
    # --- 설정 ---
    JSON_DIR = os.path.join(base_dir, 'json')      # 카테고리별 JSON 파일 폴더
    OUT_DIR = os.path.join(base_dir, 'analysis')   # 분석 결과 출력 폴더
    DETAIL_DIR = os.path.join(OUT_DIR, 'detail_by_language')  # 언어별 상세 취약점 폴더

    # 출력 디렉터리 생성
    for d in (OUT_DIR, DETAIL_DIR):
        os.makedirs(d, exist_ok=True)

    # --- 데이터 취합 ---
    # by_language: { language: { kingdom: count, ... }, ... }
    by_language = defaultdict(lambda: defaultdict(int))
    detail_by_language = defaultdict(list)

    if not os.path.exists(JSON_DIR):
        print(f"Error: Input directory not found at {JSON_DIR}")
        return

    for fn in os.listdir(JSON_DIR):
        if not fn.lower().endswith('.json'):
            continue
        kingdom = fn.rsplit('.',1)[0].replace("_", " ") # 파일명을 kingdom으로 사용
        path = os.path.join(JSON_DIR, fn)
        with open(path, encoding='utf-8') as f:
            doc = json.load(f)
        
        vulns = doc.get('fortify_report', {}) \
                   .get('vulnerabilities', {}) \
                   .get('vulnerability', [])
        
        if not isinstance(vulns, list):
            vulns = [vulns] # 단일 객체일 경우 리스트로 변환

        for v in vulns:
            langs = v.get('languages', {}).get('language', [])
            if isinstance(langs, str):
                langs = [langs]
            for lang in langs:
                lang_clean = lang.replace('/', '_').replace(' ', '_')
                by_language[lang_clean][kingdom.replace(' ', '_')] += 1
                detail_by_language[lang_clean].append(v)

    # --- 분석 결과 저장 ---
    # ── Summary by_language JSON 출력 ──
    with open(os.path.join(OUT_DIR, 'summary_by_language.json'), 'w', encoding='utf-8') as f:
        json.dump({'by_language': by_language}, f, indent=2, ensure_ascii=False)

    # ── Summary by_language XML 출력 ──
    root = ET.Element('analysis')
    bl = ET.SubElement(root, 'by_language')
    for lang, kingdoms in by_language.items():
        lang_el = ET.SubElement(bl, 'language', name=lang)
        for k, c in kingdoms.items():
            ET.SubElement(lang_el, 'kingdom', name=k).text = str(c)

    rough = ET.tostring(root, 'utf-8')
    reparsed = minidom.parseString(rough)
    with open(os.path.join(OUT_DIR, 'summary_by_language.xml'), 'w', encoding='utf-8') as f:
        f.write(reparsed.toprettyxml(indent="  "))

    # ── 상세 취약점 by_language JSON 출력 ──
    with open(os.path.join(DETAIL_DIR, 'detail_by_language.json'), 'w', encoding='utf-8') as f:
        json.dump({'by_language': detail_by_language}, f, indent=2, ensure_ascii=False)

    print(f"분석 완료. 결과가 {OUT_DIR} 폴더에 저장되었습니다.")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Fortify 결과 JSON을 분석하여 언어별 통계를 생성합니다.')
    parser.add_argument('--base-dir', type=str, required=True, help='결과물이 저장된 기본 디렉터리 경로 (json 폴더 포함)')
    args = parser.parse_args()
    
    analyze_and_save(args.base_dir)