package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_003: Verify lead changes.
 * Expected: Lead changes are saved and displayed.
 */
public class RLC_HA_003 extends BaseTest {

    @Test
    public void leadChangesSavedByHob() throws Exception {
        // HOB creates a lead and submits it (no interview decision yet)
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesLead(lead), "Lead creation");
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        leadPage.openLead(lead.nic());

        // Change the address line 1 and SAVE
        String newAddress = "22 Temple Road";
        leadPage.setAddressLine1(newAddress);
        leadPage.clickSave();
        String message = leadPage.closeAllPopups();

        // Open the lead again and read the saved value
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        leadPage.openLead(lead.nic());
        String saved = leadPage.fieldValue("addressline1");

        actual("Save pop-up: '" + message + "'. Value after reopening: " + saved);
        Assert.assertEquals(saved, newAddress, "Change was not saved");
    }
}
