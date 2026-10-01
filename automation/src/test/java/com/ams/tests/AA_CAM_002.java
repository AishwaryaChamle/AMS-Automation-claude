package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AA_CAM_002: Verify a campaign created by Agency Admin is visible to all HOBs and AS users.
 * Expected: Newly created campaign is available to all authorized HOB and AS users.
 */
public class AA_CAM_002 extends BaseTest {

    @Test
    public void campaignCreatedByAgencyAdminIsVisibleToHobAndAs() throws Exception {
        Campaign campaign = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaign(campaign), "Campaign creation");

        // HOB looks for the campaign in his campaign list
        loginPage.loginAs(Config.hob());
        menu.open("Recruitment Leads Mgmt");
        boolean visibleToHob = campaignPage.findCampaign(campaign.name()) != null;

        // Agency Supervisor (AS) looks for the campaign
        loginPage.loginAs(Config.agencySupervisor());
        menu.open("Recruitment Leads Mgmt");
        boolean visibleToAs = campaignPage.findCampaign(campaign.name()) != null;

        actual("Campaign '" + campaign.name() + "' visible to HOB: " + visibleToHob + ", visible to AS: " + visibleToAs);
        Assert.assertTrue(visibleToHob, "Campaign is not visible to HOB");
        Assert.assertTrue(visibleToAs, "Campaign is not visible to AS");
    }
}
