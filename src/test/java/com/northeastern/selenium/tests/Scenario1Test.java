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
import org.apache.poi.ss.usermodel.Cell;


public class Scenario1Test extends BaseTest {

    @Test
    public void downloadTranscript() throws Exception {
        String excelFilePath = "C:\\Users\\raksh\\Desktop\\Selenium Assignment\\Scenario_1.xlsx";
        Map<String, String> credentials;

        ReportManager.test = ReportManager.extent.createTest("Download Latest Transcript");

        driver.get("https://me.northeastern.edu");
        driver.manage().window().maximize();
        Thread.sleep(2000);

        ScreenshotUtil.takeScreenshot(driver, "Scenario1", "InitialPage");

        Reporter.log("Login Started", true);

        try {
            credentials = readCredentialsFromExcel(excelFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            credentials = new HashMap<>();
        }

        loginNUPortal(credentials.get("userId"), credentials.get("password"));
        Thread.sleep(2000);

        ScreenshotUtil.takeScreenshot(driver, "Scenario1", "AfterLogin");
        ReportManager.test.info("After Login");
        Reporter.log("Login Completed", true);

        handleDuo2FA();

        clickResourcesLink();

        try {
            clickMyTranscriptAndSwitchWindow();
        } catch (TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }

        loginTranscriptPage(driver, credentials.get("username"), credentials.get("password"));
        Thread.sleep(2000);

        handleDuo2FA();
        
        handleTranscriptDownload();
    }

    /**
     * Reads credentials from an Excel file.
     *
     * @param filePath The path to the Excel file containing credentials.
     * @return A Map containing the credentials (userId, password, username).
     * @throws IOException If there's an error reading the file or accessing its contents.
     */
    public Map<String, String> readCredentialsFromExcel(String filePath) throws IOException {
        Map<String, String> credentials = new HashMap<>();
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            // Open the Excel file
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);

            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            // Assume credentials are in the second row (index 1)
            Row row = sheet.getRow(1);

            // Read credentials from cells and handle potential null values
            credentials.put("userId", getCellValueSafely(row.getCell(0)));
            credentials.put("password", getCellValueSafely(row.getCell(1)));
            credentials.put("username", getCellValueSafely(row.getCell(2)));

        } catch (IOException e) {
            // Log the error (replace with your preferred logging mechanism)
            System.err.println("Error reading Excel file: " + e.getMessage());
            throw e; // Re-throw the exception to be handled by the caller
        } finally {
            // Close resources in finally block to ensure they are always closed
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    System.err.println("Error closing workbook: " + e.getMessage());
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.err.println("Error closing file input stream: " + e.getMessage());
                }
            }
        }

        return credentials;
    }

    /**
     * Safely gets the string value from a cell, handling null and non-string types.
     *
     * @param cell The cell to read from.
     * @return The cell's string value, or an empty string if the cell is null or not a string.
     */
    private String getCellValueSafely(Cell cell) {
        if (cell == null) {
            return "";
        }
        try {
            return cell.getStringCellValue();
        } catch (IllegalStateException e) {
            // If the cell is not a string, convert it to a string
            return String.valueOf(cell.getNumericCellValue());
        }
    }

    private void loginNUPortal(String userId, String password) throws InterruptedException, IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            WebElement userIdInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("i0116")));
            userIdInput.clear();
            userIdInput.sendKeys(userId);
            Thread.sleep(2000);

            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
            nextButton.click();
        } catch (Exception e) {
            System.out.println("An error occurred while entering userId and clicking next: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("i0118")));
            passwordInput.clear();
            passwordInput.sendKeys(password);
            Thread.sleep(2000);

            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
            signInButton.click();
        } catch (TimeoutException e) {
            System.out.println("Password field not found. The page might have changed or loaded incorrectly.");
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", "PasswordFieldNotFound");
            throw e;
        }
    }

    private void loginTranscriptPage(WebDriver driver, String username, String password) throws InterruptedException, IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            WebElement usernameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
            usernameInput.clear();
            usernameInput.sendKeys(username);
            Thread.sleep(2000);

            WebElement passwordInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("password")));
            passwordInput.clear();
            passwordInput.sendKeys(password);
            Thread.sleep(2000);

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("_eventId_proceed")));
            loginButton.click();
        } catch (Exception e) {
            System.out.println("An error occurred during transcript page login: " + e.getMessage());
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", "TranscriptLoginError");
            throw e;
        }
    }

    private void handleDuo2FA() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement trustDeviceButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("trust-browser-button")));
            trustDeviceButton.click();
            System.out.println("Clicked 'Yes, this is my device' button");
        } catch (TimeoutException e) {
            System.out.println("'Is this your device?' prompt not found. Proceeding with Duo authentication.");
        }

        try {
            WebElement duoIframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("duo_iframe")));
            driver.switchTo().frame(duoIframe);
            System.out.println("Switched to Duo iframe");

            WebElement sendPushButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Send Me a Push') or contains(@class, 'positive auth-button')]")));
            sendPushButton.click();
            System.out.println("Clicked 'Send Me a Push' button");

            Thread.sleep(20000);

            driver.switchTo().defaultContent();
            System.out.println("Switched back to default content");
        } catch (TimeoutException e) {
            System.out.println("Duo authentication prompt did not appear, continuing...");
        }

        try {
            WebElement staySignedIncCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("KmsiCheckboxField")));
            if (!staySignedIncCheckbox.isSelected()) {
                staySignedIncCheckbox.click();
            }
            System.out.println("Stay signed in is now checked");
            Thread.sleep(2000);

            WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
            yesButton.click();
            System.out.println("Clicked on Yes button");
            Thread.sleep(2000);
        } catch (TimeoutException e) {
            System.out.println("'Stay signed in?' prompt did not appear. Continuing...");
        }

        try {
            WebElement additionalPrompt = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(), 'Continue') or contains(text(), 'Proceed')]")));
            additionalPrompt.click();
            System.out.println("Clicked additional prompt after authentication");
        } catch (TimeoutException e) {
            System.out.println("No additional prompts found after authentication");
        }
    }

    private void clickResourcesLink() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement resourcesLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Resources')]")));
            resourcesLink.click();
        } catch (TimeoutException e) {
            WebElement resourcesLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(text(), 'Resources')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", resourcesLink);
        }

        Thread.sleep(2000);
        try{
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", "ResourcesPage");
            ReportManager.test.info("After ResourcesPage");
            Reporter.log("Resources Page is visited", true);
        }catch(IOException e){
             System.out.println(e.getMessage());
        }
    }

    private void clickMyTranscriptAndSwitchWindow() throws InterruptedException, TimeoutException,IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement transcriptLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'My Transcript')]")));
            transcriptLink.click();
            Thread.sleep(2000);
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", "08_TranscriptLink");
            ReportManager.test.info("In TranscriptLink");
            Reporter.log("TranscriptLink is visited", true);
        } catch (Exception e) {
            System.out.println("Error: 'My Transcript' link not clickable within timeout period.");
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", "TranscriptLinkError");
            ReportManager.test.fail("Failed to click 'My Transcript' link: " + e.getMessage());
            Reporter.log("Failed to click 'My Transcript' link", true);
            throw e;
        }

        String currentWindowHandle = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(currentWindowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }

    private void handleTranscriptDownload() throws InterruptedException, IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            captureScreenshotAndLog("My Transcript Page Before", "My Transcript Page is Opened");
            selectTranscriptOptions(wait);
            submitTranscriptRequest(wait);
            generateAndSavePDF();
        } catch (IOException e) {
            System.out.println("Error during transcript download process: " + e.getMessage());
            ReportManager.test.fail("Error during transcript download process: " + e.getMessage());
        }
    }

    private void selectTranscriptOptions(WebDriverWait wait) throws InterruptedException, IOException {
        Select transcriptLevelSelect = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("levl_id"))));
        transcriptLevelSelect.selectByVisibleText("Graduate");

        Select transcriptTypeSelect = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("type_id"))));
        transcriptTypeSelect.selectByVisibleText("Audit Transcript");

        Thread.sleep(2000);
        
        try {
            captureScreenshotAndLog("10_SelectedTranscriptOptions", "Selected Transcript Options");
        } catch (Exception e) {
            System.out.println("Error capturing screenshot for transcript options: " + e.getMessage());
            ReportManager.test.fail("Error capturing screenshot for transcript options: " + e.getMessage());
        }
    }

    private void submitTranscriptRequest(WebDriverWait wait) throws InterruptedException, IOException {
        WebElement submitTranscriptButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Submit']")));
        submitTranscriptButton.click();
        Thread.sleep(2000);
        
        try {
            captureScreenshotAndLog("Submitted Transcript Request", "Submit Transcript Request");
        } catch (Exception e) {
            System.out.println("Error capturing screenshot for transcript request submission: " + e.getMessage());
            ReportManager.test.fail("Error capturing screenshot for transcript request submission: " + e.getMessage());
        }
    }

    private void generateAndSavePDF() throws InterruptedException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("paperWidth", 8.27);
            params.put("paperHeight", 11.69);
            params.put("printBackground", true);
            String pdfBase64 = (String) ((ChromeDriver) driver).executeCdpCommand("Page.printToPDF", params).get("data");

            if (pdfBase64 == null || pdfBase64.isEmpty()) {
                throw new IOException("PDF data is null or empty. Failed to generate PDF.");
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream("transcript.pdf")) {
                fileOutputStream.write(Base64.getDecoder().decode(pdfBase64));
            }

            Thread.sleep(2000);
            captureScreenshotAndLog("PDF Saved", "PDF Saved");
            ReportManager.test.pass("PDF Saved");
        } catch (IOException e) {
            System.out.println("Error generating or saving PDF: " + e.getMessage());
            ReportManager.test.fail("Error generating or saving PDF: " + e.getMessage());
        }
    }

    private void captureScreenshotAndLog(String screenshotName, String logMessage) {
        try {
            ScreenshotUtil.takeScreenshot(driver, "Scenario1", screenshotName);
            ReportManager.test.info(logMessage);
            Reporter.log(logMessage, true);
        } catch (IOException e) {
            System.out.println("Error capturing screenshot: " + e.getMessage());
            ReportManager.test.fail("Failed to capture screenshot: " + e.getMessage());
        }
    }
}