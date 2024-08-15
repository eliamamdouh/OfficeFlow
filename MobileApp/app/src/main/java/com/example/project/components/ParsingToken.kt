package com.example.project.components

import android.util.Base64
import org.json.JSONObject

fun parseUserIdFromToken(token: String): String? {
    try {
        // JWT tokens have three parts separated by periods
        val parts = token.split(".")
        if (parts.size != 3) {
            return null
        }

        // Decode the payload part (second part)
        val payload = parts[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
        val decodedString = String(decodedBytes)

        // Parse the payload JSON to extract the userId
        val jsonObject = JSONObject(decodedString)
        return jsonObject.getString("userId") // azon yb2a kda?
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}
