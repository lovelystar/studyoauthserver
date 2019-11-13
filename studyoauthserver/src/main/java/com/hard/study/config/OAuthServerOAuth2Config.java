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
			.tokenKeyAccess("permitAll()") // 모두 접근 가능
			.checkTokenAccess("isAuthenticated()");
		
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
