package com.oneline.gamecraft.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.R
import com.oneline.gamecraft.modal.TotalTransctionModal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class totalHistoryAdapter(
    private val context: Context,
    private val jsonArray: ArrayList<TotalTransctionModal>
) :
    RecyclerView.Adapter<totalHistoryAdapter.PaymentViewHolder>() {
    init {
        System.loadLibrary("keys")
    }

    external fun Hatbc(): String

    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_transction_history, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val item = jsonArray[position]
        val newFormatDate = formatDateTime(item.date)
        holder.paymentTitleTextView.text = item.title
        holder.date.text = newFormatDate



        if (item.status.toInt()== 1) {
            holder.point.text = "+" + item.amount

        } else if(item.status.toInt()== -1) {
            holder.point.text =  "+" + item.amount

        }else{
            holder.point.text =  "-" + item.amount
        }
    }


    override fun getItemCount(): Int {
        return jsonArray.size
    }

    fun updateTrans(updateTrans: ArrayList<TotalTransctionModal>) {
        jsonArray.clear()
        jsonArray.addAll(updateTrans)
        notifyDataSetChanged()
    }

    fun formatDateTime(input: String): String {
        // Define the input format
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // Parse the input date-time string
        val dateTime = LocalDateTime.parse(input, inputFormatter)

        // Define the output format
        val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm a")

        // Format the date-time to the desired output format
        return dateTime.format(outputFormatter)
    }

    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paymentTitleTextView: TextView = itemView.findViewById(R.id.tv_title)
        val date: TextView = itemView.findViewById(R.id.tv_date)

        //  val status: TextView = itemView.findViewById(R.id.tv_status)
        val point: TextView = itemView.findViewById(R.id.tv_amount)
    }

}
