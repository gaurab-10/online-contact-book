package com.smart.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.smart.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(this.userDetailsService)
		.passwordEncoder(passwordEncoder()); // to match password with`
											// the database one which is encoded.
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		
		http.csrf().disable() // `if it is not disabled then we cannot access the post method of restController``
				.authorizeRequests()
				.antMatchers("/user/**").hasRole("NORMAL")
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/**").permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/dologin")
		     	.defaultSuccessUrl("/default")
				;
				
		     //	.failureUrl("/login");``
	}
	
	

}
