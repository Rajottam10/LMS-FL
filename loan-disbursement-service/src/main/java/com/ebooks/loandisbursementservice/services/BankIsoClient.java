package com.ebooks.loandisbursementservice.services;

import com.ebooks.loandisbursementservice.dtos.IsoTransferRequest;
import com.ebooks.loandisbursementservice.dtos.IsoTransferResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankIsoClient {
    private final RestClient restClient;

    public IsoTransferResponse transfer(IsoTransferRequest request) {
        log.info("Sending IsoTransferRequest: bankCode={}, accountNumber={}, amount={}",
                request.getBankCode(), request.getAccountNumber(), request.getAmount());

        try {
            return restClient.post()
                    .uri("/iso/transfer")
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.error("Client error ({}): Request={}, Body={}",
                                res.getStatusCode(), request, res.getBody());
                        throw new RuntimeException("Client error: " + res.getBody());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("Server error ({}): Request={}, Body={}",
                                res.getStatusCode(), request, res.getBody());
                        throw new RuntimeException("Server error: " + res.getBody());
                    })
                    .body(IsoTransferResponse.class);
        } catch (Exception e) {
            log.error("Error in transfer method for request {}: {}", request, e.getMessage());
            throw new RuntimeException("Failed to call transfer endpoint: " + e.getMessage(), e);
        }
    }
}