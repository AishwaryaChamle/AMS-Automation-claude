package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_002: Verify lead details display.
 * Expected: Lead details are displayed.
 */
public class RLC_HA_002 extends BaseTest {

    @Test
    public void leadDetailsDisplayedToHob() throws Exception {
        // HOB creates a lead and submits it (no interview decision yet)
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesLead(lead), "Lead creation");
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        leadPage.openLead(lead.nic());

        String shown = leadPage.fieldValue("firstName") + " " + leadPage.fieldValue("lastName") + ", NIC " + leadPage.fieldValue("nic")
                + ", contact " + leadPage.fieldValue("contact") + ", email " + leadPage.fieldValue("email");

        actual("Lead details shown: " + shown);
        Assert.assertEquals(leadPage.fieldValue("nic"), lead.nic(), "NIC not displayed");
        Assert.assertEquals(leadPage.fieldValue("email"), lead.email(), "Email not displayed");
    }
}
