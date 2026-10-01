package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_DM_005: Verify document access restriction.
 * Expected: Access to the document is denied.
 */
public class RLC_DM_005 extends BaseTest {

    // HOB/AS of a different branch, given by the tester for this test case only
    private static final String OTHER_BRANCH_AH = "102187";
    private static final String OTHER_BRANCH_AS = "102191";

    @Test
    public void documentAccessDeniedForUnauthorizedUser() throws Exception {
        // Precondition: HOB of branch WD01 creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        String campaign = flows.sharedCampaign();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Note the lead's address from the Immediate Supervisor's Approval Tray (used for the direct-URL check)
        flows.findInApprovalTray(Config.supervisor(), "In Progress", lead.fullName());
        approvalTray.openDetails(lead.fullName());
        String leadPath = leadPage.currentPath();

        List<String> observations = new ArrayList<>();
        List<String> accessGranted = new ArrayList<>();
        for (String[] user : List.of(new String[] {"AH", OTHER_BRANCH_AH}, new String[] {"AS", OTHER_BRANCH_AS})) {
            String role = user[0] + " " + user[1];

            // Step 3: try to open the lead from Recruitment Leads Mgmt
            loginPage.loginAs(user[1]);
            menu.open("Recruitment Leads Mgmt");
            boolean campaignShown = campaignPage.findCampaign(campaign) != null;
            boolean leadShown = false;
            if (campaignShown) {
                campaignPage.openLeads(campaign);
                leadShown = leadPage.rowForNic(lead.nic()) != null;
            }

            // Step 4: try to open the lead's document directly via its address
            leadPage.openPath(leadPath);
            boolean documentShown = leadPage.waitForText("Document 1");
            boolean documentOpened = documentShown && leadPage.viewDocument("Document 1");

            observations.add(role + ": campaign listed: " + campaignShown + ", lead listed: " + leadShown
                    + ", document via " + leadPath + " shown: " + documentShown + ", VIEW opened: " + documentOpened);
            if (leadShown) {
                accessGranted.add(role + " sees the WD01 lead in Recruitment Leads Mgmt");
            }
            if (documentOpened) {
                accessGranted.add(role + " opened the WD01 lead's document via " + leadPath);
            }
        }

        actual("WD01 lead " + lead.nic() + ". " + String.join(". ", observations));
        Assert.assertTrue(accessGranted.isEmpty(), "Access to the document was not denied: " + String.join("; ", accessGranted));
    }
}
