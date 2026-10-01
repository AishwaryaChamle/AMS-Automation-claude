package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData.Campaign;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CM_004: Verify that the system prevents submission when mandatory fields are not completed and displays the appropriate validation/mandatory-field notification.
 * Expected: Submission is prevented and validation/mandatory-field notification is displayed.
 */
public class RLC_CM_004 extends BaseTest {

    @Test
    public void mandatoryFieldValidationOnCreateCampaign() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Campaign Management");
        campaignPage.clickCreateCampaign();

        // Click PROCEED without filling anything
        campaignPage.fillAndProceed(new Campaign("", "", "", "", ""));
        String outcome = campaignPage.proceedOutcome();

        actual("Outcome of PROCEED with empty form: " + outcome);
        Assert.assertTrue(outcome.startsWith("VALIDATION"), "Form was submitted without mandatory fields");
        Assert.assertTrue(outcome.contains("Campaign Name is required"), "Mandatory-field message for Campaign Name missing");
    }
}
