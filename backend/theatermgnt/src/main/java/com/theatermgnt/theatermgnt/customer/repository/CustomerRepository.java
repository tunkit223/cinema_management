package com.theatermgnt.theatermgnt.customer.repository;

import com.theatermgnt.theatermgnt.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByAccountId(String accountId);
}
