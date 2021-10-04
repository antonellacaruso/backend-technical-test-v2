package com.tui.proof.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
			.csrf().disable()	
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/public/login/").anonymous()
                .antMatchers("/orders/pilotes/getOrdersByClient/").hasAuthority("ADMIN");
                
                //.anyRequest().authenticated();  
			http.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
			http.headers().cacheControl();
		}
		
		  @Override
		  public void configure(WebSecurity web) {
		    	System.out.println("sfsd!");
		  	web.ignoring().antMatchers(HttpMethod.POST, "/api/pilotes/update/");
		  	web.ignoring().antMatchers(HttpMethod.POST, "/api/pilotes/place/");
		  	web.ignoring().antMatchers(HttpMethod.POST, "/api/pilotes/delete/");
			web.ignoring().antMatchers(HttpMethod.POST, "/public/login/");

		    }




}