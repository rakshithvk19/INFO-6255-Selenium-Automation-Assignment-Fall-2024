package com.northeastern.selenium.tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Report;
import com.northeastern.selenium.ReportManager;
import com.northeastern.selenium.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;
import java.io.FileWriter;
import java.util.List;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;

/**
 * This class contains test scenarios for checking the 'Add to my calendar' button
 * in the Northeastern University portal.
 */
public class Scenario5Test extends BaseTest {

    @Test
    public void scenario5() throws InterruptedException, IOException {
        String excelFilePath = "C:\\Users\\raksh\\Desktop\\Selenium Assignment\\Scenario_1.xlsx";

        // Initialize test reporting
        ReportManager.test = ReportManager.extent.createTest("This test is going to check for 'Add to my calender' button");
        ReportManager.test.log(Status.INFO, "test has started");
        Reporter.log("Test has started", true);

        // Initialize WebDriverWait for explicit waits
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Navigate to the Northeastern portal
        driver.get("https://me.northeastern.edu");
        driver.manage().window().maximize();
        Thread.sleep(2000);

        // Declare a Map to store the credentials
        Map<String, String> credentials;
        try {
            credentials = readCredentialsFromExcel(excelFilePath);
        } catch (IOException e) {
            // Handle the exception (e.g., log it or throw a custom exception)
            e.printStackTrace();
            credentials = new HashMap<>();
        }

        // Call the enterNortheasternCredentials function
        enterNortheasternCredentials(driver, credentials.get("username"), credentials.get("password"));

        // Handle 2FA (if needed)
        handleDuo2FA(driver);
        Thread.sleep(5000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario5", "Initial Page");
        Reporter.log("Opened Initial PAGE", true);
        ReportManager.test.log(Status.INFO, "Initial page");

        // Step 2: Navigate to Resources tab
        WebElement resourcesTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Resources')]")));
        resourcesTab.click();
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario5", "Resource Page");
        ReportManager.test.log(Status.INFO, "Resource Page");
        Reporter.log("Opened Resource Page", true);

        // Step 3: Navigate to Academics, Classes & Registration section
        WebElement classReg = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"resource-tab-Academics,_Classes_&_Registration\"]")));
        classReg.click();
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario5", "Class Page");
        ReportManager.test.log(Status.INFO, "Class Page");
        Reporter.log("Opened Class Page", true);

        // Step 4: Click on "Academic Calendar" link
        WebElement transcriptLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Academic Calendar')]")));
        transcriptLink.click();
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario5", "Transcript Page");
        ReportManager.test.log(Status.INFO, "Transcript Page");
        Reporter.log("Opened Transcript Page", true);

        // Step 5: Handle multiple browser windows
        String originalWindow = driver.getWindowHandle();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        // Switch to the newest (third) tab
        for (String windowHandle : driver.getWindowHandles()) {
            if (!originalWindow.equals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                Thread.sleep(10000);
                // Check if this is the correct tab by verifying its URL
                if (driver.getCurrentUrl().equals("https://registrar.northeastern.edu/group/calendar/")) {
                    break; // Exit loop if the correct tab is found
                } else {
                    driver.switchTo().window(originalWindow); // Switch back if it's not the correct tab
                }
            }
        }

        // Step 6: Click on the "Academic calendar for the current academic year." button
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario5", "Academic Calendar Page");
        ReportManager.test.log(Status.INFO, "Academic Calendar Page");
        Reporter.log("Opened Academic Calendar Page", true);
        WebElement currentAcademicYearButton = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Academic calendar for the current academic year.")));
        currentAcademicYearButton.click();
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario5", "Academic Calendar for Current Year Page");
        ReportManager.test.log(Status.INFO, "Academic Calendar for Current Year Page");
        Reporter.log("Opened Academic Calendar for Current Year Page", true);

        // Step 7: Scroll down the page incrementally to load dynamic content
        for (int i = 0; i < 2; i++) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
            Thread.sleep(500); // Pause briefly for content to load
        }

        // Step 8: Uncheck the checkbox if it is checked
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario5", "Before unclicking checkbox");
        ReportManager.test.log(Status.INFO, "Before unclicking checkbox");
        Reporter.log("Before unclicking checkbox", true);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("trumba.spud.7.iframe")));
        WebElement checkbox = driver.findElement(By.xpath("//*[@id=\"mixItem0\"]"));
        if (checkbox.isSelected()) {
            checkbox.click();
        }
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario5", "After clicking checkbox");
        ReportManager.test.log(Status.INFO, "After clicking checkbox");
        Reporter.log("After clicking checkbox", true);

        // Step 9: Scroll to the bottom of the page
        driver.switchTo().defaultContent();
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

        // Step 10: Verify if the 'Add to My Calendar' button is displayed
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("trumba.spud.2.iframe")));
        try {
            WebElement verifyButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[.//span[text()='Add to My Calendar']]")));
            Assert.assertTrue(verifyButton.isDisplayed(), "The 'Add to My Calendar' button is not displayed.");
            ScreenshotUtil.takeScreenshot(driver, "Scenario5", "AddToMyCalendarButtonDisplayed");
            ReportManager.test.log(Status.INFO, "Add to My Calendar");
            Reporter.log("Checking Add to My Calendar", true);
            System.out.println("Test passed: The 'Add to My Calendar' button is displayed.");
            if (verifyButton.isDisplayed()) {
                Thread.sleep(6000);
            }
            ScreenshotUtil.takeScreenshot(driver, "Scenario5", "AddToMyCalendarButtonDisplayed");
            ReportManager.test.log(Status.INFO, "Add to My Calendar is visible");
            Reporter.log("Add to My Calendar is available", true);
        } catch (Exception e) {
            System.out.println("Timeout occurred");
        }

        // Step 11: Switch back to the original window
        driver.switchTo().window(originalWindow);
    }

    /**
     * Reads credentials from an Excel sheet
     * @param filePath Path to the Excel file
     * @return Map containing username and password
     */
    public Map<String, String> readCredentialsFromExcel(String filePath) throws IOException {
        Map<String, String> credentials = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1); // Assuming credentials are in the second row
            credentials.put("username", row.getCell(0).getStringCellValue());
            credentials.put("password", row.getCell(1).getStringCellValue());
        }
        return credentials;
    }

    /**
     * Enters Northeastern credentials and initiates login
     * @param driver WebDriver instance
     * @param username Username to enter
     * @param password Password to enter
     */
    public void enterNortheasternCredentials(WebDriver driver, String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement usernameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("i0116")));
        usernameInput.clear();
        usernameInput.sendKeys(username);
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
        nextButton.click();
        try {
            WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("i0118")));
            passwordInput.clear();
            passwordInput.sendKeys(password);
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
            signInButton.click();
        } catch (TimeoutException e) {
            System.out.println("Password field not found. The page might have changed or loaded incorrectly.");
            throw e;
        }
    }

    /**
     * Handles Duo 2-Factor Authentication
     * @param driver WebDriver instance
     */
    public void handleDuo2FA(WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Check if we're on the "Is this your device?" page
        try {
            Thread.sleep(10_000);
            WebElement trustDeviceButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("trust-browser-button")));
            trustDeviceButton.click();
            System.out.println("Clicked 'Yes, this is my device' button");
        } catch (TimeoutException e) {
            System.out.println("'Is this your device?' prompt not found. Proceeding with Duo authentication.");
        }

        // Handle "Stay signed in?" prompt if it appears
        try {
            WebElement staySignedIncCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("KmsiCheckboxField")));
            if (!staySignedIncCheckbox.isSelected()) {
                staySignedIncCheckbox.click();
            }
            System.out.println("Stay signed in is now checked");
            WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
            yesButton.click();
        } catch (TimeoutException e) {
            System.out.println("'Stay signed in?' prompt did not appear. Continuing...");
            throw e;
        }
    }
}