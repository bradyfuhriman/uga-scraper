package uga.models;

import java.util.HashSet;
import java.util.Set;

public class Course {
    
    private int crn;
    private String subject;
    private String number;
    private String title;
    private String department;
    private String hours;
    private String instructor;
    private int size;
    private int seats;
    private String semester;
    private String term;
    private Set<Meeting> meetings;

    public Course(int crn, String subject, String number, String title, String department, String hours, String instructor, int size, int seats, String semester, String term) {
        this.crn = crn;
        this.subject = subject;
        this.number = number;
        this.title = title;
        this.department = department;
        this.hours = hours;
        this.instructor = instructor;
        this.size = size;
        this.seats = seats;
        this.semester = semester;
        this.term = term;
        this.meetings = new HashSet<>();
    }

    public int getCrn() {
        return crn;
    }

    public String getSubject() {
        return subject;
    }

    public String getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getDepartment() {
        return department;
    }

    public String getHours() {
        return hours;
    }

    public String getInstructor() {
        return instructor;
    }

    public int getSize() {
        return size;
    }

    public int getSeats() {
        return seats;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getTerm() {
        return term;
    }

    public Set<Meeting> getMeetings() {
        return meetings;
    }

    public void addMeeting(Meeting meeting) {
        this.meetings.add(meeting);
    }

    public void printInfo() {
        String meetingsStr = "";
        for (Meeting m : meetings) {
            meetingsStr += "\n- " + m.getDays() + ", " + m.getTime() + ", " + m.getBuilding() + ", Room " + m.getRoom() + ", " + m.getCampus();
        }
        System.out.println(subject + " " + number + ": " + title + ", " + seats + " seats left. " + "Meeting Time(s): " + meetingsStr);
        System.out.println();
    }
}