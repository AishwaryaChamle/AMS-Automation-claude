package com.ams.base;

import com.ams.pages.ApprovalTrayPage;
import com.ams.pages.CampaignPage;
import com.ams.pages.LeadPage;
import com.ams.pages.LoginPage;
import com.ams.pages.MenuBar;
import com.ams.pages.OnboardingPage;
import com.ams.pages.UploadPage;
import com.ams.utils.Flows;
import com.ams.utils.OnboardingFlows;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Parent class for every browser test.
 * Opens a fresh Chrome window before each test and closes it afterwards.
 */
public abstract class BaseTest {

    /** Folder where downloaded files (sample templates) are saved. */
    public static final Path DOWNLOAD_DIR = Path.of("target", "downloads").toAbsolutePath();

    protected WebDriver driver;

    /** Browser of the test case running now (closed when the run is stopped in the middle of it). */
    private static volatile WebDriver openBrowser;

    // Page objects shared by all tests
    protected LoginPage loginPage;
    protected MenuBar menu;
    protected CampaignPage campaignPage;
    protected UploadPage uploadPage;
    protected LeadPage leadPage;
    protected ApprovalTrayPage approvalTray;
    protected OnboardingPage onboardingPage;

    // Multi-step business flows (create lead, approve, reject, ...)
    protected Flows flows;

    // Agent onboarding flows (raise request, approvals, agreement, activation)
    protected OnboardingFlows onboarding;

    @BeforeMethod
    public void openBrowser() throws Exception {
        // While the tester confirms a failed test case, the execution is paused: start when it resumes
        com.ams.report.FailureConfirmation.waitWhilePaused();
        Files.createDirectories(DOWNLOAD_DIR);

        ChromeOptions options = new ChromeOptions();
        // The UAT server uses a self-signed certificate
        options.setAcceptInsecureCerts(true);
        options.addArguments("--ignore-certificate-errors", "--start-maximized", "--window-size=1600,1000");
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        // Save downloads without asking
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", DOWNLOAD_DIR.toString());
        prefs.put("download.prompt_for_download", false);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        openBrowser = driver;

        loginPage = new LoginPage(driver);
        menu = new MenuBar(driver);
        campaignPage = new CampaignPage(driver);
        uploadPage = new UploadPage(driver);
        leadPage = new LeadPage(driver);
        approvalTray = new ApprovalTrayPage(driver);
        onboardingPage = new OnboardingPage(driver);
        flows = new Flows(loginPage, menu, campaignPage, leadPage, approvalTray, uploadPage);
        onboarding = new OnboardingFlows(loginPage, leadPage, approvalTray, onboardingPage, flows);
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser(ITestResult result) {
        if (driver == null) {
            return;
        }
        // Keep a screenshot as evidence when a test fails
        if (result.getStatus() == ITestResult.FAILURE) {
            try {
                File shot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path target = Path.of("screenshots", getClass().getSimpleName() + ".png");
                Files.createDirectories(target.getParent());
                Files.copy(shot.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                result.setAttribute("screenshot", "automation/" + target.toString().replace('\\', '/'));
            } catch (Exception ignored) {
                // A missing screenshot must not hide the real failure
            }
        }
        openBrowser = null;
        driver.quit();
    }

    /** Screenshot (PNG) of what this test case's browser shows now, or null when there is no browser. */
    public byte[] screenshotNow() {
        try {
            return driver == null ? null : ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            return null;
        }
    }

    /** Closes the browser of the test case running now, if any (used when the run is stopped). */
    public static void closeOpenBrowser() {
        WebDriver browser = openBrowser;
        openBrowser = null;
        if (browser != null) {
            try {
                browser.quit();
            } catch (Exception ignored) {
                // the browser may already be gone
            }
        }
    }

    /**
     * Records what actually happened in the application.
     * This text goes to the Actual Result column of Executed_Suits.xls.
     */
    protected void actual(String observation) {
        Reporter.getCurrentTestResult().setAttribute("actual", observation);
    }
}
