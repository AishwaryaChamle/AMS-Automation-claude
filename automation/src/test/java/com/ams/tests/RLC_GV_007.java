package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_007: Verify Campaign Cost/Budget terminology.
 * Expected: Campaign Cost/Budget terminology is consistent with the implemented requirement across applicable screens.
 */
public class RLC_GV_007 extends BaseTest {

    @Test
    public void campaignBudgetTerminologyConsistent() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Campaign Management");
        List<String> listHeaders = campaignPage.tableHeaders();
        campaignPage.clickCreateCampaign();
        List<String> formLabels = campaignPage.formLabels();

        actual("Campaign list columns: " + listHeaders + "; Create Campaign labels: " + formLabels);
        Assert.assertTrue(listHeaders.contains("Campaign Budget"), "List does not use 'Campaign Budget'");
        Assert.assertTrue(formLabels.stream().anyMatch(l -> l.contains("Campaign Budget")), "Form does not use 'Campaign Budget'");
        Assert.assertTrue((listHeaders + " " + formLabels).toLowerCase().indexOf("cost") < 0, "Both 'Cost' and 'Budget' terms are used");
    }
}
