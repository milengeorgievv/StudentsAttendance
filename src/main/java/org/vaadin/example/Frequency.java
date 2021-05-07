package org.vaadin.example;


public class Frequency {
    private Integer exercises;
    private Integer absoluteFrequency;
    private Double relativeFrequency;

    public Frequency() {

    }

    public Frequency(Integer exercises, Integer absoluteFrequency, Double relativeFrequency) {
        this.exercises = exercises;
        this.absoluteFrequency = absoluteFrequency;
        this.relativeFrequency = relativeFrequency;
    }

    public Integer getExercises() {
        return exercises;
    }

    public void setExercises(Integer exercises) {
        this.exercises = exercises;
    }

    public Integer getAbsoluteFrequency() {
        return absoluteFrequency;
    }

    public void setAbsoluteFrequency(Integer absoluteFrequency) {
        this.absoluteFrequency = absoluteFrequency;
    }

    public Double getRelativeFrequency() {
        return relativeFrequency;
    }

    public void setRelativeFrequency(Double relativeFrequency) {
        this.relativeFrequency = relativeFrequency;
    }
}
