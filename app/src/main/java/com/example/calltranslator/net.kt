package com.example.calltranslator

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class Net {
    private val client = OkHttpClient()
    private var token = ""
    private var server = ""

    fun auth(server:String, username: String, password:String) : String
    {
        this.server = server

        val formBody = FormBody.Builder().build()
        // 1. Create request
        val request = Request.Builder()
            .url(this.server + "auth")
            .addHeader("username", username)
            .addHeader("password", password)
            .addHeader("sender", "droid")
            .post(formBody)
            .build()

        var result = ""

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            //println(response.body!!.string())
            val responseBody = response.body?.string()
            val gson = Gson() // Создаем экземпляр Gson

            try {
                val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
                result = jsonObject.get("token").asString
            } catch (e: JsonSyntaxException) {
                // Обработка ошибки парсинга JSON
            }
        }
        token = result
        return result
    }

    fun list( id:Int = 0) : ArrayList<Message>
    {
        val formBody = FormBody.Builder().build()
        // 1. Create request
        val request = Request.Builder()
            .url(server + "msg")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("action", "list")
            .addHeader("message_id", id.toString())
            .addHeader("sender", "droid")
            .post(formBody)
            .build()

        val result: ArrayList<Message> = ArrayList()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseBody = response.body?.string()
            val gson = Gson() // Создаем экземпляр Gson

            try {
                val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
                val j = jsonObject.get("messages").asJsonArray

                for (i in 0 until j.size()) {
                    val m = gson.fromJson(j.get(i), Message::class.java)
                    result.add(m)
                }

            } catch (e: JsonSyntaxException) {
                // Обработка ошибки парсинга JSON
            }
        }
        return result
    }

    fun get(id:Int) : Message?
    {
        val formBody = FormBody.Builder().build()
        // 1. Create request
        val request = Request.Builder()
            .url(server + "msg")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("action", "get")
            .addHeader("message_id", id.toString())
            .addHeader("sender", "droid")
            .post(formBody)
            .build()

        var result: Message? = null;

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseBody = response.body?.string()
            val gson = Gson() // Создаем экземпляр Gson

            try {
                val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
                val j = jsonObject.get("message");

                result = gson.fromJson(j, Message::class.java)

            } catch (e: JsonSyntaxException) {
                // Обработка ошибки парсинга JSON
            }
        }
        return result
    }

}

val net:Net = Net()