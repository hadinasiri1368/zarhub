package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Table(name = "AHA_USER_GROUP")
@Entity(name = "userGroup")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@CacheableEntity
public class UserGroup extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Long id;
    @Column(columnDefinition = "NVARCHAR2(50)", name = "NAME", nullable = false)
    private String name;
}
