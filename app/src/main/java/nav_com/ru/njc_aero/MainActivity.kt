package nav_com.ru.njc_aero

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import nav_com.ru.njc_aero.models.Flight
import nav_com.ru.njc_aero.models.ResponseModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

const val PREFS_NAME = "nav-com.njc"
const val KEY_TYPE = "prefs.selected_mode"

class MainActivity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    val _DEPARTURE = 1
    val _ARRIVAL = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.parseColor("#0E1D31")

        loadList(getSelectedMode())

        val dep = findViewById<Button>(R.id.departureBtn)
        val arr = findViewById<Button>(R.id.arriveBtn)
        val reload = findViewById<ImageButton>(R.id.reload_btn)

        dep.setOnClickListener {
            loadList(_DEPARTURE)
        }

        arr.setOnClickListener {
            loadList(_ARRIVAL)
        }

        reload.setOnClickListener {
            loadList(getSelectedMode())
        }

    }

    private fun loadList(
        flightType: Int
    ) {
        replaceSelectionButtons(flightType)
        setVisibleFrame(0, 1, 0)

        val listView = findViewById<ListView>(R.id.flight_list)

        val sendRequest = SendRequest()
        var url = "https://nav-com.ru/njc/api/v1.php?q="

        url += if (flightType == _DEPARTURE)
            "getDepartureSchedule"
        else
            "getArriveSchedule"

        sendRequest.run(
            url,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        setError()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val stringResponse = response.body.string()
                    val gson = Gson()

                    val responsedBody: ResponseModel =
                        gson.fromJson(stringResponse, ResponseModel::class.java)

                    if (responsedBody.code == 200) {
                        val listOfFlights : List<Flight> = responsedBody.response
                        runOnUiThread {
                            if (flightType == _DEPARTURE) {
                                val flightAdapter = FlightsAdapter(
                                    this@MainActivity,
                                    R.layout.flight_item,
                                    listOfFlights
                                )
                                listView.adapter = flightAdapter
                            } else if (flightType == _ARRIVAL) {
                                val flightAdapter = FlightsAdapter(
                                    this@MainActivity,
                                    R.layout.flight_item_arrive,
                                    listOfFlights
                                )
                                listView.adapter = flightAdapter
                            }

                            setVisibleFrame()
                        }

                    } else {
                        runOnUiThread {
                            setError(responsedBody.code, responsedBody.info.toString())
                        }
                    }
                }
            })
    }

    private fun replaceSelectionButtons(
        buttonNumber: Int
    ) {
        saveSelectedMode(buttonNumber)

        val dep = findViewById<Button>(R.id.departureBtn)
        val arr = findViewById<Button>(R.id.arriveBtn)

        if (buttonNumber == _DEPARTURE){
            dep.setBackgroundColor(resources.getColor(R.color.primary))
            dep.setTextColor(resources.getColor(R.color.secondary))
            (dep as MaterialButton).apply {
                icon = getDrawable(R.drawable.ic_plane_departure)
            }

            arr.setBackgroundColor(resources.getColor(R.color.secondary))
            arr.setTextColor(resources.getColor(R.color.primary))
            (arr as MaterialButton).apply {
                icon = getDrawable(R.drawable.ic_plane_arrival_white)
            }
        } else if (buttonNumber == _ARRIVAL){
            dep.setBackgroundColor(resources.getColor(R.color.secondary))
            dep.setTextColor(resources.getColor(R.color.primary))
            (dep as MaterialButton).apply {
                icon = getDrawable(R.drawable.ic_plane_departure_white)
            }

            arr.setBackgroundColor(resources.getColor(R.color.primary))
            arr.setTextColor(resources.getColor(R.color.secondary))
            (arr as MaterialButton).apply {
                icon = getDrawable(R.drawable.ic_plane_arrival)
            }
        }
    }

    private fun setError(
        errorCode: Int = 500,
        errorText: String = "Сервис временно недоступен"
    ) {
        val errortw = findViewById<TextView>(R.id.error_tw)

        errortw.text = "Ошибка $errorCode: $errorText"

        setVisibleFrame(0, 0, 1)
    }

    private fun setVisibleFrame(
        mainVisible: Int = 1,
        loadingVisible: Int = 0,
        errorVisible: Int = 0
    ){
        val mainContent = findViewById<ConstraintLayout>(R.id.mainContent)
        val loadingContent = findViewById<ConstraintLayout>(R.id.loadingContent)
        val errorContent = findViewById<ConstraintLayout>(R.id.errorContent)

        when (mainVisible) {
            0 -> mainContent.visibility = View.GONE
            1 -> mainContent.visibility = View.VISIBLE
        }

        val imageView = findViewById<ImageView>(R.id.imageView3)
        imageView.setBackgroundResource(R.drawable.loading_na)

        val animationLoading : AnimationDrawable = imageView.background as AnimationDrawable

        when (loadingVisible) {
            0 -> {
                animationLoading.stop()
                loadingContent.visibility = View.GONE
            }
            1 -> {
                animationLoading.start()
                loadingContent.visibility = View.VISIBLE
            }
        }

        when (errorVisible) {
            0 -> errorContent.visibility = View.GONE
            1 -> errorContent.visibility = View.VISIBLE
        }
    }

    private fun getSelectedMode() = sharedPrefs.getInt(KEY_TYPE, _DEPARTURE)

    private fun saveSelectedMode (mode: Int) = sharedPrefs.edit().putInt(KEY_TYPE, mode).apply()

}