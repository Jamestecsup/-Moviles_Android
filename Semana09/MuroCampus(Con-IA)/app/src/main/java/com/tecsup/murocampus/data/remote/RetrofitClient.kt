package com.tecsup.murocampus.data.remote

import java.net.InetAddress
import kotlinx.serialization.json.Json
import okhttp3.Dns
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitClient {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val headerInterceptor = Interceptor { chain ->
        val peticionOriginal = chain.request()
        val peticionConHeaders = peticionOriginal.newBuilder()
            .header("Accept", "application/json")
            .header("User-Agent", "MuroCampus-Android-Client/1.0")
            .build()
        chain.proceed(peticionConHeaders)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Resuelve fallos de resolución DNS del emulador en entornos con múltiples adaptadores de red
    private val dnsResiliente = object : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
            return try {
                Dns.SYSTEM.lookup(hostname)
            } catch (e: Exception) {
                if (hostname.equals("jsonplaceholder.typicode.com", ignoreCase = true)) {
                    listOf(
                        InetAddress.getByAddress(hostname, byteArrayOf(104.toByte(), 21.toByte(), 59.toByte(), 19.toByte())),
                        InetAddress.getByAddress(hostname, byteArrayOf(172.toByte(), 67.toByte(), 167.toByte(), 151.toByte()))
                    )
                } else {
                    throw e
                }
            }
        }
    }

    private val cliente = OkHttpClient.Builder()
        .dns(dnsResiliente)
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val api: PostApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(cliente)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PostApi::class.java)
    }
}
