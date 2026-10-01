package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_005: Verify lead creation for permitted campaign.
 * Expected: Lead is created successfully for the permitted campaign.
 */
public class RLC_HA_005 extends BaseTest {

    @Test
    public void leadCreatedForPermittedCampaign() throws Exception {
        Lead lead = TestData.adultLead();
        String message = flows.hobCreatesLead(lead);
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        String row = leadPage.rowText(lead.nic());

        actual("Pop-up: '" + message + "'. Row in the campaign lead list: " + row);
        Assert.assertTrue(message.contains("New Lead Initiated"), "Lead was not created: " + message);
        Assert.assertNotNull(row, "Lead not shown for the campaign");
    }
}
