package com.example.derskazanimi.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class ReleaseInfo(
    val tagName: String,
    val releaseName: String,
    val apkDownloadUrl: String
)

object UpdateChecker {

    private const val LATEST_RELEASE_API =
        "https://api.github.com/repos/abdurrahmancosk/DersKazanimi/releases/latest"

    suspend fun fetchLatestRelease(): Result<ReleaseInfo> = withContext(Dispatchers.IO) {
        try {
            val connection = URL(LATEST_RELEASE_API).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext Result.failure(Exception("Sunucu hatası: $responseCode"))
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)

            val tagName = json.optString("tag_name", "")
            val releaseName = json.optString("name", tagName)
            val assets = json.optJSONArray("assets")

            var apkUrl: String? = null
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.optString("name", "")
                    if (name.endsWith(".apk")) {
                        apkUrl = asset.optString("browser_download_url", null)
                        break
                    }
                }
            }

            if (apkUrl == null) {
                Result.failure(Exception("Yayında APK dosyası bulunamadı"))
            } else {
                Result.success(ReleaseInfo(tagName, releaseName, apkUrl))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
