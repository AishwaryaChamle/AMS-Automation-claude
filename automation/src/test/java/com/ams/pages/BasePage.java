package com.ams.pages;

import com.ams.base.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Common Selenium helpers used by every page object.
 * All waits are explicit waits (no Thread.sleep).
 */
public abstract class BasePage {

    // The success / alert pop-up shown after most actions
    protected static final By POPUP_HEADING = By.xpath("//img[@alt='success' or @alt='error']/following-sibling::*[self::h2] | //div[@role='dialog']//h2");
    protected static final By POPUP_OK = By.xpath("//button[normalize-space()='OK']");

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        // Tables re-render after loading; a vanished element is simply looked up again
        this.wait = (WebDriverWait) new WebDriverWait(driver, Duration.ofSeconds(Config.timeoutSeconds()))
                .ignoring(org.openqa.selenium.StaleElementReferenceException.class);
    }

    /** Waits until an element matching the locator is visible and returns it (hidden duplicates are ignored). */
    protected WebElement visible(By locator) {
        return wait.until(d -> d.findElements(locator).stream().filter(e -> e.isDisplayed()).findFirst().orElse(null));
    }

    /** Clicks an element once it is visible and enabled. Falls back to a JavaScript click if something overlaps it. */
    protected void click(By locator) {
        // Find and click in one step, so a re-rendered element is simply found again
        wait.until(d -> {
            WebElement element = d.findElements(locator).stream()
                    .filter(e -> e.isDisplayed() && e.isEnabled()).findFirst().orElse(null);
            if (element == null) {
                return false;
            }
            click(element);
            return true;
        });
    }

    protected void click(WebElement element) {
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            jsClick(element);
        }
    }

    protected void jsClick(WebElement element) {
        // dispatchEvent also works for SVG icons, which have no click() method
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});"
                + "arguments[0].dispatchEvent(new MouseEvent('click', {bubbles: true, cancelable: true, view: window}));", element);
    }

    /** Clears a text box and types a new value. */
    protected void type(By locator, String text) {
        WebElement field = visible(locator);
        clearField(field);
        field.sendKeys(text);
    }

    protected void clearField(WebElement field) {
        // Select all is Cmd+A on macOS and Ctrl+A on Windows / Linux
        field.sendKeys(Keys.chord(com.ams.base.Os.isMac() ? Keys.COMMAND : Keys.CONTROL, "a"), Keys.DELETE);
    }

    protected void selectByText(By locator, String visibleText) {
        new Select(visible(locator)).selectByVisibleText(visibleText);
    }

    protected boolean isDisplayed(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return elements.stream().anyMatch(e -> e.isDisplayed());
    }

    /** Visible text of the whole page (used for simple "is this text shown" checks). */
    public String pageText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    /** Waits until a table in the main area has finished loading ("Loading ..." / "Fetching data ..." rows are gone). */
    protected void waitForTableLoaded() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("main table tbody")));
        wait.until(d -> {
            String text = d.findElement(By.cssSelector("main")).getText();
            return !text.contains("Loading") && !text.contains("Fetching data");
        });
    }

    /**
     * Error texts shown under form fields, e.g. "Campaign Name is required".
     * Only short, visible leaf texts that read like a validation message are returned.
     */
    public List<String> fieldErrors() {
        return driver.findElements(By.xpath("//main//*[self::p or self::span or self::div or self::small][not(*)]")).stream()
                .map(BasePage::visibleTextOrEmpty)
                .filter(t -> t.length() > 3 && t.length() < 150)
                .filter(t -> t.matches("(?is).*\\b(required|invalid|must|should|cannot|can't|not allowed|greater|less than|before|after|only|already|enter a valid|please enter|please select)\\b.*"))
                .filter(t -> !t.startsWith("Please upload") && !t.startsWith("Please provide") && !t.startsWith("Please update"))
                .distinct()
                .toList();
    }

    /** Switches "Result Per Page" to 50 so that recently added rows are all on the first page. */
    protected void showMaxRows() {
        By pageSize = By.xpath("//main//select[option[normalize-space()='50']]");
        List<WebElement> selects = driver.findElements(pageSize);
        if (!selects.isEmpty() && !"50".equals(new Select(selects.get(0)).getFirstSelectedOption().getText().trim())) {
            new Select(selects.get(0)).selectByVisibleText("50");
            waitForTableLoaded();
        }
    }

    /**
     * After clicking a button such as SUBMIT or PROCEED: returns "POPUP: <message>" when a pop-up
     * appears (it is closed), or "VALIDATION: <messages>" when the form shows field errors instead.
     */
    public String actionOutcome() {
        if (!appearsWithin(POPUP_OK, 15) && fieldErrors().isEmpty()) {
            return "NO RESPONSE: no pop-up and no validation message appeared";
        }
        if (isDisplayed(POPUP_OK)) {
            return "POPUP: " + closeAllPopups();
        }
        return "VALIDATION: " + String.join("; ", fieldErrors());
    }

    /** True when the element appears within the given number of seconds. */
    protected boolean appearsWithin(By locator, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(d -> d.findElements(locator).stream().anyMatch(e -> e.isDisplayed()));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /** Opens a page of the application directly by its path, e.g. /app/approval-tray */
    public void openPath(String path) {
        driver.get(Config.baseUrl() + path);
        trackRequests();
    }

    /**
     * Counts the application's running server requests (XHR / fetch), so a page can wait until its data
     * has loaded. Must be called after every full page load (driver.get); in-app navigation keeps it.
     */
    protected void trackRequests() {
        ((JavascriptExecutor) driver).executeScript(
                "if (window.__amsRequests) return;"
                        + "const r = window.__amsRequests = {running: 0, last: Date.now()};"
                        + "const started = () => { r.running++; r.last = Date.now(); };"
                        + "const ended = () => { r.running--; r.last = Date.now(); };"
                        + "const send = XMLHttpRequest.prototype.send;"
                        + "XMLHttpRequest.prototype.send = function() {"
                        + "  started(); this.addEventListener('loadend', ended);"
                        + "  return send.apply(this, arguments); };"
                        + "const fetchOriginal = window.fetch;"
                        + "window.fetch = function() {"
                        + "  started(); return fetchOriginal.apply(this, arguments).finally(ended); };");
    }

    /** True when no server request of the application is running. */
    protected boolean noRequestsRunning() {
        Object running = ((JavascriptExecutor) driver).executeScript("return window.__amsRequests ? window.__amsRequests.running : 0;");
        return running == null || ((Number) running).intValue() <= 0;
    }

    /**
     * Waits until the application has been quiet (no server request running or finished) for a moment.
     * A new page starts loading its data just after it appears, so a single "no request running" check is too early.
     */
    protected void waitForRequests() {
        long start = System.currentTimeMillis();
        wait.until(d -> {
            Object last = ((JavascriptExecutor) d).executeScript("return window.__amsRequests ? window.__amsRequests.last : 0;");
            long quietSince = Math.max(start, last == null ? 0 : ((Number) last).longValue());
            return noRequestsRunning() && System.currentTimeMillis() - quietSince >= 1200;
        });
    }

    /** Visible text of an element, or "" if it is hidden or was re-rendered meanwhile. */
    private static String visibleTextOrEmpty(WebElement element) {
        try {
            return element.isDisplayed() ? element.getText().trim() : "";
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
            return "";
        }
    }

    /** Waits until the given text appears anywhere on the page. */
    public boolean waitForText(String text) {
        try {
            wait.until(d -> pageText().contains(text));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns the heading of the pop-up message (for example "Campaign Created Successfully!"). */
    public String popupMessage() {
        // Every pop-up (success, alert, upload errors) has an OK button; read the box around it
        WebElement okButton = visible(POPUP_OK);
        WebElement box = okButton.findElement(By.xpath("./ancestor::div[.//h2 or .//p][1]"));
        String text = box.getText();
        if (text.replace("OK", "").isBlank()) {
            text = okButton.findElement(By.xpath("./ancestor::div[.//h2 or .//p][2]")).getText();
        }
        return text.replaceAll("\\s*\\bOK$", "").trim();
    }

    /**
     * Some actions show several pop-ups one after another
     * (e.g. "New Lead Initiated!" followed by "Lead Approved Successfully!").
     * Reads and closes each of them and returns all messages joined with " / ".
     */
    public String closeAllPopups() {
        StringBuilder messages = new StringBuilder();
        while (true) {
            messages.append(messages.length() == 0 ? "" : " / ").append(popupMessage());
            clickOk();
            // Wait until the next pop-up appears or the page has finished processing
            wait.until(d -> isDisplayed(POPUP_OK) || !(pageText().contains("Processing...") || pageText().contains("Loading")));
            if (!isDisplayed(POPUP_OK)) {
                return messages.toString();
            }
        }
    }

    /** Closes the pop-up by clicking OK. */
    public void clickOk() {
        // Some pop-ups close on their own; only click OK while it is still shown
        wait.until(d -> {
            WebElement ok = d.findElements(POPUP_OK).stream().filter(e -> e.isDisplayed()).findFirst().orElse(null);
            if (ok != null) {
                click(ok);
            }
            return !isDisplayed(POPUP_OK);
        });
    }

    /** Returns the current URL path, e.g. /app/approval-tray */
    public String currentPath() {
        return driver.getCurrentUrl().replace(Config.baseUrl(), "");
    }
}
