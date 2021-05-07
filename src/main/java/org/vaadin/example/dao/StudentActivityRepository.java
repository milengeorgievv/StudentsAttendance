package org.vaadin.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.vaadin.example.entity.StudentActivityEntity;

import java.util.List;

public interface StudentActivityRepository extends JpaRepository<StudentActivityEntity, Long> {
    @Query(value = "SELECT COUNT(description) FROM student_activity \n" +
            "WHERE event_context LIKE '%Assignment: Качване на Упр.%'\n" +
            "AND event_name LIKE '%A file has been uploaded.%'\n" +
            "GROUP BY SUBSTRING(description,0, 25)",
            nativeQuery = true)
    List<Integer> findFrequencyDistributionValues();
}
