package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;
import org.zarhub.config.cache.CacheableEntity;
import org.zarhub.model.*;
import java.io.Serializable;


@Table(name = "USER_PERMISSION")
@Entity(name = "userPermission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CacheableEntity
public class UserPermission extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "F_USER_ID")
    private Users user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_PERMISSION_ID")
    private Permission permission;
}
