package com.oneline.gamecraft

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.oneline.gamecraft.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProfileBinding.inflate(layoutInflater)
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
        binding.llGetHelp.setOnClickListener {
            Utils.playPopSound(this)
            Utils.openUrl(
                this, Uri.parse(TinyDB.getString(this, "telegram_link", "")).toString()
            )
        }
        binding.llSponser.setOnClickListener {
            Utils.playPopSound(this)
            Utils.openUrl(
                this, Uri.parse(TinyDB.getString(this, "sponser_link", "")).toString()
            )
        }
        binding.llReferLink.setOnClickListener {
            Utils.playPopSound(this)
            startActivity(Intent(this, ReferActivity::class.java))

        }
        binding.llLogout.setOnClickListener {
            Utils.playPopSound(this)
            TinyDB.clearPreferences(this)
            startActivity(Intent(this, LoginRegisterActivity::class.java))
            finish()
        }
        binding.tvName.text=TinyDB.getString(this,"name","")
        binding.tvPlayerId.text=TinyDB.getString(this,"phone","")
        binding.tvPlayerId1.text=TinyDB.getString(this,"phone","")
        binding.tvBalance.text=TinyDB.getString(this,"balance","")
        Utils.slotAnimation(TinyDB.getString(this,"balance","").toString().toInt(),  binding.tvBalance)


    }
}