package uga.scraper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class Stripper {

    /** Strips text from a PDF file and saves it to a text file. */
    public static void strip(File pdf) throws IOException {
        PDDocument document = Loader.loadPDF(pdf);
        PDFTextStripper stripper = new PDFTextStripper();
        String result = trim(stripper.getText(document));
        document.close();
        PrintWriter writer = new PrintWriter("./src/data/" + pdf.getPath().substring(14, pdf.getPath().lastIndexOf(".")) + "");
        writer.print(result);
        writer.close();
    }

    /** Removes unnecessary bits from the stripped text. */
    public static String trim(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.startsWith("Report", i) & i < str.length() - 250) {
                i = str.indexOf("TERM", i) + 5;
            }
            result.append(str.charAt(i));
        }
        result.replace(result.indexOf("CONFIDENTIALITY"), result.length(), "");
        return result.toString();
    }
}