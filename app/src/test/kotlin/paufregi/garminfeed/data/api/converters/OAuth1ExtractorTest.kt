package paufregi.garminfeed.data.api.converters

import com.google.common.truth.Truth.assertThat

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

import org.junit.Test
import paufregi.garminfeed.data.api.models.OAuth1

class OAuth1ExtractorTest {

    private val converter = Oauth1Extractor()

    private val apiResponse = "oauth_token=TEST_TOKEN&oauth_token_secret=TEST_TOKEN_SECRET"
    private val mediaType = "application/json"

    @Test
    fun `Extract Oauth1`() {
        val responseBody = apiResponse.toResponseBody(mediaType.toMediaType())

        val result = converter.convert(responseBody)

        assertThat(result).isEqualTo(OAuth1("TEST_TOKEN", "TEST_TOKEN_SECRET"))
    }

    @Test
    fun `No Oauth1`() {
        val responseBody = "".toResponseBody(mediaType.toMediaType())

        val result = converter.convert(responseBody)

        assertThat(result).isEqualTo(OAuth1("", ""))
    }
}