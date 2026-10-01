package com.ams.utils;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.pages.ApprovalTrayPage;
import com.ams.pages.LeadPage;
import com.ams.pages.LoginPage;
import com.ams.pages.OnboardingPage;
import com.ams.utils.TestData.AriProfile;
import com.ams.utils.TestData.Lead;
import org.openqa.selenium.support.ui.FluentWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * Agent onboarding flows from Agent_Onboarding_flow.md shared by the onboarding test cases:
 * turn a lead into a "New Lead", fill and raise the onboarding request, approve / reject it,
 * upload the Agent Agreement and activate the sales code.
 *
 * Routing (Agent_Onboarding_flow.md, section 3): ARI total below 30 goes to the Sr MBD
 * (Immediate Supervisor 102118), then LOP, then Agency Admin. ARI 30 or more, or age 50 or more,
 * goes to LOP and then Agency Admin.
 */
public class OnboardingFlows {

    // Pop-up messages
    public static final String REQUEST_RAISED = "Agent Onboarding Request Initiated";
    public static final String APPROVED = "approved successfully";
    public static final String REJECTED = "rejected successfully";
    public static final String SALES_CODE_GENERATED = "Sales Code Generated";
    public static final String ACTIVATION_RAISED = "Activation Request Initiated";
    public static final String SALES_CODE_ACTIVATED = "Sales Code Activated";

    // Status column of the Agent Onboarding summary
    public static final String STATUS_NEW_LEAD = "New Lead";
    public static final String STATUS_PENDING = "Pending Submission";
    public static final String STATUS_REJECTED = "Rejected";
    public static final String STATUS_SUBMITTED = "Submitted";
    public static final String STATUS_APPROVED = "Approved";

    private final LoginPage loginPage;
    private final LeadPage leadPage;
    private final ApprovalTrayPage approvalTray;
    private final OnboardingPage onboardingPage;
    private final Flows flows;

    public OnboardingFlows(LoginPage loginPage, LeadPage leadPage, ApprovalTrayPage approvalTray,
                           OnboardingPage onboardingPage, Flows flows) {
        this.loginPage = loginPage;
        this.leadPage = leadPage;
        this.approvalTray = approvalTray;
        this.onboardingPage = onboardingPage;
        this.flows = flows;
    }

    // ---------- Roles ----------

    /** Sr MBD / SMBD approver = Immediate Supervisor 102118 (confirmed by the business). */
    public static String smbd() {
        return Config.supervisor();
    }

    // ---------- Notification texts ----------

    public static String pendingNotification(Lead lead) {
        return "Agent Onboarding request for " + lead.fullName() + " is pending your approval";
    }

    public static String rejectedNotification(Lead lead) {
        return "Your Agent Onboarding request for " + lead.fullName() + " has been rejected by";
    }

    public static String approvedNotification(Lead lead) {
        return "Your Agent Onboarding request for " + lead.fullName() + " has been approved by";
    }

    /** How many times a text appears in the notification panel. */
    public static int occurrences(String notifications, String text) {
        return notifications.split(java.util.regex.Pattern.quote(text), -1).length - 1;
    }

    // ---------- New Lead ----------

    /** A candidate aged 30 whose lead is fully approved and proceeded to onboarding by HOB. */
    public Lead newLead() throws Exception {
        return newLead(TestData.leadAged(30));
    }

    /**
     * Recruitment_Leads_Mgmt.md: HOB creates the lead and approves it for interview, the Immediate
     * Supervisor approves it, and HOB clicks Proceed. The candidate is then a "New Lead" in Agent Onboarding.
     */
    public Lead newLead(Lead lead) throws Exception {
        flows.fullyApproveAdultLead(lead);
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        leadPage.proceedToOnboarding(lead.nic());
        return lead;
    }

    // ---------- Agent Onboarding summary ----------

    /** Logs in, opens Agent Management > Agent Onboarding and searches for the applicant. */
    public void openInSummary(String userId, Lead lead) {
        loginPage.loginAs(userId);
        onboardingPage.openSummary();
        onboardingPage.search(lead.fullName());
    }

    /** Status of the application as seen by the user, or "not listed". */
    public String statusFor(String userId, Lead lead) {
        openInSummary(userId, lead);
        return onboardingPage.isListed(lead.fullName()) ? onboardingPage.status(lead.fullName()) : "not listed";
    }

    // ---------- Raise the onboarding request ----------

    /**
     * The requester (HOB, AS or LOP) edits the New Lead and completes every step up to the
     * Average Recruitment Indicator page. Returns the ARI total.
     */
    public int fillApplication(String requester, Lead lead, AriProfile profile) {
        openInSummary(requester, lead);
        onboardingPage.edit(lead.fullName());
        fillSteps(profile);
        return onboardingPage.ariTotal();
    }

    /** Fills the 9 steps of an SC / Life Insurance Advisor application from step 1 to the ARI page. */
    private void fillSteps(AriProfile profile) {
        onboardingPage.fillApplicantDetails("SC", "Life Insurance Advisor");
        onboardingPage.next();
        onboardingPage.uploadMandatoryDocuments();
        onboardingPage.next();
        onboardingPage.fillPersonalDetails();
        onboardingPage.next();
        onboardingPage.fillAddressDetails();
        onboardingPage.next();
        onboardingPage.fillQualifications(profile);
        onboardingPage.next();
        onboardingPage.fillWorkDetails();
        onboardingPage.next();
        onboardingPage.fillBankDetails();
        onboardingPage.next();
        onboardingPage.confirmDependants();
        onboardingPage.next();
        onboardingPage.fillWitness();
        onboardingPage.next();
    }

    /** Fills the application and clicks RAISE REQUEST (checks the success pop-up). Returns the ARI total. */
    public int raiseRequest(String requester, Lead lead, AriProfile profile) {
        int total = fillApplication(requester, lead, profile);
        expect(onboardingPage.raiseRequest(), REQUEST_RAISED, "Raising the onboarding request as " + requester);
        return total;
    }

    /**
     * The requester opens the rejected application, corrects it (new bank account number, as a
     * resubmission needs at least one change) and raises the request again. Returns the pop-up message.
     */
    public String resubmit(String requester, Lead lead) {
        openInSummary(requester, lead);
        onboardingPage.edit(lead.fullName());
        onboardingPage.nextUntil(OnboardingPage.BANK_STEP);
        onboardingPage.changeAccountNumber();
        onboardingPage.nextUntil(OnboardingPage.ARI_STEP);
        return onboardingPage.raiseRequest();
    }

    // ---------- Approval tray ----------

    /** True when the onboarding request is waiting in the user's Approval Tray (In Progress). */
    public boolean isPendingWith(String userId, Lead lead) {
        flows.findInApprovalTray(userId, "In Progress", lead.fullName());
        return approvalTray.isListed(lead.fullName());
    }

    /** Approver opens the request from the Approval Tray, goes to the ARI page and approves or rejects it. */
    public String decide(String approverId, Lead lead, boolean approve, String remark) {
        flows.findInApprovalTray(approverId, "In Progress", lead.fullName());
        approvalTray.openOnboardingDetails(lead.fullName());
        // Candidates above 50: the LOP adds the Medical Report on the documents step
        if (onboardingPage.currentStep().equals(OnboardingPage.APPLICANT_STEP)) {
            onboardingPage.nextUntil(OnboardingPage.DOCUMENTS_STEP);
            onboardingPage.uploadMedicalReportIfRequired();
        }
        onboardingPage.nextUntil(OnboardingPage.ARI_STEP);
        return onboardingPage.decide(approve, remark);
    }

    /** Approves the request. Returns the pop-up message (for the Agency Admin it contains the sales code). */
    public String approve(String approverId, Lead lead) {
        String message = decide(approverId, lead, true, "Approved by " + approverId + " - automation");
        if (!message.toLowerCase().contains(APPROVED.toLowerCase()) && !message.contains("Sales Code")) {
            throw new AssertionError("Approval by " + approverId + " did not succeed. Message shown: " + message);
        }
        return message;
    }

    /** Rejects the request with the given remark. Returns the pop-up message. */
    public String reject(String approverId, Lead lead, String remark) {
        String message = decide(approverId, lead, false, remark);
        expect(message, REJECTED, "Rejection by " + approverId);
        return message;
    }

    /** Approvers of the request: Sr MBD, LOP, Agency Admin for ARI below 30 (age below 50); otherwise LOP, Agency Admin. */
    public static List<String> approvalChain(int ariTotal, int age) {
        if (ariTotal < 30 && age < 50) {
            return List.of(smbd(), Config.lop(), Config.agencyAdmin());
        }
        return List.of(Config.lop(), Config.agencyAdmin());
    }

    /** Every approver in the chain approves. The Agency Admin approval generates the sales code, which is returned. */
    public String approveAll(Lead lead, int ariTotal) {
        String message = "";
        for (String approver : approvalChain(ariTotal, lead.age())) {
            message = approve(approver, lead);
        }
        expect(message, SALES_CODE_GENERATED, "Agency Admin approval");
        return OnboardingPage.salesCodeIn(message);
    }

    // ---------- Agent Agreement and sales code activation ----------

    /** The requester opens the approved application (Edit) on the witness page, where the agreement is handled. */
    public void openAgreementStage(String requester, Lead lead) {
        openInSummary(requester, lead);
        onboardingPage.edit(lead.fullName());
        onboardingPage.nextUntil(OnboardingPage.WITNESS_STEP);
    }

    /**
     * The requester downloads the Agent Agreement, uploads it as the signed copy and raises the
     * sales code activation request. Returns the pop-up message.
     */
    public String uploadAgreementAndRequestActivation(String requester, Lead lead) {
        openInSummary(requester, lead);
        String salesCode = onboardingPage.salesCode(lead.fullName());
        onboardingPage.edit(lead.fullName());
        onboardingPage.nextUntil(OnboardingPage.WITNESS_STEP);
        onboardingPage.uploadAgreement(agreementFile(salesCode));
        onboardingPage.nextUntil(OnboardingPage.ARI_STEP);
        return onboardingPage.raiseActivationRequest();
    }

    /** Downloads the agreement on the open witness page (if not downloaded yet) and returns the file. */
    private Path agreementFile(String salesCode) {
        if (onboardingPage.isAgreementDownloadOffered() && !onboardingPage.pageText().contains("Agent Agreement Downloaded")) {
            onboardingPage.downloadAgreement();
        }
        Path file = BaseTest.DOWNLOAD_DIR.resolve("agreement_" + salesCode + ".pdf");
        // Wait until Chrome has finished saving the file
        new FluentWait<>(file).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofMillis(500))
                .withMessage("Agent Agreement " + file.getFileName() + " was not downloaded")
                .until(f -> Files.exists(f) && f.toFile().length() > 0);
        return file;
    }

    /** Agency Admin approves the sales code activation request. Returns the pop-up message. */
    public String activate(Lead lead) {
        String message = decide(Config.agencyAdmin(), lead, true, "Sales code activated - automation");
        expect(message, SALES_CODE_ACTIVATED, "Sales code activation");
        return message;
    }

    /** New lead → onboarding request → all approvals → agreement → activation. Returns the active sales code. */
    public String onboardAndActivate(String requester, Lead lead, AriProfile profile) {
        int total = raiseRequest(requester, lead, profile);
        String salesCode = approveAll(lead, total);
        expect(uploadAgreementAndRequestActivation(requester, lead), ACTIVATION_RAISED, "Sales code activation request");
        activate(lead);
        return salesCode;
    }

    // ---------- Agency Admin: create an agent directly ----------

    /** Result of an Agency Admin "New Application". */
    public record CreatedAgent(Lead person, String branch, boolean branchSelectable, String message, String salesCode) {
    }

    /** Agent_Onboarding_flow.md 1A: Agency Admin creates an agent with a new NIC and submits it. */
    public CreatedAgent createAgentAsAgencyAdmin() {
        Lead person = TestData.leadAged(30);
        loginPage.loginAs(Config.agencyAdmin());
        onboardingPage.openSummary();
        onboardingPage.startNewApplication(person.nic());
        if (!onboardingPage.currentStep().equals(OnboardingPage.APPLICANT_STEP)) {
            throw new AssertionError("NIC " + person.nic() + " was not accepted: " + onboardingPage.newApplicationMessage());
        }
        onboardingPage.fillAgentApplicant(person.firstName(), person.lastName());
        onboardingPage.next();
        onboardingPage.uploadAgentDocuments();
        onboardingPage.next();
        onboardingPage.fillAgentPersonalDetails();
        onboardingPage.next();
        onboardingPage.fillAgentContactDetails(person.email());
        onboardingPage.next();
        // Branch is enabled only after Group, Cluster 2 and Cluster 1 are chosen, so it is read after the page is filled
        onboardingPage.fillAgentWorkStation();
        String branch = onboardingPage.branch();
        boolean branchSelectable = onboardingPage.isBranchSelectable();
        onboardingPage.next();
        onboardingPage.fillBankDetails();
        String message = onboardingPage.submitAgent();
        return new CreatedAgent(person, branch, branchSelectable, message, OnboardingPage.salesCodeIn(message));
    }

    // ---------- Checks ----------

    /** Fails the test when the pop-up message does not contain the expected text. */
    public static void expect(String message, String expected, String step) {
        if (message == null || !message.toLowerCase().contains(expected.toLowerCase())) {
            throw new AssertionError(step + " did not succeed. Message shown: " + message);
        }
    }
}
