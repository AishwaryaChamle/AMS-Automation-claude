package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CS_009: Verify campaign status display.
 * Expected: Each campaign displays its applicable status.
 */
public class RLC_CS_009 extends BaseTest {

    @Test
    public void eachCampaignDisplaysItsStatus() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Campaign Management");
        List<String> statuses = campaignPage.columnValues("Status");

        actual("Statuses shown on the first page: " + statuses);
        Assert.assertFalse(statuses.isEmpty(), "No campaigns listed");
        Assert.assertTrue(statuses.stream().noneMatch(s -> s.isBlank()), "Some campaigns have no status");
    }
}
