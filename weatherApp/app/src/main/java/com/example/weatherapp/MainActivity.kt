package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefectureMap = mapOf(
            "北海道" to "hokkaido",
            "青森県" to "aomori",
            "岩手県" to "iwate",
            "宮城県" to "miyagi",
            "秋田県" to "akita",
            "山形県" to "yamagata",
            "福島県" to "fukushima",
            "茨城県" to "ibaraki",
            "栃木県" to "tochigi",
            "群馬県" to "gumma",
            "埼玉県" to "saitama",
            "千葉県" to "chiba",
            "東京都" to "tokyo",
            "神奈川県" to "kanagawa",
            "新潟県" to "niigata",
            "富山県" to "toyama",
            "石川県" to "ishikawa",
            "福井県" to "fukui",
            "山梨県" to "yamanashi",
            "長野県" to "nagano",
            "岐阜県" to "gifu",
            "静岡県" to "shizuoka",
            "愛知県" to "aichi",
            "三重県" to "mie",
            "滋賀県" to "shiga",
            "京都府" to "kyoto",
            "大阪府" to "osaka",
            "兵庫県" to "hyogo",
            "奈良県" to "nara",
            "和歌山県" to "wakayama",
            "鳥取県" to "tottori",
            "島根県" to "shimane",
            "岡山県" to "okayama",
            "広島県" to "hiroshima",
            "山口県" to "yamaguchi",
            "徳島県" to "tokushima",
            "香川県" to "kagawa",
            "愛媛県" to "ehime",
            "高知県" to "kochi",
            "福岡県" to "fukuoka",
            "佐賀県" to "saga",
            "長崎県" to "nagasaki",
            "熊本県" to "kumamoto",
            "大分県" to "oita",
            "宮崎県" to "miyazaki",
            "鹿児島県" to "kagoshima",
            "沖縄県" to "okinawa"
        )

        // viewの取得
        val btnSearch: Button = findViewById<Button>(R.id.btnSearch)
        val txCityName: TextView = findViewById<TextView>(R.id.txCityName)
        val txWeather: TextView = findViewById<TextView>(R.id.txWeather)
        val txMaxTemp: TextView = findViewById<TextView>(R.id.txMaxTemp)
        val txMinTemp: TextView = findViewById<TextView>(R.id.txMinTemp)
        val btnClear: Button = findViewById<Button>(R.id.btnClear)

        val spinner: Spinner = findViewById<Spinner>(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.prefs_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // ボタン押下時
        btnSearch.setOnClickListener() {
            val pref: String = spinner.selectedItem.toString()
            val requestUrl: String = makeUrl(pref)
            weatherTask(requestUrl)
        }
        btnClear.setOnClickListener() {
            txCityName.text = "都市名"
            txWeather.text = "都市の天気"
            txMaxTemp.text = "最高気温"
            txMinTemp.text = "最低気温"
        }
    }

    private fun makeUrl(value: String): String {
        // URL生成
        val apiUrl: String = getString(R.string.apiUrl)
        val apiKey: String = BuildConfig.API_KEY
        return "${apiUrl}&q=${value}&appid=${apiKey}"
    }

    private fun weatherTask(requestUrl: String) {
        // コルーチンスコープ(非同期処理の領域)の展開
        lifecycleScope.launch {

            val result = weatherBackgroundTask(requestUrl)

            weatherJsonTask(result)
        }
    }

    private suspend fun weatherBackgroundTask(requestUrl: String): String{
        val response: String = withContext(Dispatchers.IO) {
            var httpResult: String = ""
            try {
                val url: URL = URL(requestUrl)
                val br: BufferedReader = BufferedReader(InputStreamReader(url.openStream()))
                httpResult = br.readText()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return@withContext httpResult
        }
        return response
    }

    @SuppressLint("SetTextI18n")
    private fun weatherJsonTask(result: String) {
        val txCityName: TextView = findViewById<TextView>(R.id.txCityName)
        val txWeather: TextView = findViewById<TextView>(R.id.txWeather)
        val txMaxTemp: TextView = findViewById<TextView>(R.id.txMaxTemp)
        val txMinTemp: TextView = findViewById<TextView>(R.id.txMinTemp)

        // JSONの取得
        val json = JSONObject(result)

        // 各TextViewに値をセット
        txCityName.text = json.getString("name")

        val arrayWeather = json.getJSONArray("weather")
        txWeather.text = arrayWeather.getJSONObject(0).getString("description")

        val mainObj = json.getJSONObject("main")
        txMaxTemp.text = "最高気温：${mainObj.getInt("temp_max")-273}℃"
        txMinTemp.text = "最低気温：${mainObj.getInt("temp_min")-273}℃"
    }

}