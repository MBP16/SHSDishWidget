package com.mbp16.shsdishwiget.utils

import android.app.Activity
import com.google.gson.Gson
import java.net.URL

data class Asset(
    val browser_download_url: String
)

data class Release(
    val tag_name: String,
    val assets: List<Asset>,
    val body: String
)

fun checkUpdate(activity: Activity): Any {
    val url = URL("https://api.github.com/repos/MBP16/SHSDishWidget/releases")
    val connection = url.openConnection()
    connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
    val releases = connection.getInputStream().bufferedReader().use { it.readText() }
    val parsedObject = Gson().fromJson(releases, Array<Release>::class.java)
    val latestRelease = parsedObject[0]
    val currentVersion = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName
    return if (latestRelease.tag_name != currentVersion) {
        latestRelease
    } else {
        false
    }
}