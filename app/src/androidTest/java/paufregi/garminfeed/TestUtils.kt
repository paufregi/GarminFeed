package paufregi.garminfeed

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import paufregi.garminfeed.data.api.models.OAuth2
import paufregi.garminfeed.test.R
import java.util.Date


fun createOAuth2(expiresAt: Date) = OAuth2(
    scope = "SCOPE",
    jti = "JTI",
    accessToken = JWT.create().withExpiresAt(expiresAt).sign(Algorithm.none()),
    tokenType = "TOKEN_TYPE",
    refreshToken = "REFRESH_TOKEN",
    expiresIn = 0,
    refreshTokenExpiresIn = 0
)

//1 Day  : 1000 * 60 * 60 * 24 milliseconds
val tomorrow = Date(Date().time + (1000 * 60 * 60 * 24))

val htmlForCSRF = """
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

val htmlForTicket = """
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

const val connectPort = 8081
const val garminSSOPort = 8082
const val garthPort = 8083

fun loadRes(res: Int): String =
    getInstrumentation().context.resources.openRawResource(res).bufferedReader().use { it.readText() }

var sslSocketFactory = HandshakeCertificates.Builder()
    .heldCertificate(HeldCertificate.decode(loadRes(R.raw.server)))
    .build().sslSocketFactory()

