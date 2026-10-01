package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CS_003: Verify creator campaign access.
 * Expected: Each creator can access campaign functionality permitted to that role without user-specific restrictions.
 */
public class RLC_CS_003 extends BaseTest {

    @Test
    public void creatorHasFullCampaignAccess() throws Exception {
        Campaign campaign = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaign(campaign), "Campaign creation");
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        List<String> actions = campaignPage.actionsOf(campaign.name());

        actual("Actions offered to the creator: " + actions);
        Assert.assertTrue(actions.containsAll(List.of("Edit", "Deactivate", "Bulk Upload")), "Creator is missing actions: " + actions);
    }
}
