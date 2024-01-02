package paufregi.garminfeed.garmin.converters

import okhttp3.ResponseBody
import paufregi.garminfeed.garmin.data.Oauth1
import retrofit2.Converter

class Oauth1Converter : Converter<ResponseBody, Oauth1> {
    override fun convert(value: ResponseBody): Oauth1 {
        val queryMap = value.string().split('&').associate {
            val parts = it.split('=')
            val k = parts.firstOrNull().orEmpty()
            val v = parts.drop(1).firstOrNull().orEmpty()
            Pair(k, v)
        }

        return Oauth1(
            queryMap["oauth_token"].orEmpty(),
            queryMap["oauth_token_secret"].orEmpty()
        )
    }
}