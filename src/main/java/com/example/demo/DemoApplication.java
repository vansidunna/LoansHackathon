package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
public class DemoApplication {
	
  @Autowired
  private LoanRequestRepo service;
	
  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @PostMapping("/requests")
  public ResponseEntity<?> saveRequest(@RequestBody LoanRequest req) {
  	service.save(req);
  	return ResponseEntity.noContent().build();
  }

  @GetMapping("/requests")
  public List<LoanRequest> getAll() {
    return service.getAll();
  }

  @GetMapping("/requests/{requestId}")
  public ResponseEntity<LoanRequest> getRequest(@PathVariable("requestId") Long reqId) {
    return service.get(reqId)
            .map(ResponseEntity::<LoanRequest>ok)
            .orElse(ResponseEntity.notFound().build());
  }
  
}
