package com.oneline.gamecraft

import android.content.Intent
import android.net.Uri
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.adapter.AdsCompanyAdapter
import com.oneline.gamecraft.databinding.ActivityWatchEarnDetailsBinding
import com.oneline.gamecraft.extrazz.CustomAdLoader
import com.oneline.gamecraft.modal.CompanyAdsModal
import com.oneline.gamecraft.services.MusicService
import com.ice.money1.videoplayyer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class WatchEarnDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityWatchEarnDetailsBinding
    lateinit var link: String
    lateinit var contact: String
    lateinit var adloder: CustomAdLoader
    var AdsCompany = ArrayList<CompanyAdsModal>()
    var isapplicable = true

    init {
        System.loadLibrary("keys")
    }

    external fun Hatbc(): String
    external fun HatGy(): String
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWatchEarnDetailsBinding.inflate(layoutInflater)
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
        adloder = CustomAdLoader(
            context = this,
            siteAdsUrl = com.oneline.gamecraft.Companion.siteAdsUrl,
            uniqueAppId = com.oneline.gamecraft.Companion.ADS_UNIQUE_APP_ID,
            onAdDismiss = {
                claimCoin()
            }
        )
        adloder.loadAd()
        getWatchearnvalue()
        getadsCompamy()

        //!! binding.tvRemain.text = TinyDB.getString(this, "earning_ad_limit", "")
        Utils.slotAnimation(
            TinyDB.getString(this, "earning_ad_limit", "")!!.toInt(),
            binding.tvRemain
        )
        val scaleUp: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        binding.cvWatchEarn.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvWatchEarn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvWatchEarn.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    if (binding.cbRead.isChecked) {
                        if (isapplicable) {
                            if (TinyDB.getBoolean(this, "isMusicOn", true)) {
                                stopService(
                                    Intent(
                                        this@WatchEarnDetailsActivity,
                                        MusicService::class.java
                                    )
                                )
                                isapplicable = false
                                adloder.showAd()
                            }

                        }
                    } else {
                        Toast.makeText(this, "Please Check the Box", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }
        binding.cvPromoteApp.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvPromoteApp.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvPromoteApp.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    Utils.openUrl(
                        this, Uri.parse(link).toString()
                    )
                }
            }
            true
        }
        binding.cvContact.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvContact.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvContact.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    Utils.openUrl(
                        this, Uri.parse(contact).toString()
                    )
                }
            }
            true
        }
    }

    fun getWatchearnvalue() {
        Utils.showLoadingPopUp(this)
        val deviceid: String =
            Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        val time = System.currentTimeMillis()


        val url2 =
            "${com.oneline.gamecraft.Companion.siteUrl}get_watchearn_config.php"
        val emails = TinyDB.getString(this, "phone", "")!!
        val queue1: RequestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url2, { response ->
            val ytes = Base64.getDecoder().decode(response)
            val res = String(ytes, Charsets.UTF_8)

            if (res.contains(",")) {
                val alldata = res.trim().split(",")
                //   Utils.slotAnimation(binding.tvRemain)
                //    binding.tvGame.text=alldata[0]
                binding.tvCoin.text = alldata[1]
                link = alldata[0]
                contact = alldata[2]

            } else {
                Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
            }
            Utils.dismissLoadingPopUp()
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
                encodemap["fdvbdfbhbrthyjsafewwt5yt5"] = emails64

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

    fun getadsCompamy() {
        //  Utils.showLoadingPopUp(this)
        binding.rvAdsCom.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        val url = "${com.oneline.gamecraft.Companion.siteUrl}get_company_ads_list.php"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest =
            object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
                if (response.length() > 0) {
                    AdsCompany.clear()

                    for (i in 0 until response.length()) {
                        val dataObject = response.getJSONObject(i)
                        val link = dataObject.getString("link")
                        val title = dataObject.getString("title")
                        val historyRedeemModal = CompanyAdsModal(link, title)
                        AdsCompany.add(historyRedeemModal)
                    }

                    val adapter = AdsCompanyAdapter(this, AdsCompany)
                    binding.rvAdsCom.adapter = adapter
                    //  binding.pb.visibility = View.GONE
                } else {
                    // binding.nodata.visibility = View.VISIBLE
                    // binding.rvAdsCom.visibility = View.GONE
                }
                Utils.dismissLoadingPopUp()
            }, Response.ErrorListener { error ->
                Toast.makeText(this, "Internet Slow !", Toast.LENGTH_SHORT).show()
            }) {}

        requestQueue.add(jsonArrayRequest)
    }

    private fun claimCoin() {
        val deviceid: String =
            Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        val time = System.currentTimeMillis()

        val url2 = "${com.oneline.gamecraft.Companion.siteUrl}earning_ads_point.php"
        val emails = Base64.getEncoder()
            .encodeToString(TinyDB.getString(this, "phone", "")!!.toByteArray())

        val queue1: RequestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url2, { response ->
            val ytes = Base64.getDecoder().decode(response)
            val res = String(ytes, Charsets.UTF_8)
            isapplicable = true
            if (TinyDB.getBoolean(this, "isMusicOn", true)) {
                stopService(Intent(this@WatchEarnDetailsActivity, MusicService::class.java))
                startService(Intent(this@WatchEarnDetailsActivity, MusicService::class.java))
            }
            if (res.contains(",")) {
                val alldata = res.trim().split(",")
                Toast.makeText(this, alldata[0], Toast.LENGTH_SHORT).show()

                TinyDB.saveString(this, "earning_ad_limit", alldata[2])
                TinyDB.saveString(this, "balance", alldata[1])
                binding.tvRemain.text = TinyDB.getString(this, "earning_ad_limit", "")
            } else {
                Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
            }
            adloder.loadAd()
        }, Response.ErrorListener { error ->
            Utils.dismissLoadingPopUp()
            Toast.makeText(this, "Internet Slow: $error", Toast.LENGTH_SHORT).show()
            // requireActivity().finish()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                val dbit32 = videoplayyer.encrypt(deviceid, Hatbc()).toString()
                val tbit32 = videoplayyer.encrypt(time.toString(), Hatbc()).toString()
                val email = videoplayyer.encrypt(emails.toString(), Hatbc()).toString()


                val den64 = Base64.getEncoder().encodeToString(dbit32.toByteArray())
                val ten64 = Base64.getEncoder().encodeToString(tbit32.toByteArray())
                val email64 = Base64.getEncoder().encodeToString(email.toByteArray())

                val encodemap: MutableMap<String, String> = HashMap()
                encodemap["deijvfijvmfhvfvhfbhbchbfybebd"] = den64
                encodemap["waofhfuisgdtdrefssfgsgsgdhddgd"] = ten64
                encodemap["fdvbdfbhbrthyjsafewwt5yt5"] = email64

                val jason = Json.encodeToString(encodemap)

                val den264 = Base64.getEncoder().encodeToString(jason.toByteArray())

                val final = URLEncoder.encode(den264, StandardCharsets.UTF_8.toString())

                params["dase"] = final

                val encodedAppID =
                    Base64.getEncoder().encodeToString(
                        com.oneline.gamecraft.Companion.APP_ID.toString().toByteArray()
                    )
                params["app_id"] = encodedAppID

                return params
            }
        }

        queue1.add(stringRequest)
    }


}