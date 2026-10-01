package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Campaign;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_007: Verify individual lead entry restriction.
 * Expected: Lead entry is unavailable or access is denied.
 * Restricted campaign (confirmed by the business): a campaign deactivated by the Agency Admin.
 */
public class RLC_HA_007 extends BaseTest {

    @Test
    public void leadEntryRestrictedForInactiveCampaign() throws Exception {
        // Agency Admin creates a campaign and deactivates it
        Campaign campaign = TestData.ongoingCampaign();
        Flows.expectSuccess(flows.createCampaign(campaign), "Campaign creation");
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        campaignPage.changeStatus(campaign.name(), "Deactivate");
        flows.openCampaignListAsAgencyAdmin(campaign.name());
        String status = campaignPage.statusOf(campaign.name());

        // HOB tries to add a lead to the inactive campaign
        loginPage.loginAs(Config.hob());
        menu.open("Recruitment Leads Mgmt");
        String outcome;
        boolean restricted;
        if (campaignPage.findCampaign(campaign.name()) == null) {
            outcome = "campaign not listed for HOB";
            restricted = true;
        } else {
            campaignPage.openLeads(campaign.name());
            if (!leadPage.isCreateLeadAvailable()) {
                outcome = "campaign listed, but no Create Lead option";
                restricted = true;
            } else if (!leadPage.isCreateLeadEnabled()) {
                outcome = "campaign listed, Create Lead button is disabled";
                restricted = true;
            } else {
                // The option is offered: try to save a lead; it must be refused
                leadPage.clickCreateLead();
                leadPage.fillLeadDetails(TestData.adultLead());
                leadPage.clickSubmit();
                String result = leadPage.actionOutcome();
                outcome = "Create Lead offered; submit result: " + result;
                restricted = !Flows.isSuccess(result);
            }
        }

        actual("Campaign status after Deactivate: " + status + ". HOB lead entry: " + outcome);
        Assert.assertTrue(status.toLowerCase().matches(".*(inactive|deactivat).*"), "Campaign was not deactivated: " + status);
        Assert.assertTrue(restricted, "Lead entry is not restricted for the inactive campaign: " + outcome);
    }
}
