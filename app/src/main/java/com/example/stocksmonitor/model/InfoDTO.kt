package com.example.stocksmonitor.model

data class InfoDTO(val action:String,
                   val stock: Stock,val alerts:MutableList<Alert>?)
