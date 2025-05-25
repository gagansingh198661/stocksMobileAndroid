package com.example.stocksmonitor

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocksmonitor.model.InfoDTO
import com.example.stocksmonitor.service.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class MainActivity : ComponentActivity() {

    private lateinit var ownedStockView: RecyclerView
    private lateinit var watchlistView: RecyclerView
    private lateinit var ownedStocksAdapter: StockAdapter
    private lateinit var watchlistAdapter: StockAdapter
    private var job: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setOwnedStockList()
//        setWatchList()
        ownedStockView = findViewById(R.id.stocksList)
        ownedStockView.layoutManager = LinearLayoutManager(this)
        ownedStocksAdapter = StockAdapter(mutableListOf(),this)
        ownedStockView.adapter = ownedStocksAdapter



        watchlistView = findViewById(R.id.watchlist)
        watchlistView.layoutManager = LinearLayoutManager(this)
        watchlistAdapter = StockAdapter(mutableListOf(),this)
        watchlistView.adapter = watchlistAdapter
    }





    override fun onStart() {
        super.onStart()
        job = startRepeatingFunction().launchIn(lifecycleScope) // Start the flow
    }

    override fun onStop(){
        super.onStop()
        job?.cancel()

    }



    private fun startRepeatingFunction() = flow {
        while (true) {
            emit(Unit) // Emit a signal every 10 seconds
            delay(30000) // Pause for 10 seconds
        }
    }.flowOn(Dispatchers.Main) // Run on the Main UI Thread
        .onEach { callStocksData() }

    @SuppressLint("CheckResult")
    private fun callStocksData(){
        try {
            val ownedStock : MutableList<InfoDTO> = mutableListOf();
            val watchList : MutableList<InfoDTO> = mutableListOf();
            RetrofitClient.apiService.getStockData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ stockList ->
                    ownedStock.addAll(stockList.filter { it-> it.stock.own==true })
                    watchList.addAll(stockList.filter { it-> it.stock.own!=true })
                    ownedStocksAdapter.updateStockList(ownedStock)
                    watchlistAdapter.updateStockList(watchList)
                }, { error ->
                    Log.e("API_ERROR", "Error fetching stocks: ${error.message}")
                })


        }catch ( e:Exception){
            Log.e("MainActivity",e.stackTraceToString())
        }

    }



}
