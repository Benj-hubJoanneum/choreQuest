package com.example.chorequest.service.server

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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
                    // Ensure that the response body is not null
                    if (response.isSuccessful && response.body != null) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            callback(responseBody)
                        } else {
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(null)
                } finally {
                    response.close()
                }
            }
        })
    }

    // Save data by ID
    fun saveDataById(id: String, user: JSONObject, callback: (Boolean) -> Unit) {
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            user.toString()
        )
        val request = Request.Builder()
            .url("$baseUrl/$id")  // Updated URL to match the Python server
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
