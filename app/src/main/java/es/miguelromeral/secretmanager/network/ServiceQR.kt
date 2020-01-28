package es.miguelromeral.secretmanager.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

private const val BASE_URL =
    "https://api.qrserver.com/v1/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ServiceQR {
    @GET("create-qr-code/")
    fun getProperties(@Query("data") data:String, @Query("size") size:String): Call<ResponseBody>
}

object ApiQR {
    val retrofitService : ServiceQR by lazy {
        retrofit.create(ServiceQR::class.java) }
}


fun getSizeQuery() = "${size}x${size}"


const val size = 300