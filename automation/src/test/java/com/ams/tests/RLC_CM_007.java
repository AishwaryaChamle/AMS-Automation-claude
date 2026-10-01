package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import java.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CM_007: Verify that the updated campaign information is successfully saved and displayed correctly.
 * Expected: Updated campaign information is saved and displayed.
 */
public class RLC_CM_007 extends BaseTest {

    @Test
    public void updatedCampaignInformationIsSaved() throws Exception {
        // Campaign details can be edited only while the campaign is Upcoming (not yet started)
        Campaign campaign = TestData.upcomingCampaign();
        Flows.expectSuccess(flows.createCampaign(campaign), "Campaign creation");
        LocalDate newEndDate = LocalDate.now().plusDays(45);
        String newBudget = "175000";

        // Action menu > Edit, change budget and end date, PROCEED
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        campaignPage.chooseAction(campaign.name(), "Edit");
        campaignPage.editCampaign(newBudget, newEndDate.format(TestData.UI_DATE));
        String message = campaignPage.closeAllPopups();

        // Check the saved values in the campaign list
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        String row = campaignPage.rowText(campaign.name());
        String digits = row.replace(",", "");

        actual("Pop-up: '" + message + "'. Campaign row after edit: " + row);
        Assert.assertTrue(message.contains("Edited Successfully"), "Edit was not saved: " + message);
        Assert.assertTrue(digits.contains(newBudget), "Updated budget not shown");
        Assert.assertTrue(row.contains(TestData.listDate(newEndDate)), "Updated end date not shown");
    }
}
