package com.oneline.gamecraft

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.oneline.gamecraft.databinding.ActivityGamePointBinding

class GamePointActivity : AppCompatActivity() {
    lateinit var binding: ActivityGamePointBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGamePointBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Utils.winPopSound(this)
        binding.tvGamePoint.text=TinyDB.getString(this,"playearnPoint","0")
        Utils.slotAnimation(TinyDB.getString(this,"playearnPoint","0").toString().toInt(),  binding.tvGamePoint)
        binding.cvOkay.setOnClickListener {
            finish()
        }
    }
}