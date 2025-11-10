package com.ebooks.bankisoservice.controllers;

import com.ebooks.bankisoservice.dtos.IsoTransferRequest;
import com.ebooks.bankisoservice.dtos.IsoTransferResponse;
import com.ebooks.bankisoservice.services.BankIsoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/iso")
@RequiredArgsConstructor
public class BankIsoController {
    private final BankIsoService bankIsoService;

    @PostMapping("/transfer")
    public ResponseEntity<IsoTransferResponse> transfer(@Valid @RequestBody IsoTransferRequest request) {
        IsoTransferResponse response = bankIsoService.transfer(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/prepay")
    public ResponseEntity<IsoTransferResponse> prepay(@Valid @RequestBody IsoTransferRequest request){
        IsoTransferResponse response = bankIsoService.prepay(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance/{customerNumber}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String customerNumber){
        BigDecimal balance = bankIsoService.getBalance(customerNumber);
        return ResponseEntity.ok(balance);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}