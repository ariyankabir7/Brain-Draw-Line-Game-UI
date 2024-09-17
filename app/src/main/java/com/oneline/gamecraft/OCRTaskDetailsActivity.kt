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
import com.oneline.gamecraft.adapter.TaskDescriptionAdapter
import com.oneline.gamecraft.databinding.ActivityOcrtaskDetailsBinding
import com.oneline.gamecraft.modal.OCRJoiningTaskModel
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

class OCRTaskDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityOcrtaskDetailsBinding

    init {
        System.loadLibrary("keys")
    }

    lateinit var offer_id: String
    external fun Hatbc(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityOcrtaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val insetsController = ViewCompat.getWindowInsetsController(v)
            insetsController?.isAppearanceLightStatusBars = true
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val selectedOCRJoiningTaskModel = OCRJoiningTaskModel.selectedOCRJoiningTaskModel!!
        offer_id = selectedOCRJoiningTaskModel.offer_id
        binding.tvTitle.text = selectedOCRJoiningTaskModel.offer_title
        binding.tvBalance.text = selectedOCRJoiningTaskModel.offer_coin
        val arrDesc = ArrayList(selectedOCRJoiningTaskModel.offer_description.split(","))
        val adapter = TaskDescriptionAdapter(this, arrDesc)
        binding.rvDescription.layoutManager = LinearLayoutManager(this)
        binding.rvDescription.adapter = adapter

        val scaleUp: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        binding.cvStart.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvStart.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvStart.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    val intent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(selectedOCRJoiningTaskModel.offer_link)
                        )
                    startActivity(intent)
                    binding.cvStart.visibility = View.GONE
                    binding.cvUpload.visibility = View.VISIBLE
                }
            }
            true
        }
        binding.cvHowto.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvHowto.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvHowto.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    val intent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(selectedOCRJoiningTaskModel.how_to_link)
                        )
                    startActivity(intent)

                }
            }
            true
        }

        binding.cvUpload.setOnClickListener {
            Utils.playPopSound(this)
            ImagePicker.with(this)
                .galleryOnly()    //User can only select image from Gallery
                .start()
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
                        "${com.oneline.gamecraft.Companion.siteUrl}verify_ocr_joining_task.php"
                    val emails = TinyDB.getString(this, "phone", "")!!
                    val queue1: RequestQueue = Volley.newRequestQueue(this)

                    val stringRequest = object : StringRequest(Method.POST, url2, { response ->
                        val ytes = Base64.getDecoder().decode(response)
                        val res = String(ytes, Charsets.UTF_8)

                        if (res.contains(",")) {
                            val alldata = res.trim().split(",")
                            Toast.makeText(this, alldata[0], Toast.LENGTH_SHORT).show()
                            TinyDB.saveString(this, "balance", alldata[1])
                            Utils.dismissLoadingPopUp()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
                            Utils.dismissLoadingPopUp()
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