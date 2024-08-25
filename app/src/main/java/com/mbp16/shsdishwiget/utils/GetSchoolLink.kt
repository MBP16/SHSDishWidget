package com.mbp16.shsdishwiget.utils

import org.jsoup.Jsoup

fun getRealMainPageLink(mainPageLink: String): String {
    var link = mainPageLink.replace("http://", "https://")
    val response = Jsoup.connect(link)
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
        .ignoreHttpErrors(true)
        .get()
    val elements = response.select("a")
    if (elements.isEmpty()) {
        link = response.toString().replace("\"", "'").split("location.href='")[1].split("'")[0]
    }
    return link
}

fun getSchoolMealPageLink(mainPageLink: String): String {
    val response = Jsoup.connect(mainPageLink)
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
        .ignoreHttpErrors(true)
        .get()
    val elements = response.select("a")
    var result = "none"
    for (element in elements) {
        if ("식단" in element.text()) {
            result = element.attr("abs:href")
            break
        } else if ("급식" in element.text()) {
            result = element.attr("abs:href")
            if ("일정" in element.text() || "메뉴" in element.text()) {
                break
            }
        }
    }

    return result
}