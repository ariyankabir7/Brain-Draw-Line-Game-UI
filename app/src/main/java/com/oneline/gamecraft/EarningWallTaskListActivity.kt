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
import com.oneline.gamecraft.adapter.earningwallAdapter
import com.oneline.gamecraft.databinding.ActivityEarningWallTaskListBinding
import com.oneline.gamecraft.modal.EarningwallModal
import java.util.Base64

class EarningWallTaskListActivity : AppCompatActivity() {
    lateinit var binding: ActivityEarningWallTaskListBinding
    var earningwallHistory = ArrayList<EarningwallModal>()
    lateinit var adapter: earningwallAdapter  // Declare adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEarningWallTaskListBinding.inflate(layoutInflater)
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

        // Initialize and set the adapter with an empty list
        adapter = earningwallAdapter(this, earningwallHistory)
        binding.rvTask.adapter = adapter

        // Fetch data
        getEarningWallTask()
    }

    private fun getEarningWallTask() {
        val emails = Base64.getEncoder()
            .encodeToString(TinyDB.getString(this, "phone", "")!!.toByteArray())

        val url = "${com.oneline.gamecraft.Companion.siteUrl}get_earningwall_tasklist.php"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = object : JsonArrayRequest(Method.GET, url, null,
            Response.Listener { response ->
                if (response.length() > 0) {
                    earningwallHistory.clear()

                    for (i in 0 until response.length()) {
                        val dataObject = response.getJSONObject(i)

                        val offer_id = dataObject.getString("offer_id")
                        val title = dataObject.getString("offer_title")
                        val icon = dataObject.getString("offer_icon")
                        val offer_coin = dataObject.getString("offer_coin")
                        val offer_link = dataObject.getString("offer_link")
                        val status = dataObject.getString("offer_status")
                        val offer_description = dataObject.getString("offer_description")

                        val historyRedeemModal = EarningwallModal(
                            offer_id, title, icon, offer_coin, offer_link, status,offer_description
                        )
                        earningwallHistory.add(historyRedeemModal)
                    }

                    // Notify the adapter of data changes
                    adapter.notifyDataSetChanged()
                } else {
                    binding.nodata.visibility = View.VISIBLE
                    binding.rvTask.visibility = View.GONE
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Internet Slow!", Toast.LENGTH_SHORT).show()
            }) {}

        requestQueue.add(jsonArrayRequest)
    }
}
