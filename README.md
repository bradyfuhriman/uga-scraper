# UGA Scraper

UGA Scraper is a web scraper for the University of Georgia's course catalogs. It downloads PDFs from [this](https://reg.uga.edu/enrollment-and-registration/schedule-of-classes/) website, parses them for course info, and retrieves a list of available courses.

## How to Use

Download the latest release here and run the jar with ```java -jar uga-scraper-1.0.0.jar```. A ```data``` folder will be created in the same directory the jar is in. After a few moments, the folder will be populated with ```.json``` files, one for each semester, containing a list of JSON objects describing courses available at UGA. You will also see a ```dev``` folder containing the PDFs and text files used to retrieve the course info.

Below is an example of a JSON course object that might be found in ```data/fall.json```.

```
{
    "crn": 57781,
    "subject": "CSCI",
    "number": "1302",
    "title": "Software Development",
    "department": "School of Computing",
    "hours": "4.0",
    "instructor": "Cotterell",
    "size": 90,
    "seats": 0,
    "semester": "Fall",
    "term": "1",
    "meetings": [
        {
            "days": "TR",
            "time": "11:10 am-12:25 pm",
            "building": "Miller Plant Science",
            "room": "2401",
            "campus": "Athens"
        },
        {
            "days": "M",
            "time": "11:30 am-12:20 pm",
            "building": "Miller Plant Science",
            "room": "2401",
            "campus": "Athens"
        }
    ]
}
```
