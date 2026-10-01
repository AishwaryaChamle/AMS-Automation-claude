package com.ams.pages;

import com.ams.utils.TestData.Lead;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.nio.file.Path;
import java.util.List;

/**
 * Lead List of a campaign and the Create Lead / lead details form.
 * The same form is shown to HOB, AS, Immediate Supervisor and Agency Admin
 * (read-only fields + an interview / approval section).
 */
public class LeadPage extends BasePage {

    // Lead list
    private final By createLeadButton = By.xpath("//main//button[contains(normalize-space(.),'Create Lead')]");
    private final By leadRows = By.cssSelector("main table tbody tr");

    // Lead form
    private final By titleSelect = By.name("title");
    private final By firstNameField = By.name("firstName");
    private final By lastNameField = By.name("lastName");
    private final By nicField = By.name("nic");
    private final By dobField = By.name("dob");
    private final By ageField = By.name("age");
    private final By contactField = By.name("contact");
    private final By emailField = By.name("email");
    private final By address1Field = By.name("addressline1");
    private final By address2Field = By.name("addressline2");
    private final By address3Field = By.name("addressline3");
    private final By citySelect = By.name("pCity");
    private final By interviewStatusSelect = By.name("interviewStatus");
    private final By remarkField = By.name("remark");
    private final By supportingDocSelect = By.name("supportingDoc");
    private final By documentInput = By.cssSelector("main input[type='file']");
    private final By scheduleDateField = By.name("scheduleInterview");
    private final By approvedButton = By.xpath("//button[normalize-space()='APPROVED']");
    private final By rejectButton = By.xpath("//button[normalize-space()='REJECT']");
    private final By submitButton = By.xpath("//button[normalize-space()='SUBMIT']");
    private final By saveButton = By.xpath("//button[normalize-space()='SAVE']");

    public LeadPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Lead list ----------

    public boolean isCreateLeadAvailable() {
        waitForTableLoaded();
        return isDisplayed(createLeadButton);
    }

    /** True when the Create Lead button is shown and can be clicked (it is disabled for inactive campaigns). */
    public boolean isCreateLeadEnabled() {
        return isCreateLeadAvailable() && visible(createLeadButton).isEnabled();
    }

    public void clickCreateLead() {
        click(createLeadButton);
        visible(nicField);
        waitForFormLoaded();
    }

    /** The form shows "Fetching data securely..." while dropdown values load. */
    private void waitForFormLoaded() {
        wait.until(d -> !pageText().contains("Fetching data") && !pageText().contains("Loading"));
    }

    /** Row of the lead list that shows the given NIC, or null. */
    public WebElement rowForNic(String nic) {
        waitForTableLoaded();
        showMaxRows();
        for (WebElement row : driver.findElements(leadRows)) {
            if (row.getText().contains(nic)) {
                return row;
            }
        }
        return null;
    }

    /** Whole text of the lead row (name, NIC, DOB, age and status label such as "Rejected by HOB"). */
    public String rowText(String nic) {
        WebElement row = rowForNic(nic);
        return row == null ? null : row.getText().replace("\n", " | ");
    }

    public List<String> leadRowsText() {
        waitForTableLoaded();
        return driver.findElements(leadRows).stream().map(e -> e.getText()).filter(t -> !t.isBlank()).toList();
    }

    /** True when the Proceed (to onboarding) button of the lead is enabled. */
    public boolean isProceedEnabled(String nic) {
        WebElement row = rowForNic(nic);
        if (row == null) {
            throw new AssertionError("Lead with NIC " + nic + " not found in the lead list");
        }
        // Rejected leads show no Proceed button at all
        List<WebElement> proceed = row.findElements(By.xpath(".//button[normalize-space()='Proceed']"));
        return !proceed.isEmpty() && proceed.get(0).isEnabled();
    }

    /** Clicks Proceed and returns the path of the page that opens (the onboarding screen). */
    public String proceedToOnboarding(String nic) {
        String listUrl = driver.getCurrentUrl();
        click(rowForNic(nic).findElement(By.xpath(".//button[normalize-space()='Proceed']")));
        wait.until(d -> !d.getCurrentUrl().equals(listUrl));
        return currentPath();
    }

    /** Heading / title of the current page. */
    public String pageTitle() {
        return visible(By.cssSelector("header h6")).getText();
    }

    /** True when the lead row shows the Edit icon (only leads that are not yet approved / rejected). */
    public boolean hasEditIcon(String nic) {
        WebElement row = rowForNic(nic);
        // The icon stays in the page but is hidden for approved / rejected leads, so only a visible icon counts
        return row != null && row.findElements(By.cssSelector("img[alt='Edit']")).stream().anyMatch(WebElement::isDisplayed);
    }

    /** Opens the lead details by clicking the Edit icon of the lead. */
    public void openLead(String nic) {
        WebElement row = rowForNic(nic);
        if (row == null) {
            throw new AssertionError("Lead with NIC " + nic + " not found in the lead list");
        }
        click(row.findElement(By.cssSelector("img[alt='Edit']")));
        visible(nicField);
        waitForFormLoaded();
    }

    // ---------- Lead form ----------

    /** Fills all candidate details. Blank values are skipped (for validation tests). */
    public void fillLeadDetails(Lead lead) {
        if (!lead.title().isEmpty()) {
            // Option text is "Mr" or "Mr." depending on the screen, so ignore the dot
            org.openqa.selenium.support.ui.Select title = new org.openqa.selenium.support.ui.Select(visible(titleSelect));
            String wanted = lead.title().replace(".", "");
            title.getOptions().stream()
                    .filter(o -> o.getText().replace(".", "").trim().equals(wanted))
                    .findFirst()
                    .ifPresent(o -> title.selectByVisibleText(o.getText()));
        }
        typeIfNotBlank(firstNameField, lead.firstName());
        typeIfNotBlank(lastNameField, lead.lastName());
        if (!lead.nic().isEmpty()) {
            type(nicField, lead.nic());
            // Leaving the NIC field lets the application derive DOB and age
            driver.findElement(nicField).sendKeys(Keys.TAB);
        }
        typeIfNotBlank(contactField, lead.contact());
        typeIfNotBlank(emailField, lead.email());
        typeIfNotBlank(address1Field, lead.address1());
        typeIfNotBlank(address2Field, lead.address2());
        typeIfNotBlank(address3Field, lead.address3());
        if (!lead.city().isEmpty()) {
            selectByText(citySelect, lead.city());
        }
    }

    private void typeIfNotBlank(By field, String value) {
        if (value != null && !value.isEmpty()) {
            type(field, value);
        }
    }

    /** DOB and age filled in by the application from the NIC. */
    public String derivedDob() {
        return driver.findElement(dobField).getDomProperty("value");
    }

    public String derivedAge() {
        return driver.findElement(ageField).getDomProperty("value");
    }

    public void waitForDerivedAge() {
        wait.until(d -> !derivedAge().isBlank());
    }

    /** Chooses the interview status, the schedule date (if shown) and a remark. */
    public void setInterview(String status, String scheduleDate, String remark) {
        // The form can re-render right after loading and reset the choice, so select until it sticks
        wait.until(d -> {
            org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(visible(interviewStatusSelect));
            if (!select.getFirstSelectedOption().getText().equals(status)) {
                select.selectByVisibleText(status);
                return false;
            }
            return true;
        });
        // The Schedule Interview field appears only after the status is chosen: wait for it
        if (scheduleDate != null && appearsWithin(scheduleDateField, 10)) {
            pickScheduleDate(scheduleDate);
        }
        if (remark != null) {
            // Enabled only once the schedule date has been selected
            wait.until(d -> d.findElement(remarkField).isEnabled());
            type(remarkField, remark);
        }
    }

    /**
     * Sets "Schedule Interview" like the tester does: opens the date picker with the calendar icon and
     * selects the date. The Remark box is only enabled once a date has been selected.
     * The browser's own calendar pop-up cannot be clicked by Selenium, so the date is selected in its
     * date input the way the pop-up does it (value + change event), and the pop-up is closed.
     * (Typing into the masked text box does not work: it is cleared again when the field loses focus.)
     */
    private void pickScheduleDate(String ddMMyyyy) {
        String isoDate = java.time.LocalDate.parse(ddMMyyyy, com.ams.utils.TestData.UI_DATE).toString();
        WebElement hiddenDate = driver.findElement(By.xpath("//input[@name='scheduleInterview']/following-sibling::input[@type='date']"));
        driver.findElement(By.xpath("//input[@name='scheduleInterview']/following-sibling::*[local-name()='svg']")).click();
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set;"
                        + "setter.call(arguments[0], arguments[1]);"
                        + "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));"
                        + "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));"
                        + "arguments[0].blur();", hiddenDate, isoDate); // blur closes the calendar pop-up
        wait.until(d -> ddMMyyyy.equals(d.findElement(scheduleDateField).getDomProperty("value")));
    }

    public String scheduleDateValue() {
        return driver.findElement(scheduleDateField).getDomProperty("value");
    }

    /** True when the Agency Admin decision (Selected / Not Selected / Postponed) is shown instead of an interview status. */
    public boolean isAgencyAdminDecisionShown() {
        return isDisplayed(By.name("approvalComment"));
    }

    /** Agency Admin chooses Selected / Not Selected / Postponed and enters a remark. */
    public void setAgencyAdminDecision(String decision, String remark) {
        click(By.xpath("//input[@name='approvalComment' and @value='" + decision + "']"));
        WebElement remarkBox = visible(By.xpath("//*[normalize-space()='Remark *:']/following::textarea[1]"));
        clearField(remarkBox);
        remarkBox.sendKeys(remark);
    }

    /** Waits until the decision section of the details page is ready (interview status or Agency Admin decision). */
    public void waitForDecisionSection() {
        wait.until(d -> isDisplayed(interviewStatusSelect) || isDisplayed(By.name("approvalComment")));
        wait.until(d -> !pageText().contains("Fetching data") && !pageText().contains("Loading"));
    }

    /** Interview date recorded by an earlier approver, e.g. section "HOB Interview". */
    public String interviewDateOf(String section) {
        return visible(By.xpath("//*[normalize-space()='" + section + "']/following::input[1]")).getDomProperty("value");
    }

    /**
     * The supporting documents attached to the lead, as shown on the form: "Document 1", "Document 2", ...
     * (each is a card with a preview and a VIEW button; the file name is not shown).
     */
    public List<String> documentNames() {
        return driver.findElements(documentLabels).stream()
                .map(e -> e.getText().replace(":", "").replaceAll("\\s+", " ").trim()).filter(t -> !t.isBlank()).toList();
    }

    private final By documentLabels = By.xpath("//main//label[starts-with(normalize-space(),'Document ')]");

    /**
     * Checks a supporting document (e.g. "Document 1") by clicking its VIEW button: returns true when the
     * document opens (in a new tab or in a viewer on the page). The new tab / viewer is closed again afterwards.
     */
    public boolean viewDocument(String document) {
        String view = "translate(normalize-space(.),'view','VIEW')='VIEW'";
        By viewButton = By.xpath("(//main//label[starts-with(normalize-space(),'" + document + "')]/ancestor::*[.//button[" + view + "]][1]//button[" + view + "])[1]");
        By viewer = By.cssSelector("[role='dialog'] img, [role='dialog'] iframe, [role='dialog'] embed, [role='dialog'] object, "
                + ".MuiDialog-root img, iframe, embed, object, main img[src^='blob:'], main img[src^='data:image']");
        String mainWindow = driver.getWindowHandle();
        java.util.Set<String> windowsBefore = driver.getWindowHandles();
        click(viewButton);
        boolean opened;
        try {
            opened = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                    .until(d -> d.getWindowHandles().size() > windowsBefore.size() || !d.findElements(viewer).isEmpty());
        } catch (org.openqa.selenium.TimeoutException e) {
            opened = false;
        }
        // Close the document again so the test can go on with the lead
        for (String window : driver.getWindowHandles()) {
            if (!windowsBefore.contains(window)) {
                driver.switchTo().window(window).close();
            }
        }
        driver.switchTo().window(mainWindow);
        // The viewer pop-up has a CLOSE button
        By close = By.xpath("//button[normalize-space()='CLOSE']");
        if (isDisplayed(close)) {
            click(close);
            wait.until(d -> !isDisplayed(close));
        }
        return opened;
    }

    /** Clicks "+ Add More" to add a second supporting document row. */
    public void addMoreDocuments() {
        click(By.xpath("//*[contains(normalize-space(),'+ Add More')][self::button or self::span or self::p or self::div][last()]"));
    }

    public boolean isScheduleDateShown() {
        return isDisplayed(scheduleDateField);
    }

    /**
     * Uploads a supporting document (needed for candidates under 22) and waits until its
     * "Document N" card appears. A refused file (pop-up) is reported as a failure.
     */
    public void uploadSupportingDocument(String documentType, Path file) {
        int before = driver.findElements(documentLabels).size();
        selectByText(supportingDocSelect, documentType);
        wait.until(ExpectedConditions.presenceOfElementLocated(documentInput)).sendKeys(file.toAbsolutePath().toString());
        wait.until(d -> d.findElements(documentLabels).size() > before || isDisplayed(POPUP_OK));
        if (isDisplayed(POPUP_OK)) {
            throw new AssertionError("Upload of " + file.getFileName() + " as '" + documentType + "' was refused: " + closeAllPopups());
        }
    }

    public boolean isSupportingDocumentSectionShown() {
        return isDisplayed(supportingDocSelect);
    }

    public List<String> supportingDocumentTypes() {
        return new org.openqa.selenium.support.ui.Select(visible(supportingDocSelect)).getOptions().stream()
                .map(e -> e.getText()).toList();
    }

    public boolean isApprovedEnabled() {
        return visible(approvedButton).isEnabled();
    }

    public void clickApproved() {
        click(approvedButton);
    }

    public void clickReject() {
        click(rejectButton);
    }

    public void clickSubmit() {
        click(submitButton);
    }

    public void clickSave() {
        click(saveButton);
    }

    public void setAddressLine1(String value) {
        type(address1Field, value);
    }

    public void setContact(String value) {
        type(contactField, value);
    }

    public String fieldValue(String fieldName) {
        return visible(By.name(fieldName)).getDomProperty("value");
    }

    public String selectedInterviewStatus() {
        return new org.openqa.selenium.support.ui.Select(visible(interviewStatusSelect)).getFirstSelectedOption().getText();
    }

    public List<String> interviewStatusOptions() {
        return new org.openqa.selenium.support.ui.Select(visible(interviewStatusSelect)).getOptions().stream()
                .map(e -> e.getText()).toList();
    }

    /** Validation messages shown under the form fields. */
    public List<String> validationMessages() {
        return fieldErrors();
    }
}
