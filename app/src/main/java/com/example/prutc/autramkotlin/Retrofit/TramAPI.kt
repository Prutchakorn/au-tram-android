package com.example.prutc.autramkotlin.Retrofit

import com.example.prutc.autramkotlin.TramDetail
import retrofit2.Call
import retrofit2.http.*

interface TramAPI {
    @FormUrlEncoded
    @POST("http://13.76.93.129/WS-TEST-TRAM/Home/log?")
    fun createTram(
            @Field("TramID") tramID : String,
            @Field("PointID") pointID : String
    ) : Call<TramDetail>

}