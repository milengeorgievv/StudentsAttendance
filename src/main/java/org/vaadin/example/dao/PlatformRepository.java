package org.vaadin.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.example.entity.PlatformEntity;

public interface PlatformRepository extends JpaRepository<PlatformEntity, Long>{

}
