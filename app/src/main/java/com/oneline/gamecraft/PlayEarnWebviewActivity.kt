package com.oneline.gamecraft

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.services.MusicService
import com.ice.money1.videoplayyer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64
import kotlin.random.Random

class PlayEarnWebviewActivity : AppCompatActivity() {
    var randomDelay: Long = 0
    var isTimerFinished = false
    lateinit var timer: CountDownTimer
    private lateinit var webView: WebView

    init {
        System.loadLibrary("keys")
    }

    external fun Hatbc(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_play_earn_webview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (TinyDB.getBoolean(this, "isMusicOn", true)) {
            stopService(Intent(this@PlayEarnWebviewActivity, MusicService::class.java))

        }
        randomDelay = Random.nextLong(15000, 20000)
        timer = object : CountDownTimer(randomDelay, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                addPlayPoint()
            }
        }

        // Start the timer
        timer.start()
        val link = intent.getStringExtra("link")

        webView = findViewById<WebView>(R.id.wv_web)
        setupWebView()
        webView.loadUrl("$link")

    }

    fun addPlayPoint() {

        if (TinyDB.getString(this, "playearn_limit", "0") == "0") {
            Toast.makeText(this, "Play Earn Limit End, Come Back Tomorrow !", Toast.LENGTH_SHORT)
                .show()
            finish()
        } else {
            val deviceid: String = Settings.Secure.getString(
                contentResolver, Settings.Secure.ANDROID_ID
            )
            val time = System.currentTimeMillis()

            val url3 = "${com.oneline.gamecraft.Companion.siteUrl}add_playearn_point.php"
            val email = TinyDB.getString(this, "phone", "")

            val queue3: RequestQueue = Volley.newRequestQueue(this)
            val stringRequest =
                object : StringRequest(Method.POST, url3, { response ->

                    val yes = Base64.getDecoder().decode(response)
                    val res = String(yes, Charsets.UTF_8)

                    if (res.contains(",")) {
                        Utils.dismissLoadingPopUp()
                        val alldata = res.trim().split(",")

                        TinyDB.saveString(this, "playearn_limit", alldata[2])
                        TinyDB.saveString(this, "balance", alldata[1])
                        TinyDB.saveString(this, "playearnPoint", alldata[3])
                        isTimerFinished = true

                    } else {
                        Toast.makeText(this, res, Toast.LENGTH_LONG).show()
                        finish()
                    }

                }, { error ->

                    Toast.makeText(this, "Internet Slow", Toast.LENGTH_SHORT).show()
                    // requireActivity().finish()
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()

                        val dbit32 = videoplayyer.encrypt(deviceid, Hatbc()).toString()
                        val tbit32 = videoplayyer.encrypt(time.toString(), Hatbc()).toString()
                        val email = videoplayyer.encrypt(email.toString(), Hatbc()).toString()

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

                        val encodedAppID = Base64.getEncoder()
                            .encodeToString(
                                com.oneline.gamecraft.Companion.APP_ID.toString().toByteArray()
                            )
                        params["app_id"] = encodedAppID

                        return params
                    }
                }

            queue3.add(stringRequest)
        }

    }

    private fun setupWebView() {
        val webSettings = webView.settings

        // Enable JavaScript
        webSettings.javaScriptEnabled = true

        // Enable DOM storage (optional but useful for many modern websites)
        webSettings.domStorageEnabled = true

        // Allow mixed content (loading HTTP content on HTTPS sites)
        webSettings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

        // Set a WebViewClient to handle page loading within the WebView
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val currentUrl = request?.url.toString()
                val targetUrl = "https://304-freeonline-play.gamesdonut.com/game/Slap-Run"

                // If the current URL is different from the target URL, navigate back to the target URL
                if (currentUrl != targetUrl) {
                    view?.loadUrl(targetUrl)
                    return true
                }

                // Allow the WebView to load the new URL
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                // Handle any additional logic once the page is fully loaded
            }
        }

        // Enable wide viewport and set initial scale
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        // Enable zoom controls
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false // Disable on-screen zoom controls
    }

    override fun onBackPressed() {
        // Check if the timer has finished
        if (isTimerFinished) {
            val intent = Intent(this, GamePointActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // If the timer has not finished, just finish the activity
            finish()
            Utils.playPopSound(this)
        }
    }
    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }
}