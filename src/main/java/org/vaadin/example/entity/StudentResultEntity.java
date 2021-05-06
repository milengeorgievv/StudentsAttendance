package org.vaadin.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "student_result")
public class StudentResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Float result;

    public StudentResultEntity() {
    }

    public StudentResultEntity(Long studentId, Float result) {
        this.studentId = studentId;
        this.result = result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Float getResult() {
        return result;
    }

    public void setResult(Float result) {
        this.result = result;
    }
}
