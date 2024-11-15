package paufregi.connectfeed.data.api.converters

import okhttp3.ResponseBody
import paufregi.connectfeed.data.api.models.CSRF
import retrofit2.Converter

class CSRFExtractor() : Converter<ResponseBody, CSRF> {
    private val csrfRegex = Regex("""name="_csrf"\s+value="(.+?)"""")

    override fun convert(value: ResponseBody): CSRF {
        val matchedGroups = csrfRegex.find(value.string())?.groups
        val firstGroup = matchedGroups?.get(1)

        return CSRF(firstGroup?.value.orEmpty())
    }
}