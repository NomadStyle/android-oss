package com.kickstarter.services.interceptors

import com.google.firebase.iid.FirebaseInstanceId
import com.kickstarter.libs.Build
import com.kickstarter.libs.CurrentUserType
import com.kickstarter.libs.utils.WebUtils
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

/**
 * Headers specific for the GraphQL client
 * see @see <a href="https://square.github.io/okhttp/interceptors/">https://square.github.io/okhttp/interceptors/</a>
 */
class GraphQLInterceptor(private val clientId: String,
                         private val currentUser: CurrentUserType,
                         private val build: Build) : Interceptor {
    override fun intercept(chain: Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder().method(original.method, original.body)

        this.currentUser.observable()
                .subscribe {
                    builder.addHeader("Authorization", "token " + this.currentUser.accessToken)
                }

        builder.addHeader("User-Agent", WebUtils.userAgent(this.build))
                .addHeader("X-KICKSTARTER-CLIENT", this.clientId)
                .addHeader("Kickstarter-Android-App-UUID", FirebaseInstanceId.getInstance().id)

        return chain.proceed(builder.build())
    }
}
