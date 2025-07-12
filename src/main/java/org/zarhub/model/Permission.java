package org.zarhub.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Table(name = "AHA_PERMISSION")
@Entity(name = "permission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@CacheableEntity
public class Permission extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Long id;
    @Column(columnDefinition = "NVARCHAR2(50)", name = "NAME", nullable = false)
    private String name;
    @Column(columnDefinition = "NVARCHAR2(300)", name = "URL", nullable = false)
    private String url;
    @Column(columnDefinition = "NUMBER(1)", name = "IS_SENSITIVE", nullable = false)
    private Boolean isSensitive;
}
