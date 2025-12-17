package com.apexauth.apexauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

  @GetMapping("/hello")
  public Map<String, Object> hello() {
    Map<String, Object> response = new HashMap<>();
    response.put("message", "ApexAuth is working!");
    response.put("timestamp", Instant.now().toString());
    response.put("status", "OK");
    return response;
  }
}
