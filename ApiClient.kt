package com.moviesplus.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {
    // Replace this with your actual API base URL; keep the trailing slash.
    private const val BASE_URL = "https://movies-puls.free.je/api/?path="

    suspend fun get(path: String): JSONObject = withContext(Dispatchers.IO) {
        val url = URL(BASE_URL + path)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("Accept", "application/json")
        }
        try {
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val text = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val json = JSONObject(text)
            if (code !in 200..299 || json.optBoolean("success", true).not()) {
                val message = json.optJSONObject("error")?.optString("message") ?: "API request failed ($code)"
                throw IllegalStateException(message)
            }
            json
        } finally {
            connection.disconnect()
        }
    }

    fun dataArray(json: JSONObject): JSONArray {
        val data = json.opt("data")
        return when (data) {
            is JSONArray -> data
            is JSONObject -> JSONArray()
            else -> JSONArray()
        }
    }

    fun rowsFromHome(json: JSONObject, section: String): JSONArray {
        val data = json.optJSONObject("data") ?: return JSONArray()
        return data.optJSONArray(section) ?: JSONArray()
    }
}
