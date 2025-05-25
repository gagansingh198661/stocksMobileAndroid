package com.example.stocksmonitor

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.example.stocksmonitor.model.Stock
import com.example.stocksmonitor.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.util.Locale

class StockDetailActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_detail)
        val bundle = intent.getBundleExtra("stock")
        val stockSymbolTextView = findViewById<TextView>(R.id.stockSymbol)
        stockSymbolTextView.text=bundle?.getString("stockSymbol")
        val currentPriceValueTextView = findViewById<TextView>(R.id.currentPriceValue)
        currentPriceValueTextView.text = bundle?.getString("currentPrice")
        val targetPriceValueTextView = findViewById<TextView>(R.id.targetPriceValue)

        targetPriceValueTextView.text = bundle?.getString("targetPrice")
        Log.d("StockDetailsActivity","value : "+bundle?.getString("targetPrice"))
        val soldPriceValueTextView = findViewById<TextView>(R.id.soldPriceValue)
        soldPriceValueTextView.text = bundle?.getString("lastSoldPrice")
        val unitsValueEditView = findViewById<EditText>(R.id.unitsValue)
        val unitsValue = bundle?.getInt("units")
        Log.d("StockDetailActivity", "onCreate: "+bundle?.getInt("units").toString())
        unitsValueEditView.setText(unitsValue.toString())

        val requiredProfitValueTextView = findViewById<EditText>(R.id.requiredProfitValue)

        val boughtPriceValueTextView = findViewById<TextView>(R.id.boughtPriceValue)
        val boughtPrice : Float = bundle?.getString("boughtPrice")?.toFloat() ?: 0F
        boughtPriceValueTextView.text = boughtPrice.toString()
        if (unitsValue != null) {
            addListener(requiredProfitValueTextView,targetPriceValueTextView,unitsValue,boughtPrice)
        }
        if(bundle?.getString("targetPrice")==null){
            val requiredProfitValue = requiredProfitValueTextView.text.toString().toFloat()
            val targetPriceTemp =
                unitsValue?.let { calculateTargetPrice(it,boughtPrice,requiredProfitValue) }
            targetPriceValueTextView.text = String.format(Locale.ENGLISH,"%.2f",targetPriceTemp)
        }

        val activeSwitchView = findViewById<Switch>(R.id.active)
        activeSwitchView.isChecked = bundle?.getBoolean("active")?:false

        val ownSwitchView = findViewById<Switch>(R.id.own)
        ownSwitchView.isChecked = bundle?.getBoolean("own")?:false


        val saveButton = findViewById<Button>(R.id.save)
        saveButton.setOnClickListener ( object : OnClickListener{
            override fun onClick(v: View?) {

                val targetPrice = targetPriceValueTextView.text?.toString()?.toBigDecimal()
                val currentPrice = currentPriceValueTextView.text?.toString()?.toBigDecimal()
                val boughtPriceInner = boughtPriceValueTextView.text?.toString()?.toBigDecimal()?: BigDecimal(0)
                val units = unitsValueEditView.text?.toString()?.toInt()?:0
                val stockSymbol = stockSymbolTextView.text.toString()
                val active = activeSwitchView.isChecked
                val own = ownSwitchView.isChecked
                val stock = Stock(stockSymbol,null,null,null,currentPrice,targetPrice,own,active,boughtPriceInner,targetPrice,units)
                try{
                    val call = RetrofitClient.apiService.saveStock(stock)
                    call.enqueue(object : Callback<Stock>{
                        override fun onResponse(call: Call<Stock>, response: Response<Stock>) {
                            println(response.body())
                        }

                        override fun onFailure(call: Call<Stock>, t: Throwable) {
                            TODO("Not yet implemented")
                        }

                    })


                }catch (e:Exception){
                    Log.e("StockDetailActivity", "Error fetching stocks: ${e.message}")
                }

            }
        })
    }


    fun addListener(requiredProfitView:EditText,targetPrice : TextView,unitsValue : Int,boughtPrice : Float){
        requiredProfitView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                try {
                    val requiredProfitValue = requiredProfitView.text.toString().toFloat()
                    val targetPriceValue = calculateTargetPrice(unitsValue,boughtPrice,requiredProfitValue)
                    Log.d("StockDetailActivity", "afterTextChanged: "+targetPriceValue)
                    targetPrice.text = String.format("%.2f",targetPriceValue)
                }catch (e:NumberFormatException){
                    targetPrice.text = "00"
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
        val totalCost :Float = boughtPrice*units + 5 + profitRequired
        val targetPrice:Float = totalCost/units;
        return targetPrice
    }
}