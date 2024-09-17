package com.oneline.gamecraft

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
import com.oneline.gamecraft.adapter.screenshotAdapter
import com.oneline.gamecraft.databinding.ActivityScreenShotTaskListBinding
import com.oneline.gamecraft.modal.ScreenshotModal
import java.util.Base64

class ScreenShotTaskListActivity : AppCompatActivity() {
    lateinit var binding: ActivityScreenShotTaskListBinding
    var screenShot= ArrayList<ScreenshotModal>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScreenShotTaskListBinding.inflate(layoutInflater)
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

    }
    fun  getscreenShotTask() {
        Utils.showLoadingPopUp(this)
        val emails = Base64.getEncoder()
            .encodeToString(TinyDB.getString(this, "phone", "")!!.toByteArray())
        val appId = Base64.getEncoder().encodeToString(com.oneline.gamecraft.Companion.APP_ID.toString().toByteArray())

        val url = "${com.oneline.gamecraft.Companion.siteUrl}get_screenshot_tasklist.php?email=$emails&app_id=$appId"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest =
            object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->

                if (response.length() > 0) {
                    screenShot.clear()

                    for (i in 0 until response.length()) {
                        val dataObject = response.getJSONObject(i)

                        val task_id = dataObject.getString("task_id")
                        val title = dataObject.getString("task_title")
                        val icon = dataObject.getString("task_icon")
                        val offer_coin = dataObject.getString("task_coin")
                        val offer_link = dataObject.getString("task_link")
                        val status = dataObject.getString("task_status")
                        val task_description = dataObject.getString("task_description")
                        val howto = dataObject.getString("how_to_link")
                        val screenModal = ScreenshotModal(task_id, title,icon,offer_coin,offer_link,status,task_description,howto)
                        screenShot.add(screenModal)
                    }
                    Utils.dismissLoadingPopUp()
                    val adapter = screenshotAdapter(this, screenShot)
                    binding.rvTask.adapter = adapter
                } else {
                    binding.nodata.visibility = View.VISIBLE
                    binding.rvTask.visibility = View.GONE
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(this, "Internet Slow !", Toast.LENGTH_SHORT).show()
            }) {}

        requestQueue.add(jsonArrayRequest)
    }

    override fun onResume() {
        super.onResume()
        getscreenShotTask()
    }
}