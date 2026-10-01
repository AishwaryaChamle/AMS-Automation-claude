package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CS_001: Verify Marketing-created campaign visibility.
 * Expected: The Marketing-created campaign is visible to Agency Admin.
 */
public class RLC_CS_001 extends BaseTest {

    @Test
    public void marketingCampaignVisibleToAgencyAdmin() throws Exception {
        // Marketing Team creates a campaign
        Campaign campaign = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaignAs(Config.marketing(), campaign), "Marketing campaign creation");

        // Agency Admin looks for it
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        boolean visible = campaignPage.findCampaign(campaign.name()) != null;

        actual("Marketing campaign '" + campaign.name() + "' visible to Agency Admin: " + visible);
        Assert.assertTrue(visible, "Marketing campaign is not visible to Agency Admin");
    }
}
