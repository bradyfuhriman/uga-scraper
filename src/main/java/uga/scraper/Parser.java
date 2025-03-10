package uga.scraper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import uga.models.Course;
import uga.models.Meeting;

public class Parser {
    
    private static Course course;   // Holds the data for the most recently introduced course

    /** Parses text file for course data. */
    public static void parse(File file) throws IOException, ClassNotFoundException, SQLException, URISyntaxException, InterruptedException {
        FileReader reader = new FileReader(file);
        BufferedReader buffer = new BufferedReader(reader);
        CourseHandler.clearData();
        
        int crn = 0;
        String subject, number, title, department, line;
        subject = number = title = department = line = "";

        while ((line = buffer.readLine()) != null) {

            int index = 0;  // Used to hold the current position in the string during parsing
            
            // A line will either introduce a new course or provide details for the most recent course.
            // Introduction: "AAEC 2580 Agricultural and Applied EconAppl Microeconomic Principles"
            // Details: "3.010752 10:20 am-11:10 amM W F 1011 0104 100Smith3.0 Athens0 A 54- 1"

            if (!Character.isDigit(line.charAt(index))) {   // Course introduction line

                // Find course subject
                subject = line.substring(index, line.indexOf(' '));
                index = line.indexOf(' ') + 1;

                // Find course number
                number = line.substring(index, line.indexOf(' ', index));
                index = line.indexOf(' ', index) + 1;

                // Find the separation of title and department
                String temp = "";
                for (; !line.substring(index, index + 2).matches("[a-z][A-Z]") && index < line.length() - 2; index++) {
                    temp += line.charAt(index);
                }
                temp += line.charAt(index);
                
                // If the previous loop made it to the end of the line, there is no department name
                if (index == line.length() - 2) {
                    department = "N/A";
                    temp += line.charAt(index + 1);
                    title = temp;
                } else {
                    department = temp;
                    index += 1;
                    title = line.substring(index);
                }

            } else {    // Course details line

                String max_hours = line.substring(index, line.indexOf('.') + 2);
                index = line.indexOf('.') + 2;
                
                // Find CRN
                boolean newCrn = false;
                if (line.charAt(index) == ' ') {

                    // Some courses meet on different times and locations on different days of the week.
                    // If no CRN is listed, this line has additional meeting info that corresponds to the most recent CRN.
                    
                    index++;
                } else {
                    newCrn = true;
                    crn = Integer.parseInt(line.substring(index, line.indexOf(' ')));
                    index = line.indexOf(' ') + 1;
                }

                // Find meeting time
                String time;
                if (line.substring(index).startsWith("TBA") || line.substring(index).startsWith("NCRR")) {

                    // TBA = "To Be Announced"
                    // NCRR = "No Classroom Required"

                    time = line.substring(index, line.indexOf(' ', index));
                    index = line.indexOf(' ',index) + 1;
                } else {
                    time = line.substring(index, index + 17);
                    index += 17;
                }

                // Find meeting days
                String days = "";
                for (; !Character.isDigit(line.charAt(index)) && !line.substring(index).startsWith("TBA") && !line.substring(index).startsWith("NCRR"); index++) {
                    if (line.charAt(index) != ' ') {
                        days += line.charAt(index);
                    }
                }

                if (days.isEmpty()) {
                    days = "N/A";
                }

                // Find building number
                String buildingNumber = line.substring(index, line.indexOf(' ', index));
                index = line.indexOf(' ', index) + 1;

                // Get building name from BuildingMap
                String buildingName;
                if (buildingNumber.equals("TBA") || buildingNumber.equals("NCRR")) {
                    buildingName = buildingNumber;
                } else if (BuildingMap.getName(buildingNumber) == null) {
                    buildingName = "Building " + buildingNumber;
                } else {
                    buildingName = BuildingMap.getName(buildingNumber);
                }

                // Find classroom number
                String room = line.substring(index, line.indexOf(' ', index));
                index = line.indexOf(' ', index) + 1;

                // Find class size
                int size = 0;
                if (line.substring(index, line.indexOf(' ', index)).matches("[.0-9]*")) {

                    // Lines will have class size, instructor, and min credit hours next to each other, eg. "100Smith3.0".
                    // This line has no instructor name, so the numbers are blended together, eg. "1003.0".

                    size = Integer.parseInt(line.substring(index, line.indexOf('.', index) - 1));
                    index = line.indexOf('.', index) - 1;

                } else {
                    String temp = "";
                    for (; Character.isDigit(line.charAt(index)); index++) {
                        temp += line.charAt(index);
                    }
                    size = Integer.parseInt(String.valueOf(temp));
                }
                
                // Find course instructor
                String instructor;
                if (Character.isLetter(line.charAt(index))) {
                    instructor = line.substring(index, line.indexOf('.', index) - 1);
                    index = line.indexOf('.', index) - 1;
                } else {
                    instructor = "TBA";
                }
                
                if (instructor.startsWith("TBA 0")) {
                    instructor = "TBA";
                }
                
                // Find credit hours
                String hours, min_hours = line.substring(index, line.indexOf(' ', index));
                if (min_hours.equals(max_hours)) {
                    hours = max_hours;
                } else {
                    hours = min_hours + " - " + max_hours;
                }
                index = line.indexOf(' ', index) + 1;
                
                // Find campus
                String campus = "";
                for (; !line.substring(index, index + 2).matches("[a-z]\\d|[a-z][A-Z]|[a-zA-Z] "); index++) {
                    campus += line.charAt(index);
                }
                campus += line.charAt(index);
                index++;

                // Skip over some unecessary info
                if (line.charAt(index) == ' ') {
                    index++;
                }
                index = line.indexOf(' ', index) + 1;

                if (line.charAt(index) == '-') {
                    index++;
                }
                
                if (!Character.isDigit(line.charAt(index))) {
                    index += 2;
                }

                // Find available seats
                int seats = Integer.parseInt(line.substring(index, line.lastIndexOf('-')).trim());

                // Find section of term
                String term = line.substring(line.lastIndexOf('-') + 2);

                // Find semester
                String semester = file.getPath().substring(11);
                semester = semester.substring(0, 1).toUpperCase() + semester.substring(1, semester.indexOf('.'));

                // Create course and meeting objects
                if (newCrn) {
                    course = new Course(crn, subject, number, title, department, hours, instructor, size, seats, semester, term);
                }
                Meeting meeting = new Meeting(days, time, buildingName, room, campus, course);

                // Save course and meeting data
                CourseHandler.saveCourse(course);
                CourseHandler.saveMeeting(meeting);
            }
        }
        buffer.close();
    }
}