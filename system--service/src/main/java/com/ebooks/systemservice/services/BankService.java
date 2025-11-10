
package com.ebooks.systemservice.services;

import com.ebooks.systemservice.dtos.BankCreateRequest;
import com.ebooks.systemservice.dtos.BankResponse;
import com.ebooks.systemservice.dtos.BankUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;

public interface BankService {
    BankResponse createBankWithBankAdmin(BankCreateRequest bankCreateRequest);

    List<BankResponse> getAllBanksWithBankAdmin();

    void deleteBankByBankCode(String bankCode);

    BankResponse updateBankWithBankAdmin(String bankCode, @Valid BankUpdateRequest bankUpdateRequest);

    BankResponse findByBankCode(String bankCode);
}
