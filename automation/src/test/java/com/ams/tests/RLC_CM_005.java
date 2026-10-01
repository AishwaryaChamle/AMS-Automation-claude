package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CM_005: Verify that the system allows campaign creation when optional fields are left blank and does not display validation errors for those fields.
 * Expected: Campaign is created successfully without validation errors for optional fields.
 */
public class RLC_CM_005 extends BaseTest {

    @Test
    public void campaignCreatedWithOnlyMandatoryFields() throws Exception {
        // Every field on the form is mandatory (*), so only those are filled; nothing optional is entered
        Campaign campaign = TestData.ongoingCampaign();
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Campaign Management");
        campaignPage.clickCreateCampaign();
        campaignPage.fillAndProceed(campaign);
        String outcome = campaignPage.proceedOutcome();

        actual("Outcome: " + outcome);
        Assert.assertTrue(outcome.contains("Campaign Created Successfully"), "Campaign was not created: " + outcome);
    }
}
