package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@SpringBootApplication
@RestController
public class DemoApplication {
	
  @Autowired
  private RequestRepo service;
	
  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @GetMapping("/")
  public String hello() {
    return "hello world!";
  }
  
  @PostMapping("/requests")
  public String saveRequst(Request req) {
  	service.save(req);
  }
  
  @GetMapping("/requests/{requestId}")
  public Request getRequest(@PathVariable("requestId") Long reqId) {
    service.get(reqId);
  }
  
}
