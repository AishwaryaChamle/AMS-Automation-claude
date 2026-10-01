package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_008: Verify AS lead routing.
 * Expected: AS-created lead is routed to HOB as required.
 */
public class RLC_HA_008 extends BaseTest {

    @Test
    public void agencySupervisorLeadRoutedToHob() throws Exception {
        // AS creates a lead and submits it (the AS form has no interview / approval section)
        Lead lead = TestData.adultLead();
        flows.startLead(Config.agencySupervisor(), flows.sharedCampaign(), lead);
        leadPage.clickSubmit();
        String message = leadPage.closeAllPopups();

        // HOB must find the lead and be able to open it (edit icon)
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        String row = leadPage.rowText(lead.nic());
        boolean hobCanAction = leadPage.hasEditIcon(lead.nic());

        actual("AS pop-up: '" + message + "'. HOB lead row: " + row + "; HOB can open it for approval: " + hobCanAction);
        Assert.assertNotNull(row, "AS lead is not visible to HOB");
        Assert.assertTrue(hobCanAction, "AS lead is not routed to HOB for action");
    }
}
