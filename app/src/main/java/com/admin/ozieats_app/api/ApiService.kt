package com.admin.ozieats_app.api

import android.content.Context
import androidx.databinding.library.BuildConfig
import com.admin.ozieats_app.utils.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiService {
    companion object {
        operator fun invoke(context: Context): ApiClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            //var networkConnectionInterceptor=NetworkConnectionInterceptor(context)
            if (BuildConfig.DEBUG) loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            //Create a new Interceptor.
            /*val headerAuthorizationInterceptor = Interceptor { chain ->
                var request: Request = chain.request()
                val headers: Headers = request.headers().newBuilder().add(
                    Key.AUTHORIZATION,
                    SharedPrefsManager.newInstance(context).getString(Preference.PREF_TOKEN)
                ).build()
                request = request.newBuilder().headers(headers).build()
                chain.proceed(request)
            }*/

            val client = OkHttpClient.Builder()
            val clientBuilder = client.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build()
            (return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(clientBuilder)
                .build().create(ApiClient::class.java))
        }
    }
}