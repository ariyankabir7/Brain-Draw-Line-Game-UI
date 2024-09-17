package com.oneline.gamecraft.adapter

import android.content.Context
import android.provider.Settings
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.oneline.gamecraft.Companion
import com.oneline.gamecraft.R
import com.oneline.gamecraft.TinyDB
import com.oneline.gamecraft.Utils
import com.oneline.gamecraft.modal.RedeemModal
import com.ice.money1.videoplayyer
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class RedeemAdapter(
    private val context: Context,
    private val jsonArray: ArrayList<RedeemModal>
) :
    RecyclerView.Adapter<RedeemAdapter.PaymentViewHolder>() {
    init {
        System.loadLibrary("keys")
    }

    external fun Hatbc(): String
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)
    private lateinit var itemName: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_withdrawal_option, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val item = jsonArray[position]


        holder.coinsTextView.text = item.coins
        holder.paymentTitleTextView.text = if (item.payment_title.contains("Amazon GiftCards")) {
            item.payment_title.take(10)


        } else {
            item.payment_title
        }
        itemName = item.payment_title

        Glide.with(context)
            .load(item.icon_link) // Replace `holder.profile` with the appropriate key if needed
            .into(holder.withOption)
        val scaleUp: Animation = AnimationUtils.loadAnimation(context, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(context, R.anim.scale_down)

        holder.redeemBtn.setOnTouchListener {
                v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.redeemBtn.startAnimation(scaleDown)
                }
                MotionEvent.ACTION_UP -> {
                    holder.redeemBtn.startAnimation(scaleUp)
                    v.performClick()
                if (TinyDB.getString(context, "balance", "0")!!.toInt() >= item.coins.toInt()) {

                showPpopupDialog(item.payment_title, item.id,item.icon_link,item.coins)

            } else {
                Utils.showRedSnackBar(holder.redeemBtn, "Insufficient Balance !")
            }
                    }
                }
            true
        }
    }

    private fun showPpopupDialog(paymentTitle: String, id: String, iconLink: String, coins: String) {
        AlertDialog.Builder(context, R.style.TransparentDialogTheme).setView(R.layout.popup_withdrawal_details)
            .setCancelable(true).create().apply {
                show()

                val textView = findViewById<TextInputEditText>(R.id.tie_id)
                val tite = findViewById<TextView>(R.id.tv_with_amount)
                val tvfees = findViewById<TextView>(R.id.tv_fees)
                val tvcoins = findViewById<TextView>(R.id.tv_with_coins)
                val logo = findViewById<ImageView>(R.id.ImageView)

                tvcoins?.text=coins
                tite?.text=paymentTitle


                when {
                    paymentTitle.contains("UPI") -> {
                        textView?.hint = "Enter UPI Id"
                        textView?.inputType = InputType.TYPE_CLASS_TEXT
                        tvfees?.visibility = View.VISIBLE
                    }
                    paymentTitle.contains("Google") -> {
                        textView?.hint = "Enter Email Id"
                        textView?.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        tvfees?.visibility = View.INVISIBLE
                    }
                    paymentTitle.contains("Recharge") -> {
                        textView?.hint = "Enter Phone No"
                        textView?.inputType = InputType.TYPE_CLASS_PHONE
                        tvfees?.visibility = View.INVISIBLE
                    }
                    paymentTitle.contains("Hindcash") -> {
                        textView?.hint = "Enter Phone No"
                        textView?.inputType = InputType.TYPE_CLASS_PHONE
                        tvfees?.visibility = View.INVISIBLE
                    }
                }
                findViewById<Button>(R.id.submitButton)?.setOnClickListener {
                    val inputText = textView?.editableText

                    if (inputText!!.isEmpty()) {
                        Toast.makeText(context, "Enter Details Properly !", Toast.LENGTH_SHORT).show()
                    } else if (!isValidInput(paymentTitle, inputText.toString())) {
                        Toast.makeText(context, "Invalid input format!", Toast.LENGTH_SHORT).show()
                    } else {
                        redeemCoin(inputText, id)
                        dismiss()
                    }
                }
                findViewById<MaterialCardView>(R.id.cv_closeBtn)?.setOnClickListener {
                    dismiss()
                }

            }

    }
    private fun isValidInput(paymentTitle: String, input: String): Boolean {
        return when {
            paymentTitle.contains("UPI") -> {
                // Implement UPI ID validation logic here
                input.matches("^(?=.*@)[a-zA-Z0-9@._-]+\$".toRegex())
            }
            paymentTitle.contains("Google") -> {
                // Email validation using regex
                android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
            }
            paymentTitle.contains("Recharge") -> {
                // Phone number validation (assuming 10-digit phone number)
                input.matches("^[0-9]{10}\$".toRegex())
            }
            paymentTitle.contains("Hindcash") -> {
                // Phone number validation (assuming 10-digit phone number)
                input.matches("^[0-9]{10}\$".toRegex())
            }
            else -> {
                // Default to email validation
                android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
            }
        }
    }
    private fun redeemCoin(text: Editable?, id: String) {
        Utils.showLoadingPopUp(context)
        val deviceid: String = Settings.Secure.getString(
            context.contentResolver, Settings.Secure.ANDROID_ID
        )
        val time = System.currentTimeMillis()

        val url4 = "${Companion.siteUrl}redeem_point.php"
        val email = TinyDB.getString(context, "phone", "")

        val queue4: RequestQueue = Volley.newRequestQueue(context)
        val stringRequest =
            object : StringRequest(Method.POST, url4, { response ->

                val yes = Base64.getDecoder().decode(response)
                val res = String(yes, Charsets.UTF_8)
                if (res.contains(",")) {
                    val alldata = res.trim().split(",")
                    Toast.makeText(context, alldata[0], Toast.LENGTH_SHORT).show()
                    TinyDB.saveString(context, "balance", alldata[1])


                } else {
                    Toast.makeText(context, res, Toast.LENGTH_SHORT).show()

                }

                Utils.dismissLoadingPopUp()


            }, Response.ErrorListener { error ->
                Utils.dismissLoadingPopUp()
                Toast.makeText(context, "Internet Slow", Toast.LENGTH_SHORT).show()
                // requireActivity().finish()
            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()

                    val dbit32 = videoplayyer.encrypt(deviceid, Hatbc()).toString()
                    val tbit32 = videoplayyer.encrypt(time.toString(), Hatbc()).toString()
                    val email = videoplayyer.encrypt(email.toString(), Hatbc()).toString()
                    val w32 = videoplayyer.encrypt(id.toString(), Hatbc()).toString()
                    val d32 = videoplayyer.encrypt(text.toString(), Hatbc()).toString()

                    val den64 = Base64.getEncoder().encodeToString(dbit32.toByteArray())
                    val ten64 = Base64.getEncoder().encodeToString(tbit32.toByteArray())
                    val email64 = Base64.getEncoder().encodeToString(email.toByteArray())
                    val w64 = Base64.getEncoder().encodeToString(w32.toByteArray())
                    val d64 = Base64.getEncoder().encodeToString(d32.toByteArray())

                    val encodemap: MutableMap<String, String> = HashMap()
                    encodemap["deijvfijvmfhvfvhfbhbchbfybebd"] = den64
                    encodemap["waofhfuisgdtdrefssfgsgsgdhddgd"] = ten64
                    encodemap["fdvbdfbhbrthyjsafewwt5yt5"] = email64
                    encodemap["fsfsdfefsefwefwefewfwefvfvdfbdbd"] = w64
                    encodemap["defsdfefsefwefwefewfwefvfvdfbdbd"] = d64

                    val jason = Json.encodeToString(encodemap)

                    val den264 = Base64.getEncoder().encodeToString(jason.toByteArray())

                    val final = URLEncoder.encode(den264, StandardCharsets.UTF_8.toString())

                    params["dase"] = final
                    val encodedAppID = Base64.getEncoder()
                        .encodeToString(Companion.APP_ID.toString().toByteArray())
                    params["app_id"] = encodedAppID

                    return params
                }
            }

        queue4.add(stringRequest)
    }

    override fun getItemCount(): Int {
        return jsonArray.size
    }


    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paymentTitleTextView: TextView = itemView.findViewById(R.id.tv_name)
        val coinsTextView: TextView = itemView.findViewById(R.id.tv_coins)
        val redeemBtn: MaterialCardView = itemView.findViewById(R.id.bt_claim)
        val withOption: ImageView = itemView.findViewById(R.id.iv_icon)


    }
}
