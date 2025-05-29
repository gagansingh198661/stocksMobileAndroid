package com.example.stocksmonitor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stocksmonitor.model.InfoDTO

class StockAdapter(private val infoDTOList: MutableList<InfoDTO>,private val context: Context):RecyclerView.Adapter<StockAdapter.StockViewHolder>() ,
    AdapterView.OnItemClickListener {


    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stockSymbol: TextView = itemView.findViewById(R.id.stockSymbol)
        val currentPrice: TextView = itemView.findViewById(R.id.currentPrice)
        val lastSoldPrice: TextView = itemView.findViewById(R.id.lastSoldPrice)
        val targetPrice:TextView = itemView.findViewById(R.id.targetPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val infoDTO = infoDTOList[position]
        holder.lastSoldPrice.text = infoDTO.stock.lastSoldPrice?.toPlainString()
        holder.stockSymbol.text = infoDTO.stock.stockSymbol
        holder.currentPrice.text = infoDTO.stock.currentPrice?.toPlainString()
        holder.targetPrice.text = infoDTO.stock.targetPrice?.toPlainString()
        holder.itemView.setOnClickListener(View.OnClickListener {
            val pos = holder.bindingAdapterPosition
            val infoDTOInner = infoDTOList[pos]
            val intent  = Intent(context,StockDetailActivity::class.java)
            val  bundle =  Bundle()
            bundle.putString("lastSoldPrice",infoDTOInner.stock.lastSoldPrice?.toPlainString())
            bundle.putString("stockSymbol",infoDTOInner.stock.stockSymbol)
            bundle.putString("currentPrice",infoDTOInner.stock.currentPrice.toString())
            bundle.putString("targetPrice",infoDTOInner.stock.targetPrice?.toPlainString())
            bundle.putInt("units",infoDTOInner.stock.units)
            bundle.putString("boughtPrice",infoDTOInner.stock.boughtPrice.toString())
            bundle.putBoolean("own",infoDTOInner.stock.own)
            bundle.putBoolean("active",infoDTOInner.stock.active)
            intent.putExtra("stock",bundle)
            it?.context?.startActivity(intent)
        })
    }

    override fun getItemCount(): Int = infoDTOList.size

    fun updateStockList(newStocks: List<InfoDTO>) {
        infoDTOList.clear()
        infoDTOList.addAll(newStocks)
        notifyDataSetChanged() // Refresh UI
    }



    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //Log.d("Adapter", "onClick: Clicked")
    }
}