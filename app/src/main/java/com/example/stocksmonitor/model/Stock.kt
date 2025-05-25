package com.example.stocksmonitor.model

import java.math.BigDecimal

data class Stock(val stockSymbol:String,val name:String?,
    val lowestPrice:BigDecimal?,val highestPrice:BigDecimal?,
    val currentPrice:BigDecimal?,val targetPrice:BigDecimal?,
    val own:Boolean,val active:Boolean,val boughtPrice:BigDecimal?,
    val lastSoldPrice:BigDecimal?,val units:Int=10){


}
