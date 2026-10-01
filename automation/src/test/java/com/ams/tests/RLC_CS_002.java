package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CS_002: Verify Agency Admin-created campaign visibility.
 * Expected: The Agency Admin-created campaign is visible to Marketing Team.
 */
public class RLC_CS_002 extends BaseTest {

    @Test
    public void agencyAdminCampaignVisibleToMarketing() throws Exception {
        Campaign campaign = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaign(campaign), "Agency Admin campaign creation");

        flows.openCampaignListAs(Config.marketing(), campaign.name());
        boolean visible = campaignPage.findCampaign(campaign.name()) != null;

        actual("Agency Admin campaign '" + campaign.name() + "' visible to Marketing Team: " + visible);
        Assert.assertTrue(visible, "Agency Admin campaign is not visible to Marketing Team");
    }
}
