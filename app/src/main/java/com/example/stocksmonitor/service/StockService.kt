package com.example.stocksmonitor.service


import com.example.stocksmonitor.model.InfoDTO
import com.example.stocksmonitor.model.Stock
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface StockService {

    @GET("/getData")
    fun getStockData(): Single<List<InfoDTO>>

    @PATCH("/stockAndCreateAlert")
    fun saveStock(@Body stock: Stock):Call<Stock>
}