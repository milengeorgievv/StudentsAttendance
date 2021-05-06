package org.vaadin.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.example.entity.StudentActivityEntity;

public interface StudentActivityRepository extends JpaRepository<StudentActivityEntity, Long> {
}
