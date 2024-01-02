package paufregi.garminfeed.garmin.converters

import okhttp3.ResponseBody
import paufregi.garminfeed.garmin.data.Ticket
import retrofit2.Converter

class TicketExtractor : Converter<ResponseBody, Ticket> {
    private val ticketRegex = Regex("""embed\?ticket=([^"]+)""")

    override fun convert(value: ResponseBody): Ticket {
        return Ticket(ticketRegex.find(value.string())?.groups?.get(1)?.value.orEmpty())
    }
}