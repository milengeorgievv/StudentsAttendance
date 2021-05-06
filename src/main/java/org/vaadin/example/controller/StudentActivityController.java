package org.vaadin.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.vaadin.example.dao.StudentActivityRepository;
import org.vaadin.example.entity.StudentActivityEntity;

import java.util.List;

@RestController
@RequestMapping("/student-activities")
public class StudentActivityController {

    @Autowired
    private StudentActivityRepository studentActivityRepository;

    @GetMapping
    public List<StudentActivityEntity> getAllStudentActivities() {
        List<StudentActivityEntity> allStudentActivities = studentActivityRepository.findAll();
        return allStudentActivities;
    }

    @PostMapping
    public StudentActivityEntity createNewStudentActivity(@RequestBody StudentActivityEntity studentActivity) {
        StudentActivityEntity createdStudentActivity = studentActivityRepository.save(studentActivity);
        return createdStudentActivity;
    }
}
