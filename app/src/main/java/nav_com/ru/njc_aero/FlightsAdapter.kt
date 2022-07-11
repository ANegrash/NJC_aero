package nav_com.ru.njc_aero

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import nav_com.ru.njc_aero.models.Flight

class FlightsAdapter (
        context: Context?,
        resource: Int,
        jsonObjects: List<Flight>
    ) : ArrayAdapter<Flight?>(context!!, resource, jsonObjects) {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private val layout: Int = resource
        private val jsonObject: List<Flight> = jsonObjects

        override fun getView (
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {

            val view = inflater.inflate(layout, parent, false)
            val city = view.findViewById<TextView>(R.id.city)
            val code = view.findViewById<TextView>(R.id.flight_code)
            val time = view.findViewById<TextView>(R.id.time)
            val stand = view.findViewById<TextView>(R.id.stand)
            val standHeader = view.findViewById<TextView>(R.id.stand_header)
            val info = view.findViewById<TextView>(R.id.status)
            val not_today = view.findViewById<TextView>(R.id.not_today)
            val airlineLogo = view.findViewById<ImageView>(R.id.airline_logo)
            val obj: Flight = jsonObject[position]

            Picasso.get()
                .load(obj.airlinesLogo)
                .placeholder(R.drawable.default_aircompany)
                .error(R.drawable.default_aircompany)
                .into(airlineLogo)

            time.text = getTrueTime(obj.time)

            val getTime = obj.time
            if (getTime.contains("\n")){
                not_today.visibility = View.VISIBLE
                not_today.text = getTime.split("\n")[1]
            } else {
                not_today.visibility = View.GONE
            }

            if (obj.stand !== null) {
                standHeader.visibility = View.VISIBLE
                stand.visibility = View.VISIBLE
                stand.text = obj.stand.toString().ifEmpty { "-" }
            }

            city.text = obj.city
            code.text = obj.flightCode
            info.text = obj.status.ifEmpty { "-" }

            return view
        }

        private fun getTrueTime (
            time: String
        ): String {
            return time.split("\n").toTypedArray()[0]
        }
    }