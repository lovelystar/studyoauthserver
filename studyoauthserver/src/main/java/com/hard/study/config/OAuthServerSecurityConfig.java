package com.hard.study.config;

import java.util.Arrays;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuthServerSecurityConfig extends WebSecurityConfigurerAdapter {
	
//	@Autowired
//	private OAuthServerCustomLogoutConfig customLogout;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private Environment env;
	
	// 사용자 인증 처리 방식 선택 ( inMemory || JDBC 등 )
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth
			
			//	.inMemoryAuthentication()
			//		.withUser("")
			//		.password("")
			//		.roles("USER")
			//	.and()
			//		.withUser("") ......;
			// 	OR
			//	.jdbcAuthentication();
			.userDetailsService(userDetailsManager())
			.passwordEncoder(passwordEncoder());
		
	}
	
	// 인증 매커니즘 커스텀
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
			.formLogin() // security 기본 로그인 페이지 사용
			.and()
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//					.deleteCookies("JSESSIONID")
						.clearAuthentication(true)
							.invalidateHttpSession(true)
								.logoutSuccessUrl("http://localhost:8082/studyoauthserver/login")
//					.logoutSuccessHandler(customLogout)
			.and()
			.httpBasic()
			.and()
			.anonymous() // 인증되지 않은 사용자를 허용하지 않겠다.
				.disable()
			.authorizeRequests() // 모든 요청은 인증 되어야만 한다.
				.anyRequest()
					.authenticated()
			.and()
			.rememberMe() // 로그인 정보 유지
				.rememberMeParameter("sumin-study-remember-me") // 쿠키의 명칭 지정
					.key("remember-me-key-2019-11") // 이 키를 가지고 잇는 쿠키만 인증
						.tokenValiditySeconds(96400) // 쿠키 만료시간
							.tokenRepository(persistentTokenRepository()) // 자동 로그인한 id, pwd 저장
			.and()
			.cors()
			.and()
			.csrf();
		
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		
		CorsConfiguration corsConfig = new CorsConfiguration();
		
		corsConfig.setAllowedOrigins(Arrays.asList(env.getProperty("config.oauth2.study-base-url"))); // http://localhost:8081 으로 오는 것을 허용
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedHeaders(Arrays.asList("Content-Type", "X-XSRF-TOKEN", "Authorization", "Content-Length", "X-Requested-With")); // Header
		corsConfig.setAllowedMethods(Arrays.asList("*")); // GET, POST 같은 것
		corsConfig.setMaxAge(3600L);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		
		source.registerCorsConfiguration("/**", corsConfig);
		
		return source;
		
	}
	
	// 로그인 정보 유지를 위해 id, pwd를 DB에 저장, 조회
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		
		JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource);
		
		return jdbcTokenRepository;
		
	}
	
	// Jwt 사용을 위해서 추가 ( 암호화 )
	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		
		KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new FileSystemResource("src/main/resources/oauth2key/oauth2jks_sumin.jks"), "sumin2019jks".toCharArray());
		
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setKeyPair(factory.getKeyPair("oauth2jks_sumin"));
		
		return converter;
		
	}
	
	// 인증
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		
		return super.authenticationManagerBean();
		
	}
	
	// code와 state값을 가져와서 토큰 발급
	@Bean
	protected AuthorizationCodeServices authorizationCodeServices() {
		
		return new JdbcAuthorizationCodeServices(dataSource);
		
	}
	
	// client 정보 가져올 때 사용
	@Bean
	@Primary
	public JdbcClientDetailsService jdbcClientDetailsService(DataSource dataSource) {
		
		return new JdbcClientDetailsService(dataSource);
		
	}
	
	// 리소스 소유자의 승인을 추가, 검색, 취소
	@Bean
	public ApprovalStore approvalStore() {
		
		return new JdbcApprovalStore(dataSource);
		
	}
	
	// 비밀번호 암+복호화
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
		
	}
	
	// client를 조회하고 업데이트 쿼리가 담겨있따.
	@Bean
	public JdbcUserDetailsManager userDetailsManager() {
		
		JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
		manager.setDataSource(dataSource);
		
		// mysql 8버전 이상은 `groups` 로 테이블명이 구분되지 않아서 에러 _ 재정의 필요
//		manager.setEnableGroups(true);
		
		return manager;
		
	}
	
}
