package com.quynhtadinh.finalexample.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.quynhtadinh.finalexample.security.CustomOAuth2UserService;
import com.quynhtadinh.finalexample.security.CustomOidcUserService;

@Configuration
@EnableWebSecurity
@EnableJpaRepositories("com.quynhtadinh.finalexample.repository")
@ComponentScan("com.quynhtadinh.finalexample")
public class WebSecurityConfig {

	@Autowired
	private AuthenticationConfiguration authConfiguration;

	@Autowired
	private CustomOidcUserService customOidcUserService;

	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf().disable();

		// Các trang công khai: trang chủ, thực đơn/sản phẩm, tìm kiếm, đăng nhập/đăng ký, tài nguyên tĩnh
		http.authorizeRequests().antMatchers(
				"/", "/index", "/products", "/detail", "/search", "/stores",
				"/login", "/registration", "/oauth2/**", "/login/oauth2/**",
				"/assets/**", "/css/**", "/js/**", "/images/**", "/resources/**")
				.permitAll();

		// Khu vực quản trị: chỉ ROLE_ADMIN
		http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN");

		// Giỏ hàng / đặt hàng: yêu cầu đăng nhập
		http.authorizeRequests().antMatchers(
				"/carts", "/add-to-cart", "/delete-cart", "/update-cart",
				"/checkout", "/prepare-shipping").authenticated();

		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

		http.authorizeRequests().anyRequest().authenticated()
				.and().formLogin()
					.loginPage("/login")
					.defaultSuccessUrl("/")
					.permitAll()
				.and().oauth2Login()
					.loginPage("/login")
					.defaultSuccessUrl("/")
					.userInfoEndpoint()
						.oidcUserService(customOidcUserService)
						.userService(customOAuth2UserService)
					.and()
				.and().logout().permitAll();

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return authConfiguration.getAuthenticationManager();
	}

}
