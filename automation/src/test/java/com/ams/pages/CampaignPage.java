package com.ams.pages;

import com.ams.utils.TestData.Campaign;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Campaign list, Create Campaign and Edit Campaign screens.
 * Agency Admin reaches it via Recruitment Leads Mgmt > Campaign Management;
 * HOB / AS see the campaign list directly under Recruitment Leads Mgmt.
 */
public class CampaignPage extends BasePage {

    // Campaign list
    private final By createCampaignButton = By.xpath("//main//button[contains(normalize-space(.),'Create Campaign')]");
    private final By searchBySelect = By.name("searchBy");
    private final By searchBox = By.xpath("//select[@name='searchBy']/following::input[1]");
    private final By searchButton = By.xpath("//button[normalize-space()='Search']");
    private final By tableRows = By.cssSelector("main table tbody tr");
    private final By nextPageButton = By.xpath("//button[normalize-space()='›']");

    // Create / Edit campaign form
    private final By campaignNameField = By.name("campaignName");
    private final By startDateField = By.name("startDate");
    private final By endDateField = By.name("endDate");
    private final By budgetField = By.name("budget");
    private final By leadTargetField = By.name("leadTarget");
    private final By proceedButton = By.xpath("//button[normalize-space()='PROCEED']");

    public CampaignPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Create / edit ----------

    public boolean isCreateCampaignAvailable() {
        waitForTable();
        return isDisplayed(createCampaignButton);
    }

    public void clickCreateCampaign() {
        click(createCampaignButton);
        visible(campaignNameField);
    }

    /** Fills the Create Campaign form (empty values are left blank) and clicks PROCEED. */
    public void fillAndProceed(Campaign campaign) {
        typeIfPresent(campaignNameField, campaign.name());
        typeIfPresent(startDateField, campaign.startDate());
        typeIfPresent(endDateField, campaign.endDate());
        typeIfPresent(budgetField, campaign.budget());
        typeIfPresent(leadTargetField, campaign.leadTarget());
        click(proceedButton);
    }

    private void typeIfPresent(By field, String value) {
        if (value != null && !value.isEmpty()) {
            type(field, value);
            // Close any open date picker by clicking on the form heading
            driver.findElement(By.xpath("//h2[normalize-space()='Campaign Information']")).click();
        }
    }

    /**
     * After PROCEED: returns "POPUP: <message>" when a pop-up appears (the pop-up is closed),
     * or "VALIDATION: <messages>" when the form shows field errors instead.
     */
    public String proceedOutcome() {
        return actionOutcome();
    }

    /** True when the Create Campaign form (with its PROCEED button) is shown within a few seconds. */
    public boolean isCreateFormShown() {
        return appearsWithin(proceedButton, 8) && appearsWithin(campaignNameField, 1);
    }

    /**
     * Picks Activate / Deactivate from the Action menu, confirms if asked,
     * and returns the pop-up message(s).
     */
    public String changeStatus(String campaignName, String action) {
        chooseAction(campaignName, action);
        By confirm = By.xpath("//button[normalize-space()='Yes' or normalize-space()='YES' or normalize-space()='Confirm' or normalize-space()='CONFIRM']");
        wait.until(d -> isDisplayed(POPUP_OK) || isDisplayed(confirm));
        if (isDisplayed(confirm)) {
            click(confirm);
        }
        return closeAllPopups();
    }

    /** Text of a campaign row (name, dates, budget, status). */
    public String rowText(String campaignName) {
        return wait.ignoring(org.openqa.selenium.StaleElementReferenceException.class).until(d -> {
            WebElement row = findCampaign(campaignName);
            return row == null ? "(not found)" : row.getText().replace("\n", " | ");
        });
    }

    /** Column headers of the campaign table. */
    public List<String> tableHeaders() {
        waitForTable();
        return driver.findElements(By.cssSelector("main table thead th")).stream().map(e -> e.getText().trim()).toList();
    }

    /** Label shown next to a form field, e.g. "Campaign Budget*:" */
    public List<String> formLabels() {
        return driver.findElements(By.cssSelector("main label")).stream().map(e -> e.getText()).filter(t -> !t.isBlank()).toList();
    }

    /** Searches the campaign list by status (Agency Admin only). */
    public void searchByStatus(String status) {
        waitForTable();
        selectByText(searchBySelect, "Status");
        type(searchBox, status);
        click(searchButton);
        waitForTableLoaded();
    }

    /** Validation messages shown under the form fields. */
    public List<String> validationMessages() {
        return fieldErrors();
    }

    /** Changes budget and / or end date on the Edit Campaign form and clicks PROCEED. */
    public void editCampaign(String newBudget, String newEndDate) {
        visible(budgetField);
        if (newBudget != null) {
            type(budgetField, newBudget);
        }
        if (newEndDate != null) {
            // Recruitment_Leads_Mgmt.md: the End Date can be changed with the date picker when editing
            if (!visible(endDateField).isEnabled()) {
                throw new AssertionError("End Date cannot be changed: the field is disabled in the Edit Campaign form");
            }
            type(endDateField, newEndDate);
            driver.findElement(By.xpath("//h2[normalize-space()='Campaign Information']")).click();
        }
        click(proceedButton);
    }

    public String fieldValue(String fieldName) {
        return visible(By.name(fieldName)).getDomProperty("value");
    }

    // ---------- Campaign list ----------

    private void waitForTable() {
        waitForTableLoaded();
    }

    /** Uses the Search box (Agency Admin only) to show just the given campaign. */
    public void searchByName(String campaignName) {
        waitForTable();
        if (!isDisplayed(searchBox)) {
            return; // HOB / AS list has no search box
        }
        selectByText(searchBySelect, "Campaign Name");
        type(searchBox, campaignName);
        click(searchButton);
        // Either the campaign row appears or an error pop-up "No record found" is shown
        wait.until(d -> rowFor(campaignName) != null || isDisplayed(POPUP_OK) || pageText().contains("No records"));
        if (isDisplayed(POPUP_OK)) {
            clickOk();
        }
    }

    /** Finds the table row of a campaign, looking through up to 5 pages. Returns null when not found. */
    public WebElement findCampaign(String campaignName) {
        return wait.until(d -> java.util.Optional.ofNullable(findCampaignOnce(campaignName))).orElse(null);
    }

    private WebElement findCampaignOnce(String campaignName) {
        waitForTable();
        if (driver.findElements(tableRows).isEmpty()) {
            return null;
        }
        showMaxRows();
        for (int page = 0; page < 5; page++) {
            WebElement row = rowFor(campaignName);
            if (row != null) {
                return row;
            }
            List<WebElement> next = driver.findElements(nextPageButton);
            if (next.isEmpty() || !next.get(0).isEnabled()) {
                return null;
            }
            String firstRowBefore = driver.findElements(tableRows).get(0).getText();
            click(next.get(0));
            wait.until(d -> !d.findElements(tableRows).isEmpty() && !d.findElements(tableRows).get(0).getText().equals(firstRowBefore));
        }
        return null;
    }

    private WebElement rowFor(String campaignName) {
        for (WebElement row : driver.findElements(tableRows)) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() > 2 && cells.get(1).getText().trim().equals(campaignName)) {
                return row;
            }
        }
        return null;
    }

    /** Status text of a campaign row (Ongoing, Upcoming, Inactive, Expired ...). */
    public String statusOf(String campaignName) {
        // The table re-renders after loading, so retry if a row goes stale
        return wait.ignoring(org.openqa.selenium.StaleElementReferenceException.class)
                .until(d -> cellUnder(findCampaign(campaignName), "Status").getText().trim());
    }

    /** Text of every row on the current page. */
    public List<String> visibleRows() {
        waitForTable();
        showMaxRows();
        return driver.findElements(tableRows).stream().map(e -> e.getText()).filter(t -> !t.isBlank() && !t.equals("*")).toList();
    }

    /** Column values (e.g. all "Status" cells) of the current page. */
    public List<String> columnValues(String header) {
        waitForTable();
        int index = headerIndex(header);
        return driver.findElements(tableRows).stream()
                .map(r -> r.findElements(By.tagName("td")))
                .filter(c -> c.size() > index)
                .map(c -> c.get(index).getText().trim())
                .toList();
    }

    /** Opens the "Action" menu of a campaign row and returns the offered options. */
    public List<String> actionsOf(String campaignName) {
        return wait.ignoring(org.openqa.selenium.StaleElementReferenceException.class).until(d -> {
            openActionMenu(campaignName);
            return driver.findElements(By.cssSelector("main table td button.w-full")).stream().map(e -> e.getText()).toList();
        });
    }

    /** Opens the "Action" menu of a campaign row and picks an option (Edit, Activate, Deactivate, Bulk Upload). */
    public void chooseAction(String campaignName, String action) {
        wait.ignoring(org.openqa.selenium.StaleElementReferenceException.class).until(d -> {
            openActionMenu(campaignName);
            return true;
        });
        click(By.xpath("//main//table//td//button[normalize-space()='" + action + "']"));
    }

    private void openActionMenu(String campaignName) {
        WebElement row = findCampaign(campaignName);
        if (row == null) {
            throw new AssertionError("Campaign '" + campaignName + "' not found in the campaign list");
        }
        jsClick(cellUnder(row, "Action").findElement(By.cssSelector("button, svg")));
        // Some campaign states offer no actions at all, so do not insist on a menu appearing
        appearsWithin(By.cssSelector("main table td button.w-full"), 5);
    }

    /** Clicks the "Leads List" (eye) button of a campaign. */
    public void openLeads(String campaignName) {
        WebElement row = findCampaign(campaignName);
        if (row == null) {
            throw new AssertionError("Campaign '" + campaignName + "' not found in the campaign list");
        }
        jsClick(cellUnder(row, "Leads List").findElement(By.cssSelector("button, svg")));
        // HOB / AS go to .../leads, Agency Admin goes to .../campaign-leads
        wait.until(ExpectedConditions.urlContains("leads"));
        waitForTableLoaded();
    }

    private int headerIndex(String header) {
        List<WebElement> headers = driver.findElements(By.cssSelector("main table thead th"));
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().trim().equalsIgnoreCase(header)) {
                return i;
            }
        }
        throw new AssertionError("Column '" + header + "' not found in the table");
    }

    private WebElement cellUnder(WebElement row, String header) {
        return row.findElements(By.tagName("td")).get(headerIndex(header));
    }
}
