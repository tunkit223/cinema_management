package com.theatermgnt.theatermgnt.authorization.repository;

import com.theatermgnt.theatermgnt.authorization.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
