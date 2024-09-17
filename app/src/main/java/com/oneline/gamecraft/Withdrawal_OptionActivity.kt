package com.oneline.gamecraft

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.adapter.RedeemAdapter
import com.oneline.gamecraft.databinding.ActivityWithdrawalOptionBinding
import com.oneline.gamecraft.modal.RedeemModal

class Withdrawal_OptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityWithdrawalOptionBinding
    var upi = ArrayList<RedeemModal>()
    private var googlePay = ArrayList<RedeemModal>()
    var amazon = ArrayList<RedeemModal>()
    var recharge = ArrayList<RedeemModal>()
    var hindcash = ArrayList<RedeemModal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWithdrawalOptionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val insetsController = ViewCompat.getWindowInsetsController(v)
            insetsController?.isAppearanceLightStatusBars = true
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val scaleUp: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        binding.tvBalance.text = TinyDB.getString(this, "balance", "")
        fetchDataFromServer()
        binding.llUpiBtn.setBackgroundResource(R.drawable.withdrawal_option_button_bg)
        binding.llHistoryBtn.setOnTouchListener {
                v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llHistoryBtn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llHistoryBtn.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    val intent = Intent(this, WithdrawalHistoryActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        binding.llUpiBtn.setOnTouchListener {
                v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llUpiBtn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llUpiBtn.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    binding.llUpiBtn.setBackgroundResource(R.drawable.withdrawal_option_button_bg)
                    binding.llGoogleBtn.setBackgroundResource(R.drawable.button_bg)
                    binding.llAmazonBtn.setBackgroundResource(R.drawable.button_bg)
                    binding.llRecharge.setBackgroundResource(R.drawable.button_bg)

                    setRecycleData(upi)
                }
            }
            true

        }
        binding.llGoogleBtn.setOnTouchListener {v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llGoogleBtn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llGoogleBtn.startAnimation(scaleUp)
                    v.performClick()
                    binding.llGoogleBtn.setBackgroundResource(R.drawable.withdrawal_option_button_bg)
                    binding.llUpiBtn.setBackgroundResource(R.drawable.button_bg)
                    binding.llAmazonBtn.setBackgroundResource(R.drawable.button_bg)
                    binding.llRecharge.setBackgroundResource(R.drawable.button_bg)
                    Utils.playPopSound(this)
                    setRecycleData(googlePay)
                }
            }
            true


        }
        binding.llAmazonBtn.setOnTouchListener {v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llAmazonBtn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llAmazonBtn.startAnimation(scaleUp)
                    v.performClick()
                    binding.llAmazonBtn.setBackgroundResource(R.drawable.withdrawal_option_button_bg)
                    binding.llGoogleBtn.setBackgroundResource(R.drawable.button_bg)
                    binding.llUpiBtn.setBackgroundResource(R.drawable.button_bg)
                    binding.llRecharge.setBackgroundResource(R.drawable.button_bg)
                    Utils.playPopSound(this)
                    setRecycleData(amazon)
                }
            }
            true
        }
        binding.llRecharge.setOnTouchListener {v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llRecharge.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llRecharge.startAnimation(scaleUp)
                    v.performClick()
                    binding.llRecharge.setBackgroundResource(R.drawable.withdrawal_option_button_bg)
                    binding.llGoogleBtn.setBackgroundResource(R.drawable.button_bg)
                    binding.llUpiBtn.setBackgroundResource(R.drawable.button_bg)
                    binding.llAmazonBtn.setBackgroundResource(R.drawable.button_bg)
                    setRecycleData(recharge)
                    Utils.playPopSound(this)
                }

            }
            true
        }
        binding.llHindcash.setOnTouchListener {v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llHindcash.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llHindcash.startAnimation(scaleUp)
                    v.performClick()
                    binding.llRecharge.setBackgroundResource(R.drawable.withdrawal_option_button_bg)
                    binding.llGoogleBtn.setBackgroundResource(R.drawable.button_bg)
                    binding.llUpiBtn.setBackgroundResource(R.drawable.button_bg)
                    binding.llAmazonBtn.setBackgroundResource(R.drawable.button_bg)
                    setRecycleData(hindcash)
                    Utils.playPopSound(this)
                }

            }
            true
        }
    }

    private fun fetchDataFromServer() {
        Utils.showLoadingPopUp(this)
        val url = "${com.oneline.gamecraft.Companion.siteUrl}get_withdrawal_options.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Utils.dismissLoadingPopUp()
                upi.clear()
                amazon.clear()
                googlePay.clear()
                recharge.clear()
                hindcash.clear()


                for (i in 0 until response.length()) {
                    val dataObject = response.getJSONObject(i)
                    val coins = dataObject.getString("coins")
                    val icon_link = dataObject.getString("icon_link")
                    val id = dataObject.getString("id")
                    val payment_title = dataObject.getString("payment_title")
                    val status = dataObject.getString("status")
                    val transObj = RedeemModal(coins, icon_link, id, payment_title, status)

                    if (payment_title.contains("UPI")) {
                        upi.add(transObj)
                    } else if (payment_title.contains("Google")) {
                        googlePay.add(transObj)
                    } else if (payment_title.contains("Amazon")) {
                        amazon.add(transObj)
                    } else if (payment_title.contains("Recharge")) {
                        recharge.add(transObj)
                    }else if (payment_title.contains("Hindcash")) {
                        hindcash.add(transObj)
                    }
                }

                setRecycleData(upi)
                Utils.dismissLoadingPopUp()

            },
            { error ->
                error.printStackTrace()
            }
        )

        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

    fun setRecycleData(list: ArrayList<RedeemModal>) {
        val adapter = RedeemAdapter(this, list)
        binding.rvRedeemHistory.layoutManager = LinearLayoutManager(this)
        binding.rvRedeemHistory.adapter = adapter
    }
}