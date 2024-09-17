package com.oneline.gamecraft

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.oneline.gamecraft.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        binding.llOpenLoginBtn.setOnTouchListener {v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llOpenLoginBtn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llOpenLoginBtn.startAnimation(scaleUp)
                    v.performClick()
                    TinyDB.saveString(this, "clickItem", "login")
                    Utils.playPopSound(this)
                    startActivity(Intent(this, LoginRegisterActivity::class.java))
                    finish()
                }
            }
            true
        }
        binding.llOpenRegisterBtn.setOnTouchListener {v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.llOpenRegisterBtn.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.llOpenRegisterBtn.startAnimation(scaleUp)
                    v.performClick()
                    TinyDB.saveString(this, "clickItem", "register")
                    Utils.playPopSound(this)
                    startActivity(Intent(this, LoginRegisterActivity::class.java))
                    finish()
                }
            }
            true

        }
    }
}