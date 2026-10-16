package com.progettiportfoglio.multichat.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice 
public class GlobalExceptionHandler {


    @ExceptionHandler (ResponseStatusException.class)
    public ResponseEntity<String> invalidCall(ResponseStatusException r){
    
        return new ResponseEntity<String>(r.getMessage(),r.getStatusCode());
    }
}
