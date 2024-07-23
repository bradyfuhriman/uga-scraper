package uga;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Set;

import com.google.gson.Gson;

import uga.models.Course;
import uga.scraper.CourseHandler;
import uga.scraper.Downloader;
import uga.scraper.Parser;
import uga.scraper.Stripper;

public class UGAScraper {

    private static final Gson gson = new Gson();

    // When the program is run, the lists below will be initialized to ArrayLists containing the JSON objects describing the courses offered during their respective semesters.
    private static Set<Course> springCourses, summerCourses, fallCourses;
    
    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, SQLException, InterruptedException {
        new File("./data/dev").mkdirs();
        getCourses("spring");
        getCourses("summer");
        getCourses("fall");
    }

    public static void getCourses(String semester) throws IOException, URISyntaxException, ClassNotFoundException, SQLException, InterruptedException {
        Downloader.download("https://apps.reg.uga.edu/soc/SOC" + semester + ".pdf");    // Download a PDF from UGA's website
        Stripper.strip(new File("./data/dev/SOC" + semester + ".pdf"));                 // Strip its contents and put it in a text file
        Parser.parse(new File("./data/dev/" + semester + ".txt"));                      // Parse the text file for course data
        Set<Course> courses = CourseHandler.getCourses();                               // Retrieve a list of courses
        PrintWriter out = new PrintWriter("./data/" + semester + ".json");              // Write data to data/semester.json
        out.println(gson.toJson(courses));
        out.close();
    }
}
