package paufregi.connectfeed.data.api.converters

import paufregi.connectfeed.data.api.models.CSRF
import retrofit2.Converter

class CSRFStringConverter : Converter<CSRF, String> {
    override fun convert(value: CSRF): String {
        return value.value
    }
}