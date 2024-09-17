package com.oneline.gamecraft

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.databinding.ActivityHomeBinding
import com.oneline.gamecraft.modal.EarningAdModel
import com.oneline.gamecraft.services.MusicService
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.ice.money1.videoplayyer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding

    init {
        System.loadLibrary("keys")
    }

    var earningAdModel: EarningAdModel? = null
    var isMuted = false
    private var mediaPlayer: MediaPlayer? = null
    external fun Hatbc(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
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

        getUserValue()
        //   loadEarningAd()

        binding.llOffers.setOnTouchListener() { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llOffers.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llOffers.startAnimation(scaleUp)
                    v.performClick()

                    Utils.playPopSound(this)
                    val intent = Intent(this, OffersActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        binding.llWallet.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llWallet.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llWallet.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    val intent = Intent(this, WithdrawalActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        binding.llPlay.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llPlay.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llPlay.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    if (TinyDB.getString(this, "play_limit", "0") == "0") {
                        Toast.makeText(
                            this,
                            "Play Limit End, Come Back Tomorrow !",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val intent = Intent(this, PlayActivity::class.java)
                        startActivity(intent)
                    }

                }
            }
            true
        }
        binding.ivSettings.setOnClickListener {
            Utils.playPopSound(this)
            showSettingsPopup()
        }
        binding.llProfile.setOnClickListener {
            Utils.playPopSound(this)
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.tvPlayerId.text = TinyDB.getString(this, "phone", "")
        binding.tvBalance.text = TinyDB.getString(this, "balance", "")
        Utils.applyBounceAnimation(binding.ivLogo)
    }

    private fun getUserValue() {
        Utils.showLoadingPopUp(this)
        val deviceid: String = Settings.Secure.getString(
            this.contentResolver, Settings.Secure.ANDROID_ID
        )
        val time = System.currentTimeMillis()

        val url2 = "${com.oneline.gamecraft.Companion.siteUrl}getuservalue.php"
        val email = TinyDB.getString(this, "email", "")

        val queue1: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest =
            object : StringRequest(Method.POST, url2, { response ->

                val ytes = Base64.getDecoder().decode(response)
                val res = String(ytes, Charsets.UTF_8)

                if (res.contains(",")) {
                    val alldata = res.trim().split(",")
                    TinyDB.saveString(this, "phone", alldata[0])
                    TinyDB.saveString(this, "maintenance", alldata[1])
                    TinyDB.saveString(this, "version", alldata[2])
                    TinyDB.saveString(this, "balance", alldata[3])
                    TinyDB.saveString(this, "name", alldata[4])
                    TinyDB.saveString(this, "play_limit", alldata[5])
                    TinyDB.saveString(this, "playearn_limit", alldata[6])
                    TinyDB.saveString(this, "telegram_link", alldata[7])
                    TinyDB.saveString(this, "earning_ad_limit", alldata[8])
                    TinyDB.saveString(this, "total_registration", alldata[9])
                    TinyDB.saveString(this, "total_referral", alldata[10])
                    TinyDB.saveString(this, "app_link", alldata[11])
                    TinyDB.saveString(this, "refer_code", alldata[12])
                    TinyDB.saveString(this, "sponser_link", alldata[13])
                    TinyDB.saveString(this, "adgate_link", alldata[14])
                    TinyDB.saveString(this, "lootably_link", alldata[15])
                    TinyDB.saveString(this, "ayet_link", alldata[16])
                    TinyDB.saveString(this, "farly_link", alldata[17])

                    //setBalanceText()
                    // binding.tvBalance.text=TinyDB.getString(this,"balance","")
                    Utils.slotAnimation(alldata[3].toInt(), binding.tvBalance)
                    binding.tvPlayerId.text = TinyDB.getString(this, "phone", "")
                    if (alldata[2].toInt() > com.oneline.gamecraft.Companion.APP_VERSION) {
                        showUpdatePopup()
                    } else if (alldata[1] == "1") {
                        showMaintaincePopup()
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        Utils.dismissLoadingPopUp()
                    }, 1000)

                } else {
                    Toast.makeText(this, res, Toast.LENGTH_LONG).show()
                    finish()
                }


            }, Response.ErrorListener { error ->
                Utils.dismissLoadingPopUp()
                Toast.makeText(this, "Internet Slow", Toast.LENGTH_SHORT).show()
                finish()
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

                    val encodedAppID = Base64.getEncoder().encodeToString(
                        com.oneline.gamecraft.Companion.APP_ID.toString().toByteArray()
                    )
                    params["app_id"] = encodedAppID

                    return params
                }
            }

        queue1.add(stringRequest)
    }

    private fun showSettingsPopup() {
        AlertDialog.Builder(this, R.style.updateDialogTheme).setView(R.layout.popup_settings)
            .setCancelable(true).create().apply {
                show()
                findViewById<MaterialCardView>(R.id.cv_okay)?.setOnClickListener {
                    dismiss()
                }
                if (TinyDB.getBoolean(this@HomeActivity, "isMusicOn", true)) {
                    findViewById<ImageView>(R.id.iv_music)?.setImageDrawable(getDrawable(R.drawable.on))
                } else {
                    findViewById<ImageView>(R.id.iv_music)?.setImageDrawable(getDrawable(R.drawable.off))
                }
                findViewById<ImageView>(R.id.iv_music)?.setOnClickListener {
                    if (TinyDB.getBoolean(this@HomeActivity, "isMusicOn", true)) {
                        TinyDB.saveBoolean(this@HomeActivity, "isMusicOn", false)
                        findViewById<ImageView>(R.id.iv_music)?.setImageDrawable(getDrawable(R.drawable.off))
                        stopService(Intent(this@HomeActivity, MusicService::class.java))
                    } else {
                        TinyDB.saveBoolean(this@HomeActivity, "isMusicOn", true)
                        findViewById<ImageView>(R.id.iv_music)?.setImageDrawable(getDrawable(R.drawable.on))
                        startService(Intent(this@HomeActivity, MusicService::class.java))
                    }
                }

                if (TinyDB.getBoolean(this@HomeActivity, "isSoundOn", true)) {
                    findViewById<ImageView>(R.id.iv_sound)?.setImageDrawable(getDrawable(R.drawable.on))
                } else {
                    findViewById<ImageView>(R.id.iv_sound)?.setImageDrawable(getDrawable(R.drawable.off))
                }

                findViewById<ImageView>(R.id.iv_sound)?.setOnClickListener {
                    if (TinyDB.getBoolean(this@HomeActivity, "isSoundOn", true)) {
                        TinyDB.saveBoolean(this@HomeActivity, "isSoundOn", false)
                        findViewById<ImageView>(R.id.iv_sound)?.setImageDrawable(getDrawable(R.drawable.off))
                    } else {
                        TinyDB.saveBoolean(this@HomeActivity, "isSoundOn", true)
                        findViewById<ImageView>(R.id.iv_sound)?.setImageDrawable(getDrawable(R.drawable.on))
                    }
                }

            }

    }

    private fun showPpopupDialog() {
        AlertDialog.Builder(this, R.style.TransparentDialogTheme).setView(R.layout.popup_back)
            .setCancelable(true).create().apply {
                show()

                findViewById<MaterialButton>(R.id.buttonCancel)?.setOnClickListener {
                    dismiss()
                }
                findViewById<MaterialButton>(R.id.buttonConfirm)?.setOnClickListener {
                    dismiss()
                    super.onBackPressed()
                    finish()
                }
            }

    }

    private fun showMaintaincePopup() {
        AlertDialog.Builder(this, R.style.updateDialogTheme).setView(R.layout.popup_maintaince)
            .setCancelable(false).create().apply {
                show()
                findViewById<MaterialCardView>(R.id.cv_okay)?.setOnClickListener {
                    Utils.openUrl(
                        this@HomeActivity,
                        TinyDB.getString(this@HomeActivity, "telegram_link", "0")!!
                    )
                }
            }

    }

    private fun showUpdatePopup() {
        AlertDialog.Builder(this, R.style.updateDialogTheme).setView(R.layout.popup_newupdate)
            .setCancelable(false).create().apply {
                show()
                findViewById<MaterialCardView>(R.id.cv_okay)?.setOnClickListener {
                    Utils.openUrl(
                        this@HomeActivity,
                        "https://play.google.com/store/apps/details?id=$packageName"
                    )
                }
            }

    }


    override fun onBackPressed() {
        showPpopupDialog()

    }

    override fun onResume() {
        super.onResume()
        binding.tvBalance.text = TinyDB.getString(this, "balance", "")

    }
}