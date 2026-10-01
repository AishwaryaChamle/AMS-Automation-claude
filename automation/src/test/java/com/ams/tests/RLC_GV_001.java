package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_001: Verify Marketing Team campaign permissions.
 * Expected: Marketing Team can perform only the campaign actions permitted by the requirements.
 */
public class RLC_GV_001 extends BaseTest {

    @Test
    public void marketingTeamHasCampaignPermissions() throws Exception {
        // Marketing Team can create a campaign and manage it
        Campaign campaign = TestData.ongoingCampaign();
        String message = flows.createCampaignAs(Config.marketing(), campaign);
        flows.openCampaignListAs(Config.marketing(), campaign.name());
        List<String> actions = campaignPage.actionsOf(campaign.name());
        List<String> cards = menu.recruitmentCards();

        actual("Marketing create pop-up: '" + message + "'. Actions: " + actions + ". Recruitment cards: " + cards);
        Assert.assertTrue(message.contains("Campaign Created Successfully"), "Marketing cannot create campaigns");
        Assert.assertTrue(actions.contains("Edit") && actions.contains("Bulk Upload"), "Marketing cannot manage its campaign: " + actions);
    }
}
