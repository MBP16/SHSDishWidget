import requests
import re
from bs4 import BeautifulSoup


def get_meal_id(day: int, meal_type: int):
    url = "https://seoul.sen.hs.kr/77703/subMenu.do"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3',
    }
    response = requests.get(url, headers=headers)
    meal_list = BeautifulSoup(response.text, 'html.parser').select_one('table > tbody').find_all('td')
    for i in meal_list:
        if str(day) in i.text:
            meal = i
            break
    return meal.find_all('a')[meal_type].get('onclick').split("'")[1]


def get_meal(id: int, data_type: str):
    url = f"https://seoul.sen.hs.kr/dggb/module/mlsv/selectMlsvDetailPopup.do?mlsvId={id}"
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3',
    }
    response = requests.post(url, headers=headers)
    soup = BeautifulSoup(response.text, 'html.parser')
    result = soup.find('th', text=data_type).find_next('td').text.strip()
    return result


if __name__ == "__main__":
    day, type = input().split(" ")
    get_meal(get_meal_id(int(day), int(type)))
