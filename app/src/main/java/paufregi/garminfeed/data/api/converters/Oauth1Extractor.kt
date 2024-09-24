package paufregi.garminfeed.data.api.converters

import okhttp3.ResponseBody
import paufregi.garminfeed.data.api.models.OAuth1
import retrofit2.Converter

class Oauth1Extractor : Converter<ResponseBody, OAuth1> {
    override fun convert(value: ResponseBody): OAuth1 {
        val queryMap = value.string().split('&').associate {
            val parts = it.split('=')
            val k = parts.firstOrNull()
            val v = parts.drop(1).firstOrNull()
            Pair(k, v)
        }

        return OAuth1(
            queryMap["oauth_token"].orEmpty(),
            queryMap["oauth_token_secret"].orEmpty()
        )
    }
}