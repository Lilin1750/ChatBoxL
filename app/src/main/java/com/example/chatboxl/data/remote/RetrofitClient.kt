package com.example.chatboxl.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/*
 * RetrofitClient类，用于创建Retrofit实例并获取DeepSeekApi接口实例
 * 必须是单例，确保持有的DeepseekApi动态代理对象和retrofit实例是单例的，避免重复创建，浪费资源
 * 所以使用 by lazy {} 确保只初始化一次
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.deepseek.com/v1/"

    private val client = OkHttpClient.Builder().build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val apiService: DeepSeekApi by lazy {
        retrofit.create(DeepSeekApi::class.java)
    }
}