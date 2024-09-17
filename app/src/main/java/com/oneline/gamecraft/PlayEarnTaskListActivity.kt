package com.oneline.gamecraft

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.oneline.gamecraft.adapter.playearnAdapter
import com.oneline.gamecraft.databinding.ActivityPlayEarnTaskListBinding
import com.oneline.gamecraft.modal.PlayEarnModal
import com.oneline.gamecraft.services.MusicService
import java.util.Base64

class PlayEarnTaskListActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlayEarnTaskListBinding
    var playEarnHistory = ArrayList<PlayEarnModal>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayEarnTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val insetsController = ViewCompat.getWindowInsetsController(v)
            insetsController?.isAppearanceLightStatusBars = true
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Set up RecyclerView
        binding.rvTask.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        getPlayEarnTask()
    }
    fun  getPlayEarnTask(){
        Utils.showLoadingPopUp(this)
        val emails = Base64.getEncoder()
            .encodeToString(TinyDB.getString(this, "phone", "")!!.toByteArray())

        val url = "${com.oneline.gamecraft.Companion.siteUrl}get_playearn_tasklist.php"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest =
            object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->

                if (response.length() > 0) {
                    playEarnHistory.clear()

                    for (i in 0 until response.length()) {
                        val dataObject = response.getJSONObject(i)

                        val game_id = dataObject.getString("game_id")
                        val title = dataObject.getString("game_title")
                        val icon = dataObject.getString("game_banner_img")
                        val offer_coin = dataObject.getString("game_coin")
                        val offer_link = dataObject.getString("game_link")
                        val playEarnHistory1 = PlayEarnModal(game_id, title,icon,offer_coin,offer_link)
                        playEarnHistory.add(playEarnHistory1)
                    }
                    Utils.dismissLoadingPopUp()
                    // Initialize the adapter with an empty list first
                    val adapter = playearnAdapter(this, playEarnHistory)
                    binding.rvTask.adapter = adapter
                } else {
                   // binding.nodata.visibility = View.VISIBLE
                    binding.rvTask.visibility = View.GONE
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(this, "Internet Slow !", Toast.LENGTH_SHORT).show()
            }) {}

        requestQueue.add(jsonArrayRequest)
    }
    override fun onResume() {
        super.onResume()
        if(TinyDB.getBoolean(this, "isMusicOn", true)){
            stopService(Intent(this@PlayEarnTaskListActivity, MusicService::class.java))
            startService(Intent(this@PlayEarnTaskListActivity, MusicService::class.java))
        }
    }
}