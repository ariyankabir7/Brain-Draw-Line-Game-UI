package com.oneline.gamecraft

import android.R.attr.label
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.oneline.gamecraft.databinding.ActivityOffersBinding
import com.oneline.gamecraft.modal.EarningAdModel
import com.google.android.material.card.MaterialCardView
import com.ice.money1.videoplayyer
import com.pubscale.sdkone.offerwall.OfferWall
import com.pubscale.sdkone.offerwall.OfferWallConfig
import com.pubscale.sdkone.offerwall.models.OfferWallInitListener
import com.pubscale.sdkone.offerwall.models.OfferWallListener
import com.pubscale.sdkone.offerwall.models.Reward
import com.pubscale.sdkone.offerwall.models.errors.InitError

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONArray
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class OffersActivity : AppCompatActivity() {
    lateinit var binding: ActivityOffersBinding
    var earningAdModel: EarningAdModel? = null
    var isMuted = false
    private var mediaPlayer: MediaPlayer? = null


    init {
        System.loadLibrary("keys")
    }

    external fun Hatbc(): String
    external fun HatGy(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOffersBinding.inflate(layoutInflater)
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



        val offerWallConfig =
            OfferWallConfig.Builder(this, com.oneline.gamecraft.Companion.PUBSCALE_APP_ID)
                .setUniqueId(
                    TinyDB.getString(
                        this,
                        "phone",
                        ""
                    )!! + "-" + com.oneline.gamecraft.Companion.APP_ID
                )
                .setFullscreenEnabled(true) //optional
                .build()

        OfferWall.init(offerWallConfig, object : OfferWallInitListener {
            override fun onInitSuccess() {
            }

            override fun onInitFailed(error: InitError) {
            }
        })
        val scaleUp: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        //  loadEarningAd()

        binding.llPubscal.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llPubscal.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llPubscal.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    OfferWall.launch(this, offerWallListener)
                }
            }
            true
        }
        binding.llEarningIo.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llEarningIo.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llEarningIo.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    val intent = Intent(this, WatchEarnDetailsActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        binding.llPlayearn.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llPlayearn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llPlayearn.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    startActivity(Intent(this, PlayEarnActivity::class.java))
                }
            }
            true
        }
        binding.llEarningWall.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llEarningWall.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llEarningWall.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    startActivity(Intent(this, EarningWallActivity::class.java))
                }
            }
            true
        }

        binding.llScreenShort.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llScreenShort.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llScreenShort.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    startActivity(Intent(this, ScreenShotActivity::class.java))
                }
            }
            true
        }
        binding.llAdgate.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llAdgate.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llAdgate.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    Utils.openUrl(
                        this, Uri.parse(TinyDB.getString(this, "adgate_link", "")).toString()
                    )
                }
            }
            true
        }
        binding.llLootably.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llLootably.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llLootably.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    Utils.openUrl(
                        this, Uri.parse(TinyDB.getString(this, "lootably_link", "")).toString()
                    )
                }
            }
            true
        }
        binding.llFarly.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llFarly.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llFarly.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    Utils.openUrl(
                        this, Uri.parse(TinyDB.getString(this, "farly_link", "")).toString()
                    )
                }
            }
            true
        }
        binding.llAyetstdio.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llAyetstdio.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llAyetstdio.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    Utils.openUrl(
                        this, Uri.parse(TinyDB.getString(this, "ayet_link", "")).toString()
                    )
                }
            }
            true
        }
        binding.llTimewall.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llTimewall.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llTimewall.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    val email = TinyDB.getString(this, "phone", "")
                    val url = "${com.oneline.gamecraft.Companion.timewallUrl}$email-${com.oneline.gamecraft.Companion.APP_ID}"
                    val builder = CustomTabsIntent.Builder()
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(this, Uri.parse(url))
                }
            }
            true
        }

    }

    private fun loadEarningAd() {
        Utils.showLoadingPopUp(this)
        val urlforAds =
            "${com.oneline.gamecraft.Companion.siteAdsUrl}get_ad.php"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = object : StringRequest(urlforAds, { response ->
            val jsonArr = JSONArray(videoplayyer.decrypt(response, HatGy()))
            if (jsonArr.length() > 0) {

                for (i in 0 until jsonArr.length()) {
                    val dataObject = jsonArr.getJSONObject(i)
                    val added_on = dataObject.getString("added_on")
                    val ads_video_link = dataObject.getString("ads_video_link")
                    val after_video_img_link = dataObject.getString("after_video_img_link")
                    val ads_id = dataObject.getString("ads_id")
                    val btn_color = dataObject.getString("btn_color")
                    val btn_txt = dataObject.getString("btn_txt")
                    val skip_btn_timer = dataObject.getString("skip_btn_timer")
                    val id = dataObject.getString("id")
                    val title = dataObject.getString("title")
                    val desc = dataObject.getString("ads_des")
                    val text_color = dataObject.getString("text_color")
                    val click_link = dataObject.getString("click_link")
                    val code = dataObject.getString("code")
                    val after_ads_btn_color = dataObject.getString("after_ads_btn_color")
                    val afterTimmer = dataObject.getString("after_ads_btn_timmer")
                    val how_video = dataObject.getString("how_video")
                    val after_ads_text_color = dataObject.getString("after_ads_text_color")
                    val after_ads_btn_text = dataObject.getString("after_ads_btn_text")

                    earningAdModel = EarningAdModel(
                        added_on,
                        ads_video_link,
                        after_video_img_link,
                        ads_id,
                        btn_color,
                        btn_txt,
                        skip_btn_timer,
                        click_link,
                        id,
                        title,
                        desc,
                        text_color,
                        code,
                        after_ads_btn_color,
                        afterTimmer,
                        how_video,
                        after_ads_text_color,
                        after_ads_btn_text
                    )


                }
            }
            Utils.dismissLoadingPopUp()
        }, { error ->
            Toast.makeText(this, "Internet Slow !", Toast.LENGTH_SHORT).show()
            Utils.dismissLoadingPopUp()
        }) {}

        requestQueue.add(jsonArrayRequest)
    }

    private fun showPopup() {
        val dialog = android.app.AlertDialog.Builder(this, R.style.FullScreenDialog)
            .setView(R.layout.popup_ads)
            .setCancelable(false)
            .create()

        dialog.show()

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val cvTimmer = dialog.findViewById<CardView>(R.id.cv_timmer_bg)
        val cvCodeBtn = dialog.findViewById<MaterialCardView>(R.id.cv_code_btn)
        val installBtn = dialog.findViewById<CardView>(R.id.cv_action_btn)
        val installBtn2 = dialog.findViewById<MaterialCardView>(R.id.installBtn)
        val closeBtn = dialog.findViewById<ImageView>(R.id.iv_close_ad_after_video)
        val skipBtn = dialog.findViewById<ImageView>(R.id.iv_close_ad)
        val policy = dialog.findViewById<ImageView>(R.id.iv_policy)
        val earnads = dialog.findViewById<ImageView>(R.id.earnAdsImage)
        val promote = dialog.findViewById<ImageView>(R.id.promoteImage)
        val howtoVideo = dialog.findViewById<ImageView>(R.id.howImage)
        val afterVideoclosebtn = dialog.findViewById<ImageView>(R.id.iv_after_video_ad)
        val title = dialog.findViewById<TextView>(R.id.tv_ads_title)
        val desc = dialog.findViewById<TextView>(R.id.tv_ads_short_des)
        val code = dialog.findViewById<TextView>(R.id.tv_code)
        val tvaction = dialog.findViewById<TextView>(R.id.tv_action_btn)
        val tvaction2 = dialog.findViewById<TextView>(R.id.tv_action_btn2)

        val ll = dialog.findViewById<LinearLayout>(R.id.after_ads)
        val bb = dialog.findViewById<LinearLayout>(R.id.before_ads)
        val pb = dialog.findViewById<ProgressBar>(R.id.linearProgressIndicator)
        val mute_unmute = dialog.findViewById<ImageView>(R.id.mute_unmute)
        val layerDrawable =
            ContextCompat.getDrawable(this, R.drawable.shine_effect) as LayerDrawable?

        layerDrawable?.let {

            installBtn.foreground = it

            // Create an animation for the shine effect
            val animator = ValueAnimator.ofFloat(-1f, 1f)
            animator.duration = 2500 // Duration of the shine animation
            animator.repeatCount = ValueAnimator.INFINITE // Loop the animation
            animator.interpolator = LinearInterpolator()

            animator.addUpdateListener { animation ->
                val progress = animation.animatedValue as Float

                installBtn.foreground?.setBounds(
                    (installBtn.width * progress).toInt(), 0,
                    (installBtn.width * (progress + 1)).toInt(), installBtn.height
                )

                installBtn.invalidate()
            }

            animator.start()
        }
        Glide.with(this)
            .load(Uri.parse(earningAdModel!!.after_video_img_link))
            .into(afterVideoclosebtn)
        title?.text = earningAdModel?.title
        desc?.text = earningAdModel?.desc
        code?.text = earningAdModel?.code
        tvaction?.text = earningAdModel?.btn_txt
        tvaction.setTextColor(Color.parseColor(earningAdModel!!.text_color))
        tvaction2.setTextColor(Color.parseColor(earningAdModel!!.after_ads_text_color))
        tvaction2?.text = earningAdModel?.after_ads_btn_text

        val videoView = dialog.findViewById<VideoView>(R.id.vv_show_video)
        val videoUri = Uri.parse(earningAdModel!!.ads_video_link)

        policy.setOnClickListener {
            Utils.openUrl(this, "https://earningads.io/")
        }
        earnads.setOnClickListener {
            Utils.openUrl(this, "https://earningads.io/")
        }
        promote.setOnClickListener {
            Utils.openUrl(this, "https://earningads.io/")
        }
        howtoVideo.setOnClickListener {
            Utils.openUrl(this, earningAdModel!!.how_video)
        }
        videoView?.setVideoURI(videoUri)
        val touchBlocker = View.OnTouchListener { _, _ -> true }

        videoView?.setOnErrorListener { mp, what, extra ->
            Log.e("VideoPopup", "Error occurred: $what, $extra")
            Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show()
            true // Indicate that the error has been handled
        }
        videoView.setOnPreparedListener { mp ->
            mediaPlayer = mp
            if (bb.isVisible) {
                mp.start()
                videoView.setOnTouchListener(touchBlocker)
                val countDownTimer =
                    object : CountDownTimer(earningAdModel!!.skip_btn_timer.toLong() * 1000, 300) {
                        override fun onTick(millisUntilFinished: Long) {

                        }

                        override fun onFinish() {

                            cvTimmer.visibility = View.VISIBLE
                            mute_unmute.visibility = View.VISIBLE
                            updateImpression()
                        }
                    }
                countDownTimer.start()
            }
        }
        mute_unmute.setOnClickListener {
            isMuted = !isMuted
            mute_unmute.setImageResource(if (isMuted) R.drawable.play_off else R.drawable.play_on)
            mediaPlayer?.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
        }
        videoView.setOnCompletionListener {
            videoView.pause()

            ll.visibility = View.VISIBLE
            bb.visibility = View.GONE
            videoView.stopPlayback()
            // applyBounceAnimation(installBtn2)
        }
        if (earningAdModel!!.code == "") {
            cvCodeBtn.visibility = View.GONE
        }
        installBtn2?.setOnClickListener {
            videoView.pause()

            updateClick()
            Utils.openUrl(this, earningAdModel!!.click_link)
            videoView.stopPlayback()
        }
        installBtn?.setOnClickListener {

            Utils.openUrl(this, earningAdModel!!.click_link)
        }
        skipBtn.setOnClickListener {
            videoView.pause()
            videoView.stopPlayback()
            ll.visibility = View.VISIBLE
            bb.visibility = View.GONE
            val layerDrawable = ContextCompat.getDrawable(
                this,
                R.drawable.shine_effect
            ) as LayerDrawable?

            layerDrawable?.let {

                installBtn2.foreground = it

                // Create an animation for the shine effect
                val animator = ValueAnimator.ofFloat(-1f, 1f)
                animator.duration = 2500 // Duration of the shine animation
                animator.repeatCount = ValueAnimator.INFINITE // Loop the animation
                animator.interpolator = LinearInterpolator()

                animator.addUpdateListener { animation ->
                    val progress = animation.animatedValue as Float

                    installBtn2.foreground?.setBounds(
                        (installBtn2.width * progress).toInt(), 0,
                        (installBtn2.width * (progress + 1)).toInt(), installBtn2.height
                    )

                    installBtn2.invalidate()
                }

                animator.start()
            }
            val countDownTimer =
                object :
                    CountDownTimer(earningAdModel!!.after_ads_btn_timmer.toLong() * 1000, 50) {
                    override fun onTick(millisUntilFinished: Long) {

                        val progress =
                            ((earningAdModel!!.after_ads_btn_timmer.toFloat() * 1000 - millisUntilFinished.toFloat()) / (earningAdModel!!.after_ads_btn_timmer.toFloat() * 1000) * 100).toInt()
                        pb.progressTintList = ColorStateList.valueOf(Color.GREEN)
                        pb.progress = progress
                    }

                    override fun onFinish() {
                        pb.visibility = View.GONE
                        closeBtn.visibility = View.VISIBLE

                    }
                }
            countDownTimer.start()
            // applyBounceAnimation(installBtn2)
        }
        installBtn2.setCardBackgroundColor(Color.parseColor(earningAdModel!!.after_ads_btn_color))
        installBtn.setCardBackgroundColor(Color.parseColor(earningAdModel!!.btn_color))


        closeBtn.setOnClickListener {
           // claimCoin()
            dialog.dismiss()
            loadEarningAd()
        }
        cvCodeBtn.setOnClickListener {
            val clipboard =
                this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label.toString(), earningAdModel!!.code)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show()
        }

    }

    fun updateImpression() {

        val url3 = "${com.oneline.gamecraft.Companion.siteAdsUrl}u_i.php"
        val queue3: RequestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url3, { response ->

        }, { error ->
            Utils.dismissLoadingPopUp()

            Toast.makeText(this, "Internet Slow: $error", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                val ads_id32 = videoplayyer.encrypt(earningAdModel!!.ads_id, Hatbc()).toString()
                val uapp_id32 =
                    videoplayyer.encrypt(com.oneline.gamecraft.Companion.ADS_UNIQUE_APP_ID, Hatbc())
                        .toString()


                val den64 = Base64.getEncoder().encodeToString(ads_id32.toByteArray())
                val ten64 = Base64.getEncoder().encodeToString(uapp_id32.toByteArray())

                val encodemap: MutableMap<String, String> = HashMap()
                encodemap["deijvfijvmfhvfvhfbhbchbfybebd"] = den64
                encodemap["waofhfuisgdtdrefssfgsgsgdhddgd"] = ten64


                val jason = Json.encodeToString(encodemap)

                val den264 = Base64.getEncoder().encodeToString(jason.toByteArray())

                val final = URLEncoder.encode(den264, StandardCharsets.UTF_8.toString())

                params["dase"] = final

                return params
            }

        }
        queue3.add(stringRequest)
    }



    fun updateClick() {

        val url3 = "${com.oneline.gamecraft.Companion.siteAdsUrl}u_c.php"
        val queue3: RequestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url3, { response ->

        }, { error ->
            Utils.dismissLoadingPopUp()

            Toast.makeText(this, "Internet Slow: $error", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                val ads_id32 = videoplayyer.encrypt(earningAdModel!!.ads_id, Hatbc()).toString()
                val uapp_id32 =
                    videoplayyer.encrypt(com.oneline.gamecraft.Companion.ADS_UNIQUE_APP_ID, Hatbc())
                        .toString()


                val den64 = Base64.getEncoder().encodeToString(ads_id32.toByteArray())
                val ten64 = Base64.getEncoder().encodeToString(uapp_id32.toByteArray())

                val encodemap: MutableMap<String, String> = HashMap()
                encodemap["deijvfijvmfhvfvhfbhbchbfybebd"] = den64
                encodemap["waofhfuisgdtdrefssfgsgsgdhddgd"] = ten64


                val jason = Json.encodeToString(encodemap)

                val den264 = Base64.getEncoder().encodeToString(jason.toByteArray())

                val final = URLEncoder.encode(den264, StandardCharsets.UTF_8.toString())

                params["dase"] = final

                return params
            }

        }
        queue3.add(stringRequest)
    }

    private val offerWallListener = object : OfferWallListener {

        override fun onOfferWallShowed() {

        }

        override fun onOfferWallClosed() {

        }

        override fun onRewardClaimed(reward: Reward) {

        }

        override fun onFailed(message: String) {
            Toast.makeText(this@OffersActivity, "No Offers Available", Toast.LENGTH_SHORT).show()
        }
    }

}