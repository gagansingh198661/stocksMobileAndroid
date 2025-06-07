package com.example.stocksmonitor.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocksmonitor.adapters.AlarmAdapter
import com.example.stocksmonitor.R
import com.example.stocksmonitor.model.InfoDTO
import com.example.stocksmonitor.model.Stock
import com.example.stocksmonitor.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.util.Locale

class StockDetailActivity : ComponentActivity() {

    private lateinit var alertsView: RecyclerView
    private lateinit var stockSymbolString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_detail)
        val bundle = intent.getBundleExtra("stock")
        val stockSymbolTextView = findViewById<TextView>(R.id.stockSymbol)
        stockSymbolTextView.text = bundle?.getString("stockSymbol")
        stockSymbolString = bundle?.getString("stockSymbol").toString()
        val currentPriceValueTextView = findViewById<TextView>(R.id.currentPriceValue)
        currentPriceValueTextView.text = bundle?.getString("currentPrice")

        Log.d("StockDetailsActivity","value : "+bundle?.getString("targetPrice"))
        val soldPriceValueEditText = findViewById<EditText>(R.id.soldPriceValueSD)
        bundle?.getString("lastSoldPrice")?.let { soldPriceValueEditText.setText(it.toString()) }

        val unitsValueEditView = findViewById<EditText>(R.id.unitsValueSD)
        val unitsValue = bundle?.getInt("units")
        Log.d("StockDetailActivity", "onCreate: "+bundle?.getInt("units").toString())
        unitsValueEditView.setText(unitsValue.toString())


        val boughtPriceValueEditText = findViewById<EditText>(R.id.boughtPriceValueSD)
        var boughtPrice : Float =  0F
        if(bundle?.getString("boughtPrice")!=null){
            boughtPrice = bundle.getString("boughtPrice").toString().toFloat()
        }
        boughtPriceValueEditText.setText(boughtPrice.toString())

        val activeSwitchView = findViewById<Switch>(R.id.alarmActive)
        activeSwitchView.isChecked = bundle?.getBoolean("active")?:false

        val ownSwitchView = findViewById<Switch>(R.id.own)
        ownSwitchView.isChecked = bundle?.getBoolean("own")?:false


        alertsView=findViewById(R.id.alarmlist)
        alertsView.layoutManager = LinearLayoutManager(this)
        setAdapter(bundle?.getString("stockSymbol").toString())

        val saveButton = findViewById<Button>(R.id.save)
        saveButton.setOnClickListener ( object : OnClickListener{
            override fun onClick(v: View?) {
                val progressBar = findViewById<ProgressBar>(R.id.updateLoading)
                progressBar.visibility = View.VISIBLE
                val lastSoldPrice = soldPriceValueEditText.text?.toString()?.toBigDecimal()

                val currentPrice = currentPriceValueTextView.text?.toString()?.toBigDecimal()
                val boughtPriceInner = boughtPriceValueEditText.text?.toString()?.toBigDecimal()?: BigDecimal(0)
                val units = unitsValueEditView.text?.toString()?.toInt()?:0
                val stockSymbol = stockSymbolTextView.text.toString()
                val active = activeSwitchView.isChecked
                val own = ownSwitchView.isChecked
                val stock = Stock(stockSymbol,null,null,null,currentPrice,null,own,active,boughtPriceInner,lastSoldPrice,units)
                try{
                    val call = RetrofitClient.apiService.saveStock(stock)
                    call.enqueue(object : Callback<Stock>{
                        override fun onResponse(call: Call<Stock>, response: Response<Stock>) {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@StockDetailActivity,"Details Saved" as CharSequence,Toast.LENGTH_SHORT).show()
                            println(response.body())
                        }

                        override fun onFailure(call: Call<Stock>, t: Throwable) {
                        }

                    })


                }catch (e:Exception){
                    Log.e("StockDetailActivity", "Error fetching stocks: ${e.message}")
                }
            }
        })

        val createAlert = findViewById<ImageButton>(R.id.createAlertStockDetail)
        createAlert.setOnClickListener { it ->


            val intentNew  = Intent(this@StockDetailActivity, CreateAlertActivity::class.java)
            intentNew.putExtra("stockSymbol",stockSymbolString)
            it?.context?.startActivity(intentNew)
        }




    }

    private fun setAdapter(stockSymbol:String) {
        try{
            val call = RetrofitClient.apiService.getStock(stockSymbol)
            call.enqueue(object : Callback<InfoDTO>{
                override fun onResponse(call: Call<InfoDTO>, response: Response<InfoDTO>) {
                    val infodto = response.body()
                    val alarmAdapter = infodto?.let { it.alerts?.let { it1 -> AlarmAdapter(it1,this@StockDetailActivity) } }
                    alertsView.adapter = alarmAdapter

                }

                override fun onFailure(call: Call<InfoDTO>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })


        }catch (e:Exception){
            Log.e("StockDetailActivity", "Error fetching stocks: ${e.message}")
        }

    }


    fun addListener(boughtPrice : Float,soldPriceValueEditText:EditText,requiredProfitView:EditText,targetPrice : TextView,unitsValue : Int){
        requiredProfitView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                try {
                    val requiredProfitValue = requiredProfitView.text.toString().toFloat()
                    val targetPriceValue = calculateTargetPrice(unitsValue,boughtPrice,requiredProfitValue)
                    Log.d("StockDetailActivity", "afterTextChanged: "+targetPriceValue)
                    targetPrice.text = String.format("%.2f",targetPriceValue)
                }catch (e:NumberFormatException){
                    targetPrice.text = "0.0"
                    Log.e("StockDetailActivity", "afterTextChanged: "+e )

                }

            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })


    }

    fun calculateTargetPrice(units:Int, boughtPrice:Float,profitRequired:Float):Float{
        if (boughtPrice.equals(0f)){
            return 0f
        }
        val totalCost :Float = boughtPrice*units + 5 + profitRequired
        val targetPrice:Float = totalCost/units;
        return targetPrice
    }
}