package com.distributedtracing.helloservice.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@AllArgsConstructor
@Slf4j
public class WebClientConfig {

  private Environment env;

  @Bean
  WebClient getWebClient() {
    String host = env.getProperty("STATUS_SERVICE_HOST","localhost");
    String port = env.getProperty("STATUS_SERVICE_PORT","8082");
    String url = "http://" + host + ":" + port;
    log.debug("Destination url: {}", url);
    return WebClient.create(url);
  }
}
