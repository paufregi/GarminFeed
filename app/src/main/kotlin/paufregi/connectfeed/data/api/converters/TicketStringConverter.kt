package paufregi.connectfeed.data.api.converters

import paufregi.connectfeed.data.api.models.Ticket
import retrofit2.Converter

class TicketStringConverter : Converter<Ticket, String> {
    override fun convert(value: Ticket): String {
        return value.value
    }
}