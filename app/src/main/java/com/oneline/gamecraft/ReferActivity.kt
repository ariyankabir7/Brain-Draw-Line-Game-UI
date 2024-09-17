package com.oneline.gamecraft

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.oneline.gamecraft.databinding.ActivityReferBinding

class ReferActivity : AppCompatActivity() {
    lateinit var binding: ActivityReferBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val insetsController = ViewCompat.getWindowInsetsController(v)
            insetsController?.isAppearanceLightStatusBars = true
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvReferLink.text = "https://gamecraft.fun/gameui50/refer/?code=${TinyDB.getString(this, "refer_code", "")}&app_id=${com.oneline.gamecraft.Companion.APP_ID}"
        binding.tvTotalRefer.text = TinyDB.getString(this, "total_registration", "")
        binding.tvReferCompleted.text = TinyDB.getString(this, "total_referral", "")
        binding.cvCopy.setOnClickListener {
            Utils.playPopSound(this)
            Utils.copyToClipboard(this, binding.tvReferLink.text.toString())

        }
    }
}