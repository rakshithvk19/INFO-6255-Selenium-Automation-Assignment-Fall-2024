package com.northeastern.selenium.tests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Extractdata {

    public static void main(String[] args) {
        try {
            // Load the existing emailable report
            Document doc = Jsoup.parse(String.valueOf(new File("/Users/sumanayanakonda/Desktop/Selenium_Assignment/test-output/emailable-report.html")));

            // Create a new document for the custom report
            Document newDoc = Jsoup.parse(String.valueOf(new File("/Users/sumanayanakonda/Desktop/Selenium_Assignment/test-output/index.html")));

            // Extract test results from the existing report
            Elements testRows = doc.select("table#summary tbody tr");

            int totalTests = 0;
            int passedTests = 0;
            int failedTests = 0;

            // Iterate over each test result row
            for (Element row : testRows) {
                String testName = row.select("td").get(0).text();
                String status = row.hasClass("failedeven") ? "Failed" : "Passed";
                String time = row.select("td").get(3).text();
                String errorMessage = status.equals("Failed") ? row.select(".stacktrace").text() : "";

                // Increment counters
                totalTests++;
                if (status.equals("Passed")) {
                    passedTests++;
                } else {
                    failedTests++;
                }

                // Append results to the new report
                Element newRow = newDoc.createElement("tr");
                newRow.appendElement("td").text(testName);
                newRow.appendElement("td").text(status).addClass(status.equals("Passed") ? "pass" : "fail");
                newRow.appendElement("td").text(time);
                newRow.appendElement("td").text(errorMessage);
                newDoc.select("table").append(String.valueOf(newRow));
            }

            // Update summary counts in the new report
//            newDoc.getElementById("totalTests").text(String.valueOf(totalTests));
            newDoc.getElementById("passedTests").text(String.valueOf(passedTests));
            newDoc.getElementById("failedTests").text(String.valueOf(failedTests));

            // Write the new report to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("path/to/custom-report.html"))) {
                writer.write(newDoc.outerHtml());
            }

            System.out.println("Custom report generated successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}