package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_AT_007: Verify approved lead onboarding.
 * Expected: Approved lead moves to onboarding as required.
 */
public class RLC_AT_007 extends BaseTest {

    @Test
    public void approvedLeadMovesToOnboarding() throws Exception {
        Lead lead = TestData.adultLead();
        flows.fullyApproveAdultLead(lead);
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        String page = leadPage.isProceedEnabled(lead.nic()) ? leadPage.proceedToOnboarding(lead.nic()) : "(Proceed disabled)";

        actual("Page opened by Proceed: " + page);
        Assert.assertFalse(page.contains("Proceed disabled") || page.contains("/campaign-management/leads"), "Approved lead did not move to onboarding");
    }
}
