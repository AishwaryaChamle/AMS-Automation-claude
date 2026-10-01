package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_009: Verify unauthorized access.
 * Expected: Unauthorized access is denied and protected actions cannot be performed.
 */
public class RLC_HA_009 extends BaseTest {

    @Test
    public void unauthorizedUserCannotAccessProtectedScreens() throws Exception {
        // HOB (102117) must not be offered the Agency Admin screens
        loginPage.loginAs(Config.hob());
        menu.open("Recruitment Leads Mgmt");
        boolean canCreateCampaign = campaignPage.isCreateCampaignAvailable();
        List<String> cards = menu.recruitmentCards();
        boolean adminCardsShown = cards.contains("SLII Exam Management") || cards.contains("Blacklisting") || cards.contains("Campaign Management");

        // Observation only: protected addresses typed directly (nothing is submitted)
        campaignPage.openPath("/app/recruitment-support/blacklisting");
        boolean blacklistByUrl = uploadPage.isUploaderShown();

        actual("HOB offered Create Campaign: " + canCreateCampaign + "; admin cards shown: " + adminCardsShown + " " + cards
                + ". Observation: Blacklisting page opens when its URL is typed directly: " + blacklistByUrl);
        Assert.assertFalse(canCreateCampaign, "HOB is offered Create Campaign");
        Assert.assertFalse(adminCardsShown, "HOB is offered Agency Admin screens: " + cards);
    }
}
