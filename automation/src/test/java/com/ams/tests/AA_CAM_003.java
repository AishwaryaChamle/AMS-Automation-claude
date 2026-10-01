package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AA_CAM_003: Verify Agency Admin can deactivate an active campaign.
 * Expected: Campaign status changes from Active to Inactive successfully. The deactivated campaign is no longer available for new campaign-related transactions as per business rules.
 */
public class AA_CAM_003 extends BaseTest {

    @Test
    public void agencyAdminDeactivatesActiveCampaign() throws Exception {
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
