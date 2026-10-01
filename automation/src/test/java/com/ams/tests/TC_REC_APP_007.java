package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_007: Verify onboarding eligibility for candidate age < 22 after Agency Admin approval
 * Expected: HOB should be able to proceed the lead to onboarding and HOB, AS, and LOP should be able to fill the onboarding form
 */
public class TC_REC_APP_007 extends BaseTest {

    @Test
    public void hobCanProceedYoungLeadToOnboardingAfterAgencyAdminApproval() throws Exception {
        // Candidate under 22: HOB, Immediate Supervisor and Agency Admin approve
        Lead lead = TestData.youngLead();
        flows.fullyApproveYoungLead(lead);

        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        boolean proceedEnabled = leadPage.isProceedEnabled(lead.nic());
        String onboardingPage = proceedEnabled ? leadPage.proceedToOnboarding(lead.nic()) : "(not opened)";

        actual("Proceed enabled after Agency Admin approval: " + proceedEnabled + ". Page opened: " + onboardingPage);
        Assert.assertTrue(proceedEnabled, "Proceed is not enabled after all approvals");
        Assert.assertFalse(onboardingPage.contains("/campaign-management/leads"), "Proceed did not open the onboarding screen");
    }
}
