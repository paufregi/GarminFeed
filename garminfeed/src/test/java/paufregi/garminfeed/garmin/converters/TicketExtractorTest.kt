package paufregi.garminfeed.garmin.converters

import com.google.common.truth.Truth.assertThat

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

import org.junit.Test
import paufregi.garminfeed.garmin.data.Ticket

class TicketExtractorTest {

    private val converter = TicketExtractor()

    private val html = """
        <!DOCTYPE html>
        <html class="no-js">
        	<head>
        		<title>Success</title>
        		<meta charset="utf-8">
        		<meta http-equiv="X-UA-Compatible" content="IE=edge;" />
        		<meta name="description" content="">
        		<meta name="viewport" content="width=device-width, initial-scale=1">
        		<meta http-equiv="cleartype" content="on">
        		<script type="text/javascript">
        			var redirectAfterAccountLoginUrl 	  = "https:\/\/sso.garmin.com\/sso\/embed";
        			var redirectAfterAccountCreationUrl = "https:\/\/sso.garmin.com\/sso\/embed";
        			var consumeServiceTicket         	  = "true";
        			var service_url                  	  = "https:\/\/sso.garmin.com\/sso\/embed";
        			var parent_url                   	  = "https:\/\/sso.garmin.com\/sso\/embed";
        			var response_url                 	  = "https:\/\/sso.garmin.com\/sso\/embed?ticket=TEST_TICKET_VALUE";
        			var logintoken                   	  = "";
        			var socialLogin                   	  = "";
        			var performMFACheck                 = "";
        		</script>
        	</head>
        	<body>
        		<div id="GAuth-component">
        			<img src='/sso/images/ajax-loader.gif' class="loaderImage"/>
        		</div>
        	</body>m
        </html>
    """.trimIndent()
    private val mediaType = "text/html; charset=UTF-8"

    @Test
    fun `Extract Ticket`() {
        val responseBody = html.toResponseBody(mediaType.toMediaType())

        val result = converter.convert(responseBody)

        assertThat(result).isEqualTo(Ticket("TEST_TICKET_VALUE"))
    }

    @Test
    fun `No Ticket`() {
        val responseBody = "".toResponseBody(mediaType.toMediaType())

        val result = converter.convert(responseBody)

        assertThat(result).isEqualTo(Ticket(""))
    }
}