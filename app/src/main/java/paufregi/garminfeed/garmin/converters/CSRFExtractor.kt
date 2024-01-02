package paufregi.garminfeed.garmin.converters

import okhttp3.ResponseBody
import paufregi.garminfeed.garmin.data.CSRF
import retrofit2.Converter

class CSRFExtractor : Converter<ResponseBody, CSRF> {
    private val csrfRegex = Regex("""name="_csrf"\s+value="(.+?)"""")

    override fun convert(value: ResponseBody): CSRF {
        return CSRF(csrfRegex.find(value.string())?.groups?.get(1)?.value.orEmpty())
    }
}