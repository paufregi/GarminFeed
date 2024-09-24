package paufregi.garminfeed.data.api

import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import paufregi.garminfeed.data.api.models.CSRF
import paufregi.garminfeed.data.api.models.OAuthConsumer
import paufregi.garminfeed.data.api.models.Ticket
import java.net.HttpURLConnection

class GarminSSOTest {

    private var server: MockWebServer = MockWebServer()
    private lateinit var api: GarminSSO

    private val htmlForCSRF = """
        <!DOCTYPE html>
        <html lang="en" class="no-js">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <meta name="viewport" content="width=device-width" />
                <meta http-equiv="X-UA-Compatible" content="IE=edge;" />
                <title>GARMIN Authentication Application</title>

        	    <link rel="stylesheet" href=""/>
            </head>
            <body>

                <!-- begin GAuth component -->
                <div id="GAuth-component">
                    <!-- begin login component-->
                    <div id="login-component" class="blueForm-basic">
        	     	   <input type="hidden" id="contextPath" value="/sso" />
                        <!-- begin login form -->
                        <div id="login-state-default">
                            <h2>Sign In</h2>

                            <form method="post" id="login-form">

                                <div class="form-alert">
                                    <div id="username-error" style="display:none;"></div>
                                    <div id="password-error" style="display:none;"></div>
                                </div>
                                <div class="textfield">
        							<label for="username">Email</label>
                                   		<!-- If the lockToEmailAddress parameter is specified then we want to mark the field as readonly,
                                   		preload the email address, and disable the other input so that null isn't sent to the server. We'll
                                   		also style the field to have a darker grey background and disable the mouse pointer
                                   		 -->

        								<!-- If the lockToEmailAddress parameter is NOT specified then keep the existing functionality and disable the readonly input field
        							     -->
        							    <input class="login_email" name="username" id="username" value="" type="email" spellcheck="false" autocorrect="off" autocapitalize="off"/>

                                </div>

                                <div class="textfield">
                                    <label for="password">Password</label>
                                    <a id="loginforgotpassword" class="login-forgot-password" style="cursor:pointer">(Forgot?)</a>
                                    <input type="password" name="password" id="password" spellcheck="false" autocorrect="off" autocapitalize="off" />
                                     <strong id="capslock-warning" class="information" title="Caps lock is on." style="display: none;">Caps lock is on.</strong>
        					    </div>
                                <input type="hidden" name="embed" value="true"/>
                                <input type="hidden" name="_csrf" value="TEST_CSRF_VALUE" />
                                <button type="submit" id="login-btn-signin" class="btn1" accesskey="l">Sign In</button>
                                <!-- The existence of the "rememberme" parameter at all will remember the user! -->
                            </form>
                        </div>
                        <!-- end login form -->

                        <!-- begin Create Account message -->
        	            <div id="login-create-account">

        	            </div>
        	            <!-- end Create Account message -->

        	            <!-- begin Social Sign In component -->
        	            <div id="SSI-component">

        	            </div>
        	            <!-- end Social Sign In component -->
                        <div class="clearfix"></div> <!-- Ensure that GAuth-component div's height is computed correctly. -->
                    </div>
                    <!-- end login component-->

        		</div>
        		<!-- end GAuth component -->
        </html>
    """.trimIndent()

    private val htmlForTicket = """
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

    @Before
    fun setUp() {
        server.start()
        api = GarminSSO.client(server.url("/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Get CSRF`() = runTest{
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(htmlForCSRF)
        server.enqueue(response)

        val res = api.getCSRF()

        val request = server.takeRequest()

        assertThat(request.method).isEqualTo("GET")
        assertThat(request.requestUrl?.toUrl()?.path.toString()).isEqualTo("/sso/signin")
        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(CSRF("TEST_CSRF_VALUE"))
    }

    @Test
    fun `Get CSRF - failure`() = runTest{
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        server.enqueue(response)

        val res = api.getCSRF()

        assertThat(res.isSuccessful).isFalse()
        assertThat(res.body()).isNull()
    }

    @Test
    fun `Get Ticket`() = runTest{
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(htmlForTicket)
        server.enqueue(response)

        val res = api.login(username = "user", password = "pass", csrf = CSRF("csrf"))

        val request = server.takeRequest()

        assertThat(request.method).isEqualTo("POST")
        assertThat(request.requestUrl?.toUrl()?.path).isEqualTo("/sso/signin")
        assertThat(request.body.toString()).isEqualTo("[text=username=user&password=pass&_csrf=csrf&embed=true]")
        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(Ticket("TEST_TICKET_VALUE"))
    }

    @Test
    fun `Get Ticket - failure`() = runTest{
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        server.enqueue(response)

        val res = api.login(username = "user", password = "pass", csrf = CSRF("csrf"))

        assertThat(res.isSuccessful).isFalse()
        assertThat(res.body()).isNull()
    }
}