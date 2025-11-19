package com.zyn.common.entity;

import com.zyn.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

/**
 * 角色实体类
 */
@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private UserRole name;

    @Column(name = "description", length = 200)
    private String description;
}

