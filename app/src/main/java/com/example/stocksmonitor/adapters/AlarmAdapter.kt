package com.example.stocksmonitor.adapters

import android.content.Context
import android.view.Gravity
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
        holder.alarmActive.setOnCheckedChangeListener { buttonView, isChecked ->
            val call = RetrofitClient.apiService.toggleAlert(alert.id,isChecked)
            call.enqueue(object : Callback<Boolean>{
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    val success:Boolean? = response.body()
                    success?.let { if(success) {
                            if(isChecked){
                                val toast = Toast.makeText(
                                    context, "Alarm Activated" as CharSequence,
                                    Toast.LENGTH_SHORT
                                )
                                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
                                toast.show()

                            }else{
                                val toast = Toast.makeText(
                                    context, "Alarm Deactivated" as CharSequence,
                                    Toast.LENGTH_SHORT
                                )
                                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
                                toast.show()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                }
            })
        }
        holder.alarmTypeView.text = alert.alertType
        holder.alarmTargetView.text = alert.lowerlimit?.toPlainString() ?: "0.0"
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
                        }
                    }

                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
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