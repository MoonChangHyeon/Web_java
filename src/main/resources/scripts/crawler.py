"""
Fortify 취약점 카테고리 크롤러입니다.
카테고리 설명, 취약점 목록, 상세 데이터를 스크래핑하고,
카테고리별 XML 및 JSON 파일로 저장합니다.
--output-dir 인자를 통해 결과 저장 경로를 외부에서 전달받습니다.
"""

from datetime import datetime
import logging
import os
import time
import csv
from xml.dom import minidom
import xml.etree.ElementTree as ET
import json
import argparse
from collections import defaultdict

import requests
from bs4 import BeautifulSoup

# --- 로깅 설정 ---
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)


# --- 카테고리별 초기 페이지 정보 ---
LAST_PAGES = {
    "Input Validation and Representation": 10,
    "API Abuse": 5,
    "Security Features": 16,
    "Time and State": 1,
    "Errors": 1,
    "Code Quality": 5,
    "Encapsulation": 6,
    "Environment": 33,
}

BASE_URL = "https://vulncat.fortify.com"
AVAILABLE_KINGDOMS = [
    "Input Validation and Representation", "API Abuse", "Security Features",
    "Time and State", "Errors", "Code Quality", "Encapsulation", "Environment",
]
kingdom_descriptions = {} # 카테고리 설명을 저장할 딕셔너리

def get_soup(url, params=None):
    """지정된 URL에서 BeautifulSoup 객체를 반환합니다."""
    try:
        response = requests.get(url, params=params, timeout=30)
        response.raise_for_status()
        return BeautifulSoup(response.content, 'html.parser')
    except requests.RequestException as e:
        logger.error(f"URL 요청 중 오류 발생: {url}, {e}")
        return None

def scrape_list_page(kingdom, page):
    """취약점 목록 페이지에서 기본 정보를 스크래핑합니다."""
    # ### URL 생성 방식 수정 ###
    # 경로에 카테고리를 넣는 대신, 'kingdom' 쿼리 파라미터로 전달합니다.
    url = f"{BASE_URL}/ko/weakness"
    params = {'kingdom': kingdom, 'page': page}
    soup = get_soup(url, params=params)

    if not soup:
        return []

    if kingdom not in kingdom_descriptions:
         desc_tag = soup.select_one('.description p')
         kingdom_descriptions[kingdom] = desc_tag.text.strip() if desc_tag else "설명 없음"

    vulns = []
    for row in soup.select('table.vuln-list tbody tr'):
        cols = row.select('td')
        if len(cols) >= 2:
            title_tag = cols[0].find('a')
            if title_tag:
                vulns.append({
                    'numeric_id': cols[1].text.strip(),
                    'title': title_tag.text.strip(),
                    'detail_link': BASE_URL + title_tag['href'],
                    'kingdom': kingdom
                })
    return vulns


def scrape_vulnerability_details(detail_url):
    """취약점 상세 페이지에서 언어, 설명 등 상세 정보를 스크래핑합니다."""
    soup = get_soup(detail_url)
    if not soup:
        return {}

    languages = [lang.text.strip() for lang in soup.select('.language-link-container a')]
    abstract = soup.select_one('.abstract p')
    explanation = soup.select_one('.explanation p')

    return {
        'languages': {'language': languages},
        'description': {
            'Abstract': abstract.text.strip() if abstract else '',
            'Explanation': explanation.text.strip() if explanation else ''
        }
    }

def save_as_csv(data, file_path):
    """데이터를 CSV 파일로 저장합니다."""
    if not data: return
    keys = data[0].keys()
    with open(file_path, 'w', newline='', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fieldnames=keys)
        writer.writeheader()
        writer.writerows(data)
    logger.info(f"성공적으로 {file_path}에 저장했습니다.")

def save_as_xml(data, file_path, kingdom_name, kingdom_desc):
    """데이터를 XML 파일로 저장합니다."""
    root = ET.Element('fortify_report')
    info = ET.SubElement(root, 'info')
    ET.SubElement(info, 'kingdom').text = kingdom_name
    ET.SubElement(info, 'description').text = kingdom_desc
    ET.SubElement(info, 'vulnerability_count').text = str(len(data))
    ET.SubElement(info, 'generated_at').text = datetime.now().isoformat()
    scraped = ET.SubElement(info, 'scraped_fields')
    for field in ['numeric_id', 'title', 'detail_link', 'kingdom', 'languages', 'abstract', 'explanation']:
        ET.SubElement(scraped, 'field').text = field
    vulns = ET.SubElement(root, 'vulnerabilities')
    for item in data:
        vuln_el = ET.SubElement(vulns, 'vulnerability')
        desc_el = ET.SubElement(vuln_el, 'description')
        desc_data = item.get('description', {})
        ET.SubElement(desc_el, 'Abstract').text = desc_data.get('Abstract', '')
        ET.SubElement(desc_el, 'Explanation').text = desc_data.get('Explanation', '')

        for key, value in item.items():
            if key not in ['description', 'languages']:
                ET.SubElement(vuln_el, key).text = str(value)

        langs_el = ET.SubElement(vuln_el, 'languages')
        lang_list = item.get('languages', {}).get('language', [])
        if isinstance(lang_list, str): lang_list = [lang_list]
        for lang in lang_list:
            ET.SubElement(langs_el, 'language').text = lang

    rough_string = ET.tostring(root, 'utf-8')
    reparsed = minidom.parseString(rough_string)
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(reparsed.toprettyxml(indent="  "))
    logger.info(f"성공적으로 {file_path}에 저장했습니다.")


def convert_xml_file_to_json(xml_path, json_path):
    """XML 파일을 읽어 JSON 파일로 변환합니다."""
    try:
        with open(xml_path, 'r', encoding='utf-8') as f:
            xml_str = f.read()
        
        root = ET.fromstring(xml_str)
        # 간단한 변환 로직 (필요시 더 정교하게 수정 가능)
        def etree_to_dict(t):
            d = {t.tag: {} if t.attrib else None}
            children = list(t)
            if children:
                dd = defaultdict(list)
                for dc in map(etree_to_dict, children):
                    for k, v in dc.items():
                        dd[k].append(v)
                d = {t.tag: {k: v[0] if len(v) == 1 else v for k, v in dd.items()}}
            if t.attrib:
                d[t.tag].update(('@' + k, v) for k, v in t.attrib.items())
            if t.text:
                text = t.text.strip()
                if children or t.attrib:
                    if text:
                      d[t.tag]['#text'] = text
                else:
                    d[t.tag] = text
            return d

        json_data = etree_to_dict(root)
        
        with open(json_path, 'w', encoding='utf-8') as f:
            json.dump(json_data, f, ensure_ascii=False, indent=2)
        logger.info(f"성공적으로 {json_path} (으)로 변환했습니다.")

    except ET.ParseError as e:
        logger.error(f"XML 파싱 오류 {xml_path}: {e}")
    except Exception as e:
        logger.error(f"JSON 변환 중 오류 발생 {xml_path}: {e}")


def main(output_dir):
    """메인 실행 함수"""
    # --- 파일 출력 경로 설정 ---
    XML_DIR = os.path.join(output_dir, 'xml')
    JSON_DIR = os.path.join(output_dir, 'json')
    os.makedirs(XML_DIR, exist_ok=True)
    os.makedirs(JSON_DIR, exist_ok=True)

    logger.info("========== Fortify 스크래핑 시작 ==========")
    all_detailed_results = []
    selected_kingdoms = AVAILABLE_KINGDOMS

    for kingdom in selected_kingdoms:
        logger.info(f"\n>>>>>>>>> 카테고리: {kingdom} <<<<<<<<<")
        
        # 실제 마지막 페이지 번호 확인
        url = f"{BASE_URL}/ko/weakness"
        params = {'kingdom': kingdom}
        soup = get_soup(url, params=params)

        if not soup: 
            logger.warning(f"카테고리 '{kingdom}'의 첫 페이지를 불러올 수 없습니다. 건너뜁니다.")
            continue
        
        last_page_tag = soup.select_one('ul.pagination li.pager-last a')
        actual_last = int(last_page_tag['href'].split('=')[-1]) if last_page_tag else 1
        
        logger.info(f"'{kingdom}' 카테고리의 총 페이지 수: {actual_last}")

        for page_num in range(actual_last): # 페이지는 0부터 시작
            logger.info(f"[{kingdom}] 목록 페이지 스크래핑 중... (페이지 {page_num})")
            vulns = scrape_list_page(kingdom, page_num)
            for i, v in enumerate(vulns, 1):
                logger.info(f"--- 상세 정보 스크래핑: {i}/{len(vulns)} (페이지 {page_num}) ---")
                d = scrape_vulnerability_details(v['detail_link'])
                if d:
                    all_detailed_results.append({**v, **d})
                time.sleep(0.5)

    if all_detailed_results:
        logger.info("\n========== 최종 결과 파일 저장 시작 ==========")
        for kingdom in selected_kingdoms:
            data = [item for item in all_detailed_results if item['kingdom'] == kingdom]
            if data:
                name = kingdom.replace(" ", "_").replace("&", "and")
                # csv_path = os.path.join(JSON_DIR, f"{name}.csv") # CSV도 JSON 폴더에 저장
                xml_path = os.path.join(XML_DIR, f"{name}.xml")
                json_path = os.path.join(JSON_DIR, f"{name}.json")
                
                # save_as_csv(data, csv_path) # 필요시 주석 해제
                save_as_xml(data, xml_path, kingdom_name=kingdom, kingdom_desc=kingdom_descriptions.get(kingdom, "설명 없음"))
                convert_xml_file_to_json(xml_path, json_path)


if __name__ == '__main__':
    # 명령줄 인자 파서 설정
    parser = argparse.ArgumentParser(description='Fortify 웹사이트에서 취약점 정보를 스크래핑합니다.')
    parser.add_argument('--output-dir', type=str, required=True, help='결과 파일을 저장할 디렉터리 경로')
    args = parser.parse_args()

    main(args.output_dir)