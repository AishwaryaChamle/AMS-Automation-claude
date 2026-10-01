package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CS_004: Verify equivalent-role campaign access.
 * Expected: Equivalent-role users receive equivalent permitted campaign access.
 */
public class RLC_CS_004 extends BaseTest {

    @Test
    public void equivalentRoleUsersGetSameCampaignAccess() throws Exception {
        // Campaign created by the first Agency Admin
        Campaign campaign = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaign(campaign), "Campaign creation");
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        List<String> creatorActions = campaignPage.actionsOf(campaign.name());

        // A second Agency Admin (same role, different user)
        flows.openCampaignListAs(Config.agencyAdmin2(), campaign.name());
        boolean visibleToOther = campaignPage.findCampaign(campaign.name()) != null;
        actual("Second Agency Admin can see the campaign: " + visibleToOther);
        Assert.assertTrue(visibleToOther, "Campaign created by one Agency Admin is not visible to another Agency Admin");
        List<String> otherActions = campaignPage.actionsOf(campaign.name());

        actual("Actions for creator: " + creatorActions + "; for second Agency Admin: " + otherActions);
        Assert.assertEquals(otherActions, creatorActions, "Users with the same role get different campaign access");
    }
}
