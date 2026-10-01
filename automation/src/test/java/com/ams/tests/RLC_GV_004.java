package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_004: Verify Agency Admin role-based access.
 * Expected: Agency Admin receives the role-based access specified for those functions.
 */
public class RLC_GV_004 extends BaseTest {

    @Test
    public void agencyAdminRoleBasedAccess() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        List<String> cards = menu.recruitmentCards();
        menu.openRecruitmentCard("Campaign Management");
        boolean canCreateCampaign = campaignPage.isCreateCampaignAvailable();

        actual("Agency Admin cards: " + cards + "; Create Campaign: " + canCreateCampaign);
        Assert.assertTrue(cards.containsAll(List.of("Campaign Management", "SLII Exam Management", "Recruitment Funnel", "Blacklisting")), "Agency Admin is missing screens: " + cards);
        Assert.assertTrue(canCreateCampaign, "Agency Admin cannot create campaigns");
    }
}
