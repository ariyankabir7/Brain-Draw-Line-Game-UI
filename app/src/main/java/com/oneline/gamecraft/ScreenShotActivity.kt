package com.oneline.gamecraft

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.databinding.ActivityScreenShotBinding
import com.ice.money1.videoplayyer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class ScreenShotActivity : AppCompatActivity() {
    lateinit var binding: ActivityScreenShotBinding
    init {
        System.loadLibrary("keys")
    }

    external fun Hatbc(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScreenShotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val insetsController = ViewCompat.getWindowInsetsController(v)
            insetsController?.isAppearanceLightStatusBars = true
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getEarningwallnvalue()
        val scaleUp: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        binding.cvOkay.setOnTouchListener {
                v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvOkay.startAnimation(scaleDown)
                }
                MotionEvent.ACTION_UP -> {
                    binding.cvOkay.startAnimation(scaleUp)
                    v.performClick()
                    if (binding.cbRead.isChecked) {
                        startActivity(Intent(this, ScreenShotTaskListActivity::class.java))

                    } else {
                        Toast.makeText(this, "Please Check the Box", Toast.LENGTH_SHORT).show()

                    }
                }

                }
            true
        }
    }
    fun getEarningwallnvalue(){
        Utils.showLoadingPopUp(this)
        val deviceid: String =
            Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        val time = System.currentTimeMillis()


        val url2 =
            "${com.oneline.gamecraft.Companion.siteUrl}get_screenshot_config.php"
        val emails = TinyDB.getString(this, "phone", "")!!
        val queue1: RequestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url2, { response ->
            val ytes = Base64.getDecoder().decode(response)
            val res = String(ytes, Charsets.UTF_8)

            if (res.contains(",")) {
                val alldata = res.trim().split(",")
                Utils.slotAnimation(alldata[0].toInt(),  binding.tvApps)
                binding.tvCoins.text=alldata[1]
                Utils.dismissLoadingPopUp()
            } else {
                Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
            }

        }, Response.ErrorListener { error ->
            Utils.dismissLoadingPopUp()
            Toast.makeText(this, "Internet Slow: $error", Toast.LENGTH_SHORT).show()
            // requireActivity().finish()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                val dbit32 = videoplayyer.encrypt(deviceid, Hatbc()).toString()
                val tbit32 = videoplayyer.encrypt(time.toString(), Hatbc()).toString()
                val emails = videoplayyer.encrypt(emails.toString(), Hatbc()).toString()


                val den64 = Base64.getEncoder().encodeToString(dbit32.toByteArray())
                val ten64 = Base64.getEncoder().encodeToString(tbit32.toByteArray())
                val emails64 = Base64.getEncoder().encodeToString(emails.toByteArray())


                val encodemap: MutableMap<String, String> = HashMap()
                encodemap["deijvfijvmfhvfvhfbhbchbfybebd"] = den64
                encodemap["waofhfuisgdtdrefssfgsgsgdhddgd"] = ten64
                encodemap["fdvbdfbhbrthyjsafewwt5yt5gfgfgg"] = emails64

                val jason = Json.encodeToString(encodemap)

                val den264 = Base64.getEncoder().encodeToString(jason.toByteArray())

                val final = URLEncoder.encode(den264, StandardCharsets.UTF_8.toString())

                params["dase"] = final

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