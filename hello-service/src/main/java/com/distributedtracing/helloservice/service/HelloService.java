package com.distributedtracing.helloservice.service;

import com.distributedtracing.helloservice.model.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.MDC.MDCCloseable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.Optional;
import static com.distributedtracing.helloservice.model.Constants.CONTEXT_MAP;
import static com.distributedtracing.helloservice.model.Constants.TRACER;

@Service
@AllArgsConstructor
@Slf4j
public class HelloService {

  private WebClient webClient;

  public Mono<ResponseEntity<ResponseMessage>> getStatusServiceResponse1(String authHeader) {
    return Mono.deferContextual(contextView -> {
      Optional<Map<String, String>> contextMap = contextView.getOrEmpty(CONTEXT_MAP);
      String tracer = contextMap.get().get(TRACER);
      MDCCloseable closeable = MDC.putCloseable(TRACER, tracer);
      long successStartTime = System.currentTimeMillis();
      log.debug("Making a call to /success endpoint of Status Service");
      Mono<ResponseMessage> successMono =
          webClient
              .get()
              .uri("/api/v1/success")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .header(HttpHeaders.AUTHORIZATION, authHeader)
              .header(TRACER, tracer)
              .exchangeToMono(response -> {
                long currentTime = System.currentTimeMillis();
                String logMessage = "\nStatusCode: {} \nExecution time: {} milliseconds \nTracer: {}";
                if(response.statusCode().is4xxClientError()) {
                  log.error("Call to /api/v1/success endpoint failed." + logMessage, response.statusCode(), (currentTime - successStartTime), tracer);
                } else if(response.statusCode().is2xxSuccessful())
                  log.debug("Call to /api/v1/success endpoint completed." + logMessage, response.statusCode(), (currentTime - successStartTime), tracer);
                return response.bodyToMono(ResponseMessage.class);
              });
      long failureStartTime = System.currentTimeMillis();
      log.debug("Making a call to /fail endpoint of Status Service");
      Mono<ResponseMessage> failureMono =
          webClient
              .get()
              .uri("/api/v1/fail")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .header(HttpHeaders.AUTHORIZATION, authHeader)
              .header(TRACER, tracer)
              .exchangeToMono(response -> {
                long currentTime = System.currentTimeMillis();
                String logMessage = "\nStatusCode: {} \nExecution time: {} milliseconds \nTracer: {}";
                if(response.statusCode().is4xxClientError()) {
                  log.error("Call to /api/v1/fail endpoint failed." + logMessage, response.statusCode(), (currentTime - failureStartTime), tracer);
                  return Mono.just(new ResponseMessage());
                } else if(response.statusCode().is2xxSuccessful())
                  log.debug("Call to /api/v1/fail endpoint completed." + logMessage, response.statusCode(), (currentTime - failureStartTime), tracer);
                return response.bodyToMono(ResponseMessage.class);
              });

      return Mono.zip(successMono, failureMono)
          .map(tuple -> {
            closeable.close();
            ResponseMessage successResp = tuple.getT1();
            ResponseMessage failureResp = tuple.getT2();
            if (successResp.getMessage() == null || failureResp.getMessage() == null)
              return new ResponseEntity<>(new ResponseMessage("Access Denied"), HttpStatus.FORBIDDEN);
            else
              return new ResponseEntity<>(new ResponseMessage(successResp.getMessage()), HttpStatus.OK);
          });
    });
  }

}
