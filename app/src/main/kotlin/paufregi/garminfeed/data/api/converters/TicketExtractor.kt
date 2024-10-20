package paufregi.garminfeed.data.api.converters

import okhttp3.ResponseBody
import paufregi.garminfeed.data.api.models.Ticket
import retrofit2.Converter

class TicketExtractor : Converter<ResponseBody, Ticket> {
    private val ticketRegex = Regex("""embed\?ticket=([^"]+)""")

    override fun convert(value: ResponseBody): Ticket {
        val matchedGroups = ticketRegex.find(value.string())?.groups
        val firstGroup = matchedGroups?.get(1)

        return Ticket(firstGroup?.value.orEmpty())
    }
}