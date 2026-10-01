package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_010: Verify rejection by Agency Admin for candidate age < 22
 * Expected: Lead should be rejected immediately and should not proceed to onboarding
 */
public class TC_REC_APP_010 extends BaseTest {

    @Test
    public void agencyAdminRejectsYoungLead() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor approves, so the lead moves to Agency Admin
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Immediate Supervisor approval");

        // Agency Admin rejects the lead
        String message = flows.rejectInTray(Config.agencyAdmin(), lead);

        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        boolean proceedEnabled = leadPage.isProceedEnabled(lead.nic());

        actual("Agency Admin pop-up: '" + message + "'. HOB lead row: " + leadPage.rowText(lead.nic()));
        Assert.assertTrue(message.contains("Rejected Successfully"), "Agency Admin rejection failed: " + message);
        Assert.assertFalse(proceedEnabled, "Rejected lead can still be proceeded to onboarding");
    }
}
