package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CM_011: Verify that an active campaign can be successfully deactivated and its status is updated to Inactive/Deactivated.
 * Expected: Campaign status changes to inactive/deactivated.
 */
public class RLC_CM_011 extends BaseTest {

    @Test
    public void activeCampaignCanBeDeactivated() throws Exception {
        Campaign campaign = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaign(campaign), "Campaign creation");
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        String statusBefore = campaignPage.statusOf(campaign.name());

        // Action menu > Deactivate
        String message = campaignPage.changeStatus(campaign.name(), "Deactivate");

        // Reload the list and read the new status and the offered actions
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        String statusAfter = campaignPage.statusOf(campaign.name());
        List<String> actions = campaignPage.actionsOf(campaign.name());

        actual("Status before: " + statusBefore + ". Pop-up: '" + message + "'. Status after: " + statusAfter + ". Actions now offered: " + actions);
        Assert.assertTrue(statusAfter.toLowerCase().matches(".*(inactive|deactivat).*"), "Status did not change to Inactive/Deactivated: " + statusAfter);
        Assert.assertFalse(actions.contains("Deactivate"), "Campaign still offers Deactivate: " + actions);
    }
}
