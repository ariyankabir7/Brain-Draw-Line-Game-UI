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
import com.bumptech.glide.Glide
import com.oneline.gamecraft.databinding.ActivityPlayEarnDetailsBinding

class PlayEarnDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlayEarnDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityPlayEarnDetailsBinding.inflate(layoutInflater)
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
        val title=intent.getStringExtra("title")
        val coin=intent.getStringExtra("coin")
        val link=intent.getStringExtra("link")
        val icon=intent.getStringExtra("icon")
        binding.tvAppname.text=title
        binding.tvBalance.text=coin
        Glide.with(this)
            .load(icon)
            .into(binding.ivLogo)
        val scaleUp: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        binding.cvStart.setOnTouchListener{ v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cvStart.startAnimation(scaleDown)
                }

                MotionEvent.ACTION_UP -> {
                    binding.cvStart.startAnimation(scaleUp)
                    v.performClick()
                    Utils.playPopSound(this)
                    val intent = Intent(this, PlayEarnWebviewActivity::class.java)
                    intent.putExtra("link", link)
                  startActivity(intent)


                }
            }
            true
        }
    }
}