package com.oneline.gamecraft

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.databinding.ActivityMainBinding
import java.util.Base64

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var completed = "0"
    var pending = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        // val isMusicOn = TinyDB.getBoolean(this, "isMusicOn", true)
//        if (isMusicOn) {
//            startService(Intent(this, MusicService::class.java))
//        } else {
//            stopService(Intent(this, MusicService::class.java))
//        }
        Utils.applyBounceAnimation(binding.ivLogo)
        if (TinyDB.getString(this@MainActivity, "email", "") != "") {

            getOCROffers()

        }

        binding.lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                if (TinyDB.getString(this@MainActivity, "email", "") == "") {
                    val intent = Intent(this@MainActivity, LoginRegisterActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    if (pending.toInt() > 0) {
                        startActivity(Intent(this@MainActivity, OCRTaskActivity::class.java))
                    } else {
                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))

                    }
                    finish()
                }
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
        Handler(Looper.getMainLooper()).postDelayed({
            binding.lottieAnimationView.playAnimation()
        }, 1000)
    }


    fun getOCROffers() {
        val emails = Base64.getEncoder()
            .encodeToString(TinyDB.getString(this, "phone", "")!!.toByteArray())
        val appId = Base64.getEncoder()
            .encodeToString(com.oneline.gamecraft.Companion.APP_ID.toString().toByteArray())

        val url =
            "${com.oneline.gamecraft.Companion.siteUrl}get_ocr_joining_task_details.php?email=$emails&app_id=$appId"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val strRequest =
            object : StringRequest(Method.GET, url, Response.Listener { response ->
                if (response.contains(",")) {
                    val alldata = response.trim().split(",")
                    TinyDB.saveString(this, "completed", alldata[0])
                    TinyDB.saveString(this, "pending", alldata[1])
                    completed = alldata[0]
                    pending = alldata[1]

                }

            }, Response.ErrorListener { error ->
                Toast.makeText(this, "Internet Slow !", Toast.LENGTH_SHORT).show()
            }) {}

        requestQueue.add(strRequest)
    }
}