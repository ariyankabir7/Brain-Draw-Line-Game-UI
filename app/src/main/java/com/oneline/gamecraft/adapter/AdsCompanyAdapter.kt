package com.oneline.gamecraft.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oneline.gamecraft.R
import com.oneline.gamecraft.Utils
import com.oneline.gamecraft.modal.CompanyAdsModal
import com.google.android.material.card.MaterialCardView

class AdsCompanyAdapter(
    private val context: Context,
    private val jsonArray: ArrayList<CompanyAdsModal>
) :
    RecyclerView.Adapter<AdsCompanyAdapter.PaymentViewHolder>() {
    init {
        System.loadLibrary("keys")
    }

    external fun Hatbc(): String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_ads_comapny, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val item = jsonArray[position]

        holder.paymentTitleTextView.text = item.title
        holder.cvbtn.setOnClickListener {
            Utils.openUrl(
                context, item.link
            )
        }
    }


    override fun getItemCount(): Int {
        return jsonArray.size
    }

    fun updateTrans(updateTrans: ArrayList<CompanyAdsModal>) {
        jsonArray.clear()
        jsonArray.addAll(updateTrans)
        notifyDataSetChanged()
    }



    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paymentTitleTextView: TextView = itemView.findViewById(R.id.tv_title)
        val cvbtn: MaterialCardView = itemView.findViewById(R.id.cv_ads_link_btn)
    }

}
