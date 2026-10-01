package com.ams.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Approval Tray: In Progress / Rejected / Completed requests of the logged-in approver. */
public class ApprovalTrayPage extends BasePage {

    private final By searchBox = By.cssSelector("input[placeholder='Search...']");
    private final By searchButton = By.xpath("//button[normalize-space()='Search']");
    private final By rows = By.cssSelector("main table tbody tr");

    public ApprovalTrayPage(WebDriver driver) {
        super(driver);
    }

    /** Selects a tab: "In Progress", "Rejected" or "Completed". */
    public void openTab(String tabName) {
        click(By.xpath("//button[normalize-space()='" + tabName + "']"));
        waitForTable();
    }

    private void waitForTable() {
        waitForTableLoaded();
        wait.until(d -> !d.findElements(rows).isEmpty() || pageText().contains("No records found"));
    }

    /** Shows 50 rows per page on the current tab (newest requests first). */
    public void showAllRows() {
        waitForTable();
        showMaxRows();
    }

    /** Searches the current tab for a candidate name. */
    public void search(String name) {
        waitForTable();
        type(searchBox, name);
        click(searchButton);
        waitForTableLoaded();
        wait.until(d -> findRow(name) != null || pageText().contains("No records found"));
    }

    /** Row text for the candidate (Request Type, Name, Remarks) or null when not listed. */
    public String rowText(String name) {
        WebElement row = findRow(name);
        return row == null ? null : row.getText();
    }

    public boolean isListed(String name) {
        return findRow(name) != null;
    }

    private WebElement findRow(String name) {
        for (WebElement row : driver.findElements(rows)) {
            if (row.getText().toUpperCase().contains(name.toUpperCase())) {
                return row;
            }
        }
        return null;
    }

    /** Clicks "Details" of an Agent Onboarding request and waits for the onboarding application to open. */
    public void openOnboardingDetails(String name) {
        if (findRow(name) == null) {
            throw new AssertionError("'" + name + "' is not listed in the Approval Tray");
        }
        // Found and clicked in one step, because the table re-renders after the search
        click(By.xpath("//main//table//tbody/tr[contains(., '" + name.toUpperCase() + "')]//button[normalize-space()='Details']"));
        wait.until(ExpectedConditions.urlContains("/agents/onboarding/"));
    }

    /** Clicks "Details" of the candidate's request and waits for the lead form. */
    public void openDetails(String name) {
        WebElement row = findRow(name);
        if (row == null) {
            throw new AssertionError("'" + name + "' is not listed in the Approval Tray");
        }
        click(row.findElement(By.xpath(".//button[normalize-space()='Details']")));
        // Wait for the decision section (interview status for supervisors, Selected / Not Selected for Agency Admin)
        wait.until(d -> isDisplayed(By.name("interviewStatus")) || isDisplayed(By.name("approvalComment")));
        wait.until(d -> !pageText().contains("Fetching data") && !pageText().contains("Loading"));
    }
}
