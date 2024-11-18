package com.northeastern.selenium.tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.northeastern.selenium.EncryptionUtil;
import com.northeastern.selenium.ReportManager;
import com.northeastern.selenium.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.Select;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;


public class Scenario1Test extends BaseTest {

    @Test
    public void downloadTranscript() throws Exception {
        // File path for the Excel sheet containing test data
        String excelFilePath = "C:\\Users\\raksh\\Desktop\\Selenium Assignment\\Scenario_1.xlsx";
        // Declare a Map to store the credentials
        Map<String, String> credentials;

        // Create a new test in the Extent Report
        ReportManager.test = ReportManager.extent.createTest("Download Latest Transcript");

        // Navigate to the Northeastern portal
        driver.get("https://me.northeastern.edu");
        driver.manage().window().maximize();
        Thread.sleep(2000);

        // Take screenshot of initial page
        ScreenshotUtil.takeScreenshot(driver, "Scenario1", "InitialPage");

        // Log the start of login process
        Reporter.log("Login Started", true);

        //Reading credentials from Excel
        try {
            credentials = readCredentialsFromExcel(excelFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            credentials = new HashMap<>();
        }

        // Perform login
        loginNUPortal(credentials.get("userId"), credentials.get("password"));
        Thread.sleep(2000);

        // Take screenshot after login
        ScreenshotUtil.takeScreenshot(driver, "Scenario1", "AfterLogin");
        ReportManager.test.info("After Login");
        Reporter.log("Login Completed", true);

        // Handle Duo 2FA
        handleDuo2FA();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click on Resources link
        try {
            WebElement resourcesLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Resources')]")));
            resourcesLink.click();
        } catch (TimeoutException e) {
            // If the link is not clickable, use JavaScript to click
            WebElement resourcesLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(text(), 'Resources')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", resourcesLink);
        }

        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario1", "ResourcesPage");
        ReportManager.test.info("After ResourcesPage");
        Reporter.log("Resources Page is visited", true);

        // Click on "My Transcript" link
        try {
            WebElement transcriptLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'My Transcript')]")));
            transcriptLink.click();
            Thread.sleep(2000);
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", "08_TranscriptLink");
            ReportManager.test.info("In TranscriptLink");
            Reporter.log("TranscriptLink is visited", true);
        } catch (TimeoutException e) {
            System.out.println("Error: 'My Transcript' link not clickable within timeout period.");
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", "TranscriptLinkError");
            ReportManager.test.fail("Failed to click 'My Transcript' link: " + e.getMessage());
            Reporter.log("Failed to click 'My Transcript' link", true);
            throw e; // Re-throw the exception to stop test execution if necessary
        }

        // Switch to the new window
        String currentWindowHandle = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(currentWindowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }

        // Login to the transcript page
        loginTranscriptPage(driver, credentials.get("username"), credentials.get("password") );
        Thread.sleep(2000);

        // Handle Duo 2FA
        handleDuo2FA();
        Thread.sleep(2000);

        ScreenshotUtil.takeScreenshot(driver, "Scenario1", "My Transcript Page Before");
        ReportManager.test.info("My Transcript Page Before");
        Reporter.log("My Transcript Page is Opened", true);

        // Select options in the dropdowns
        WebElement transcriptLevelDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("levl_id")));
        Select transcriptLevelSelect = new Select(transcriptLevelDropdown);
        transcriptLevelSelect.selectByVisibleText("Graduate");

        WebElement transcriptTypeDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("type_id")));
        Select transcriptTypeSelect = new Select(transcriptTypeDropdown);
        transcriptTypeSelect.selectByVisibleText("Audit Transcript");

        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario1", "10_SelectedTranscriptOptions");
        ReportManager.test.info("Selected Transcript Options");
        Reporter.log("Selected Transcript Options", true);

        // Click on Submit button to generate the transcript
        WebElement submitTranscriptButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Submit']")));
        submitTranscriptButton.click();
        Thread.sleep(2000);

        ScreenshotUtil.takeScreenshot(driver, "Scenario1", "Submitted Transcript Request");
        ReportManager.test.info("Submit Transcript Request");
        Reporter.log("Submit Transcript Request", true);

        // Generate and save PDF
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("paperWidth", 8.27);
            params.put("paperHeight", 11.69);
            params.put("printBackground", true);
            String pdfBase64 = (String) ((ChromeDriver) driver).executeCdpCommand("Page.printToPDF", params).get("data");

            // Check if PDF data was generated
            if (pdfBase64 == null || pdfBase64.isEmpty()) {
                throw new IOException("PDF data is null or empty. Failed to generate PDF.");
            }

            // Decode and save the PDF file
            try (FileOutputStream fileOutputStream = new FileOutputStream("transcript.pdf")) {
                fileOutputStream.write(Base64.getDecoder().decode(pdfBase64));
            }

            Thread.sleep(2000);
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", "PDF Saved");
            ReportManager.test.pass("PDF Saved");
            Reporter.log("PDF Saved", true);
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e);
            ReportManager.test.fail("Exception occurred: " + e);
        }
    }

    /**
     * Reads credentials from an Excel sheet
     * @param filePath Path to the Excel file
     * @return Map containing userId, password, username
     */
    public Map<String, String> readCredentialsFromExcel(String filePath) throws IOException {
        Map<String, String> credentials = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1); // Assuming credentials are in the second row
            credentials.put("userId", row.getCell(0).getStringCellValue());
            credentials.put("password", row.getCell(1).getStringCellValue());
            credentials.put("username", row.getCell(2).getStringCellValue());

        }
        return credentials;
    }

    /**
     * Performs the login process in northeastern Portal
     *
     * @param userId The userId for login
     * @param password The password for login
     * @throws InterruptedException If the thread is interrupted during sleep
     * @throws IOException If an I/O error occurs
     */
    private void loginNUPortal(String userId, String password) throws InterruptedException, IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            // Enter userId
            WebElement userIdInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("i0116")));
            userIdInput.clear();
            userIdInput.sendKeys(userId);
            Thread.sleep(2_000);

            // Click "Next" after entering userId
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
            nextButton.click();
        } catch (Exception e) {
            System.out.println("An error occurred while entering userId and clicking next: " + e.getMessage());
            e.printStackTrace();
        }

        // Wait for password field and enter password
        try {
            WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("i0118")));
            passwordInput.clear();
            passwordInput.sendKeys(password);

            // Add a small delay to ensure the password is entered
            Thread.sleep(2_000);

            // Click "Sign in" after entering password
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
            signInButton.click();
        } catch (TimeoutException e) {
            System.out.println("Password field not found. The page might have changed or loaded incorrectly.");
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", "PasswordFieldNotFound");
            throw e;
        }
    }

    /**
     * Performs the login process for the transcript page
     *
     * @param username The username for login
     * @param password The password for login
     * @throws InterruptedException If the thread is interrupted during sleep
     * @throws IOException If an I/O error occurs
     */
    private void loginTranscriptPage(WebDriver driver, String username, String password) throws InterruptedException, IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            // Enter username
            WebElement usernameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
            usernameInput.clear();
            usernameInput.sendKeys(username);
            Thread.sleep(2000);

            // Enter password
            WebElement passwordInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("password")));
            passwordInput.clear();
            passwordInput.sendKeys(password);
            Thread.sleep(2000);

            // Click "Login" button
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("_eventId_proceed")));
            loginButton.click();
        } catch (Exception e) {
            System.out.println("An error occurred during transcript page login: " + e.getMessage());
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", "TranscriptLoginError");
            throw e;
        }
    }

    /**
     * Handles the Duo 2-Factor Authentication process
     *
     * @throws InterruptedException If the thread is interrupted during sleep
     */
    private void handleDuo2FA() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Check if we're on the "Is this your device?" page
        try {
            WebElement trustDeviceButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("trust-browser-button")));
            trustDeviceButton.click();
            System.out.println("Clicked 'Yes, this is my device' button");
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
            Thread.sleep(20000);

            // Switch back to the default content
            driver.switchTo().defaultContent();
            System.out.println("Switched back to default content");
        } catch (TimeoutException e) {
            System.out.println("Duo authentication prompt did not appear, continuing...");
            // System.out.println("Page source: " + driver.getPageSource());
            // throw e;
        }

        // Handle "Stay signed in?" prompt if it appears
        try {
            WebElement staySignedIncCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("KmsiCheckboxField")));
            if (!staySignedIncCheckbox.isSelected()) {
                staySignedIncCheckbox.click();
            }
            System.out.println("Stay signed in is now checked");
            Thread.sleep(2_000);

            WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
            yesButton.click();
            System.out.println("Clicked on Yes button");
            Thread.sleep(2_000);

        } catch (TimeoutException e) {
            System.out.println("'Stay signed in?' prompt did not appear. Continuing...");
            // throw e;
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
}