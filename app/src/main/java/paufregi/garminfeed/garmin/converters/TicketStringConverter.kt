package paufregi.garminfeed.garmin.converters

import paufregi.garminfeed.garmin.data.Ticket
import retrofit2.Converter

class TicketStringConverter : Converter<Ticket, String> {
    override fun convert(value: Ticket): String {
        return value.value
    }
}