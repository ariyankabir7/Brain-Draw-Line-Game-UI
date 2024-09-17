package com.oneline.gamecraft

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.oneline.gamecraft.adapter.TaskDescriptionAdapter
import com.oneline.gamecraft.databinding.ActivityScreenShotTaskDetailsBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.ice.money1.videoplayyer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class ScreenShotTaskDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityScreenShotTaskDetailsBinding

    init {
        System.loadLibrary("keys")
    }

    lateinit var offer_id: String

    external fun Hatbc(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityScreenShotTaskDetailsBinding.inflate(layoutInflater)
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
        val title = intent.getStringExtra("title")
        val coin = intent.getStringExtra("coin")
        val link = intent.getStringExtra("link")
        val icon = intent.getStringExtra("icon")
        val howto = intent.getStringExtra("howto")
        offer_id = intent.getStringExtra("task_id").toString()
        val task_description = intent.getStringExtra("task_description").toString()

        val arrDesc = ArrayList(task_description.split(","))
        val adapter = TaskDescriptionAdapter(this, arrDesc)
        binding.rvDescription.layoutManager = LinearLayoutManager(this)

        binding.rvDescription.adapter = adapter
        binding.tvTitle.text = title
        binding.tvBalance.text = coin
        Glide.with(this)
            .load(icon)
            .into(binding.ivLogo)
        val scaleUp: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        binding.cvHowto.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvHowto.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvHowto.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    Utils.openUrl(
                        this, howto!!
                    )
                }
            }
            true
        }
        binding.cvStart.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvStart.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvStart.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    Utils.openUrl(this, Uri.parse(link).toString())
                    binding.cvUploadProof.visibility = View.VISIBLE
                }
            }
            true
        }
        binding.cvUploadProof.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvUploadProof.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvUploadProof.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    binding.cvStart.visibility = View.VISIBLE
                    ImagePicker.with(this)
                        .galleryOnly()    //User can only select image from Gallery
                        .start()
                }
            }
            true

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            performOCR(uri)
            //imgProfile.setImageURI(Uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performOCR(uri: Uri) {
        try {
            val image: InputImage = InputImage.fromFilePath(this, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener(OnSuccessListener { visionText ->
                    // Process the recognized text
                    val recognizedText = visionText.text

                    val deviceid: String =
                        Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
                    val time = System.currentTimeMillis()
                    Utils.showLoadingPopUp(this)

                    val url2 =
                        "${com.oneline.gamecraft.Companion.siteUrl}verify_screenshort_ocr_task.php"
                    val emails = TinyDB.getString(this, "phone", "")!!
                    val queue1: RequestQueue = Volley.newRequestQueue(this)

                    val stringRequest = object : StringRequest(Method.POST, url2, { response ->
                        val ytes = Base64.getDecoder().decode(response)
                        val res = String(ytes, Charsets.UTF_8)

                        if (res.contains(",")) {
                            val alldata = res.trim().split(",")
                            Toast.makeText(this, alldata[0], Toast.LENGTH_SHORT).show()
                            TinyDB.saveString(this, "balance", alldata[1])

                            finish()
                        } else {
                            Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
                            finish()
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
                            val ocr =
                                videoplayyer.encrypt(recognizedText.toString(), Hatbc()).toString()
                            val offerId =
                                videoplayyer.encrypt(offer_id.toString(), Hatbc()).toString()


                            val den64 = Base64.getEncoder().encodeToString(dbit32.toByteArray())
                            val ten64 = Base64.getEncoder().encodeToString(tbit32.toByteArray())
                            val emails64 = Base64.getEncoder().encodeToString(emails.toByteArray())
                            val ocr64 = Base64.getEncoder().encodeToString(ocr.toByteArray())
                            val offerId64 =
                                Base64.getEncoder().encodeToString(offerId.toByteArray())

                            val encodemap: MutableMap<String, String> = HashMap()
                            encodemap["deijvfijvmfhvfvhfbhbchbfybebd"] = den64
                            encodemap["waofhfuisgdtdrefssfgsgsgdhddgd"] = ten64
                            encodemap["fdvbdfbhbrthyjsafewwt5yt5"] = emails64
                            encodemap["geugeubvjbvrugerugcceectgtg"] = ocr64
                            encodemap["gheghreghggnerg7ebvdfvdufgeurg"] = offerId64

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
                })
                .addOnFailureListener(OnFailureListener { e ->
                    // Handle any errors
                    Toast.makeText(this, "OCR Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                })
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }
}