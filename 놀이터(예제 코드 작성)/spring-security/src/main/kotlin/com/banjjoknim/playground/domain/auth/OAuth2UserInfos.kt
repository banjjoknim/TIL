package com.banjjoknim.playground.domain.auth

class GoogleUserInfo(
    /**
     * DefaultOAuth2Service#loadUser(OAuth2UserRequest)
     * ```kotlin
     * val oAuth2User = super.loadUser(userRequest)
     * val attributes = oAuth2User.attributes
     * ```
     */
    private val attributes: Map<String, Any?>
) : OAuth2UserInfo {

    override fun getProviderId(): String {
        return attributes["sub"] as String
    }

    override fun getProvider(): String {
        return "google"
    }

    override fun getEmail(): String {
        return attributes["email"] as String
    }

    override fun getName(): String {
        return attributes["name"] as String
    }
}

class FacebookUserInfo(
    /**
     * DefaultOAuth2Service#loadUser(OAuth2UserRequest)
     * ```kotlin
     * val oAuth2User = super.loadUser(userRequest)
     * val attributes = oAuth2User.attributes
     * ```
     */
    private val attributes: Map<String, Any?>
) : OAuth2UserInfo {
    override fun getProviderId(): String {
        return attributes["id"] as String
    }

    override fun getProvider(): String {
        return "facebook"
    }

    override fun getEmail(): String {
        return attributes["email"] as String
    }

    override fun getName(): String {
        return attributes["name"] as String
    }

}
