package com.ams.utils;

import com.ams.base.Config;
import com.ams.pages.ApprovalTrayPage;
import com.ams.pages.CampaignPage;
import com.ams.pages.LeadPage;
import com.ams.pages.LoginPage;
import com.ams.pages.MenuBar;
import com.ams.pages.UploadPage;
import com.ams.utils.TestData.Campaign;
import com.ams.utils.TestData.Lead;

/**
 * Business flows from Recruitment_Leads_Mgmt.md that several test cases share:
 * create a campaign, create a lead as HOB, approve / reject it in the Approval Tray.
 */
public class Flows {

    public static final String AGREED = "Agreed for the interview";
    public static final String NOT_AVAILABLE = "Not available for the interview";
    public static final String CANNOT_BE_CONTACTED = "Cannot be contacted";
    public static final String DOCUMENT_TYPE = "Project 100 Report";

    // Notification texts (Approval Tray / notification panel)
    public static String pendingNotification(Lead lead) {
        return "Recruitment Lead request for " + lead.fullName() + " is pending your approval";
    }

    public static String approvedNotification(Lead lead, String approver) {
        return "Recruitment Lead request for " + lead.fullName() + " has been approved by " + approver;
    }

    public static String rejectedNotification(Lead lead, String approver) {
        return "Recruitment Lead request for " + lead.fullName() + " has been rejected by " + approver;
    }

    /** One ongoing campaign is created per run and shared by the lead tests. */
    private static volatile String sharedCampaignName;

    /** A second campaign used only by the bulk-upload tests, so bulk leads do not crowd the HOB lead list. */
    private static volatile String bulkCampaignName;

    private final LoginPage loginPage;
    private final MenuBar menu;
    private final CampaignPage campaignPage;
    private final LeadPage leadPage;
    private final ApprovalTrayPage approvalTray;
    private final UploadPage uploadPage;

    public Flows(LoginPage loginPage, MenuBar menu, CampaignPage campaignPage, LeadPage leadPage,
                 ApprovalTrayPage approvalTray, UploadPage uploadPage) {
        this.loginPage = loginPage;
        this.menu = menu;
        this.campaignPage = campaignPage;
        this.leadPage = leadPage;
        this.approvalTray = approvalTray;
        this.uploadPage = uploadPage;
    }

    // ---------- Campaigns ----------

    /** Agency Admin creates a campaign. Returns the pop-up message; the pop-up is closed afterwards. */
    public String createCampaign(Campaign campaign) {
        return createCampaignAs(Config.agencyAdmin(), campaign);
    }

    /** A campaign creator (Agency Admin or Marketing Team) creates a campaign. Returns the pop-up message. */
    public String createCampaignAs(String userId, Campaign campaign) {
        loginPage.loginAs(userId);
        menu.openRecruitmentCard("Campaign Management");
        campaignPage.clickCreateCampaign();
        campaignPage.fillAndProceed(campaign);
        String message = campaignPage.popupMessage();
        campaignPage.clickOk();
        return message;
    }

    /** Name of an Ongoing campaign that leads can be added to (created on first use). */
    public String sharedCampaign() {
        // Lock on the class: tests run in parallel and must all share the same campaign
        synchronized (Flows.class) {
            return createSharedCampaignIfNeeded();
        }
    }

    private String createSharedCampaignIfNeeded() {
        if (sharedCampaignName == null) {
            Campaign campaign = TestData.ongoingCampaign();
            String message = createCampaign(campaign);
            expectSuccess(message, "Shared campaign creation");
            sharedCampaignName = campaign.name();
        }
        return sharedCampaignName;
    }

    /** Ongoing campaign used by the bulk-upload tests (created on first use). */
    public String bulkCampaign() {
        synchronized (Flows.class) {
            return createBulkCampaignIfNeeded();
        }
    }

    private String createBulkCampaignIfNeeded() {
        if (bulkCampaignName == null) {
            Campaign base = TestData.ongoingCampaign();
            Campaign campaign = new Campaign(TestData.campaignName(), base.startDate(), base.endDate(), base.budget(), base.leadTarget());
            expectSuccess(createCampaign(campaign), "Bulk campaign creation");
            bulkCampaignName = campaign.name();
        }
        return bulkCampaignName;
    }

    /** Agency Admin opens Campaign Management and searches for a campaign. */
    public void openCampaignListAsAgencyAdmin(String campaignName) {
        openCampaignListAs(Config.agencyAdmin(), campaignName);
    }

    /** A campaign manager (Agency Admin / Marketing) opens Campaign Management and searches for a campaign. */
    public void openCampaignListAs(String userId, String campaignName) {
        loginPage.loginAs(userId);
        menu.openRecruitmentCard("Campaign Management");
        campaignPage.searchByName(campaignName);
    }

    /** Agency Admin opens the Bulk Upload page of a campaign (Action > Bulk Upload). */
    public void openBulkUpload(String campaignName) {
        openCampaignListAsAgencyAdmin(campaignName);
        campaignPage.chooseAction(campaignName, "Bulk Upload");
        uploadPage.uploadInstruction();
    }

    /** Agency Admin uploads a bulk-lead file for a campaign. Returns the result message (pop-up closed). */
    public String bulkUpload(String campaignName, java.nio.file.Path file) {
        openBulkUpload(campaignName);
        String message = uploadPage.upload(file);
        uploadPage.closePopupIfShown();
        return message;
    }

    // ---------- SLII / Blacklisting uploads ----------

    /** Agency Admin opens a card (e.g. "Blacklisting") and uploads a file. Returns the result message (pop-up closed). */
    public String uploadAsAgencyAdmin(String card, java.nio.file.Path file) {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard(card);
        String message = uploadPage.upload(file);
        uploadPage.closePopupIfShown();
        return message;
    }

    /** Uploads another file on the page that is already open (no new login). */
    public String uploadAgain(java.nio.file.Path file) {
        // After an upload the uploader box disappears until the page is reloaded
        uploadPage.reload();
        String message = uploadPage.upload(file);
        uploadPage.closePopupIfShown();
        return message;
    }

    // ---------- Notifications ----------

    /** Logs in and returns the text of the notification panel. */
    public String notificationsOf(String userId) {
        loginPage.loginAs(userId);
        return menu.notificationsText();
    }

    // ---------- Leads ----------

    /** Logs in and opens the Lead List of a campaign (HOB or AS). */
    public void openLeadList(String userId, String campaignName) {
        loginPage.loginAs(userId);
        menu.open("Recruitment Leads Mgmt");
        campaignPage.openLeads(campaignName);
    }

    /** Opens the Create Lead form as the given user and fills in the candidate details. */
    public void startLead(String userId, String campaignName, Lead lead) {
        openLeadList(userId, campaignName);
        leadPage.clickCreateLead();
        leadPage.fillLeadDetails(lead);
        leadPage.waitForDerivedAge();
    }

    /**
     * HOB creates a lead, sets "Agreed for the interview" with today's date and clicks APPROVED.
     * For candidates under 22 a supporting document is uploaded first.
     * Returns the pop-up message and closes it.
     */
    public String hobCreatesAndApprovesLead(Lead lead) throws Exception {
        startLead(Config.hob(), sharedCampaign(), lead);
        if (lead.age() < 22) {
            leadPage.uploadSupportingDocument(DOCUMENT_TYPE, UploadFiles.supportingImage());
        }
        leadPage.setInterview(AGREED, TestData.today(), "Approved by HOB for interview - automation");
        leadPage.clickApproved();
        return closePopup();
    }

    /**
     * HOB creates a lead and rejects it. Returns the pop-up message and closes it.
     * (APPROVED / REJECT are only offered after choosing "Agreed for the interview".)
     */
    public String hobCreatesAndRejectsLead(Lead lead) throws Exception {
        startLead(Config.hob(), sharedCampaign(), lead);
        leadPage.setInterview(AGREED, TestData.today(), "Rejected by HOB - automation");
        leadPage.clickReject();
        return closePopup();
    }

    /** HOB creates a lead with a status that needs no approval (e.g. "Cannot be contacted") and submits it. */
    public String hobCreatesLeadWithStatus(Lead lead, String interviewStatus) throws Exception {
        startLead(Config.hob(), sharedCampaign(), lead);
        leadPage.setInterview(interviewStatus, null, null);
        leadPage.clickSubmit();
        return closePopup();
    }

    /**
     * Opens Create Lead as HOB and fills the form without waiting for DOB / age
     * (used with invalid or blacklisted NICs, where the application may refuse the NIC).
     */
    public void startLeadWithoutChecks(Lead lead) {
        openLeadList(Config.hob(), sharedCampaign());
        leadPage.clickCreateLead();
        leadPage.fillLeadDetails(lead);
    }

    /** HOB creates a lead and only submits it (no interview decision yet). */
    public String hobCreatesLead(Lead lead) throws Exception {
        startLead(Config.hob(), sharedCampaign(), lead);
        leadPage.clickSubmit();
        return closePopup();
    }

    // ---------- Approval tray ----------

    /**
     * Logs in, clicks "Approval Tray" in the side menu and searches for the candidate in the given tab.
     * The menu is always clicked, also when the user lands on the Approval Tray after login,
     * so the requests are checked the way the tester does it.
     */
    public void findInApprovalTray(String userId, String tab, String candidateName) {
        loginPage.loginAs(userId);
        menu.open("Approval Tray");
        if ("In Progress".equals(tab)) {
            approvalTray.search(candidateName);
        } else {
            // The search box only filters the In Progress tab, so list the other tab without searching
            approvalTray.openTab(tab);
            approvalTray.showAllRows();
        }
    }

    /**
     * Approver opens the lead from the Approval Tray and approves it. Returns the pop-up message.
     * The Immediate Supervisor sets an interview status and date; the Agency Admin chooses "Selected".
     */
    public String approveInTray(String userId, Lead lead) {
        findInApprovalTray(userId, "In Progress", lead.fullName());
        approvalTray.openDetails(lead.fullName());
        if (leadPage.isAgencyAdminDecisionShown()) {
            leadPage.setAgencyAdminDecision("Selected", "Approved by Agency Admin - automation");
        } else {
            leadPage.setInterview(AGREED, TestData.today(), "Approved by Supervisor - automation");
        }
        leadPage.clickApproved();
        return closePopup();
    }

    /** Approver opens the lead from the Approval Tray and rejects it. Returns the pop-up message. */
    public String rejectInTray(String userId, Lead lead) {
        findInApprovalTray(userId, "In Progress", lead.fullName());
        approvalTray.openDetails(lead.fullName());
        if (leadPage.isAgencyAdminDecisionShown()) {
            leadPage.setAgencyAdminDecision("Not Selected", "Rejected by Agency Admin - automation");
        } else {
            leadPage.setInterview(AGREED, TestData.today(), "Rejected by Supervisor - automation");
        }
        leadPage.clickReject();
        return closePopup();
    }

    private String closePopup() {
        return leadPage.closeAllPopups();
    }

    // ---------- Complete approval chains ----------

    /** Candidate above 22: HOB approves, then the Immediate Supervisor approves. */
    public void fullyApproveAdultLead(Lead lead) throws Exception {
        expectSuccess(hobCreatesAndApprovesLead(lead), "HOB approval");
        expectSuccess(approveInTray(Config.supervisor(), lead), "Immediate Supervisor approval");
    }

    /** Candidate under 22: HOB, Immediate Supervisor and Agency Admin approve. */
    public void fullyApproveYoungLead(Lead lead) throws Exception {
        expectSuccess(hobCreatesAndApprovesLead(lead), "HOB approval");
        expectSuccess(approveInTray(Config.supervisor(), lead), "Immediate Supervisor approval");
        expectSuccess(approveInTray(Config.agencyAdmin(), lead), "Agency Admin approval");
    }

    /** Success pop-ups say "... Successfully!" or "New Lead Initiated!". */
    public static boolean isSuccess(String popupMessage) {
        return (popupMessage.contains("Successfully") || popupMessage.contains("Initiated"))
                && !popupMessage.contains("Alert") && !popupMessage.contains("Failed");
    }

    public static void expectSuccess(String popupMessage, String step) {
        if (!isSuccess(popupMessage)) {
            throw new AssertionError(step + " did not succeed. Message shown: " + popupMessage);
        }
    }
}
