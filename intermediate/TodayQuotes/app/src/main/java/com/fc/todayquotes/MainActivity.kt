package com.fc.todayquotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {
    private val viewPager: ViewPager2 by lazy {
        findViewById<ViewPager2>(R.id.viewPager)
    }
    private val progressBar: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initData()
    }

    private fun initViews() {
        viewPager.setPageTransformer { page, position ->
            // 흐려지는 효과
            when {
                position.absoluteValue >= 1.0f -> {
                    page.alpha = 0f
                }
                position == 0f -> {
                    page.alpha = 1f
                }
                else -> {
                    page.alpha = 1f - position.absoluteValue * 2
                }
            }
        }
    }

    private fun initData() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            progressBar.visibility = View.GONE
            if (it.isSuccessful) {
                val quote = parseQuotesJson(remoteConfig.getString("quotes"))
                val isNameRevealed = remoteConfig.getBoolean("is_name_revealed")

                displayQuotesPager(quote, isNameRevealed)

            }
        }
    }

    private fun displayQuotesPager(quote: List<Quote>, nameRevealed: Boolean) {
        val adapter = QuotesPagerAdapter(quote, nameRevealed)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(adapter.itemCount / 2, false)
    }

    private fun parseQuotesJson(json: String): List<Quote> {
        val jsonArray = JSONArray(json)
        var jsonList = emptyList<JSONObject>()
        for (i in 0 until jsonArray.length()) {
            val jsonObj = jsonArray.getJSONObject(i)
            jsonObj?.let {
                jsonList = jsonList + it
            }
        }

        return jsonList.map {
            Quote(it.getString("quote"), it.getString("name"))
        }
    }
}