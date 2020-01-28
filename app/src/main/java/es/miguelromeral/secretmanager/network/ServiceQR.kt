package es.miguelromeral.secretmanager.network

import android.content.Context
import android.content.Intent
import android.net.Uri
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
    // http://goqr.me/api/doc/create-qr-code/
    @GET("create-qr-code/")
    fun getImage(
        @Query("data") data:String,
        @Query("size") size:String,
        @Query("color") color:String = "000",
        @Query("bgcolor") bgcolor:String = "fff",
        @Query("qzone") qzone:String = "1",
        @Query("format") format:String = "png",
        @Query("ecc") ecc:String = "M")
            : Call<ResponseBody>

    companion object{
        fun getURL(text: String, size: String) = "${BASE_URL}create-qr-code/?data=$text&size=${getSizeQuery()}&color=000&bgcolor=fff&qzone=1&format=png&ecc=M"

        fun openQRIntent(context: Context, text: String){
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(ServiceQR.getURL(text, "300"))
            })
        }
    }
}

object ApiQR {
    val retrofitService : ServiceQR by lazy {
        retrofit.create(ServiceQR::class.java) }
}


fun getSizeQuery() = "${size}x${size}"


const val size = 300