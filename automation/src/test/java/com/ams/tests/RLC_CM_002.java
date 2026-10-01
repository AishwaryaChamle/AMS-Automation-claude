package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CM_002: Verify that the system successfully creates the campaign and displays the creation success notification.
 * Expected: Campaign is created successfully and the creation success notification is displayed.
 */
public class RLC_CM_002 extends BaseTest {

    @Test
    public void campaignCreationShowsSuccessNotification() throws Exception {
        Campaign campaign = TestData.upcomingCampaign();
        String message = flows.createCampaign(campaign);

        flows.openCampaignListAsAgencyAdmin(campaign.name());
        boolean listed = campaignPage.findCampaign(campaign.name()) != null;

        actual("Pop-up after PROCEED: '" + message + "'. Campaign listed: " + listed);
        Assert.assertTrue(message.contains("Campaign Created Successfully"), "'Campaign Created Successfully' notification not shown: " + message);
        Assert.assertTrue(listed, "Campaign is not listed after creation");
    }
}
