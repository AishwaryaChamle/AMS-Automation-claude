package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_006: Verify lead creation.
 * Expected: Lead is created successfully.
 */
public class RLC_HA_006 extends BaseTest {

    @Test
    public void agencySupervisorCreatesLead() throws Exception {
        // Agency Supervisor (AS) creates a lead in the campaign
        Lead lead = TestData.adultLead();
        flows.startLead(Config.agencySupervisor(), flows.sharedCampaign(), lead);
        leadPage.clickSubmit();
        String message = leadPage.closeAllPopups();
        flows.openLeadList(Config.agencySupervisor(), flows.sharedCampaign());
        String row = leadPage.rowText(lead.nic());

        actual("AS pop-up: '" + message + "'. Row: " + row);
        Assert.assertTrue(message.contains("New Lead Initiated"), "Lead was not created: " + message);
        Assert.assertNotNull(row, "Lead not listed");
    }
}
