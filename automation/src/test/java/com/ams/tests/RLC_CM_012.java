package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CM_012: Verify that an expired campaign remains visible according to the implemented campaign visibility behavior and that its Expired status/state is clearly identifiable.
 * Expected: Expired campaign remains visible according to the implemented campaign visibility behavior and its expired/status state is identifiable.
 */
public class RLC_CM_012 extends BaseTest {

    @Test
    public void expiredCampaignRemainsVisibleWithExpiredStatus() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Campaign Management");
        campaignPage.searchByStatus("Expired");
        List<String> statuses = campaignPage.columnValues("Status");

        actual("Statuses shown when searching for 'Expired': " + statuses);
        Assert.assertFalse(statuses.isEmpty(), "No expired campaign is visible in the campaign list");
        Assert.assertTrue(statuses.stream().allMatch(s -> s.equalsIgnoreCase("Expired")), "Expired status not identifiable: " + statuses);
    }
}
