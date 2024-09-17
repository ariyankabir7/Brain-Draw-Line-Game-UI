package com.oneline.gamecraft

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.databinding.ActivityLoginRegisterBinding
import com.ice.money1.videoplayyer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

class LoginRegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginRegisterBinding

    init {
        System.loadLibrary("keys")
    }

    external fun HatGy(): String
    external fun HatTy(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
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
        Utils.applyBounceAnimation(binding.ivLogo)
        val scaleUp: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        binding.llLogin.visibility = View.VISIBLE
        binding.llRegister.visibility = View.GONE
        binding.cvRegister.setCardBackgroundColor(getColor( R.color.white))
        binding.cvLogin.setCardBackgroundColor(getColor( R.color.LoginRegisterTextBG))

        binding.cvLogin.setOnClickListener {
            // Show login form and hide register form
            binding.llLogin.visibility = View.VISIBLE
            binding.llRegister.visibility = View.GONE

            binding.cvRegister.setCardBackgroundColor(getColor( R.color.white))
            binding.cvLogin.setCardBackgroundColor(getColor( R.color.LoginRegisterTextBG))
        }

        binding.cvRegister.setOnClickListener {
            // Show register form and hide login form
            binding.llLogin.visibility = View.GONE
            binding.llRegister.visibility = View.VISIBLE

            binding.cvRegister.setCardBackgroundColor(getColor( R.color.LoginRegisterTextBG))
            binding.cvLogin.setCardBackgroundColor(getColor( R.color.white))
        }

        binding.llLoginBtn.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llLoginBtn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llLoginBtn.startAnimation(scaleUp)
                    v.performClick()
                    binding.llLogin.visibility = View.VISIBLE
                    binding.llRegister.visibility = View.GONE
                    login(binding.etLnumber.text.toString())
                }
            }
            true
        }
        binding.llRegisterBtn.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llRegisterBtn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llRegisterBtn.startAnimation(scaleUp)
                    v.performClick()
                    binding.llLogin.visibility = View.GONE
                    binding.llRegister.visibility = View.VISIBLE
                    signup(binding.etNumber.text.toString(), binding.etName.text.toString())
                }
            }
            true
        }
    }

    private fun login(
        phone_no: String,

        ) {
        Utils.showLoadingPopUp(this)
        val url = "${com.oneline.gamecraft.Companion.siteUrl}login.php"
        val queue: RequestQueue = Volley.newRequestQueue(this)

        val deviceid: String = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val time = System.currentTimeMillis()

        val stringRequest = @RequiresApi(Build.VERSION_CODES.O)
        object : StringRequest(
            Method.POST, url,
            { response ->
                Utils.dismissLoadingPopUp()


                val ytes = Base64.getDecoder().decode(response)

                val decng = String(ytes, Charsets.UTF_8)

                if (decng.contains(",")) {
                    val signupData = decng.split(",")
                    var yourString = signupData[1]
                    yourString = yourString.substring(0, yourString.length - HatTy().toInt())
                    Toast.makeText(this, signupData[0], Toast.LENGTH_SHORT).show()
                    TinyDB.saveString(this, "email", yourString)
                    TinyDB.saveString(this, "phone", phone_no)


                    startActivity(Intent(this@LoginRegisterActivity, HomeActivity::class.java))
                    finish()


                } else {

                    Toast.makeText(this, decng, Toast.LENGTH_SHORT).show()
                }


            },
            Response.ErrorListener { error ->
                Utils.dismissLoadingPopUp()
                Toast.makeText(this, "Internet Slow", Toast.LENGTH_SHORT).show()
            }) {

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                val dbit32 = videoplayyer.encrypt(deviceid, HatGy()).toString()
                val tbit32 =
                    videoplayyer.encrypt(time.toString(), HatGy()).toString()
                val token32 =
                    videoplayyer.encrypt(phone_no, HatGy())
                        .toString()

                val den64 =
                    Base64.getEncoder().encodeToString(dbit32.toByteArray())
                val ten64 =
                    Base64.getEncoder().encodeToString(tbit32.toByteArray())
                val token64 =
                    Base64.getEncoder().encodeToString(token32.toByteArray())

                val encodemap: MutableMap<String, String> = HashMap()
                encodemap["deijvfijvmfhvfvhfbhbchbfybebd"] = den64
                encodemap["waofhfuisgdtdrefssfgsgsgdhddgd"] = ten64
                encodemap["fdvbdfbhbrthyjsafewwt5yt5"] = token64


                val jason = Json.encodeToString(encodemap)

                val den264 =
                    Base64.getEncoder().encodeToString(jason.toByteArray())

                val final =
                    URLEncoder.encode(den264, StandardCharsets.UTF_8.toString())

                params["dase"] = final

                val encodedAppID = Base64.getEncoder()
                    .encodeToString(com.oneline.gamecraft.Companion.APP_ID.toString().toByteArray())
                params["app_id"] = encodedAppID

                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun signup(
        phone_no: String,
        name: String

    ) {
        Utils.showLoadingPopUp(this)

        val url = "${com.oneline.gamecraft.Companion.siteUrl}signup.php"
        val queue: RequestQueue = Volley.newRequestQueue(this)

        val deviceid: String = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val time = System.currentTimeMillis()

        val stringRequest =
        object : StringRequest(
            Method.POST, url,
            { response ->
                Utils.dismissLoadingPopUp()


                val ytes = Base64.getDecoder().decode(response)

                val decng = String(ytes, Charsets.UTF_8)

                if (decng.contains(",")) {
                    val signupData = decng.split(",")
                    var yourString = signupData[1]
                    yourString = yourString.substring(0, yourString.length - HatTy().toInt())
                    Toast.makeText(this, signupData[0], Toast.LENGTH_SHORT).show()
                    TinyDB.saveString(this, "email", yourString)
                    TinyDB.saveString(this, "phone", phone_no)

                    startActivity(Intent(this@LoginRegisterActivity, BonusActivity::class.java))
                    finish()


                } else {

                    Toast.makeText(
                        this,
                        decng,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }


            },
            Response.ErrorListener { error ->
                Utils.dismissLoadingPopUp()
                Toast.makeText(this, "Internet Slow", Toast.LENGTH_SHORT).show()
            }) {

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                val dbit32 = videoplayyer.encrypt(deviceid, HatGy()).toString()
                val tbit32 =
                    videoplayyer.encrypt(time.toString(), HatGy()).toString()
                val token32 = videoplayyer.encrypt(phone_no, HatGy()).toString()
                val name32 = videoplayyer.encrypt(name, HatGy()).toString()

                val den64 =
                    Base64.getEncoder().encodeToString(dbit32.toByteArray())
                val ten64 =
                    Base64.getEncoder().encodeToString(tbit32.toByteArray())
                val token64 = Base64.getEncoder().encodeToString(token32.toByteArray())
                val name64 = Base64.getEncoder().encodeToString(name32.toByteArray())

                val encodemap: MutableMap<String, String> = HashMap()
                encodemap["deijvfijvmfhvfvhfbhbchbfybebd"] = den64
                encodemap["waofhfuisgdtdrefssfgsgsgdhddgd"] = ten64
                encodemap["fdvbdfbhbrthyjsafewwt5yt5"] = token64
                encodemap["adearebvfdbbhyytryvxcvgrgr"] = name64


                val jason = Json.encodeToString(encodemap)

                val den264 =
                    Base64.getEncoder().encodeToString(jason.toByteArray())

                val final =
                    URLEncoder.encode(den264, StandardCharsets.UTF_8.toString())

                params["dase"] = final

                val encodedAppID = Base64.getEncoder()
                    .encodeToString(com.oneline.gamecraft.Companion.APP_ID.toString().toByteArray())
                params["app_id"] = encodedAppID

                return params
            }
        }
        queue.add(stringRequest)
    }



}