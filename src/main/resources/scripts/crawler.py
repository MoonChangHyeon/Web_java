"""
Fortify 취약점 카테고리 크롤러입니다.
Java로부터 kingdom과 pages를 인자로 받아 단일 카테고리를 크롤링합니다.
"""

from datetime import datetime
import logging
import os
import time
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

# --- 기본 URL 설정 ---
BASE_URL = "https://vulncat.fortify.com"

def get_soup(url, params=None):
    """지정된 URL에서 BeautifulSoup 객체를 반환합니다."""
    try:
        response = requests.get(url, params=params, timeout=15)
        response.raise_for_status()
        return BeautifulSoup(response.text, 'html.parser')
    except requests.RequestException as e:
        logger.error(f"URL 요청 중 오류 발생: {url}, {e}")
        return None

def scrape_kingdom_description(kingdom: str) -> str:
    """주어진 카테고리의 설명 텍스트를 스크래핑합니다."""
    logger.info(f"[{kingdom}] 카테고리 설명 스크래핑 중...")
    try:
        soup = get_soup(f"{BASE_URL}/ko/weakness", params={"kingdom": kingdom})
        if soup:
            desc_element = soup.select_one("div.panel p")
            if desc_element:
                return desc_element.get_text(strip=True)
        return "Category description not found."
    except Exception as e:
        logger.error(f"[{kingdom}] 카테고리 설명 스크래핑 실패: {e}")
        return "Error scraping category description."

def scrape_list_page(kingdom: str, page_num: int) -> list[dict]:
    """취약점 목록 페이지에서 기본 정보를 스크래핑합니다."""
    logger.info(f"[{kingdom}] 목록 페이지 스크래핑 중... (페이지 {page_num})")
    try:
        soup = get_soup(f"{BASE_URL}/ko/weakness", params={"kingdom": kingdom, "po": page_num})
        if not soup: return []
            
        vulnerabilities = []
        for cell in soup.select(".weaknessCell"):
            title_el = cell.select_one("h1")
            link_el = cell.select_one("a.external-link")
            if title_el and link_el and link_el.has_attr('href'):
                vulnerabilities.append({
                    "numeric_id": cell.get('data-id'),
                    "title": title_el.get_text(strip=True),
                    "detail_link": BASE_URL + link_el['href'],
                    "kingdom": kingdom
                })
        return vulnerabilities
    except Exception as e:
        logger.error(f"[{kingdom}] 목록 페이지 {page_num} 스크래핑 실패: {e}")
        return []

def scrape_vulnerability_details(detail_url: str) -> dict:
    """취약점 상세 페이지에서 상세 정보를 스크래핑합니다."""
    if not detail_url.startswith("http"): return None
    logger.info(f"상세 정보 스크래핑 중: {detail_url}")
    try:
        soup = get_soup(detail_url)
        if not soup: return None
             
        details = {}
        lang_tabs = soup.select("ul.nav-tabs a")
        details['languages'] = [tab.get_text(strip=True) for tab in lang_tabs] if lang_tabs else []
        
        for section in ["Abstract", "Explanation"]:
            title_div = soup.find("div", class_="sub-title", string=section)
            content_div = title_div.find_next_sibling("div", class_="t") if title_div else None
            details[section.lower()] = content_div.get_text(strip=True) if content_div else "내용 없음"
        return details
    except Exception as e:
        logger.error(f"상세 페이지 요청 실패: {detail_url}: {e}")
        return None

def save_as_xml(data, file_path, kingdom_name, kingdom_desc):
    """데이터를 XML 파일로 저장합니다."""
    root = ET.Element('fortify_report')
    info = ET.SubElement(root, 'info')
    ET.SubElement(info, 'kingdom').text = kingdom_name
    ET.SubElement(info, 'description').text = kingdom_desc
    ET.SubElement(info, 'vulnerability_count').text = str(len(data))
    ET.SubElement(info, 'generated_at').text = datetime.now().isoformat()

    vulns = ET.SubElement(root, 'vulnerabilities')
    for item in data:
        vuln_el = ET.SubElement(vulns, 'vulnerability')
        # 모든 key-value 쌍을 XML 요소로 변환
        for key, value in item.items():
            if isinstance(value, dict):
                parent_el = ET.SubElement(vuln_el, key)
                for sub_key, sub_value in value.items():
                    if isinstance(sub_value, list):
                        list_el = ET.SubElement(parent_el, sub_key)
                        for list_item in sub_value:
                            ET.SubElement(list_el, 'item').text = str(list_item)
                    else:
                        ET.SubElement(parent_el, sub_key).text = str(sub_value)
            elif isinstance(value, list):
                 parent_el = ET.SubElement(vuln_el, key)
                 for list_item in value:
                     ET.SubElement(parent_el, 'item').text = str(list_item)
            else:
                ET.SubElement(vuln_el, key).text = str(value)

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
    except Exception as e:
        logger.error(f"JSON 변환 중 오류 발생 {xml_path}: {e}")

def main(output_dir, kingdom, pages_to_crawl):
    """메인 실행 함수"""
    XML_DIR = os.path.join(output_dir, 'xml')
    JSON_DIR = os.path.join(output_dir, 'json')
    os.makedirs(XML_DIR, exist_ok=True)
    os.makedirs(JSON_DIR, exist_ok=True)
    
    logger.info(f"\n>>>>>>>>> Starting Category: {kingdom} for {pages_to_crawl} pages <<<<<<<<<")

    kingdom_desc = scrape_kingdom_description(kingdom)
    all_vulnerabilities_in_kingdom = []

    for page_num in range(pages_to_crawl):
        vulns = scrape_list_page(kingdom, page_num)
        for v in vulns:
            details = scrape_vulnerability_details(v['detail_link'])
            if details:
                v.update(details)
            all_vulnerabilities_in_kingdom.append(v)
        time.sleep(0.5)

    if all_vulnerabilities_in_kingdom:
        logger.info(f"\n[{kingdom}] Saving {len(all_vulnerabilities_in_kingdom)} vulnerabilities...")
        name = kingdom.replace(" ", "_").replace("&", "and")
        xml_path = os.path.join(XML_DIR, f"{name}.xml")
        json_path = os.path.join(JSON_DIR, f"{name}.json")
        
        save_as_xml(all_vulnerabilities_in_kingdom, xml_path, kingdom_name=kingdom, kingdom_desc=kingdom_desc)
        convert_xml_file_to_json(xml_path, json_path)
    
    logger.info(f"Finished processing for category: {kingdom}")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Fortify 웹사이트에서 단일 취약점 카테고리를 스크래핑합니다.')
    parser.add_argument('--output-dir', type=str, required=True, help='결과 파일을 저장할 디렉터리 경로')
    parser.add_argument('--kingdom', type=str, required=True, help='스크래핑할 카테고리 이름')
    parser.add_argument('--pages', type=int, required=True, help='스크래핑할 총 페이지 수')
    args = parser.parse_args()

    main(args.output_dir, args.kingdom, args.pages)