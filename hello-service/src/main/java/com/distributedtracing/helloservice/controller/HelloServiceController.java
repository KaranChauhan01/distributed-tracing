package com.distributedtracing.helloservice.controller;

import com.distributedtracing.helloservice.model.ResponseMessage;
import com.distributedtracing.helloservice.service.HelloService;
import com.distributedtracing.helloservice.util.LogHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/hello")
@AllArgsConstructor
@Slf4j
public class HelloServiceController {

  private HelloService service;

  @GetMapping
  public Mono<ResponseEntity<ResponseMessage>> hello(@RequestHeader("Authorization") String authHeader) {
    return service.getStatusServiceResponse1(authHeader).doOnEach(LogHelper.logOnNext(
        res -> log.debug("Successfully executed the request on /hello endpoint")
    ));
  }
}
