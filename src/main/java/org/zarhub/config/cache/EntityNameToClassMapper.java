package org.zarhub.config.cache;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EntityNameToClassMapper {
    private final Map<String, Class<?>> entityMap = new HashMap<>();

    @Autowired
    public EntityNameToClassMapper(EntityManager entityManager) {
        Metamodel metamodel = entityManager.getMetamodel();
        for (EntityType<?> entityType : metamodel.getEntities()) {
            entityMap.put(entityType.getName(), entityType.getJavaType());
        }
    }

    public Class<?> getEntityClass(String entityName) {
        return entityMap.get(entityName);
    }
}

