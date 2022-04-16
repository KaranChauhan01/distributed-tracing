package com.distributedtracing.statusservice.service;

import com.distributedtracing.statusservice.model.ResponseMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StatusService {

  public Mono<ResponseMessage> getResponse() {
    return Mono.just(new ResponseMessage("Hello there, have a nice day!!"));
  }
}
