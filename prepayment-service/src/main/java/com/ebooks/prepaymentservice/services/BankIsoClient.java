package com.ebooks.prepaymentservice.services;

import com.ebooks.prepaymentservice.dtos.IsoTransferRequest;
import com.ebooks.prepaymentservice.dtos.IsoTransferResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankIsoClient {

    private final RestClient restClient;

    public IsoTransferResponse transfer(IsoTransferRequest request) {
        log.info("PREPAY ISO → bankCode={}, customer={}, amount={}",
                request.getBankCode(), request.getAccountNumber(),
                request.getAmount());

        return restClient.post()
                .uri("/iso/prepay")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String body = res.getBody() != null ? new String(res.getBody().readAllBytes()) : "";
                    log.error("4xx Client Error: {} | Body: {}", res.getStatusCode(), body);
                    throw new RuntimeException("Bank rejected transfer: " + body);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    String body = res.getBody() != null ? new String(res.getBody().readAllBytes()) : "";
                    log.error("5xx Server Error: {} | Body: {}", res.getStatusCode(), body);
                    throw new RuntimeException("Bank server error: " + body);
                })
                .body(IsoTransferResponse.class);
    }

    public BigDecimal getBalance(String customerNumber) {
        log.info("Fetching balance for customer: {}", customerNumber);

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/iso/balance/{customerNumber}")
                        .build(customerNumber))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new RuntimeException("Invalid customer: " + customerNumber);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new RuntimeException("Balance service down");
                })
                .body(BigDecimal.class);
    }
}