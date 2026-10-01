package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_009: Verify rejection by TRSM (Immediate Supervisor) during interview approval
 * Expected: Lead should be rejected immediately and should not proceed to Agency Admin or onboarding
 */
public class TC_REC_APP_009 extends BaseTest {

    @Test
    public void supervisorRejectionStopsLeadBeforeAgencyAdmin() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor rejects the lead
        String message = flows.rejectInTray(Config.supervisor(), lead);

        // The lead must not reach Agency Admin and must not be proceeded to onboarding
        flows.findInApprovalTray(Config.agencyAdmin(), "In Progress", lead.fullName());
        boolean atAgencyAdmin = approvalTray.isListed(lead.fullName());
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        boolean proceedEnabled = leadPage.isProceedEnabled(lead.nic());

        actual("Supervisor pop-up: '" + message + "'. Listed at Agency Admin: " + atAgencyAdmin + ". HOB row: " + leadPage.rowText(lead.nic()));
        Assert.assertTrue(message.contains("Rejected Successfully"), "Supervisor rejection failed: " + message);
        Assert.assertFalse(atAgencyAdmin, "Rejected lead still went to Agency Admin");
        Assert.assertFalse(proceedEnabled, "Rejected lead can still be proceeded to onboarding");
    }
}
