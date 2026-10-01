package com.ams.pages;

import com.ams.utils.TestData;
import com.ams.utils.TestData.AriProfile;
import com.ams.utils.UploadFiles;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Agent Management > Agent Onboarding (Agent_Onboarding_flow.md):
 * the summary list, the 9-step application form, the ARI page (Raise Request / Approve / Reject),
 * the Agent Agreement upload and the Agency Admin "New Application" form.
 */
public class OnboardingPage extends BasePage {

    public static final String SUMMARY_PATH = "/app/agents/onboarding/summary";

    // Steps of the application form (last part of the page URL)
    public static final String APPLICANT_STEP = "registration-personalDetails";
    public static final String DOCUMENTS_STEP = "registration-mandatory-documents";
    public static final String PERSONAL_STEP = "personal";
    public static final String ADDRESS_STEP = "address";
    public static final String EDUCATION_STEP = "education";
    public static final String WORK_STEP = "work";
    public static final String BANK_STEP = "bank";
    public static final String DEPENDANTS_STEP = "hospitalizationbenefit";
    public static final String WITNESS_STEP = "witness";
    public static final String ARI_STEP = "avgrecruitementindicator"; // spelled like this in the application URL

    private final By rows = By.cssSelector("main table tbody tr");
    private final By nextButton = By.xpath("//button[normalize-space()='NEXT']");
    private final By raiseRequestButton = By.xpath("//button[normalize-space()='RAISE REQUEST']");
    private final By activationRequestButton = By.xpath("//button[normalize-space()='RAISE REQUEST FOR SALES CODE ACTIVATION']");
    private final By downloadAgreementButton = By.xpath("//button[normalize-space()='Download Agent Agreement']");

    public OnboardingPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Summary list ----------

    /** Opens the Agent Onboarding summary (Agent Management > Agent Onboarding). */
    public void openSummary() {
        openPath(SUMMARY_PATH);
        waitForRows();
    }

    private void waitForRows() {
        waitForTableLoaded();
        wait.until(d -> !d.findElements(rows).isEmpty() || nothingFound());
    }

    /** Searches the summary by the applicant's full name. */
    public void search(String applicantName) {
        choose("search_by", "Applicant Name");
        type(By.cssSelector("input[placeholder^='Search by']"), applicantName);
        click(By.xpath("//main//button[normalize-space()='Search']"));
        // Wait until the table only shows the searched applicant (or nothing)
        wait.until(d -> nothingFound() || (!d.findElements(rows).isEmpty()
                && d.findElements(rows).stream().allMatch(r -> r.getText().toUpperCase().contains(applicantName.toUpperCase()))));
    }

    /** True when the list is empty ("No records found" / "No applications match your search criteria."). */
    private boolean nothingFound() {
        String text = pageText();
        return text.contains("No records") || text.contains("No applications match");
    }

    /** Row text of the applicant, or null when the applicant is not listed. */
    public String rowText(String applicantName) {
        WebElement row = row(applicantName);
        return row == null ? null : row.getText().replace("\n", " ");
    }

    public boolean isListed(String applicantName) {
        return row(applicantName) != null;
    }

    /** Status column, e.g. "New Lead", "Pending Submission", "Rejected", "Submitted", "Approved". */
    public String status(String applicantName) {
        return cell(applicantName, 6);
    }

    /** Sales Code column ("NA" until the Agency Admin approves the application). */
    public String salesCode(String applicantName) {
        return cell(applicantName, 3);
    }

    /** Remark column (the last approver's remark). */
    public String remark(String applicantName) {
        return cell(applicantName, 7);
    }

    /** Actions offered in the row, e.g. [Edit, Delete] or [Details]. */
    public List<String> actions(String applicantName) {
        return requiredRow(applicantName).findElements(By.cssSelector("button")).stream()
                .map(b -> b.getDomAttribute("title") != null ? b.getDomAttribute("title") : b.getText().trim())
                .toList();
    }

    /** Clicks the Edit icon of the applicant and waits for the application form. */
    public void edit(String applicantName) {
        click(requiredRow(applicantName).findElement(By.cssSelector("button[title='Edit']")));
        wait.until(d -> !currentStep().equals("summary"));
        waitForFormLoaded();
    }

    private WebElement row(String applicantName) {
        for (WebElement row : driver.findElements(rows)) {
            if (row.getText().toUpperCase().contains(applicantName.toUpperCase())) {
                return row;
            }
        }
        return null;
    }

    private WebElement requiredRow(String applicantName) {
        WebElement row = row(applicantName);
        if (row == null) {
            throw new AssertionError("'" + applicantName + "' is not listed in Agent Onboarding");
        }
        return row;
    }

    private String cell(String applicantName, int column) {
        return requiredRow(applicantName).findElement(By.xpath("./td[" + column + "]")).getText().trim();
    }

    // ---------- Moving between steps ----------

    /** Current step of the application form, e.g. "bank" (see the *_STEP constants). */
    public String currentStep() {
        String path = currentPath();
        int query = path.indexOf('?');
        if (query >= 0) {
            path = path.substring(0, query);
        }
        return path.substring(path.lastIndexOf('/') + 1);
    }

    /** Clicks NEXT and waits for the next step. Fails with the messages shown when the form does not continue. */
    public void next() {
        String before = currentStep();
        // Clicking NEXT before the saved application has loaded sends an empty update, so wait for the data first
        waitForFormLoaded();
        for (int attempt = 1; attempt <= 2; attempt++) {
            click(nextButton);
            long clickedAt = System.currentTimeMillis();
            // Wait for the next step, a message, or a quiet page (the click was ignored)
            try {
                new WebDriverWait(driver, Duration.ofSeconds(60)).until(d -> !currentStep().equals(before)
                        || isDisplayed(POPUP_OK)
                        || (noRequestsRunning() && System.currentTimeMillis() - clickedAt > 5000));
            } catch (TimeoutException e) {
                break;
            }
            if (!currentStep().equals(before)) {
                waitForFormLoaded();
                return;
            }
            if (isDisplayed(POPUP_OK) || !fieldErrors().isEmpty()) {
                break;
            }
        }
        throw new AssertionError("NEXT on step '" + before + "' did not continue. Shown: " + messagesShown());
    }

    /** Clicks NEXT until the given step is open. */
    public void nextUntil(String step) {
        for (int i = 0; i < 12 && !currentStep().equals(step); i++) {
            next();
        }
        if (!currentStep().equals(step)) {
            throw new AssertionError("Could not reach step '" + step + "', stopped at '" + currentStep() + "'");
        }
    }

    /** Waits until the step has received its saved data from the server. */
    private void waitForFormLoaded() {
        waitForRequests();
        wait.until(d -> !pageText().contains("Loading") && !pageText().contains("Fetching data"));
    }

    /** Validation messages or pop-up text currently shown (the pop-up is closed). */
    private String messagesShown() {
        if (isDisplayed(POPUP_OK)) {
            return closeAllPopups();
        }
        List<String> errors = fieldErrors();
        return errors.isEmpty() ? "no message" : String.join("; ", errors);
    }

    // ---------- Step 1: Applicant details ----------

    /** Selects Category and Designation and fills the initials (name, NIC and title come from the lead). */
    public void fillApplicantDetails(String category, String designation) {
        choose("category", category);
        choose("designation", designation);
        type(By.name("initials"), "A.");
        String lastName = visible(By.name("lastName")).getDomProperty("value");
        type(By.name("nameDenominatedBy"), "A. " + lastName);
    }

    // ---------- Step 2: Mandatory documents ----------

    /** Uploads a distinct device document into every mandatory document of an SC applicant. */
    public void uploadMandatoryDocuments() {
        uploadDocument("NIC / Driving License / Passport", UploadFiles.deviceDocument("test_nic.jpg"));
        uploadDocument("Photograph", UploadFiles.deviceDocument("test_photo.jpg"));
        uploadDocument("HOB Report", UploadFiles.deviceDocument("test_hob.pdf"));
        uploadDocument("Initial Interview Guide", UploadFiles.deviceDocument("test_interview.pdf"));
        uploadDocument("Group Annuity Form", UploadFiles.deviceDocument("test_annuity.pdf"));
    }

    /**
     * For candidates above 50 the LOP must add a Medical Report on the Mandatory Documents step before approving.
     * Uploads a device PDF that is not used in any other field when the field is shown and still empty.
     */
    public void uploadMedicalReportIfRequired() {
        By field = By.xpath("//main//*[contains(normalize-space(text()),'Medical Report')]");
        if (!isDisplayed(field)) {
            return;
        }
        WebElement viewButton = visible(field).findElement(By.xpath("./following::button[normalize-space()='VIEW'][1]"));
        if (!viewButton.isEnabled()) {
            uploadDocument("Medical Report", UploadFiles.deviceDocument("gn_cert.pdf"));
        }
    }

    /** Names of the document upload fields on the Mandatory Documents step. */
    public List<String> documentFields() {
        List<String> names = new ArrayList<>();
        for (WebElement label : driver.findElements(By.cssSelector("main label"))) {
            String text = label.getText().replaceAll("\\s+", " ").trim();
            if (!text.isEmpty()) {
                names.add(text);
            }
        }
        return names;
    }

    /**
     * Uploads a file into the upload box that follows the given field name (e.g. "Photograph")
     * and waits until its VIEW button becomes available.
     */
    public void uploadDocument(String fieldName, Path file) {
        By field = By.xpath("//main//*[contains(normalize-space(text()),'" + fieldName + "')]");
        WebElement label = wait.until(ExpectedConditions.presenceOfElementLocated(field));
        label.findElement(By.xpath("./following::input[@type='file'][1]")).sendKeys(file.toString());
        WebElement viewButton = label.findElement(By.xpath("./following::button[normalize-space()='VIEW'][1]"));
        wait.until(d -> viewButton.isEnabled() || isDisplayed(POPUP_OK));
        if (isDisplayed(POPUP_OK)) {
            throw new AssertionError("Upload of " + file.getFileName() + " into '" + fieldName + "' was refused: " + closeAllPopups());
        }
    }

    // ---------- Step 3: Personal details ----------

    public void fillPersonalDetails() {
        choose("nationality", "Sri Lankan - Sinhalese");
        choose("preferredLanguage", "English");
        choose("civilStatus", "Unmarried");
        clickRadio("children", "no");
        clickRadio("rejoining", "no");
    }

    // ---------- Step 4: Address details ----------

    /** Unique contact numbers, permanent address as correspondence address, and a Western province location. */
    public void fillAddressDetails() {
        type(By.name("whatsapp"), TestData.mobileNumber("071"));
        type(By.name("emergency"), TestData.mobileNumber("072"));
        type(By.name("homeNumber"), TestData.mobileNumber("011"));
        if (!driver.findElement(By.cssSelector("main input[type='checkbox']")).isSelected()) {
            click(By.xpath("//*[normalize-space(text())='Corresponding address is same as the permanent address.']"));
        }
        choose("postalCode", "01000 - Colombo");
        choose("province", "Western");
        choose("district", "Colombo");
        choose("electorate", "Colombo Central");
        choose("secretariat", "Colombo");
        choose("grama", "Stage (III) Part (III)");
    }

    // ---------- Step 5: Qualification & examination details ----------

    /**
     * G.C.E. O/L passed (with its certificate), A/L, Diploma and Degree not done,
     * O/L grades and languages from the ARI profile, IBSL / AML / TAP / Data Protection passed.
     */
    public void fillQualifications(AriProfile profile) {
        // The NIC of the pre-contract exam is filled from the lead once the page data has loaded
        wait.until(d -> !visible(By.name("preNic")).getDomProperty("value").isEmpty());

        examStatus("G.C.E O/L", true);
        By olCertificate = By.xpath("//tr[td[1][normalize-space()='G.C.E O/L']]//input[@type='file']");
        wait.until(ExpectedConditions.presenceOfElementLocated(olCertificate))
                .sendKeys(UploadFiles.deviceDocument("test_gn.pdf").toString());
        wait.until(d -> d.findElement(By.xpath("//tr[td[1][normalize-space()='G.C.E O/L']]")).getText().contains("test_gn.pdf"));
        examStatus("G.C.E A/L", false);
        examStatus("Diploma", false);
        examStatus("Degree", false);

        // O/L grades and language proficiency (these decide most of the ARI points)
        grade("O/L English", profile.englishGrade());
        grade("O/L Maths", profile.mathsGrade());
        tickLanguage("English");
        if (profile.speaksSinhala()) {
            tickLanguage("Sinhala");
        }
        clickRadio("scouts", "no");

        // IBSL exam: filled automatically when SLII results were uploaded for the NIC,
        // otherwise it is marked as done in another company with the confirmation document
        if (ibslValue("ibslAdmissionNo").isEmpty()) {
            click(By.xpath("//*[contains(text(),'Please tick if IBSL exam done in another company')]"));
            uploadDocument("IBSL Certificate", UploadFiles.deviceDocument("test_recruitment.pdf"));
        }

        choose("aml_fiu", "Pass");
        choose("tap", "Pass");
        choose("data_protection", "Pass");

        // Pre-contract exam and O/L index number
        type(By.name("examDate"), TestData.pastDateDigits(5));
        type(By.xpath("//input[@placeholder='Enter Marks Obtained' and not(@name='ibslMarks')]"), "75");
        choose("mode", "Web Based");
        type(By.xpath("//input[@placeholder='Enter Index No.']"), TestData.olIndexNumber());
        choose("olExams[0].year", "2015");
    }

    /** Value of an IBSL field (ibslAdmissionNo, ibslMarks, ibslExamDate, ibslMode) or "" when not shown. */
    public String ibslValue(String fieldName) {
        List<WebElement> fields = driver.findElements(By.name(fieldName));
        return fields.isEmpty() ? "" : String.valueOf(fields.get(0).getDomProperty("value")).trim();
    }

    /** Waits (up to 15 seconds) for the IBSL fields to be filled from the uploaded SLII results. */
    public boolean waitForIbslData() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> !ibslValue("ibslAdmissionNo").isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void examStatus(String exam, boolean passed) {
        List<WebElement> radios = driver.findElements(By.xpath("//tr[td[1][normalize-space()='" + exam + "']]//input[@type='radio']"));
        click(radios.get(passed ? 0 : 1));
    }

    private void grade(String subject, String grade) {
        click(By.xpath("//tr[td[1][normalize-space()='" + subject + "']]//label[span[normalize-space()='" + grade + "']]/input"));
    }

    /** Ticks Read, Write and Speak for a language. */
    private void tickLanguage(String language) {
        for (WebElement box : driver.findElements(By.xpath("//tr[td[1][normalize-space()='" + language + "']]//input[@type='checkbox']"))) {
            if (!box.isSelected()) {
                click(box);
            }
        }
    }

    // ---------- Step 6: Previous work experience and work station ----------

    public void fillWorkDetails() {
        clickRadio("hasAnyExperience", "no");
        chooseFirst("unit");
        clickRadio("introducedBySO", "no");
    }

    /** Branch shown on the Work Station Details (read-only for the applicant's own branch). */
    public String branch() {
        return visible(By.name("branch")).getDomProperty("value");
    }

    /** True when the Branch can be changed (a drop-down, or a text box that is not read-only). */
    public boolean isBranchSelectable() {
        WebElement branch = visible(By.name("branch"));
        return branch.getTagName().equals("select")
                ? branch.isEnabled() && new Select(branch).getOptions().size() > 2
                : branch.isEnabled() && branch.getDomAttribute("readonly") == null;
    }

    // ---------- Step 7: Bank details ----------

    public void fillBankDetails() {
        choose("bank", "Bank of Ceylon");
        choose("branch", "Colombo Fort");
        choose("accountType", "Savings");
        type(By.name("accountNumber"), TestData.bankAccountNumber());
        uploadDocument("Upload Passbook", UploadFiles.deviceDocument("test_bank.pdf"));
    }

    /** Corrects the bank account number (used when resubmitting a rejected application). */
    public void changeAccountNumber() {
        type(By.name("accountNumber"), TestData.bankAccountNumber());
    }

    // ---------- Step 8: Dependants (optional) ----------

    /** No dependant is added; only the Group Annuity Proposal Form confirmation is ticked. */
    public void confirmDependants() {
        By confirmation = By.xpath("//*[contains(text(),'I confirm that the entered')]");
        visible(confirmation);
        // The confirmation is the only ticked-able box when no dependant is added
        boolean ticked = driver.findElements(By.cssSelector("main input[type='checkbox']")).stream().anyMatch(e -> e.isSelected());
        if (!ticked) {
            click(confirmation);
        }
    }

    // ---------- Step 9: Witness details ----------

    public void fillWitness() {
        type(By.xpath("//input[@placeholder='Enter Name']"), "KAMAL PERERA");
        type(By.xpath("//input[@placeholder='Enter NIC']"), TestData.nicForAge(40));
        type(By.xpath("//input[@placeholder='Enter Address']"), "12 Temple Road, Colombo");
        click(By.xpath("//button[normalize-space()='SAVE']"));
        wait.until(d -> pageText().contains("Kamal Perera") || pageText().contains("KAMAL PERERA"));
        uploadDocument("Upload Signature", UploadFiles.deviceDocument("signature.jpeg"));
    }

    // ---------- ARI page: raise request / approve / reject ----------

    /** Total of the Average Recruitment Indicator shown on the last page (calculated by the server after the page opens). */
    public int ariTotal() {
        try {
            wait.until(d -> readAriTotal() > 0);
        } catch (TimeoutException e) {
            // The total really is 0 (or was not calculated); the caller's assertion reports it
        }
        return readAriTotal();
    }

    private int readAriTotal() {
        Matcher m = Pattern.compile("Total\\s+(\\d+)").matcher(mainText());
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    /** ARI points per category, e.g. "Civil Status 10, Age 4, ... Total 23". */
    public String ariBreakdown() {
        String text = mainText();
        int end = text.indexOf("PEP Question");
        return (end > 0 ? text.substring(0, end) : text).replace("Category\nPoints\n", "").replaceAll("\\n(\\d+)", " $1").replace("\n", ", ").trim();
    }

    /** Answers the PEP question with "No" and clicks RAISE REQUEST. Returns the pop-up message(s). */
    public String raiseRequest() {
        ariTotal();
        clickRadio("pep", "No");
        click(raiseRequestButton);
        return closeAllPopups();
    }

    /** Approver enters a remark and clicks APPROVE or REJECT. Returns the pop-up message(s). */
    public String decide(boolean approve, String remark) {
        ariTotal();
        waitForFormLoaded();
        type(By.name("approvalRemark"), remark);
        click(By.xpath("//button[normalize-space()='" + (approve ? "APPROVE" : "REJECT") + "']"));
        return closeAllPopups();
    }

    public boolean isRaiseRequestOffered() {
        return isDisplayed(raiseRequestButton);
    }

    // ---------- Agent Agreement and sales code activation ----------

    /** True when the witness page offers "Download Agent Agreement" (only after the Agency Admin approval). */
    public boolean isAgreementDownloadOffered() {
        return isDisplayed(By.xpath("//button[contains(normalize-space(),'Agent Agreement')]"));
    }

    public void downloadAgreement() {
        click(downloadAgreementButton);
        wait.until(d -> pageText().contains("Agent Agreement Downloaded"));
    }

    public boolean isAgreementUploadShown() {
        return isDisplayed(By.xpath("//*[contains(text(),'Upload Agent Agreement')]"));
    }

    public void uploadAgreement(Path signedAgreement) {
        uploadDocument("Upload Agent Agreement", signedAgreement);
    }

    public boolean isActivationRequestOffered() {
        return isDisplayed(activationRequestButton);
    }

    /** Clicks RAISE REQUEST FOR SALES CODE ACTIVATION. Returns the pop-up message(s). */
    public String raiseActivationRequest() {
        click(activationRequestButton);
        return closeAllPopups();
    }

    /** Sales code shown in a pop-up such as "Agent Sales Code Generated! Sales Code 226292", or null. */
    public static String salesCodeIn(String popupText) {
        Matcher m = Pattern.compile("Sales Code\\s*(\\d+)").matcher(popupText);
        return m.find() ? m.group(1) : null;
    }

    // ---------- Agency Admin: New Application (NIC) ----------

    /** Clicks "New Application", chooses NIC, enters the number and clicks PROCEED. */
    public void startNewApplication(String nic) {
        click(By.xpath("//button[contains(normalize-space(),'New Application')]"));
        clickRadio("idType", "NIC");
        type(By.name("nic"), nic);
        click(By.xpath("//button[normalize-space()='PROCEED']"));
        // Either the applicant form opens or a message (e.g. "This NIC is blacklisted") is shown
        wait.until(d -> currentStep().equals(APPLICANT_STEP) || !newApplicationMessage().isEmpty());
    }

    /** Message shown under the NIC on the New Application page, or "" when there is none. */
    public String newApplicationMessage() {
        if (!currentStep().equals("registration-application")) {
            return "";
        }
        String text = mainText();
        int start = text.indexOf("Business Registration Number");
        String below = start >= 0 ? text.substring(start + "Business Registration Number".length()) : text;
        return below.replace("PROCEED", "").trim();
    }

    /** Applicant details of an agent created directly by the Agency Admin. */
    public void fillAgentApplicant(String firstName, String lastName) {
        choose("category", "SC");
        choose("designation", "Life Insurance Advisor");
        choose("title", "Mr.");
        type(By.name("initials"), "A.");
        type(By.name("firstName"), firstName);
        type(By.name("lastName"), lastName);
        type(By.name("nameDenominatedBy"), "A. " + lastName);
    }

    public void uploadAgentDocuments() {
        uploadDocument("NIC", UploadFiles.deviceDocument("test_nic.jpg"));
        uploadDocument("Recruitement Approval", UploadFiles.deviceDocument("test_recruitment.pdf"));
    }

    public void fillAgentPersonalDetails() {
        choose("nationality", "Sri Lankan - Sinhalese");
        choose("preferredLanguage", "English");
    }

    /** Unique e-mail and contact numbers and a Colombo address. */
    public void fillAgentContactDetails(String email) {
        type(By.name("email"), email);
        type(By.name("mobile"), TestData.mobileNumber("077"));
        type(By.name("whatsapp"), TestData.mobileNumber("071"));
        type(By.name("emergency"), TestData.mobileNumber("072"));
        type(By.name("homeNumber"), TestData.mobileNumber("011"));
        type(By.name("cAddress1"), "10 Temple Road");
        choose("cCity", "Colombo");
        choose("postalCode", "01000 - Colombo");
        choose("province", "Western");
    }

    /**
     * Work Station Details from Agent_Onboarding_flow.md: Group ST, Cluster 2 DH, Cluster 1 GS, Branch WD01, Unit WD0100.
     * Each drop-down is enabled only after the previous one is chosen, so they are filled in page order.
     */
    public void fillAgentWorkStation() {
        choose("groupDepartment", "ST");
        choose("cluster2", "DH");
        choose("cluster1", "GS");
        choose("branch", "WD01");
        choose("unit", "WD0100");
    }

    /** Clicks SUBMIT on the last page of the Agency Admin form. Returns the pop-up message(s). */
    public String submitAgent() {
        click(By.xpath("//button[normalize-space()='SUBMIT']"));
        return closeAllPopups();
    }

    // ---------- Helpers ----------

    private String mainText() {
        return driver.findElement(By.cssSelector("main")).getText();
    }

    private void clickRadio(String name, String value) {
        By radio = By.xpath("//input[@name='" + name + "' and @value='" + value + "']");
        if (!visible(radio).isSelected()) {
            click(radio);
        }
    }

    /** Selects an option once it is available (drop-downs such as District are filled after the previous choice). */
    private void choose(String selectName, String optionText) {
        By select = By.name(selectName);
        wait.until(d -> {
            WebElement element = d.findElement(select);
            return element.isEnabled() && !element.findElements(By.xpath("./option[normalize-space()='" + optionText + "']")).isEmpty();
        });
        selectByText(select, optionText);
    }

    /** Selects the first real option of a drop-down (after "Select ..."). */
    private void chooseFirst(String selectName) {
        By select = By.name(selectName);
        wait.until(d -> d.findElement(select).isEnabled() && d.findElement(select).findElements(By.tagName("option")).size() > 1);
        new Select(visible(select)).selectByIndex(1);
    }
}
