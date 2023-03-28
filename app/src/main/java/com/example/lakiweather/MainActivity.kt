@file:Suppress("DEPRECATION")

package com.example.lakiweather
import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    var city:String ="Palayamkottai"
    val appid:String="a70dc824b8d298748e37f76f80f8f688"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WeatherTask().execute()
        val search = findViewById<ImageView>(R.id.search)
        search.setOnClickListener {
            val location = findViewById<EditText>(R.id.locationInput).text.toString()
            city = location
            val noInput: Boolean = location.isEmpty()
            if (!noInput) {
                WeatherTask().execute()
            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    inner class WeatherTask : AsyncTask<String, Void, String>()
    {
        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): String? {
            val response:String? = try{
                 URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$appid").readText(Charsets.UTF_8)
             } catch (e:Exception){
                 null
             }
            return response
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result.toString())
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText =SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt*1000)
                )
                val temp = main.getString("temp")+"°C"
                val tempMin = "குறைந்தபட்சம்: "+main.getString("temp_min")+"°C"
                val tempMax = "அதிகபட்சம்: "+main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")

                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name")

                when(weatherDescription){
                    "scattered clouds"->findViewById<TextView>(R.id.status).text = "சிதறிய மேகங்கள்"
                    "overcast clouds"->findViewById<TextView>(R.id.status).text ="மேகமூட்டமான மேகங்கள்"
                    "broken clouds"->findViewById<TextView>(R.id.status).text="உடைந்த மேகங்கள்"
                    "few clouds"->findViewById<TextView>(R.id.status).text="சில மேகங்கள்"
                    "clear sky"->findViewById<TextView>(R.id.status).text="தெளிந்த வானம்"

                    else->findViewById<TextView>(R.id.status).text=weatherDescription.capitalize(Locale.ENGLISH)
                }

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(
                    Date(sunrise*1000)
                )
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity
                findViewById<ProgressBar>(R.id.loader).visibility= View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
            }
            catch (e: Exception){
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }
    }
}