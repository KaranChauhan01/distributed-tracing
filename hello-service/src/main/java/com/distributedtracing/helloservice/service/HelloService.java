package com.distributedtracing.helloservice.service;

import com.distributedtracing.helloservice.model.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class HelloService {

  private WebClient webClient;

  public Mono<ResponseEntity<ResponseMessage>> getStatusServiceResponse(String authHeader) {

    UUID tracer = UUID.randomUUID();

    // start log timer for success
    log.debug("'tracer' is " + tracer);
    log.debug("Making a call to /success endpoint");
    long successStartTime = System.currentTimeMillis();
    String TRACER = "tracer";
    Mono<ResponseMessage> successMono =
        webClient
            .get()
            .uri("/api/v1/success")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authHeader)
            .header(TRACER, tracer.toString())
            .exchangeToMono(response -> {
              long currentTime = System.currentTimeMillis();
              String logMessage = "\nStatusCode: {} \nExecution time: {} milliseconds \nTracer: {}";
              if(response.statusCode().is4xxClientError()) {
                log.error("Call to /api/v1/success endpoint failed." + logMessage, response.statusCode(), (currentTime - successStartTime), tracer);
              } else if(response.statusCode().is2xxSuccessful())
                log.debug("Call to /api/v1/success endpoint completed." + logMessage, response.statusCode(), (currentTime - successStartTime), tracer);
              return response.bodyToMono(ResponseMessage.class);
            });

    // start log timer for failure
    log.debug("Making a call to /fail endpoint");
    long failureStartTime = System.currentTimeMillis();
    Mono<ResponseMessage> failureMono =
        webClient
            .get()
            .uri("/api/v1/fail")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authHeader)
            .header(TRACER, tracer.toString())
            .exchangeToMono(response -> {
              long currentTime = System.currentTimeMillis();
              String logMessage = "\nStatusCode: {} \nExecution time: {} milliseconds \nTracer: {}";
              if(response.statusCode().is4xxClientError()) {
                log.error("Call to /api/v1/fail endpoint failed." + logMessage, response.statusCode(), (currentTime - failureStartTime), tracer);
              } else if(response.statusCode().is2xxSuccessful())
                log.debug("Call to /api/v1/fail endpoint completed." + logMessage, response.statusCode(), (currentTime - failureStartTime), tracer);
              return response.bodyToMono(ResponseMessage.class);
            });

    return Mono.zip(successMono, failureMono)
      .map(tuple -> {
        ResponseMessage successResp = tuple.getT1();
        ResponseMessage failureResp = tuple.getT2();
        if (successResp.getMessage() == null || failureResp.getMessage() == null)
          return new ResponseEntity<>(new ResponseMessage("Access Denied"), HttpStatus.FORBIDDEN);
        else
          return new ResponseEntity<>(new ResponseMessage(successResp.getMessage()), HttpStatus.OK);
      });
  }
}
