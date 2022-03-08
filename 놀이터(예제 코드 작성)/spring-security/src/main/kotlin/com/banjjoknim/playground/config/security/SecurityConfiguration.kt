package com.banjjoknim.playground.config.security

import com.banjjoknim.playground.domain.auth.OAuth2Type
import com.banjjoknim.playground.domain.user.User
import com.banjjoknim.playground.domain.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

/**
 * OAuth2의 경우, 로그인이 완료된 뒤의 후처리가 필요하다.
 *
 * 1. 코드받기(인증), 2. 액세스토큰(권한) 얻기, 3. 액세스 토큰으로 사용자 정보 얻기
 *
 * 구글과 페이스북 로그인의 경우 코드가 필요 없다.
 *
 * 구글과 페이스북 측에서 우리에게 보내는 Request에 액세스 토큰과 사용자 정보등의 OAUth2 정보가 모두 포함되어 있다.
 *
 * 하지만 네이버, 카카오는 스프링 부트에서 기본적인 정보를 제공하지 않기 때문에 따로 해당 정보를 제공하는 클래스를 작성해야 한다.
 *
 * 우리는 OAuth2-Client 라는 라이브러리를 사용하고 있다.
 *
 * OAuth2-Client 라이브러리는 구글, 페이스북, 깃허브 등의 Provider를 기본적으로 제공해주지만, 네이버 카카오는 제공해주지 않는다.
 *
 * 이는 각 나라별로 OAuth2 를 지원해주는 서드 파티가 제공하는 attribute 가 모두 다르기 때문이다. 그래서 현실적으로 모든 곳을 지원해줄 수가 없다.
 *
 * OAuth2-Client 는 OAuth2ClientProperties 라는 클래스를 통한 자동 설정을 지원해주고 있다.
 *
 * OAuth2는 여러가지 방식이 있다. Authorization Code Grant Type 방식 등등..
 *
 * @see org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
 * @see org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter
 * @see org.springframework.security.config.oauth2.client.CommonOAuth2Provider
 */
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록되도록 해준다.
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 스프링 시큐리티 관련 특정 어노테이션에 대한 활성화 설정을 할 수 있다.
class SecurityConfiguration(val userRepository: UserRepository) : WebSecurityConfigurerAdapter() {

    @Bean // passwordEncoder() 메서드에서 리턴해주는 PasswordEncoder 를 스프링 빈으로 등록한다.
    fun passwordEncoder(): PasswordEncoder { // Security 로 로그인을 하려면 비밀번호는 암호화되어 있어야 하므로 PasswordEncoder 가 필요하다.
        return BCryptPasswordEncoder()
    }

    /**
     * application.yml 의 spring.security.oauth2.client.registration 에 대한 설정이 없을 경우,
     *
     * 이 메서드를 통해 스프링 빈으로 등록된 OAuth2UserService 를 아래의 configure 메서드에 OAuth2UserService 로써 Security filter chain 에 등록하려고 하면 아래의 예외가 발생한다.
     *
     * 'Method springSecurityFilterChain in org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration required a bean of type 'org.springframework.security.oauth2.client.registration.ClientRegistrationRepository' that could not be found.'
     *
     * 따라서 application.yml 의 spring.security.oauth2.client.registration 에 대한 설정을 반드시 등록해주어야 한다.
     *
     * 아래는 그 예시다.
     *
     * application.yml
     *
     * ```
     * spring:
     *   security:
     *     oauth2:
     *       client:
     *         registration:
     *           google:
     *             client-id: my-client-id
     *             client-secret: my-client-secret
     * ```
     *
     * 단, 네이버 카카오는 스프링 시큐리티에서 지원해주지 않으므로 따로 설정을 작성해주어야 한다.
     */
    @Bean
    fun oauth2UserService(): OAuth2UserService<OAuth2UserRequest, OAuth2User> {
        return PrincipalOAuth2UserService(passwordEncoder(), userRepository)
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.authorizeRequests() // 인증만 되면 들어갈 수 있는 주소 설정
            .antMatchers("/user/**").hasRole("USER")
            .antMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
            .antMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().permitAll() // 위에서 명시한 주소 외에는 모든 접근을 허용한다.
            .and()
            .formLogin()
            .loginPage("/loginPage")
            .usernameParameter("username") // 기본적으로 인증을 위해 사용자를 찾을 때 username 을 사용하는데, 이에 사용되는 파라미터 이름을 바꿔주고 싶을때 사용한다.
            .loginProcessingUrl("/login") // /login url이 호출되면 Security 가 요청을 낚아채서 대신 로그인을 진행해준다.
            .defaultSuccessUrl("/") // loginPage 의 url을 통해서 로그인을 하면 / 로 보내줄건데, 특정 페이지로 요청해서 로그인하게 되면 그 페이지를 그대로 보여주겠다는 의미.
            .and()
            .oauth2Login()
            .loginPage("/loginPage")
            .loginProcessingUrl("/login")
            .userInfoEndpoint()
            .userService(oauth2UserService())
    }
}

/**
 * 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행해준다.
 *
 * 로그인 진행이 완료되면 시큐리티 session을 만들어준다 (Security ContextHolder)
 *
 * Security ContextHolder에 들어갈 수 있는 객체 타입은 Authentication 이다.
 *
 * Authentication 객체 안에는 User 정보가 있어야 한다. 이때 User 객체 타입은 UserDetails 이다.
 *
 * Security Session -> Authentication -> UserDetails
 *
 * Security ContextHolder 내부에 Authentication이 있고, 그 속에 UserDetails가 있는 형태.
 *
 * ```kotlin
 * ex) 1. SecurityContextHolder.getContext().authentication.details
 *     2. SecurityContextHolder.getContext().authentication.principal
 *     3. SecurityContextHolder.getContext().authentication.authorities
 *     4. ...
 * ```
 */
class PrincipalDetails(
    val user: User // 컴포지션. 일반 로그인시 사용하는 생성자
) : UserDetails, OAuth2User {

    private var _attributes: MutableMap<String, Any> = mutableMapOf()

    // OAuth2 로그인시 사용하는 생성자
    constructor(user: User, attributes: Map<String, Any>) : this(user) {
        this._attributes = attributes.toMutableMap()
    }

    override fun getName(): String {
        return "someName" // 크게 중요하지 않다...
    }

    override fun getAttributes(): Map<String, Any> {
        return _attributes.toMap()
    }

    // 해당 User 의 권한을 반환하는 함수
    override fun getAuthorities(): Collection<out GrantedAuthority> {
        return listOf(GrantedAuthority { user.role })
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}


/**
 * ```
 * 스프링 시큐리티 설정에서 loginProcessingUrl("/login") 설정을 해주었기 때문에
 *
 * /login url로 요청이 오면 자동으로 시큐리티가 로그인 과정을 낚아챈다.
 *
 * 이때 UserDetailsService 타입으로 등록되어 있는 빈을 찾아서 해당 빈에 정의된 loadUserByUsername() 을 실행한다.
 * ```
 *
 * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider
 * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
 */
@Service
class PrincipalDetailService(private val userRepository: UserRepository) : UserDetailsService {
    /**
     * ```
     * 스프링 시큐리티 세션 내부에 Authentication 객체를 넣어준다. 그리고 Authentication 객체 속에는 UserDetails 객체가 들어있다.
     *
     * 추가로, 이 함수가 종료될 때 @AuthenticationPrincipal 어노테이션이 만들어진다.
     * ```
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val user = (userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("can not found user by username. username: $username"))
        return PrincipalDetails(user) // PrincipalDetails 객체가 스프링 시큐리티 세션 정보에 들어가게 된다.
    }
}

/**
 * ```
 * 구글, 페이스북 등등 OAuth2 를 이용해서 받은 userRequest 데이터에 대한 후처리를 해주는 함수를 정의하는 서비스
 *
 * 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인 완료 -> 구글에서 code 리턴 ->OAuth-Client 라이브러리가 받아서 AccessToken 요청
 *
 * OAuth2UserRequest 정보를 이용해서 loadUser 함수 호출 -> 구글로부터 회원프로필을 받아준다.
 * ```
 *
 * @see org.springframework.security.oauth2.client.userinfo.OAuth2UserService
 * @see org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
 * @see org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
 */
@Service
class PrincipalOAuth2UserService(
    val passwordEncoder: PasswordEncoder,
    val userRepository: UserRepository
) :
    DefaultOAuth2UserService() { // OAuth2 로그인의 후처리를 담당한다.

    /**
     * ```
     * 구글, 페이스북 등으로부터 받은 userRequest 데이터에 대한 후처리를 진행해주는 함수
     *
     * 추가로, 이 함수가 종료될 때 @AuthenticationPrincipal 어노테이션이 만들어진다.
     * ```
     */
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        println("${userRequest.clientRegistration}")
        println("${userRequest.accessToken}")
//        println("${userRequest.attributes}") // 5.1 버전 이전일 경우.
        println("${userRequest.additionalParameters}") // 5.1 버전 이후일 경우.

        // 강제로 회원가입 진행
        val oAuth2Type = OAuth2Type.findByProvider(userRequest.clientRegistration.registrationId)
        val oAuth2User = super.loadUser(userRequest)
        val oAuth2UserInfo = oAuth2Type.createOAuth2UserInfo(oAuth2User.attributes)

        val provider = oAuth2UserInfo.getProvider() // 값의 유무로 일반 로그인, OAuth2 로그인을 구분한다.
        val providerId = oAuth2UserInfo.getProviderId()
        val username = "${provider}_${providerId}" // OAuth2 로 로그인시, 필요 없지만 그냥 만들어준다.
        val password = passwordEncoder.encode("비밀번호") // OAuth2 로 로그인시, 필요 없지만 그냥 만들어준다.
        val email = oAuth2UserInfo.getEmail()
        val role = "ROLE_USER"

        // 회원가입 여부 확인 및 저장
        var user = userRepository.findByUsername(username)
        require(user != null) { "이미 자동으로 회원가입이 되어 있습니다." }
        user = User(
            username = username,
            password = password,
            email = email,
            role = role,
            provider = provider,
            providerId = providerId
        )
        userRepository.save(user) // 회원정보 저장

        return PrincipalDetails(user, oAuth2User.attributes) // PrincipalDetails 객체가 스프링 시큐리티 세션 정보에 들어가게 된다.
    }
}
