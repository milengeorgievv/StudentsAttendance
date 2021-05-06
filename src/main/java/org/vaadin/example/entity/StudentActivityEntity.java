package org.vaadin.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "student_activity")
public class StudentActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String time;
    private String eventContext;
    private String component;
    private String eventName;
    private String description;

    public StudentActivityEntity() {
    }

    public StudentActivityEntity(String time, String eventContext, String component, String eventName, String description) {
        this.time = time;
        this.eventContext = eventContext;
        this.component = component;
        this.eventName = eventName;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEventContext() {
        return eventContext;
    }

    public void setEventContext(String eventContext) {
        this.eventContext = eventContext;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
