package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_001: Verify HOB campaign list access.
 * Expected: HOB can access the permitted campaign list.
 */
public class RLC_HA_001 extends BaseTest {

    @Test
    public void hobCanAccessCampaignList() throws Exception {
        String campaign = flows.sharedCampaign();
        loginPage.loginAs(Config.hob());
        menu.open("Recruitment Leads Mgmt");
        boolean found = campaignPage.findCampaign(campaign) != null;

        actual("HOB campaign list rows: " + campaignPage.visibleRows().size() + "; campaign '" + campaign + "' found: " + found);
        Assert.assertTrue(found, "HOB cannot see the campaign list / campaign");
    }
}
