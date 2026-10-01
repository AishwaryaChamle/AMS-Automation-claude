package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import java.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CM_006: Verify that the system prevents campaign creation when an invalid date is entered and displays the appropriate date validation message.
 * Expected: System prevents creation and displays appropriate date validation.
 */
public class RLC_CM_006 extends BaseTest {

    @Test
    public void invalidDateRangePreventsCampaignCreation() throws Exception {
        // End date before the start date
        LocalDate start = LocalDate.now().plusDays(10);
        Campaign campaign = new Campaign(TestData.campaignName(), start.format(TestData.UI_DATE),
                start.minusDays(5).format(TestData.UI_DATE), "50000", "20");
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Campaign Management");
        campaignPage.clickCreateCampaign();
        campaignPage.fillAndProceed(campaign);
        String outcome = campaignPage.proceedOutcome();

        actual("Outcome with end date before start date: " + outcome);
        Assert.assertFalse(outcome.contains("Campaign Created Successfully"), "Campaign was created with an invalid date range");
    }
}
