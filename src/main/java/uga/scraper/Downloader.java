package uga.scraper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Downloader {

    /** Downloads a PDF from the URL and puts it in the data folder. */
    public static void download(String url) throws IOException, URISyntaxException {
        URL file = new URI(url).toURL();
        InputStream in = file.openStream();
        FileOutputStream fos = new FileOutputStream("./data/dev/" + url.substring(29));
        int length;
        byte[] buffer = new byte[1024];
        while ((length = in.read(buffer)) > -1) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        in.close();
    }
}