package com.oneline.gamecraft.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.oneline.gamecraft.EarningWallDetailsActivity
import com.oneline.gamecraft.R
import com.oneline.gamecraft.Utils
import com.oneline.gamecraft.modal.EarningwallModal
import com.google.android.material.card.MaterialCardView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class earningwallAdapter(
    private val context: Context,
    private val jsonArray: ArrayList<EarningwallModal>

) :
    RecyclerView.Adapter<earningwallAdapter.ViewHolder>() {
    init {
        System.loadLibrary("keys")
    }

    external fun Hatbc(): String

    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = jsonArray[position]
        //val newFormatDate = formatDateTime(item.date)
        val scaleUp: Animation = AnimationUtils.loadAnimation(context, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(context, R.anim.scale_down)

        holder.paymentTitleTextView.text = item.offer_title
        holder.coins.text = item.offer_coin
        Glide.with(context)
            .load(item.offer_icon)
            .into(holder.Icon)

        holder.button.setOnTouchListener {v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.button.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    holder.button.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(context)
                    val intent = Intent(context, EarningWallDetailsActivity::class.java)
                    intent.putExtra("title", item.offer_title)
                    intent.putExtra("coin", item.offer_coin)
                    intent.putExtra("link", item.offer_link)
                    intent.putExtra("icon", item.offer_icon)
                    intent.putExtra("des", item.offer_description)
                    context.startActivity(intent)
                }
            }
            true
        }
//
//        if (item.status.toInt()== 1) {
//            holder.point.text = "+" + item.amount
//
//        } else if(item.status.toInt()== -1) {
//            holder.point.text =  "+" + item.amount
//
//        }else{
//            holder.point.text =  "-" + item.amount
//        }
        }


        override fun getItemCount(): Int {
            return jsonArray.size
        }

        fun updateTrans(updateTrans: ArrayList<EarningwallModal>) {
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

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val paymentTitleTextView: TextView = itemView.findViewById(R.id.tv_title)
            val coins: TextView = itemView.findViewById(R.id.tv_coin)
             val Icon: ImageView = itemView.findViewById(R.id.iv_icon)
            val button: MaterialCardView = itemView.findViewById(R.id.cvCopy)
        }

    }
