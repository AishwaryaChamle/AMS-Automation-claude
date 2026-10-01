package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CS_008: Verify campaign chronological ordering.
 * Expected: Campaigns are displayed in the required chronological order.
 */
public class RLC_CS_008 extends BaseTest {

    @Test
    public void campaignsListedInChronologicalOrder() throws Exception {
        // Two campaigns created one after the other
        Campaign older = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaign(older), "First campaign");
        Campaign newer = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaign(newer), "Second campaign");

        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Campaign Management");
        List<String> rows = campaignPage.visibleRows();
        int newerIndex = indexOf(rows, newer.name());
        int olderIndex = indexOf(rows, older.name());

        actual("Position in list - newer campaign: " + newerIndex + ", older campaign: " + olderIndex);
        Assert.assertTrue(newerIndex >= 0 && olderIndex >= 0, "Campaigns not found on the first page");
        Assert.assertTrue(newerIndex < olderIndex, "Campaigns are not in chronological (newest first) order");
    }

    private int indexOf(List<String> rows, String name) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).contains(name)) {
                return i;
            }
        }
        return -1;
    }
}
