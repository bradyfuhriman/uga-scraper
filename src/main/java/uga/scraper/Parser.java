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

    /** Parses text file for course data. */
    public static void parse(File file) throws IOException, ClassNotFoundException, SQLException, URISyntaxException, InterruptedException {
        FileReader reader = new FileReader(file);
        BufferedReader buffer = new BufferedReader(reader);
        Utility.clearData();

        int crn = 0;
        String subject, number, title, department, line;
        subject = number = title = department = line = "";

        while ((line = buffer.readLine()) != null) {

            int index = 0;
            
            // A line will either introduce a new course or provide details for the most recent course.
            // Introduction: "AAEC 2580 Agricultural and Applied EconAppl Microeconomic Principles"
            // Details: "3.010752 10:20 am-11:10 amM W F 1011 0104 100Smith3.0 Athens0 A 54- 1"

            if (!Character.isDigit(line.charAt(index))) { // Course introduction line

                // Subject
                subject = line.substring(index, line.indexOf(' '));
                index = line.indexOf(' ') + 1;

                // Number
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

            } else { // Course details line

                String max_hours = line.substring(index, line.indexOf('.') + 2);
                index = line.indexOf('.') + 2;
                
                // CRN
                if (line.charAt(index) == ' ') {

                    // Some courses meet on different times and locations on different days of the week.
                    // If no CRN is listed, this line has additional meeting info that corresponds to the most recent CRN.

                    index++;
                } else {
                    crn = Integer.parseInt(line.substring(index, line.indexOf(' ')));
                    index = line.indexOf(' ') + 1;
                }

                // Time
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

                // Days
                String days = "";
                for (; !Character.isDigit(line.charAt(index)) && !line.substring(index).startsWith("TBA") && !line.substring(index).startsWith("NCRR"); index++) {
                    if (line.charAt(index) != ' ') {
                        days += line.charAt(index);
                    }
                }

                if (days.isEmpty()) {
                    days = "N/A";
                }

                // Building number
                String buildingNumber = line.substring(index, line.indexOf(' ', index));
                index = line.indexOf(' ', index) + 1;

                // Building name
                String buildingName;
                if (buildingNumber.equals("TBA") || buildingNumber.equals("NCRR")) {
                    buildingName = buildingNumber;
                } else if (BuildingMap.getName(buildingNumber) == null) {
                    buildingName = "Building " + buildingNumber;
                } else {
                    buildingName = BuildingMap.getName(buildingNumber);
                }

                // Room
                String room = line.substring(index, line.indexOf(' ', index));
                index = line.indexOf(' ', index) + 1;

                // Class size
                int size = 0;
                if (line.substring(index, line.indexOf(' ', index)).matches("[.0-9]*")) {

                    // Line will have class size, instructor, and min credit hours next to each other, eg. "100Smith3.0"
                    // If there is no instructor name, the numbers will blend together, eg. "1003.0"

                    size = Integer.parseInt(line.substring(index, line.indexOf('.', index) - 1));
                    index = line.indexOf('.', index) - 1;

                } else {
                    String temp = "";
                    for (; Character.isDigit(line.charAt(index)); index++) {
                        temp += line.charAt(index);
                    }
                    size = Integer.parseInt(String.valueOf(temp));
                }
                
                // Instructor
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
                
                // Hours
                String hours, min_hours = line.substring(index, line.indexOf(' ', index));
                if (min_hours.equals(max_hours)) {
                    hours = max_hours;
                } else {
                    hours = min_hours + " - " + max_hours;
                }
                index = line.indexOf(' ', index) + 1;
                
                // Campus
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

                // Available seats
                int seats = Integer.parseInt(line.substring(index, line.lastIndexOf('-')).trim());

                // Part of term
                String term = line.substring(line.lastIndexOf('-') + 2);

                // Semester
                String semester = file.getPath().substring(11);

                // Create course and meeting objects
                Course course = new Course(crn, subject, number, title, department, hours, instructor, size, seats, semester, term);
                Meeting meeting = new Meeting(days, time, buildingName, room, campus, course);

                // Save course and meeting data
                Utility.saveCourse(course);
                Utility.saveMeeting(meeting);
            }
        }
        buffer.close();
    }
}