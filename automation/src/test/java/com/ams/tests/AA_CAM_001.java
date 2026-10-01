package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AA_CAM_001: Verify Agency Admin can create a campaign by providing all valid mandatory campaign details.
 * Expected: Campaign is created successfully and becomes available in the campaign list.
 */
public class AA_CAM_001 extends BaseTest {

    @Test
    public void agencyAdminCreatesCampaignWithValidDetails() throws Exception {
        // Agency Admin creates a campaign with all mandatory details
        Campaign campaign = TestData.ongoingCampaign();
        String message = flows.createCampaign(campaign);

        // The new campaign must be listed in Campaign Management
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        boolean listed = campaignPage.findCampaign(campaign.name()) != null;

        actual("Pop-up: '" + message + "'. Campaign '" + campaign.name() + "' listed in the campaign list: " + listed);
        Assert.assertTrue(message.contains("Campaign Created Successfully"), "Success pop-up not shown: " + message);
        Assert.assertTrue(listed, "New campaign is not shown in the campaign list");
    }
}
