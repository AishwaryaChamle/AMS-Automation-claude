package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_002: Verify HOB campaign/lead permissions.
 * Expected: HOB can perform permitted campaign/lead actions and restricted actions are denied.
 */
public class RLC_GV_002 extends BaseTest {

    @Test
    public void hobPermissionsEnforced() throws Exception {
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        boolean canEnterLeads = leadPage.isCreateLeadAvailable();
        menu.open("Recruitment Leads Mgmt");
        boolean canCreateCampaign = campaignPage.isCreateCampaignAvailable();
        List<String> cards = menu.recruitmentCards();

        actual("HOB - Create Lead: " + canEnterLeads + ", Create Campaign: " + canCreateCampaign + ", admin cards: " + cards);
        Assert.assertTrue(canEnterLeads, "HOB cannot enter leads");
        Assert.assertFalse(canCreateCampaign, "HOB can create campaigns");
        Assert.assertFalse(cards.contains("Blacklisting") || cards.contains("SLII Exam Management"), "HOB can reach admin screens");
    }
}
