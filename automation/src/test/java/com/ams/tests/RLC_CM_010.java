package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CM_010: Verify that a campaign status is updated to Active successfully when the campaign is activated.
 * Expected: Campaign status changes to active (shown as "Ongoing" in the campaign list).
 */
public class RLC_CM_010 extends BaseTest {

    @Test
    public void inactiveCampaignCanBeActivated() throws Exception {
        // A running campaign is deactivated first so it is Inactive and offered "Activate"
        Campaign campaign = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaign(campaign), "Campaign creation");
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        campaignPage.changeStatus(campaign.name(), "Deactivate");

        flows.openCampaignListAsAgencyAdmin(campaign.name());
        String statusBefore = campaignPage.statusOf(campaign.name());
        List<String> actionsBefore = campaignPage.actionsOf(campaign.name());

        flows.openCampaignListAsAgencyAdmin(campaign.name());
        String message = campaignPage.changeStatus(campaign.name(), "Activate");

        flows.openCampaignListAsAgencyAdmin(campaign.name());
        String statusAfter = campaignPage.statusOf(campaign.name());
        List<String> actionsAfter = campaignPage.actionsOf(campaign.name());

        actual("Before: " + statusBefore + " " + actionsBefore + ". Pop-up: '" + message + "'. After: " + statusAfter + " " + actionsAfter);
        Assert.assertTrue(statusBefore.equalsIgnoreCase("Inactive"), "Campaign is not Inactive after Deactivate: " + statusBefore);
        Assert.assertTrue(statusAfter.equalsIgnoreCase("Ongoing"), "Campaign status is not Ongoing after Activate: " + statusAfter);
        Assert.assertTrue(actionsAfter.contains("Deactivate"), "Campaign is not active after Activate (no Deactivate action)");
    }
}
