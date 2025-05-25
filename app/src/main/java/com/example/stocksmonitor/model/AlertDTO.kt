package com.example.stocksmonitor.model

data class AlertDTO(val message:String,val currentPrice:String,
                    val previousPrice:String,val action:String,
                    val type:String)
