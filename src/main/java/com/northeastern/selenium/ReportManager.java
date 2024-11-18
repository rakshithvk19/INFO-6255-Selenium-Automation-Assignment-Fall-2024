package com.northeastern.selenium;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ReportManager {
    public static ExtentReports extent;
    public static ExtentTest test;

    public static void initReport() {
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter("reports/TestReport.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }

    public static void flushReport() {
        extent.flush();

    }
}
