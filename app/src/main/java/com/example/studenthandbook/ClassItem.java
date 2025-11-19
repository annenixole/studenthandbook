package com.example.studenthandbook;

public class ClassItem {
    private int id;
    private String day;
    private String startTime;
    private String endTime;
    private String subject; // This maps to course_name in database
    private String location; // This maps to room in database
    private String instructor;
    private String color;

    // Default constructor
    public ClassItem() {}

    // Constructor for new items (5 parameters)
    public ClassItem(String day, String startTime, String endTime, String subject, String location) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.location = location;
        this.instructor = "";
        this.color = "";
    }

    // Constructor for database retrieval (6 parameters - matches the error)
    public ClassItem(int id, String day, String startTime, String endTime, String subject, String location) {
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.location = location;
        this.instructor = "";
        this.color = "";
    }

    // Full constructor (8 parameters)
    public ClassItem(int id, String day, String startTime, String endTime, String subject, String location, String instructor, String color) {
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.location = location;
        this.instructor = instructor;
        this.color = color;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}