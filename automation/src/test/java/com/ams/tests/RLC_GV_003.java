package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_003: Verify AS permissions and restrictions.
 * Expected: AS can perform permitted actions and branch/role restrictions are enforced.
 */
public class RLC_GV_003 extends BaseTest {

    @Test
    public void agencySupervisorPermissionsEnforced() throws Exception {
        flows.openLeadList(Config.agencySupervisor(), flows.sharedCampaign());
        boolean canEnterLeads = leadPage.isCreateLeadAvailable();
        menu.open("Recruitment Leads Mgmt");
        boolean canCreateCampaign = campaignPage.isCreateCampaignAvailable();
        List<String> cards = menu.recruitmentCards();

        actual("AS - Create Lead: " + canEnterLeads + ", Create Campaign: " + canCreateCampaign + ", admin cards: " + cards);
        Assert.assertTrue(canEnterLeads, "AS cannot enter leads for the assigned campaign");
        Assert.assertFalse(canCreateCampaign, "AS can create campaigns");
        Assert.assertFalse(cards.contains("Blacklisting") || cards.contains("SLII Exam Management"), "AS can reach admin screens");
    }
}
