package paufregi.garminfeed.garmin.converters

import com.google.common.truth.Truth.assertThat

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

import org.junit.Test
import paufregi.garminfeed.garmin.data.Oauth1

class Oauth1ExtractorTest {

    private val converter = Oauth1Extractor()

    private val apiResponse = "oauth_token=TEST_TOKEN&oauth_token_secret=TEST_TOKEN_SECRET"
    private val mediaType = "application/json"

    @Test
    fun `Extract Oauth1`() {
        val responseBody = apiResponse.toResponseBody(mediaType.toMediaType())

        val result = converter.convert(responseBody)

        assertThat(result).isEqualTo(Oauth1("TEST_TOKEN", "TEST_TOKEN_SECRET"))
    }

    @Test
    fun `No Oauth1`() {
        val responseBody = "".toResponseBody(mediaType.toMediaType())

        val result = converter.convert(responseBody)

        assertThat(result).isEqualTo(Oauth1("", ""))
    }
}