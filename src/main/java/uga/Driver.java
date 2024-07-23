package uga;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Set;

import uga.models.Course;
import uga.scraper.Download;
import uga.scraper.Parser;
import uga.scraper.Stripper;
import uga.scraper.Utility;

public class Driver {
    
    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, SQLException, InterruptedException {
        
        // Enter the semester you want to get classes for. Valid strings are "spring", "summer", or "fall".
        String semester = "fall";

        Download.download("https://apps.reg.uga.edu/soc/SOC" + semester + ".pdf");  // Download PDF from UGA's website
        Stripper.strip(new File("./src/data/SOC" + semester + ".pdf"));             // Strip its contents and put it into a text file
        Parser.parse(new File("./src/data/" + semester));                           // Parse text file for course data
        Set<Course> courses = Utility.getCourses();                                 // Retrieve the courses nicely packaged

        // Prints info for all available courses
        for (Course c : courses) {
            if (c.getSeats() > 0) {
                c.printInfo();
            }
        }
    }
}
