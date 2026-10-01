package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_CM_003: Verify that users without permission cannot access the Create Campaign functionality.
 * Expected: Create Campaign functionality is unavailable or access is denied.
 */
public class RLC_CM_003 extends BaseTest {

    @Test
    public void userWithoutPermissionCannotCreateCampaign() throws Exception {
        // HOB opens Recruitment Leads Mgmt: the Create Campaign option must not be offered
        loginPage.loginAs(Config.hob());
        menu.open("Recruitment Leads Mgmt");
        boolean buttonShown = campaignPage.isCreateCampaignAvailable();
        List<String> cards = menu.recruitmentCards();

        // Observation only: the Create Campaign address typed directly (the form is not submitted)
        campaignPage.openPath("/app/recruitment-support/campaign-management/create");
        boolean formShownByUrl = campaignPage.isCreateFormShown();

        actual("Create Campaign option shown to HOB: " + buttonShown + "; Campaign Management card shown: " + cards.contains("Campaign Management")
                + ". Observation: Create Campaign form opens when its URL is typed directly: " + formShownByUrl);
        Assert.assertFalse(buttonShown, "HOB is offered the Create Campaign option");
        Assert.assertFalse(cards.contains("Campaign Management"), "HOB can reach Campaign Management");
    }
}
