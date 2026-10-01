package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CS_006: Verify campaign status transition.
 * Expected: Campaign status changes correctly between active and inactive/deactivated states.
 */
public class RLC_CS_006 extends BaseTest {

    @Test
    public void campaignStatusTransitionsBetweenActiveAndInactive() throws Exception {
        Campaign campaign = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaign(campaign), "Campaign creation");

        flows.openCampaignListAsAgencyAdmin(campaign.name());
        String first = campaignPage.statusOf(campaign.name());
        campaignPage.changeStatus(campaign.name(), "Deactivate");

        flows.openCampaignListAsAgencyAdmin(campaign.name());
        String second = campaignPage.statusOf(campaign.name());
        campaignPage.changeStatus(campaign.name(), "Activate");

        flows.openCampaignListAsAgencyAdmin(campaign.name());
        String third = campaignPage.statusOf(campaign.name());

        actual("Status: " + first + " -> (Deactivate) " + second + " -> (Activate) " + third);
        Assert.assertTrue(second.toLowerCase().matches(".*(inactive|deactivat).*"), "Deactivate did not change the status: " + second);
        Assert.assertFalse(third.toLowerCase().matches(".*(inactive|deactivat).*"), "Activate did not change the status back: " + third);
    }
}
