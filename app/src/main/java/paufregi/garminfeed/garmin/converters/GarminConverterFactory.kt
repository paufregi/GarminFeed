package paufregi.garminfeed.garmin.converters

import okhttp3.ResponseBody
import paufregi.garminfeed.garmin.data.CSRF
import paufregi.garminfeed.garmin.data.Oauth1
import paufregi.garminfeed.garmin.data.Ticket
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class GarminConverterFactory : Converter.Factory() {

    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        return when (type) {
            CSRF::class.java -> CSRFStringConverter()
            Ticket::class.java -> TicketStringConverter()
            else -> null
        }
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation?>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return when (type) {
            CSRF::class.java -> CSRFExtractor()
            Ticket::class.java -> TicketExtractor()
            Oauth1::class.java -> Oauth1Converter()
            else -> null
        }
    }
}