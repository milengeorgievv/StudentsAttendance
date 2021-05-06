package org.vaadin.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.example.entity.StudentResultEntity;

public interface StudentResultRepository extends JpaRepository<StudentResultEntity, Long> {
}
