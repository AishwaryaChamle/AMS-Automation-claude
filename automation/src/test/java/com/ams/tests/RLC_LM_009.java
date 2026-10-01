package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_LM_009: Verify invalid transition.
 * Expected: Transition is prevented and an appropriate validation/error is displayed.
 */
public class RLC_LM_009 extends BaseTest {

    @Test
    public void invalidTransitionPrevented() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Supervisor has not approved yet: moving to onboarding must be blocked
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        boolean proceedEnabled = leadPage.isProceedEnabled(lead.nic());

        actual("Proceed enabled before Supervisor approval: " + proceedEnabled + " (row: " + leadPage.rowText(lead.nic()) + ")");
        Assert.assertFalse(proceedEnabled, "Lead can move to onboarding before the required approval");
    }
}
