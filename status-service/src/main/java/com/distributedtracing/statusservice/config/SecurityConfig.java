//package com.distributedtracing.statusservice.config;
//
//import lombok.AllArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@AllArgsConstructor
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//  private final PasswordEncoder passwordEncoder;
//
//  @Override
//  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    auth.inMemoryAuthentication();
////        .withUser("testuser")
//////        .password(passwordEncoder().encode("testPass"))
////        .password("password")
////        .roles("STUDENT");
////        .authorities("USER");
////        .and()
////        .withUser("testAdmin")
////        .password(passwordEncoder().encode("testPass"))
////        .roles("ADMIN_ROLE");
//  }
//
//  @Override
//  protected void configure(HttpSecurity http) throws Exception {
//    http
//        .authorizeRequests()
//        .antMatchers("/**/success").hasRole("STUDENT")
//        .antMatchers("/**/fail").hasRole("ADMIN")
////        .anyRequest()
////        .authenticated()
//        .and()
//        .httpBasic();
//  }
//
////  @Bean
////  public PasswordEncoder passwordEncoder() {
////    return new BCryptPasswordEncoder();
////  }
//
////  @Override
////  @Bean
////  protected UserDetailsService userDetailsService() {
////    UserDetails testUser = User.builder()
////        .username("testuser")
////        .password(passwordEncoder.encode("password"))
////        .roles("STUDENT")
////        .build();
////    UserDetails testAdmin = User.builder()
////        .username("testadmin")
////        .password(passwordEncoder.encode("password"))
////        .roles("ADMIN")
////        .build();
////    return new InMemoryUserDetailsManager(testUser, testAdmin);
////  }
//}
