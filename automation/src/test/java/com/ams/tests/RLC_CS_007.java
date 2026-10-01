package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CS_007: Verify campaign modification restriction.
 * Expected: Modification controls are unavailable or the action is denied.
 */
public class RLC_CS_007 extends BaseTest {

    @Test
    public void nonCreatorCannotModifyCampaign() throws Exception {
        // HOB is not a campaign manager: no Action column and no Create Campaign
        loginPage.loginAs(Config.hob());
        menu.open("Recruitment Leads Mgmt");
        List<String> headers = campaignPage.tableHeaders();
        boolean canCreate = campaignPage.isCreateCampaignAvailable();

        actual("HOB campaign table columns: " + headers + "; Create Campaign available: " + canCreate);
        Assert.assertFalse(headers.contains("Action"), "HOB has campaign modification actions");
        Assert.assertFalse(canCreate, "HOB can create campaigns");
    }
}
