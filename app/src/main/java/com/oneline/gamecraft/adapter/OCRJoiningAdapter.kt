package com.oneline.gamecraft.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oneline.gamecraft.OCRTaskDetailsActivity
import com.oneline.gamecraft.R
import com.oneline.gamecraft.Utils
import com.oneline.gamecraft.modal.OCRJoiningTaskModel
import com.google.android.material.card.MaterialCardView

class OCRJoiningAdapter(
    private val context: Context,
    private val jsonArray: ArrayList<OCRJoiningTaskModel>
) :
    RecyclerView.Adapter<OCRJoiningAdapter.PaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val item = jsonArray[position]
        holder.paymentTitleTextView.text = item.offer_title
        holder.coin.text = item.offer_coin
        Glide.with(context).load(item.offer_icon).into(holder.offer_logo)
        holder.button.setOnClickListener {
            Utils.playPopSound(context)
            OCRJoiningTaskModel.selectedOCRJoiningTaskModel = item
            val intent = Intent(context, OCRTaskDetailsActivity::class.java)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return jsonArray.size
    }

    fun updateTrans(updateTrans: ArrayList<OCRJoiningTaskModel>) {
        jsonArray.clear()
        jsonArray.addAll(updateTrans)
        notifyDataSetChanged()
    }


    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paymentTitleTextView: TextView = itemView.findViewById(R.id.tv_title)
        val coin: TextView = itemView.findViewById(R.id.tv_coin)
        val button: MaterialCardView = itemView.findViewById(R.id.cvCopy)
        val offer_logo: ImageView = itemView.findViewById(R.id.iv_icon)
    }

}
