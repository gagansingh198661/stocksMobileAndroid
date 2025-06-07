package com.example.stocksmonitor.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.stocksmonitor.R
import com.example.stocksmonitor.model.Alert
import com.example.stocksmonitor.model.AlertRequest
import com.example.stocksmonitor.model.InfoDTO
import com.example.stocksmonitor.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class CreateAlertActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_alert)
        val stockSymbolView = findViewById<TextView>(R.id.stockSymbolCA)
        stockSymbolView.text = intent.getStringExtra("stockSymbol").toString()

        val percentageView = findViewById<EditText>(R.id.percentageCA)
        val percentageType = findViewById<Spinner>(R.id.percentageType)
        addListenerForPercentageText(percentageView,percentageType)
        addItemsOnSpinner(percentageType)
        addListenerForPercentAgeSpinner(percentageType,percentageView)
        addListenerToTargetPrice()
        getStockDetails()
        addListenerToCreateButton()
        addListenerToCancelButton()
        addListenerToUnitsView()
    }

    private fun addListenerToUnitsView() {
        val unitsCA =findViewById<EditText>(R.id.unitsCA)
        unitsCA.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                updatedCalculateProfit()
            }

        })
    }


    private fun addListenerToCancelButton() {
        val createAlertButton = findViewById<Button>(R.id.cancelButtonCA)
        createAlertButton.setOnClickListener { it ->
            finish()
        }
    }

    private fun addListenerToCreateButton() {
        val createAlertButton = findViewById<Button>(R.id.createAlertButtonCA)
        createAlertButton.setOnClickListener { it ->
            createAlert()
        }
    }

    private fun createAlert(){
        try{
            val stockSymbol = findViewById<TextView>(R.id.stockSymbolCA).text.toString()
            val targetValue = findViewById<EditText>(R.id.targetPriceCA).text.toString()
            val alert = AlertRequest(stockSymbol, targetValue)
            val call = RetrofitClient.apiService.createAlert(alert)
            call.enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    val alertMade = response.body()
                    if(alertMade!=null&&alertMade){
                        Toast.makeText(this@CreateAlertActivity,"Alert Created" as CharSequence,Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this@CreateAlertActivity,"Alert Already Exists" as CharSequence,Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.e("CreateAlertActivity : ",t.message.toString())
                }

            })


        }catch (e:Exception){
            Log.e("CreateAlert", "Error fetching stocks: ${e.message}")
        }
    }

    private fun addListenerForPercentAgeSpinner(percentageType: Spinner,percentageView : EditText) {
        val targetPriceCA = findViewById<EditText>(R.id.targetPriceCA)
        var calculatedValue = 0f
        percentageType.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                // Your code here
                try{
                    val percentageValue = percentageView.text.toString().toFloat()

                    if(i==0){
                        val targetValue = findViewById<TextView>(R.id.currentPriceCreateAlert).text.toString().toFloat()
                        calculatedValue = getCalculatedTarget(percentageValue, targetValue)
                        targetPriceCA.setText(String.format(Locale.ENGLISH,"%.2f",calculatedValue))
                        updatedCalculateProfit()
                    }else if(i==1){
                        val targetValue = findViewById<TextView>(R.id.soldPriceCA).text.toString().toFloat()
                        calculatedValue = getCalculatedTarget(percentageValue, targetValue)
                        targetPriceCA.setText(String.format(Locale.ENGLISH,"%.2f",calculatedValue))
                        updatedCalculateProfit()
                    }else if(i==2) {
                        val targetValue = findViewById<TextView>(R.id.boughtPriceCA).text.toString().toFloat()
                        calculatedValue = getCalculatedTarget(percentageValue, targetValue)
                        targetPriceCA.setText(String.format(Locale.ENGLISH,"%.2f",calculatedValue))
                        updatedCalculateProfit()
                    }else if(i==3) {
                        val targetValue = findViewById<TextView>(R.id.highestPriceCA).text.toString().toFloat()
                        calculatedValue = getCalculatedTarget(percentageValue, targetValue)
                        targetPriceCA.setText(String.format(Locale.ENGLISH,"%.2f",calculatedValue))
                        updatedCalculateProfit()
                    }

                }catch(e : Exception){
                    Log.e("CreateAlertActivity", "onItemSelected: "+e )
                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        })

    }

    private fun addItemsOnSpinner( percentageType : Spinner) {
        ArrayAdapter.createFromResource(this,R.array.price_array,android.R.layout.simple_spinner_item).also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            percentageType.adapter = adapter }

    }

    private fun getStockDetails() {
        val stockSymbol = intent.getStringExtra("stockSymbol").toString()
        try{
            val call = RetrofitClient.apiService.getStock(stockSymbol)
            call.enqueue(object : Callback<InfoDTO> {
                override fun onResponse(call: Call<InfoDTO>, response: Response<InfoDTO>) {
                    val infodto = response.body()
                    val stock = infodto?.stock
                    val currentPrice = findViewById<TextView>(R.id.currentPriceCreateAlert)
                    currentPrice.text = stock?.currentPrice.toString()
                    val soldPriceView = findViewById<TextView>(R.id.soldPriceCA)
                    if(stock!=null){
                        if(stock.lastSoldPrice==null){
                            soldPriceView.text = java.lang.String("0.00")
                        }else{
                            soldPriceView.text = stock.lastSoldPrice.toString()
                        }
                    }
                    val boughtPriceView = findViewById<TextView>(R.id.boughtPriceCA)
                    if(stock!=null){
                        if(stock.lastSoldPrice==null){
                            boughtPriceView.text = java.lang.String("0.00")
                        }else{
                            boughtPriceView.text = stock.boughtPrice.toString()
                        }
                    }
                }

                override fun onFailure(call: Call<InfoDTO>, t: Throwable) {
                    Log.e("CreateAlertActivity : ",t.message.toString())
                }

            })


        }catch (e:Exception){
            Log.e("StockDetailActivity", "Error fetching stocks: ${e.message}")
        }

    }

    private fun addListenerToTargetPrice(){
        val targetPriceCA = findViewById<EditText>(R.id.targetPriceCA)
        val createAlertButton = findViewById<Button>(R.id.createAlertButtonCA)
        targetPriceCA.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                try {

                    if (!targetPriceCA.text.toString().equals("0.00")) {
                        createAlertButton.isEnabled = true
                    } else {
                        createAlertButton.isEnabled = false
                    }
                }catch(ex:Exception){
                    Log.e("CreateAlertActivity : ",ex.toString())
                }
            }
        })
    }

    fun addListenerForPercentageText(percentageView: EditText,percentageType : Spinner){
        val targetPriceCA = findViewById<EditText>(R.id.targetPriceCA)
        percentageView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                try {
                    val percentageValue = s.toString().toFloat()
                    val targetValue = findViewById<TextView>(R.id.currentPriceCreateAlert).text.toString().toFloat()
                    val selectedPercentageTypeValue = percentageType.selectedItem.toString()
                    var calculatedValue : Float = 0.0f
                    if(selectedPercentageTypeValue.equals("current",true)){
                        calculatedValue = getCalculatedTarget(percentageValue, targetValue)
                        targetPriceCA.setText(String.format(Locale.ENGLISH,"%.2f",calculatedValue))
                        updatedCalculateProfit()
                    }else if(selectedPercentageTypeValue.equals("sold",true)){
                        calculatedValue = getCalculatedTarget(percentageValue, targetValue)
                        targetPriceCA.setText(String.format(Locale.ENGLISH,"%.2f",calculatedValue))
                        updatedCalculateProfit()
                    }else if(selectedPercentageTypeValue.equals("bought",true)) {
                        calculatedValue = getCalculatedTarget(percentageValue, targetValue)
                        targetPriceCA.setText(String.format(Locale.ENGLISH,"%.2f",calculatedValue))
                        updatedCalculateProfit()
                    }else if(selectedPercentageTypeValue.equals("highest",true)) {
                        calculatedValue = getCalculatedTarget(percentageValue, targetValue)
                        targetPriceCA.setText(String.format(Locale.ENGLISH,"%.2f",calculatedValue))
                        updatedCalculateProfit()
                    }


                    //Log.d("CreateAlertActivity : text changed percentage", "afterTextChanged: "+targetPriceValue)
                    //targetPrice.text = String.format("%.2f",targetPriceValue)
                }catch (e:NumberFormatException){
                    //targetPrice.text = "00"
                    Log.e("CreateAlertActivity", "afterTextChanged: "+e )

                }catch (e : Exception){
                    Log.e("CreateAlertActivity", "afterTextChanged: "+e )
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

    fun getCalculatedTarget(percentage : Float, targetValue : Float):Float{
        if(percentage.equals(0f) || targetValue.equals(0f)){
            return 0f
        }
        var result : Float = 0f
        result =  (percentage/100) * targetValue + targetValue
        return result
    }

    fun updatedCalculateProfit(){
        try {
            val calculatedPrice = findViewById<EditText>(R.id.targetPriceCA).text.toString().toFloat()
            val currentPriceView = findViewById<TextView>(R.id.currentPriceCreateAlert)
            val currentPrice = currentPriceView.text.toString().toFloat()
            val units = findViewById<EditText>(R.id.unitsCA).text.toString().toInt()
            val total = calculatedPrice * units - (currentPrice * units)
            findViewById<TextView>(R.id.profitsCA).text = (total).toString()
        }catch(e:Exception){
            Log.e("Please Ignore",e.message.toString())
        }
    }



}