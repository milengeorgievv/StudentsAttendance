package org.vaadin.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.vaadin.example.dao.StudentResultRepository;
import org.vaadin.example.entity.StudentResultEntity;

import java.util.List;

@RestController
@RequestMapping("/student-results")
public class StudentResultController {

    @Autowired
    private StudentResultRepository studentResultRepository;

    @GetMapping
    public List<StudentResultEntity> getAllStudentResults() {
        List<StudentResultEntity> allStudentResults = studentResultRepository.findAll();
        return allStudentResults;
    }

    @PostMapping
    public StudentResultEntity createNewStudentResult(@RequestBody StudentResultEntity studentResult) {
        StudentResultEntity createdStudentResult = studentResultRepository.save(studentResult);
        return createdStudentResult;
    }
}
