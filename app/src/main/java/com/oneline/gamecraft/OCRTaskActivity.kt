package com.oneline.gamecraft

import android.content.Intent
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
import com.oneline.gamecraft.adapter.OCRJoiningAdapter
import com.oneline.gamecraft.databinding.ActivityOcrtaskBinding
import com.oneline.gamecraft.modal.OCRJoiningTaskModel
import java.util.Base64

class OCRTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityOcrtaskBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOcrtaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val insetsController = ViewCompat.getWindowInsetsController(v)
            insetsController?.isAppearanceLightStatusBars = true
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.rvOcrJoiningTask.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        if (TinyDB.getString(this, "completed", "0")!!.toInt() >= 1) {
            val ocrCoin = TinyDB.getString(this, "joining_ocr_task_total_amount_text", "")!!

            binding.cvSkip.visibility = View.VISIBLE
        }
        val scaleUp: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        binding.cvSkip.setOnTouchListener{
                v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvSkip.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvSkip.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            }
            true
        }

    }

    fun getOCROffers() {
        val emails = Base64.getEncoder()
            .encodeToString(TinyDB.getString(this, "phone", "")!!.toByteArray())
        val appId = Base64.getEncoder()
            .encodeToString(com.oneline.gamecraft.Companion.APP_ID.toString().toByteArray())

        val url =
            "${com.oneline.gamecraft.Companion.siteUrl}get_ocr_joining_task.php?email=$emails&app_id=$appId"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest =
            object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
                val temp = ArrayList<OCRJoiningTaskModel>()
                if (response.length() > 0) {

                    for (i in 0 until response.length()) {
                        val dataObject = response.getJSONObject(i)

                        val how_to_link = dataObject.getString("how_to_link")
                        val offer_description = dataObject.getString("offer_description")
                        val offer_id = dataObject.getString("offer_id")
                        val offer_link = dataObject.getString("offer_link")
                        val offer_title = dataObject.getString("offer_title")
                        val offer_coin = dataObject.getString("offer_coin")
                        val offer_icon = dataObject.getString("offer_icon")
                        val OCRJoiningTaskModel = OCRJoiningTaskModel(
                            how_to_link,
                            offer_description,
                            offer_id,
                            offer_link,
                            offer_title,
                            offer_coin,
                            offer_icon
                        )
                        temp.add(OCRJoiningTaskModel)
                    }

                    val adapter = OCRJoiningAdapter(this, temp)
                    binding.rvOcrJoiningTask.adapter = adapter
                } else {
                    startActivity(Intent(this@OCRTaskActivity, HomeActivity::class.java))
                    finish()
                }
            }, Response.ErrorListener { _ ->
                Toast.makeText(this, "Internet Slow !", Toast.LENGTH_SHORT).show()
            }) {}

        requestQueue.add(jsonArrayRequest)
    }

    override fun onResume() {
        super.onResume()
        getOCROffers()
    }
}