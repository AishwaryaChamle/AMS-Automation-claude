package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_003: Verify onboarding eligibility for candidate age >= 22 after TRSM (Immediate Supervisor) approval
 * Expected: HOB should be able to proceed the lead to onboarding and HOB, AS, LOP should be able to fill the onboarding form
 */
public class TC_REC_APP_003 extends BaseTest {

    @Test
    public void hobCanProceedAdultLeadToOnboardingAfterSupervisorApproval() throws Exception {
        // Candidate above 22: HOB approves, then the Immediate Supervisor approves
        Lead lead = TestData.adultLead();
        flows.fullyApproveAdultLead(lead);

        // HOB opens the lead list: Proceed must be enabled and open the onboarding screen
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        boolean proceedEnabled = leadPage.isProceedEnabled(lead.nic());
        String onboardingPage = proceedEnabled ? leadPage.proceedToOnboarding(lead.nic()) : "(not opened)";

        actual("Proceed enabled for HOB: " + proceedEnabled + ". Page opened by Proceed: " + onboardingPage);
        Assert.assertTrue(proceedEnabled, "Proceed is not enabled after Supervisor approval");
        Assert.assertFalse(onboardingPage.contains("/campaign-management/leads"), "Proceed did not open the onboarding screen");
    }
}
