package com.banjjoknim.playground.domain.auth

enum class OAuth2Type(
    private val provider: String,
    private val createUserInfo: (attributes: Map<String, Any?>) -> OAuth2UserInfo
) {
    GOOGLE("google", { attributes -> GoogleUserInfo(attributes) }),
    FACEBOOK("facebook", { attributes -> FacebookUserInfo(attributes) }),
    NAVER("naver", { attributes -> NaverUserInfo(attributes) });

    fun createOAuth2UserInfo(attributes: Map<String, Any?>): OAuth2UserInfo {
        return createUserInfo(attributes)
    }

    companion object {
        fun findByProvider(provider: String): OAuth2Type {
            return values()
                .find { oAuth2Type -> oAuth2Type.provider == provider }
                ?: throw IllegalArgumentException("존재하지 않는 OAuth2 인증 타입입니다.")
        }
    }
}

