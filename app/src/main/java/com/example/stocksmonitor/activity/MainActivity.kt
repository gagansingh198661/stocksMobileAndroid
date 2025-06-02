package com.example.stocksmonitor.activity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocksmonitor.adapters.StockAdapter
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
import com.example.stocksmonitor.R
import com.example.stocksmonitor.model.AlertDTO


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
                    if(stockList!=null&& stockList.isNotEmpty()){}
                    stockList.forEach { it ->
                            if(!it.alerts.isNullOrEmpty()){
                                it.alerts.forEach { it1 ->
                                    it1.let { if(it1.alertDTO!=null&&it1.alertDTO.action!=null&&it1.alertDTO.action=="SELL"){
                                        showNotification(this@MainActivity,it1.alertDTO)

                                        }
                                    }
                                }
                            }
                        }
                    Toast.makeText(this@MainActivity,"Values Refreshed" as CharSequence, Toast.LENGTH_SHORT).show()
                }, { error ->
                    Log.e("API_ERROR", "Error fetching stocks: ${error.message}")
                })


        }catch ( e:Exception){
            Log.e("MainActivity",e.stackTraceToString())
        }

    }

    fun showNotification(context: Context, alertDTO: AlertDTO) {
        val channelId = "your_channel_id"  // Not needed for API 23, but useful for consistency
        val soundUri: Uri = Uri.parse("android.resource://${context.packageName}/${com.example.stocksmonitor.R.raw.bell_notification}")

        val builder = NotificationCompat.Builder(context)
            .setSmallIcon(com.example.stocksmonitor.R.drawable.ic_launcher_foreground)
            .setContentTitle("New Alert!")
            .setContentText("Stock ${alertDTO.message}")
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }



}
