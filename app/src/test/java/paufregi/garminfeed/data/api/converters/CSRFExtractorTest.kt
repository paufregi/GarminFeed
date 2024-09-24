package paufregi.garminfeed.data.api.converters

import com.google.common.truth.Truth.assertThat

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

import org.junit.Test
import paufregi.garminfeed.data.api.models.CSRF

class CSRFExtractorTest {

    private val converter = CSRFExtractor()

    private val html = """
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
    private val mediaType = "text/html; charset=UTF-8"

    @Test
    fun `Extract CSRF token`() {
        val responseBody = html.toResponseBody(mediaType.toMediaType())

        val result = converter.convert(responseBody)

        assertThat(result).isEqualTo(CSRF("TEST_CSRF_VALUE"))
    }

    @Test
    fun `No CSRF token`() {
        val responseBody = "".toResponseBody(mediaType.toMediaType())

        val result = converter.convert(responseBody)

        assertThat(result).isEqualTo(CSRF(""))
    }
}