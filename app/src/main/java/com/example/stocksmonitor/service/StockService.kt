package com.example.stocksmonitor.service


import com.example.stocksmonitor.model.InfoDTO
import com.example.stocksmonitor.model.Stock
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface StockService {

    @GET("/getData")
    fun getStockData(): Single<List<InfoDTO>>

    @PATCH("/stockAndCreateAlert")
    fun saveStock(@Body stock: Stock):Call<Stock>

    @GET("/getStock/{symbol}")
    fun getStock(@Path("symbol") symbol: String): Call<InfoDTO>

    @DELETE("/alert/{id}")
    fun deleteAlert(@Path("id") id: Long): Call<Boolean>
}