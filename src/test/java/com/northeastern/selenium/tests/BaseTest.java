package com.northeastern.selenium.tests;
import com.northeastern.selenium.DriverManager;
import com.northeastern.selenium.ReportManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import java.io.IOException;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.annotations.BeforeSuite;


public class BaseTest {
    protected WebDriver driver;
    ExtentReports extentReports;
    ExtentSparkReporter sparkReporter;

    @BeforeClass
    public void setUp() {
        ReportManager.initReport();
        driver = DriverManager.getDriver();
    }

    @AfterSuite
    public void tearDown() throws IOException {
        DriverManager.quitDriver();
        ReportManager.flushReport();

}}