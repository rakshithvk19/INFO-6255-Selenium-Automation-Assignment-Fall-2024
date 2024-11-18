package com.northeastern.selenium.tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.northeastern.selenium.ReportManager;
import com.northeastern.selenium.ScreenshotUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains test scenarios for adding To-Do tasks to a calendar.
 */
public class Scenario2Test extends BaseTest {

    /**
     * Test method to add two To-Do tasks to the calendar.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void addTwoToDoTasks() throws Exception {
        // Navigate to the Canvas login page
        driver.get("https://canvas.northeastern.edu/");
        Thread.sleep(2000);

        // Initialize test reporting
        ReportManager.test = ReportManager.extent.createTest("This test is going create 2 events in the calendar");
        ReportManager.test.info("test has started");
        Reporter.log("Test has started", true);

        // Take screenshot of initial page
        ScreenshotUtil.takeScreenshot(driver, "Scenario2", "InitialPage");
        ReportManager.test.info("Initial Page");
        Reporter.log("Initial page has been Opened");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String[] credentials = getLoginCredentials();
        String username = credentials[0];
        String password = credentials[1];

        // System.out.println("Password value: " + password);
        // System.out.println("Username value: " + username);

        // Use username and password for login
        login(username, password);

        // Handle "Stay signed in?" prompt if it appears
        try {
            WebElement noButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idBtn_Back")));
            noButton.click();
        } catch (TimeoutException e) {
            System.out.println("'Stay signed in?' prompt did not appear. Continuing...");
        }

        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario2", "AfterLogin");
        ReportManager.test.info("After Login");
        Reporter.log("After Login", true);

        // Navigate to Calendar
        WebElement calendarLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("global_nav_calendar_link")));
        calendarLink.click();
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario2", "CalendarPage");
        ReportManager.test.info("Calendar Page");
        Reporter.log("Calendar Page", true);

        // Read event details from Excel file
        String excelFilePath = "C:\\Users\\raksh\\Desktop\\Selenium Assignment\\Scenario_2.xlsx";
        FileInputStream inputStream = new FileInputStream(excelFilePath);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheet("Sheet1");

        List<String> addedTitles = new ArrayList<>();

        // Add two events from Excel data
        for (int i = 1; i <= 2; i++) {
            Row r = sheet.getRow(i);
            String title = r.getCell(0).getStringCellValue();
            addedTitles.add(title);
            String date = r.getCell(1).getNumericCellValue() + "";
            String time = r.getCell(2).getNumericCellValue() + "";
            String Details = r.getCell(3).getStringCellValue();

            // Click on the "+" button to add a new event
            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("create_new_event_link")));
            addButton.click();

            // Wait for the "Edit Event" popup to appear
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("edit_event_tabs")));
            Thread.sleep(2000);
            ScreenshotUtil.takeScreenshot(driver, "Scenario2", "CreateNewEvent");
            ReportManager.test.info("Create New Event");
            Reporter.log("Trying to Create New Event", true);

            // Switch to the To-Do tab
            WebElement todoTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '#edit_planner_note_form_holder')]")));
            todoTab.click();

            // Wait for the form to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit_planner_note_form_holder")));
            Thread.sleep(2000);
            ScreenshotUtil.takeScreenshot(driver, "Scenario2", "Before Entering Details");
            ReportManager.test.info("Before Entering Details");
            Reporter.log("Trying to Enter Details", true);

            // Fill in the event details
            WebElement titleInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("planner_note_title")));
            titleInput.sendKeys(title);

            WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("planner_note_date")));
            dateInput.clear();
            dateInput.sendKeys(date);

            WebElement timeInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("planner_note_time")));
            timeInput.sendKeys(time);

            WebElement detailsInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("details_textarea")));
            detailsInput.sendKeys(Details);

            // Take a screenshot before submitting
            Thread.sleep(2000);
            ScreenshotUtil.takeScreenshot(driver, "Scenario2", "After Entering Details");
            ReportManager.test.info("After Entering Details");
            Reporter.log("After Entering Details", true);

            // Submit the event
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"edit_planner_note_form_holder\"]/form/div[2]/button")));
            submitButton.click();
            Reporter.log("Event is Submitted", true);

            // Wait for the event to be added and take a screenshot
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("edit_event_tabs")));
            Thread.sleep(2000);
            ScreenshotUtil.takeScreenshot(driver, "Scenario2", "After Submission");
        }

        // Verify added events
        verifyAddedEvents(wait, addedTitles);

        // Close workbook and input stream
        workbook.close();
        inputStream.close();
    }

    /**
     * Reads data from an Excel file.
     *
     * @param filePath  path to the Excel file
     * @param sheetName name of the sheet to read from
     * @return List of String arrays containing row data
     * @throws IOException if an I/O error occurs
     */
    private List<String[]> readExcelData(String filePath, String sheetName) throws IOException {
        List<String[]> data = new ArrayList<>();
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetName);

        for (Row row : sheet) {
            String[] rowData = new String[5];
            for (int i = 0; i < 5; i++) {
                Cell cell = row.getCell(i);
                rowData[i] = cell == null ? "" : cell.toString();
            }
            data.add(rowData);
        }

        workbook.close();
        fis.close();
        return data;
    }

    /**
     * Verifies if the added events are displayed in the calendar.
     *
     * @param wait        WebDriverWait instance
     * @param addedTitles List of event titles to verify
     */
    private void verifyAddedEvents(WebDriverWait wait, List<String> addedTitles) {
        for (String title : addedTitles) {
            try {
                // Wait until the event is visible in the calendar
                WebElement eventElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), '" + title + "')]")));
                if (eventElement.isDisplayed()) {
                    System.out.println("Verified: Event '" + title + "' is displayed in the calendar.");
                }
                ReportManager.test.pass("Verified: Event '" + title + "' is displayed in the calendar.");
                Reporter.log("Verified: Event '" + title + "' is displayed in the calendar", true);
            } catch (TimeoutException e) {
                System.out.println("Event '" + title + "' not found in the calendar.");
                ReportManager.test.fail("Event '" + title + "' not found in the calendar.");
                Reporter.log("Event '" + title + "' not found in the calendar", true);
            }
        }
    }

    /**
     * Performs login to the Canvas system.
     *
     * @param username username for login
     * @param password password for login
     * @throws InterruptedException if the thread is interrupted
     * @throws IOException          if an I/O error occurs
     */
    private void login(String username, String password) throws InterruptedException, IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            WebElement loginToCanvasButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Log in to Canvas')]")));
            loginToCanvasButton.click();
            System.out.println("Clicked 'Log in to Canvas' button");
        } catch (TimeoutException e) {
            System.out.println("'Log in to Canvas' button not found. The page might have changed.");
            Thread.sleep(2000);
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", "LoginToCanvasButtonNotFound");
            throw e;
        }

        enterLoginCredentials(username, password);
    }

    /**
     * Retrieves login credentials from a specified Excel file.
     * 
     * @return String array containing username and password
     * @throws IOException if there's an error reading the Excel file
     */
    public static String[] getLoginCredentials() throws IOException {
        // File path for the Excel sheet containing test data
        String excelFilePath = "C:\\Users\\raksh\\Desktop\\Selenium Assignment\\Scenario_1.xlsx";
        
        FileInputStream inputStream = null;
        Workbook workbook = null;
        
        try {
            // Open the Excel file
            inputStream = new FileInputStream(excelFilePath);
            workbook = new XSSFWorkbook(inputStream);
            
            // Get the first sheet from the workbook
            Sheet sheet = workbook.getSheet("Sheet1");

            // Read test data from Excel sheet
            // Assuming username is in the first column (index 0) of the second row (index 1)
            String username = sheet.getRow(1).getCell(0).getStringCellValue();
            
            // Assuming encrypted password is in the second column (index 1) of the second row (index 1)
            String encryptedPassword = sheet.getRow(1).getCell(1).getStringCellValue();
            
            // Note: username1 is read but not used in this function
            String username1 = sheet.getRow(1).getCell(2).getStringCellValue();

            // Use the encryptedPassword directly without decryption
            String password = encryptedPassword;

            // Return username and password as an array
            return new String[]{username, password};
        } finally {
            // Close resources in a finally block to ensure they are always closed
            if (workbook != null) {
                workbook.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * Handles the Duo 2-Factor Authentication process
     *
     * @throws InterruptedException If the thread is interrupted during sleep
     */
    private void handleDuo2FA() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page source: " + driver.getPageSource());

        // Check if we're on the "Is this your device?" page
        try {
            WebElement trustDeviceButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("trust-browser-button")));
            trustDeviceButton.click();
            System.out.println("Clicked 'Yes, this is my device' button");
            return; // Exit the method as we've handled the prompt
        } catch (TimeoutException e) {
            System.out.println("'Is this your device?' prompt not found. Proceeding with Duo authentication.");
        }

        try {
            // Wait for the Duo iframe to be present
            WebElement duoIframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("duo_iframe")));
            // Switch to the Duo iframe
            driver.switchTo().frame(duoIframe);
            System.out.println("Switched to Duo iframe");

            // Look for the "Send Me a Push" button
            WebElement sendPushButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Send Me a Push') or contains(@class, 'positive auth-button')]")));
            sendPushButton.click();
            System.out.println("Clicked 'Send Me a Push' button");

            // Wait for manual approval (you'll need to approve on your device)
            Thread.sleep(20000); // Adjust this time as needed

            // Switch back to the default content
            driver.switchTo().defaultContent();
            System.out.println("Switched back to default content");
        } catch (TimeoutException e) {
            System.out.println("Error during Duo authentication: " + e.getMessage());
            System.out.println("Page source: " + driver.getPageSource());
            throw e;
        }

        // Check for any additional prompts or elements after authentication
        try {
            WebElement additionalPrompt = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(), 'Continue') or contains(text(), 'Proceed')]")));
            additionalPrompt.click();
            System.out.println("Clicked additional prompt after authentication");
        } catch (TimeoutException e) {
            System.out.println("No additional prompts found after authentication");
        }
    }

    /**
     * Enters login credentials and completes the login process.
     *
     * @param username The username for login
     * @param password The password for login
     * @throws InterruptedException If the thread is interrupted during sleep
     * @throws IOException If an I/O error occurs during screenshot capture
     */
    private void enterLoginCredentials(String username, String password) throws InterruptedException, IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Enter username
        try {
            WebElement usernameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("i0116")));
            usernameInput.clear();
            usernameInput.sendKeys(username);
            
            // Click "Next" after entering username
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
            nextButton.click();
        } catch (TimeoutException e) {
            System.out.println("Username field not found. The page might have changed or loaded incorrectly.");
            ScreenshotUtil.takeScreenshot(driver, "LoginProcess", "UsernameFieldNotFound");
            throw e;
        }

        // Enter password
        try {
            WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("i0118")));
            passwordInput.clear();
            passwordInput.sendKeys(password);
            System.out.println("Password entered successfully");
            
            // Add a small delay to ensure the password is entered completely
            Thread.sleep(1000);

            // Click "Sign in" after entering password
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
            signInButton.click();
        } catch (TimeoutException e) {
            System.out.println("Password field not found. The page might have changed or loaded incorrectly.");
            ScreenshotUtil.takeScreenshot(driver, "LoginProcess", "PasswordFieldNotFound");
            throw e;
        }

        // Handle Duo 2FA if implemented
        handleDuo2FA();

        // Handle "Stay signed in?" prompt if it appears
        try {
            WebElement noButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idBtn_Back")));
            noButton.click();
        } catch (TimeoutException e) {
            System.out.println("'Stay signed in?' prompt did not appear. Continuing with the login process.");
        }
    }
}