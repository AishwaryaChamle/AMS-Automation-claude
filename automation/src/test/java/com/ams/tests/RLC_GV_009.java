package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import java.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_009: Verify invalid input.
 * Expected: Invalid input is rejected and an appropriate validation/error message is displayed.
 */
public class RLC_GV_009 extends BaseTest {

    @Test
    public void invalidInputRejected() throws Exception {
        Campaign campaign = new Campaign(TestData.campaignName(), TestData.today(),
                LocalDate.now().plusDays(10).format(TestData.UI_DATE), "abc", "xyz");
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Campaign Management");
        campaignPage.clickCreateCampaign();
        campaignPage.fillAndProceed(campaign);
        String outcome = campaignPage.proceedOutcome();

        actual("Budget 'abc' / lead target 'xyz': " + outcome);
        Assert.assertFalse(outcome.contains("Created Successfully"), "Campaign was created with invalid numbers");
    }
}
