package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC_REC_APP_012: Verify HOB & AS can initiate onboarding only after completion of all required interview approvals
 * Expected: System should allow proceeding to onboarding only after all mandatory approvals are completed
 */
public class TC_REC_APP_012 extends BaseTest {

    @Test
    public void onboardingAllowedOnlyAfterAllInterviewApprovals() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Before the Supervisor approves: Proceed must be disabled for HOB
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        boolean proceedBefore = leadPage.isProceedEnabled(lead.nic());

        // After the Supervisor approves: Proceed must be enabled for HOB and AS
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Supervisor approval");
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        boolean proceedAfterHob = leadPage.isProceedEnabled(lead.nic());
        flows.openLeadList(Config.agencySupervisor(), flows.sharedCampaign());
        boolean proceedAfterAs = leadPage.isProceedEnabled(lead.nic());

        actual("Proceed before Supervisor approval: " + proceedBefore + "; after approval - HOB: " + proceedAfterHob + ", AS: " + proceedAfterAs);
        Assert.assertFalse(proceedBefore, "Proceed was enabled before all approvals");
        Assert.assertTrue(proceedAfterHob, "HOB cannot proceed after all approvals");
        Assert.assertTrue(proceedAfterAs, "AS cannot proceed after all approvals");
    }
}
