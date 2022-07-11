package nav_com.ru.njc_aero.models

data class ResponseModel(
    var code: Int,
    var response: List<Flight>,
    var info: Any
)
