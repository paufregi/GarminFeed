package paufregi.connectfeed.data.api.converters

import okhttp3.ResponseBody
import paufregi.connectfeed.data.api.models.CSRF
import paufregi.connectfeed.data.api.models.OAuth1
import paufregi.connectfeed.data.api.models.Ticket
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
            OAuth1::class.java -> Oauth1Extractor()
            else -> null
        }
    }
}