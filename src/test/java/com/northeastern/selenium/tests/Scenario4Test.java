package com.northeastern.selenium.tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.northeastern.selenium.ReportManager;
import com.northeastern.selenium.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * This class contains a test scenario for downloading a dataset from the Northeastern University library.
 */
public class Scenario4Test extends BaseTest {
    
    /**
     * Test method to download a dataset from the library.
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void downloadDataset() throws Exception {
        // Initialize test reporting
        ReportManager.test = ReportManager.extent.createTest("This test is going to download a dataset from library");
        ReportManager.test.info("test has started");
        Reporter.log("This test is going to download a dataset from library", true);

        // Step 1: Open the Northeastern University OneSearch library page
        driver.get("https://onesearch.library.northeastern.edu/discovery/search?vid=01NEU_INST:NU&lang=en");
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario4", "InitialPage");
        ReportManager.test.info("Initial Page");
        Reporter.log("Initial Page", true);

        // Step 2: Click on "Digital Repository Service"
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        // wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"mainMenu\"]/div[5]/a/span")));
        WebElement digitalRepositoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"mainMenu\"]/div[5]/a/span")));
        ScreenshotUtil.takeScreenshot(driver, "Scenario4", "DigitalRepositoryServicePage");
        ReportManager.test.info("Digital Repository Service Page");
        Reporter.log("Digital Repository Service Page", true);

        // Store the original window handle
        String originalWindow = driver.getWindowHandle();
        digitalRepositoryLink.click();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        // Wait for the new window/tab to open
        Thread.sleep(2000);

        // Switch to the new window/tab
        for (String windowHandle : driver.getWindowHandles()) {
            if (!originalWindow.equals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                Thread.sleep(10000);
                // Verify if this is the correct tab by checking its URL
                if (driver.getCurrentUrl().equals("https://repository.library.northeastern.edu/")) {
                    break;
                } else {
                    driver.switchTo().window(originalWindow);
                }
            }
        }

        ScreenshotUtil.takeScreenshot(driver, "Scenario4", "DigitalRepositoryServicePage");
        ReportManager.test.info("Digital Repository Service Page");
        Reporter.log("Digital Repository Service Page", true);

        // Scroll down to load content
        for (int i = 0; i < 2; i++) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
            Thread.sleep(3000);
        }

        // Step 3: Click on "Datasets" under Featured Content
        WebElement datasetsLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"main-content\"]/div[1]/section/div[1]/a[5]")));
        datasetsLink.click();
        ScreenshotUtil.takeScreenshot(driver, "Scenario4", "DatasetsPage");
        ReportManager.test.info("Datasets Page");
        Reporter.log("Datasets Page", true);

        // Step 4: Prepare to select a dataset (commented out for now)
        // WebElement firstDatasetLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//a[contains(@class, 'title')])[1]")));
        // firstDatasetLink.click();
        // ScreenshotUtil.takeScreenshot(driver, "Scenario4", "SelectedDatasetPage");

        // Step 5: Click on "Zip File" to download the dataset
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario4", "DatasetsServicePage");
        ReportManager.test.info("Datasets Service");
        Reporter.log("Datasets Service", true);
        WebElement zipFileButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"main-content\"]/div[2]/main/section/ul/article[1]/div/div/div/div/div[1]/a[1]")));
        zipFileButton.click();
        Thread.sleep(2000);
        ScreenshotUtil.takeScreenshot(driver, "Scenario4", "ZipFileDownloadStarted");
        ReportManager.test.info("ZipFileDownloadStarted");
        Reporter.log("ZipFileDownloadStarted", true);
        Thread.sleep(2000);

        // Capture final screenshot and mark test as passed
        ScreenshotUtil.takeScreenshot(driver, "Scenario4", "DownloadCompleted");
        ReportManager.test.pass("DownloadCompleted");
    }
}