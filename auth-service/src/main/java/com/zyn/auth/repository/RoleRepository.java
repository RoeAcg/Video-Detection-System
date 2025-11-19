package com.zyn.auth.repository;

import com.zyn.common.entity.Role;
import com.zyn.common.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 角色仓库
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据角色名称查找角色
     */
    Optional<Role> findByName(UserRole name);
}
