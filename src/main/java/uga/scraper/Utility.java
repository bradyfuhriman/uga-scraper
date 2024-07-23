package uga.scraper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;

import uga.models.Course;
import uga.models.Meeting;

public class Utility {

    private static final Gson gson = new Gson();

    private static Set<Course> courses = new LinkedHashSet<>();
    private static List<Meeting> meetings = new ArrayList<>();

    public static List<String> getCourses() {
        merge();
        List<String> jsonCourses = new ArrayList<>();
        for (Course c : courses) {
            jsonCourses.add(gson.toJson(c));
        }

        return jsonCourses;
    }

    public static void clearData() {
        courses.clear();
        meetings.clear();
    }

    public static void saveCourse(Course course) {
        courses.add(course);
    }

    public static void saveMeeting(Meeting meeting) {
        meetings.add(meeting);
    }

    public static void merge() {
        for (Meeting m : meetings) {
            m.getCourse().addMeeting(m);
            m.setCourse(null);  // Prevents recursive looping in JSON objects
        }
    }
}
