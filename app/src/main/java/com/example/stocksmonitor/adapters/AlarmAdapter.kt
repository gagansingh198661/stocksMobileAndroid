package com.example.stocksmonitor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.stocksmonitor.R
import com.example.stocksmonitor.model.Alert

import com.example.stocksmonitor.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AlarmAdapter(private val alertList: MutableList<Alert>, private val context: Context):RecyclerView.Adapter<AlarmAdapter.AlertViewHolder>() ,
    AdapterView.OnItemClickListener{


    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val alarmTypeView: TextView = itemView.findViewById(R.id.alarmType)
        val alarmTargetView: TextView = itemView.findViewById(R.id.alarmTarget)
        val calculatedValue: TextView = itemView.findViewById(R.id.calculatedValue)
        val alarmActive: Switch = itemView.findViewById(R.id.alarmActive)
        val deleteAlarm : Button = itemView.findViewById(R.id.deleteAlarm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item_layout, parent, false)
        return AlertViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alertList.size
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alertList[position]
        holder.alarmActive.isChecked = alert.active
        holder.alarmTypeView.text = alert.alertType
        holder.alarmTargetView.text = alert.lowerlimit.toPlainString()
        if(alert.alertType.indexOf("percent",0,true)!=-1){

        }
        holder.deleteAlarm.setOnClickListener { it ->

            val call = RetrofitClient.apiService.deleteAlert(alert.id)
            call.enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    val success:Boolean? = response.body()
                    success?.let { if(success){
                        alertList.removeAt(holder.bindingAdapterPosition)
                        notifyDataSetChanged()
                        Toast.makeText(context,"Alarm Deleted" as CharSequence,
                            Toast.LENGTH_SHORT).show()
                    } }

                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    private fun getCalculatedValue(alert:Alert):String{
        return ""
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
    }
}