package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CM_001: Verify that the system successfully creates a campaign when all mandatory campaign details are entered and displays a confirmation notification indicating that the campaign was created successfully.
 * Expected: Campaign is created successfully and a "Campaign Created Successfully" notification is displayed.
 */
public class RLC_CM_001 extends BaseTest {

    @Test
    public void campaignCreatedWithSuccessNotification() throws Exception {
        Campaign campaign = TestData.ongoingCampaign();
        String message = flows.createCampaign(campaign);

        flows.openCampaignListAsAgencyAdmin(campaign.name());
        boolean listed = campaignPage.findCampaign(campaign.name()) != null;

        actual("Pop-up after PROCEED: '" + message + "'. Campaign listed: " + listed);
        Assert.assertTrue(message.contains("Campaign Created Successfully"), "'Campaign Created Successfully' notification not shown: " + message);
        Assert.assertTrue(listed, "Campaign is not listed after creation");
    }
}
