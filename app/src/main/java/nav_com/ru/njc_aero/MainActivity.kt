package nav_com.ru.njc_aero

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import nav_com.ru.njc_aero.models.Flight
import nav_com.ru.njc_aero.models.ResponseModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val _DEPARTURE = 1
    val _ARRIVAL = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceSelectionButtons(_DEPARTURE)
        loadList(_DEPARTURE)

        val dep = findViewById<Button>(R.id.departureBtn)
        val arr = findViewById<Button>(R.id.arriveBtn)

        dep.setOnClickListener {
            loadList(_DEPARTURE)
            replaceSelectionButtons(_DEPARTURE)
        }

        arr.setOnClickListener {
            loadList(_ARRIVAL)
            replaceSelectionButtons(_ARRIVAL)
        }

    }

    private fun loadList(
        flightType: Int
    ) {
        setVisibleFrame(0, 1, 0)
        val listView = findViewById<ListView>(R.id.flight_list)

        val sendRequest = SendRequest()
        var url = "https://nav-com.ru/njc/api/v1.php?q="

        url += if (flightType == _DEPARTURE)
            "getDepartureShedule"
        else
            "getArriveShedule"

        sendRequest.run(
            url,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {

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
                            } else {
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

                        }
                    }
                }
            })
    }

    private fun replaceSelectionButtons(
        buttonNumber: Int
    ) {
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

        when (loadingVisible) {
            0 -> loadingContent.visibility = View.GONE
            1 -> loadingContent.visibility = View.VISIBLE
        }

        when (errorVisible) {
            0 -> errorContent.visibility = View.GONE
            1 -> errorContent.visibility = View.VISIBLE
        }
    }
}