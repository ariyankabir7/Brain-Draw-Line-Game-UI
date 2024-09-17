package com.oneline.gamecraft

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.adapter.totalHistoryAdapter
import com.oneline.gamecraft.databinding.ActivityWithdrawalBinding
import com.oneline.gamecraft.modal.TotalTransctionModal
import com.google.android.material.card.MaterialCardView
import com.ice.money1.videoplayyer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class WithdrawalActivity : AppCompatActivity() {
    lateinit var binding: ActivityWithdrawalBinding
    var totalHistory = ArrayList<TotalTransctionModal>()
    lateinit var redeemCode: String
    init {
        System.loadLibrary("keys")
    }

    external fun Hatbc(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWithdrawalBinding.inflate(layoutInflater)
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
        getAllHistory()

        binding.cvRedeemCode.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvRedeemCode.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvRedeemCode.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    showRedeemPopup()
                }
            }
            true
        }
        binding.cvWithdrawBtn.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvWithdrawBtn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvWithdrawBtn.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    val intent = Intent(this, Withdrawal_OptionActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    fun getAllHistory() {
        Utils.showLoadingPopUp(this)
        binding.rvTotalHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val emails = Base64.getEncoder()
            .encodeToString(TinyDB.getString(this, "phone", "")!!.toByteArray())
        val id=Base64.getEncoder().encodeToString("${com.oneline.gamecraft.Companion.APP_ID}".toByteArray())

        val url = "${com.oneline.gamecraft.Companion.siteUrl}get_redeem_history.php?email=$emails&app_id=$id"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest =
            object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->

                if (response.length() > 0) {
                    totalHistory.clear()

                    for (i in 0 until response.length()) {
                        val dataObject = response.getJSONObject(i)

                        val title = dataObject.getString("title")
                        val amount = dataObject.getString("amount")
                        val date = dataObject.getString("date")
                        val status = dataObject.getString("status")
                        val historyRedeemModal = TotalTransctionModal(title, amount, date, status)
                        totalHistory.add(historyRedeemModal)
                    }
                    Utils.dismissLoadingPopUp()
                    val adapter = totalHistoryAdapter(this, totalHistory)
                    binding.rvTotalHistory.adapter = adapter
                    binding.pb.visibility = View.GONE
                } else {
                    binding.nodata.visibility = View.VISIBLE
                    binding.rvTotalHistory.visibility = View.GONE
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(this, "Internet Slow !", Toast.LENGTH_SHORT).show()
            }) {}

        requestQueue.add(jsonArrayRequest)
    }

    private fun showRedeemPopup() {
        AlertDialog.Builder(this, R.style.updateDialogTheme).setView(R.layout.popup_redeem_code)
            .setCancelable(true).create().apply {
                show()
                findViewById<MaterialCardView>(R.id.cv_closeBtn)?.setOnClickListener {
                    dismiss()
                }
              val text= findViewById<EditText>(R.id.tie_id)!!.text
                findViewById<Button>(R.id.submitButton)?.setOnClickListener {
                    if ( text.isEmpty()) {

                        Toast.makeText(
                            this@WithdrawalActivity,
                            "Enter Redeem Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                      redeemCode=text.toString()
                        promocodeValue()
                    }
                }
                findViewById<Button>(R.id.bt_get_code)?.setOnClickListener {
                    Utils.openUrl(
                        this@WithdrawalActivity,
                        Uri.parse(TinyDB.getString(this@WithdrawalActivity, "telegram_link", ""))
                            .toString()
                    )

                }
            }

    }
    private fun promocodeValue() {
        Utils.showLoadingPopUp(this)
        val deviceid: String = Settings.Secure.getString(
            contentResolver, Settings.Secure.ANDROID_ID
        )
        val time = System.currentTimeMillis()

        val url3 = "${com.oneline.gamecraft.Companion.siteUrl}apply_promocode.php"
        val email = TinyDB.getString(this, "phone", "")


        val queue3: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest =
            object : StringRequest(Method.POST, url3, { response ->

                val yes = java.util.Base64.getDecoder().decode(response)
                val res = String(yes, Charsets.UTF_8)
                Utils.dismissLoadingPopUp()

                if (res.contains(",")) {
                    val alldata = res.trim().split(",")
                    TinyDB.saveString(this, "balance", alldata[1])
                    Toast.makeText(this, alldata[0], Toast.LENGTH_SHORT).show()

                        Utils.dismissLoadingPopUp()

                } else {
                    Toast.makeText(this, res, Toast.LENGTH_LONG).show()
                }


            }, Response.ErrorListener { error ->
                Utils.dismissLoadingPopUp()

                Toast.makeText(this, "Internet Slow", Toast.LENGTH_SHORT).show()
                // requireActivity().finish()
            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()

                    val dbit32 = videoplayyer.encrypt(deviceid, Hatbc()).toString()
                    val tbit32 = videoplayyer.encrypt(time.toString(), Hatbc()).toString()
                    val email = videoplayyer.encrypt(email.toString(), Hatbc()).toString()
                    val promo = videoplayyer.encrypt(redeemCode, Hatbc()).toString()

                    val den64 = java.util.Base64.getEncoder().encodeToString(dbit32.toByteArray())
                    val ten64 = java.util.Base64.getEncoder().encodeToString(tbit32.toByteArray())
                    val email64 = java.util.Base64.getEncoder().encodeToString(email.toByteArray())
                    val promo64 = java.util.Base64.getEncoder().encodeToString(promo.toByteArray())

                    val encodemap: MutableMap<String, String> = HashMap()
                    encodemap["deijvfijvmfhvfvhfbhbchbfybebddgb"] = den64
                    encodemap["waofhfuisgdtdrefssfgsgsgdhddgder"] = ten64
                    encodemap["fdvbdfbhbrthyjsafewwt5yt5tedgwcv"] = email64
                    encodemap["prvbdfbhbrthyjsafewwt5ydesfsverg"] = promo64

                    val jason = Json.encodeToString(encodemap)

                    val den264 = java.util.Base64.getEncoder().encodeToString(jason.toByteArray())

                    val final = URLEncoder.encode(den264, StandardCharsets.UTF_8.toString())

                    params["dase"] = final

                    val encodedAppID =
                        java.util.Base64.getEncoder()
                            .encodeToString(com.oneline.gamecraft.Companion.APP_ID.toString().toByteArray())
                    params["app_id"] = encodedAppID

                    return params
                }
            }

        queue3.add(stringRequest)
    }

    override fun onResume() {
        super.onResume()
        binding.tvBalance.text= TinyDB.getString(this, "balance", "")
    }
}