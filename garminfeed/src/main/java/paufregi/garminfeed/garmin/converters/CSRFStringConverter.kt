package paufregi.garminfeed.garmin.converters

import paufregi.garminfeed.garmin.data.CSRF
import retrofit2.Converter

class CSRFStringConverter : Converter<CSRF, String> {
    override fun convert(value: CSRF): String {
        return value.value
    }
}