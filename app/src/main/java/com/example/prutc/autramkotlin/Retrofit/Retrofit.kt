package com.example.prutc.autramkotlin.Retrofit

import android.util.Log
import com.example.prutc.autramkotlin.TramDetail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit {
    fun writeRetrofitData(tramID : String, pointID : String) {
        val retrofit : retrofit2.Retrofit = Retrofit.Builder()
                .baseUrl("http://13.76.93.129/WS-TEST-TRAM/Home/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api : TramAPI = retrofit.create(TramAPI::class.java)
        val call : Call<TramDetail> = api.createTram(tramID, pointID)

        call.enqueue(object : Callback<TramDetail> {
            override fun onResponse(call: Call<TramDetail>, response: Response<TramDetail>) {
                Log.d("boss", "Response Message : " + response.message())
            }

            override fun onFailure(call: Call<TramDetail>, t: Throwable) {
                Log.d("boss", "Throwable Message : " + t.message)
            }
        })
    }
}
