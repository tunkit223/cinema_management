package com.theatermgnt.theatermgnt.authorization.repository;

import com.theatermgnt.theatermgnt.authorization.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
