package com.example.mvpparaandroid.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URL content from user: 
    // https://script.google.com/macros/s/AKfycbw_Ga6a_aU9wKCdWTJCIxMP8-TYDlJ9t8qt3BFkZkjUF3RqsOd2CnpJlFst8unnm5cdiA/exec
    // Retrofit requires base URL to end with /
    private const val BASE_URL = "https://script.google.com/macros/s/AKfycbw_Ga6a_aU9wKCdWTJCIxMP8-TYDlJ9t8qt3BFkZkjUF3RqsOd2CnpJlFst8unnm5cdiA/"

    val instance: SheetApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SheetApiService::class.java)
    }
}
