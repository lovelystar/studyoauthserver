package com.hard.study.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
public class OAuthServerOAuth2Config extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;
	
	@Autowired
	private AuthorizationCodeServices authorizationCodeServices;
	
	@Autowired
	private ClientDetailsService clientDetailsService;
	
	@Autowired
	private ApprovalStore approvalStore;
	
	// DB에 토큰 저장, 조회 등
	@Bean
	public JdbcTokenStore tokenStore() {
		
		return new JdbcTokenStore(dataSource);
		
	}
	
	// 토큰을 세팅한다.
	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		
		return defaultTokenServices;
		
	}
	
	// 토큰 발급 시 보안 관리
	// "/oauth/token_key"와 "/oauth/check_token"의 접근 권한 설정
	@Override
	public void configure(AuthorizationServerSecurityConfigurer authServer) throws Exception {
		
		authServer
			.tokenKeyAccess("permitAll()") // 모두 접근 가능 << /oauth/token_key
			.checkTokenAccess("isAuthenticated()"); // << /oauth/check_token
		
	}
	
	// 토큰 발급 부분 설정
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		endpoints
			.tokenStore(tokenStore()) // tokenStore()로 토큰 발급
			.reuseRefreshTokens(false) // refreshToken 재사용 x ( grant_type = refresh_token 호출한 후 refresh_token 변경하고자 할 때 사용 )
			.approvalStore(approvalStore)
			.accessTokenConverter(accessTokenConverter)
			.authenticationManager(authenticationManager)
			.authorizationCodeServices(authorizationCodeServices); // oauth_code 테이블
		
	}
	
	// 클라이언트 정보 가져올 때 사용.
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {		
		
		clients
			.withClientDetails(clientDetailsService);		
			//.jdbc(dataSource).passwordEncoder(passwordEncoder)
			
//			clients
//				.inMemory()
//					// This client where client secret can be kept safe (e.g. server side)
//					.withClient("confidential")
//					.secret(passwordEncoder.encode("secret"))
//					.authorizedGrantTypes("client_credentials", "authorization_code", "refresh_token")
//					.scopes("read", "write")
//					.redirectUris("http://localhost:8080/client/callback")
//					.accessTokenValiditySeconds(60 * 60 * 24 * 7) // default value = 60 * 60 * 12 = 12 HOURS
//					.refreshTokenValiditySeconds(60 * 60 * 24 * 30) // default value = 60 * 60 * 24 * 30 = 30 DAYS
//				.and()
//					// Public client where client secret is vulnerable (e.g. mobile apps, browsers)
//					.withClient("public")
//					.secret("implicit")
//					.scopes("read")
//					.redirectUris("http://localhost:8080/client/callback")
//				.and()
//					.withClient("trusted")
//					.secret("secret")
//					.authorities("ROLE_TRUSTED_CLIENT")
//					.authorizedGrantTypes("client_credentials", "password", "authorization_code", "refresh_token")
//					.scopes("read", "write")
//					.redirectUris("http://localhost:8080/client/callback");
		
	}
	
}

/*
 * 인증방식의 종류
 * 1. Authorization Code Grant << 사용
 * - 일반적인 웹 사이트에서 소셜로그인과 같은 인증을 받을 때 가장 많이 사용
 * - 서버 사이드 코드가 필요하며 client / secret이 필요하다.
 * - 로그인 할 때 페이지에 response_type=code 라고 넘김
 * - 인증시 code와 state값을 받은 후에 access_token을 발급 받음
 * 
 * 2. Implicit Grant
 * - 토큰과 scope에 대한 스펙 등은 다르지만 OAuth 1.0a와 비슷한 인증방식
 * - 브라우저 기반의 어플리케이션이나 모바일 어플리케이션에서 사용
 * - client 증명서가 필요 없으며 OAuth 2.0에서 가장 많이 사용
 * - 로그인 할 때 페이지에 response_type=token 라고 넘김
 * - 과정이 줄어드는 대신 보안이 떨어지며 Authorizatino Code Grant의 간소화 ver.
 * 
 * 3. Resource Owner Password Credentials Grant
 * - Client에 아이디 / 패스워드를 저장해 두고 아이디 / 패스워드로 직접 access_token을 받아오는 방식
 * - Client를 믿을 수 없을 때에는 사용하기에 위험.
 * - 이 방식은 API서비스의 공식 App이나 믿을 수 있는 Client에 한해서만 사용
 * - 로그인 할 때 Api에 POST로 grant_type=password 라고 넘김
 * - mId / mPwd 같은 정보들을 이용해 access_token 발급
 * 
 * 4. Client Credentials Grant
 * - App이 Confidetial Client일 때 id와 secret을 가지고 인증
 * - 로그인 할 때 Api에 POST로 grant_type=client_credentials 라고 넘김
 * - 클라이언트가 곧 자원소유자
 * 
 * 5. Device Code Grant
 * - 브라우저가 없거나 제한된 장치에서 사용
 * 
 * 6. Refresh Token Grant << 사용
 * - 기존에 저장해둔 refresh token이 존재할 때 access_token 재발급
 * - 기존 access_token이 만료된다.
 * 
 * 
 * = OAuth 흐름 ( 순서 : A to Z ) =
 * 
 * Client = Resource를 직접 사용하는 사용자
 * Resource Owner = DB를 장악하고 있는 OAuth를 사용하는 사용자
 * Authorization Server = 인증서버
 * Resource Server = 자원서버
 * 
 * Client			Resource Owner		Authorization Server		Resource Server
 *    --------(A)------->
 *   Authorization Request
 *   
 *    <-------(B)--------
 *    Authorization Grant
 * 
 *    --------------------(C)------------------->
 *                Authorization Grant
 * 
 *    <-------------------(D)--------------------
 *                    Code & State
 * 
 *    --------------------(E)------------------->
 *                    Code & State
 * 
 *    <-------------------(F)--------------------
 *                    Access Token
 *    
 *    --------------------------------(G)------------------------------->
 *                                Access Token
 *    
 *    <-------------------------------(E)--------------------------------
 *                             Protected Resource
 *    
 *    
 *    
 *    
 * (1) Client가 어떤 버튼 클릭
 * (2) 버튼 누르면 로그인 창이 나온다. - 로그인 진행
 * (3) 인증서버에서 인증을 받는다.
 * (4) 인증된 사용자는 Access Token발급 ( Code & State는 Authorization Code Grant만 )
 * (5) 발급받은 Access Token으로 자원서버 접근
 * (6) Access Token이 만료되면 Refresh Token으로 재발급
 * 
 */