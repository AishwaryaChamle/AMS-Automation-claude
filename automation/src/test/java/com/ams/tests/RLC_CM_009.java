package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CM_009: Verify that the updated budget value is saved successfully and displayed correctly after updating the budget.
 * Expected: Updated budget is retained and displayed correctly.
 */
public class RLC_CM_009 extends BaseTest {

    @Test
    public void updatedCampaignBudgetIsRetained() throws Exception {
        Campaign campaign = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaign(campaign), "Campaign creation");
        String newBudget = "175000";

        // Action menu > Edit, change budget, PROCEED
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        campaignPage.chooseAction(campaign.name(), "Edit");
        campaignPage.editCampaign(newBudget, null);
        String message = campaignPage.closeAllPopups();

        // Check the saved values in the campaign list
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        String row = campaignPage.rowText(campaign.name());
        String digits = row.replace(",", "");

        actual("Pop-up: '" + message + "'. Campaign row after edit: " + row);
        Assert.assertTrue(message.contains("Edited Successfully"), "Edit was not saved: " + message);
        Assert.assertTrue(digits.contains(newBudget), "Updated budget not shown: expected " + newBudget);
    }
}
