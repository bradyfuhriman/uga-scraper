package uga.models;

public class Meeting {
    
    private String days;
    private String time;
    private String building;
    private String room;
    private String campus;
    private Course course;

    public Meeting(String days, String time, String building, String room, String campus, Course course) {
        this.days = days;
        this.time = time;
        this.building = building;
        this.room = room;
        this.campus = campus;
        this.course = course;
    }
    
    public String getDays() {
        return days;
    }

    public String getTime() {
        return time;
    }

    public String getBuilding() {
        return building;
    }

    public String getRoom() {
        return room;
    }
    
    public String getCampus() {
        return campus;
    }

    public Course getCourse() {
        return course;
    }
}