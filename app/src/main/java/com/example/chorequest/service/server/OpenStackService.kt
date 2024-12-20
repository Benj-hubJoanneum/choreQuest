package com.example.chorequest.service.server

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class OpenStackService {
    private val client = OkHttpClient()
    private val baseUrl = "http://10.77.17.158:12345"

    // Fetch data by ID
    fun fetchDataById(id: String, callback: (String?) -> Unit) {
        val request = Request.Builder()
            .url("$baseUrl/$id")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string()
                        callback(responseBody)
                    } else {
                        callback(null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(null)
                } finally {
                    response.body()?.close()
                }
            }
        })
    }

    // Save data by ID
    fun saveDataById(id: String, user: JSONObject, callback: (Boolean) -> Unit) {
        val mediaType = MediaType.parse("application/json; charset=utf-8")
        val requestBody = RequestBody.create(mediaType, user.toString())
        val request = Request.Builder()
            .url("$baseUrl/$id")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }
        })
    }
}
