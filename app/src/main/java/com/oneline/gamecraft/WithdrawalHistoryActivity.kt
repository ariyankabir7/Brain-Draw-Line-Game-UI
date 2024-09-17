package com.oneline.gamecraft

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.adapter.HistoryAdapter
import com.oneline.gamecraft.databinding.ActivityWithdrawalHistoryBinding
import com.oneline.gamecraft.modal.HistoryModal
import java.util.Base64

class WithdrawalHistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityWithdrawalHistoryBinding
    var History = ArrayList<HistoryModal>()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWithdrawalHistoryBinding.inflate(layoutInflater)
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
        binding.tvBalance.text = TinyDB.getString(this, "balance", "")
        getWithdrawalHistory()
        val scaleUp: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        binding.cvWithdrawBtn.setOnTouchListener {v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvWithdrawBtn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvWithdrawBtn.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    finish()
                }
            }
            true
        }
    }
    fun getWithdrawalHistory(){
       // Utils.showLoadingPopUp(this)
        binding.rvTotalHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val emails = Base64.getEncoder()
            .encodeToString(TinyDB.getString(this, "phone", "")!!.toByteArray())
        val id=Base64.getEncoder().encodeToString("${com.oneline.gamecraft.Companion.APP_ID}".toByteArray())
        val url = "${com.oneline.gamecraft.Companion.siteUrl}get_withdrawal_history.php?email=$emails&app_id=$id"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->

            if (response.length() > 0) {
                History.clear()

                for (i in 0 until response.length()) {
                    val dataObject = response.getJSONObject(i)

                    val title = dataObject.getString("payment_title")
                    val amount = dataObject.getString("amount")
                    val date = dataObject.getString("date")
                    val status = dataObject.getString("status")
                    val historyRedeemModal = HistoryModal(title, amount, date, status)
                    History.add(historyRedeemModal)
                }
                Utils.dismissLoadingPopUp()
                val adapter = HistoryAdapter(this, History)
                binding.rvTotalHistory.adapter = adapter
                binding.pb.visibility = View.GONE
            } else {
                binding.nodata.visibility = View.VISIBLE
                binding.pb.visibility = View.GONE
                binding.rvTotalHistory.visibility = View.GONE
            }
        }, Response.ErrorListener { error ->
            Toast.makeText(this, "Internet Slow !", Toast.LENGTH_SHORT).show()
        }) {}

        requestQueue.add(jsonArrayRequest)
    }
}