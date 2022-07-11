package nav_com.ru.njc_aero

import okhttp3.*
import java.util.concurrent.TimeUnit

class SendRequest {

        fun run(
            url: String,
            callback: Callback
        ) {

            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url(url)
                .build()
            okHttpClient.newCall(request).enqueue(callback)
        }
}