package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;
import org.zarhub.config.cache.CacheableEntity;
import org.zarhub.model.Permission;
import java.io.Serializable;


@Table(name = "Role_PERMISSION")
@Entity(name = "rolePermission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class RolePermission extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_ROLE_ID")
    private Role role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_PERMISSION_ID")
    private Permission permission;
}
