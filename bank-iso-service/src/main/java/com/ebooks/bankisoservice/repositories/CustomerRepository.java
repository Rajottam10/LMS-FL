package com.ebooks.bankisoservice.repositories;

import com.ebooks.bankisoservice.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findCustomerByCustomerNumberAndBankCode(String customerNumber, String bankCode);

    Optional<Customer> findCustomerByCustomerNumberIsNullAndBankCode(String bankCode);
}
