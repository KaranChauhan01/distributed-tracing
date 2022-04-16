package com.distributedtracing.statusservice.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Slf4j
@AllArgsConstructor
public class WebfluxSecurityConfig {

  private final PasswordEncoder passwordEncoder;

  @Bean
  public MapReactiveUserDetailsService userDetailsService() {
    UserDetails user = User
        .withUsername("testUser")
        .password(passwordEncoder.encode("password"))
        .roles("USER")
        .build();
    UserDetails admin = User
        .withUsername("testAdmin")
        .password(passwordEncoder.encode("password"))
        .roles("ADMIN", "USER")
        .build();
    return new MapReactiveUserDetailsService(user, admin);
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
        .authorizeExchange()
        .pathMatchers("/api/v1/success").hasRole("USER")
        .pathMatchers("/api/v1/fail").hasRole("ADMIN")
        .anyExchange()
        .authenticated()
        .and()
        .httpBasic()
        .authenticationEntryPoint((exchange, exception) -> {
           String url = exchange.getRequest().getPath().value();
           String tracer = getTracer(exchange.getRequest());
           return exceptionHandler(exchange.getResponse(), tracer, url, HttpStatus.UNAUTHORIZED, exception.getMessage());
        })
        .and()
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .exceptionHandling()
        .accessDeniedHandler((exchange, exception) -> {
          String url = exchange.getRequest().getPath().value();
          String tracer = getTracer(exchange.getRequest());
          return exceptionHandler(exchange.getResponse(), tracer, url, HttpStatus.FORBIDDEN, exception.getMessage());
        })
        .and()
        .build();
  }

  private String getTracer(ServerHttpRequest request) {
    var tracerHeader = request.getHeaders().getOrEmpty("tracer");
    String tracer;
    if(!tracerHeader.isEmpty())
      tracer = tracerHeader.get(0);
    else
      tracer = "";
    return tracer;
  }

  private Mono<Void> exceptionHandler(ServerHttpResponse exchangeResponse, String tracer, String url, HttpStatus status, String exceptionMessage) {
    return Mono.defer(() -> Mono
          .just(exchangeResponse)
          .flatMap((response) -> {
            log.error(
                """
                    Url: {}
                    ReasonCode: {}
                    Reason: {}
                    Tracer: {}""",
                url, status, exceptionMessage, tracer
            );
            response.setStatusCode(status);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            String s = "{\"message\": \"" + exceptionMessage + "\"}";
            DataBuffer buffer = dataBufferFactory.wrap(s.getBytes());
            return response.writeWith(Mono.just(buffer)).doOnError((error) -> DataBufferUtils.release(buffer));
          }));
  }


}
