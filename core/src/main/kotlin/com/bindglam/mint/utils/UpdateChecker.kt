package com.bindglam.mint.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import org.semver4j.Semver
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class UpdateChecker(private val user: String, private val repositoryName: String) {
    fun check(version: String): Boolean {
        val semver = Semver(version)

        HttpClient.newHttpClient().use { client ->
            val request = HttpRequest.newBuilder()
                .uri(URI("https://api.github.com/repos/$user/$repositoryName/releases"))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != 200) return false

            val responseData = Gson().fromJson(response.body(), JsonArray::class.java)
            val latestReleaseData = responseData.get(0).asJsonObject
            val latestVersion = Semver(latestReleaseData.get("tag_name").asString)

            return semver.isLowerThan(latestVersion)
        }
    }
}