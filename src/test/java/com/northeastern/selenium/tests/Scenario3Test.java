package com.northeastern.selenium.tests;

// Import statements for required libraries and classes
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.northeastern.selenium.ReportManager;
import com.northeastern.selenium.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * This class contains test scenarios for classroom guide download functionality.
 */
public class Scenario3Test extends BaseTest {

    /**
     * Test method to verify behavior when attempting to download a classroom guide with an invalid ID.
     * @throws Exception if an error occurs during test execution
     */
    @Test
    public void downloadClassroomGuideWithInvalidID() throws Exception {
        // Initialize test reporting
        ReportManager.test = ReportManager.extent.createTest("This test is going to check classrooms and their locations.");
        ReportManager.test.info("test has started");
        Reporter.log("This test is going to check classrooms and their locations.", true);

        // Navigate to the classroom search page
        driver.get("https://service.northeastern.edu/tech?id=classrooms");
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario3", "InitialPage");
        ReportManager.test.info("In the Initial page");
        Reporter.log("Started the test", true);

        // Set up WebDriverWait for element synchronization
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Locate and interact with the classroom ID input field
        WebElement classroomIDInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@placeholder, 'Search a classroom by name')]")));
        classroomIDInput.clear();
        classroomIDInput.sendKeys("INVALID_CLASSROOM_ID");

        // Locate and interact with the classroom location dropdown
        WebElement classroomLocationDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("classroomtype")));
        Select dropdown = new Select(classroomLocationDropdown);
        dropdown.selectByVisibleText("NUflex Auto");

        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario3", "InitialPage");
        Reporter.log("Opened the initial Page", true);

        // Locate and click the search button
        WebElement searchButton = driver.findElement(By.xpath("//*[@id=\"classroomFilter\"]/form/input"));
        searchButton.click();

        // Wait for and capture the error message
        WebElement errorMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'No classrooms found for the provided ID.') or contains(text(), 'No classrooms match your filter criteria.')]")));
        String actualErrorMessage = errorMessageElement.getText();
        String expectedErrorMessage = "No classrooms found for the provided ID.";

        // Capture screenshot and log the outcome
        ScreenshotUtil.takeScreenshot(driver, "Scenario3", "Outcome");
        ReportManager.test.info("Outcome: " + actualErrorMessage);
        Reporter.log("Expected: " + expectedErrorMessage, true);

        // Assert that the actual error message does not match the expected (negative test)
        Assert.assertNotEquals(actualErrorMessage, expectedErrorMessage,
                "The error message matches the expected, so the scenario should fail.");
        System.out.println("Test failed as expected: Error messages do not match.");

        // Additional assertion and reporting logic
        if (!actualErrorMessage.equals(expectedErrorMessage)) {
            System.out.println("Test failed as expected: Error messages do not match.");
            Assert.fail("Expected error message");
            ReportManager.test.fail("Test failed as expected: Error messages do not match.");
            Reporter.log("Test failed as expected: Error messages do not match.", false);
        } else {
            System.out.println("Test passed: Expected error message is displayed.");
            // Commented out pass reporting as it contradicts the previous assertion
            // ReportManager.test.pass("Test passed: Expected error message is displayed.");
            // Reporter.log("Test passed: Expected error message is displayed.", false);
        }
    }
}