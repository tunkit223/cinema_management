package com.theatermgnt.theatermgnt.account.repository;

import java.util.Optional;

import com.theatermgnt.theatermgnt.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByUsername(String username);


    boolean existsByEmail(String email);

    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsernameOrEmail(String username, String email);
}
