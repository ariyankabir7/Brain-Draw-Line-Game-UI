package com.oneline.gamecraft

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.databinding.ActivityBonusBinding
import java.util.Base64

class BonusActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBonusBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
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
        binding.cvOkay.setOnClickListener {
            Utils.playPopSound(this)
            startActivity(Intent(this, OCRTaskActivity::class.java))
            finish()
        }
        getBonus()
    }

    private fun getBonus() {
        Utils.showLoadingPopUp(this)
        val url2 = "${com.oneline.gamecraft.Companion.siteUrl}get_sign_up_bonus_text.php"
        val email = TinyDB.getString(this, "email", "")

        val queue1: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest =
            object : StringRequest(Method.POST, url2, { response ->

                if (response.contains(",")) {
                    val alldata = response.trim().split(",")
                    TinyDB.saveString(this, "sign_up_bonus_text", alldata[0])
                    TinyDB.saveString(this, "joining_ocr_task_total_amount_text", alldata[1])
                    Utils.slotAnimation(alldata[0].toInt(), binding.tvRedeemCodeAmount)
                    Utils.dismissLoadingPopUp()
                } else {
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    finish()
                }


            }, Response.ErrorListener { error ->
                Utils.dismissLoadingPopUp()
                Toast.makeText(this, "Internet Slow", Toast.LENGTH_SHORT).show()
                finish()
            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()

                    val encodedAppID = Base64.getEncoder().encodeToString(
                        com.oneline.gamecraft.Companion.APP_ID.toString().toByteArray()
                    )
                    params["app_id"] = encodedAppID


                    return params
                }
            }

        queue1.add(stringRequest)
    }
}