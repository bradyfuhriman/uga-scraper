package uga;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import uga.scraper.Download;
import uga.scraper.Parser;
import uga.scraper.Stripper;
import uga.scraper.Utility;

public class Driver {

    // When the program is run, the lists below will be initialized to ArrayLists containing the JSON objects describing the courses offered during their respective semesters.
    private static List<String> springCourses, summerCourses, fallCourses;
    
    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, SQLException, InterruptedException {
        springCourses = getCourses("spring");   // Get courses offered during spring
        summerCourses = getCourses("summer");   // Get courses offered during summer
        fallCourses = getCourses("fall");       // Get courses offered during fall
    }

    public static List<String> getCourses(String semester) throws IOException, URISyntaxException, ClassNotFoundException, SQLException, InterruptedException {
        Download.download("https://apps.reg.uga.edu/soc/SOC" + semester + ".pdf");  // Download PDF from UGA's website
        Stripper.strip(new File("./src/data/SOC" + semester + ".pdf"));             // Strip its contents and put it into a text file
        Parser.parse(new File("./src/data/" + semester));                           // Parse text file for course data
        return Utility.getCourses();                                                // Retrieve the courses nicely packaged
    }
}
