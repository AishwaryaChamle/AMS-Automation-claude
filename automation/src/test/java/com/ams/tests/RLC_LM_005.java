package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_LM_005: Verify updated lead details.
 * Expected: Updated details are retained and displayed.
 */
public class RLC_LM_005 extends BaseTest {

    @Test
    public void updatedLeadDetailsRetained() throws Exception {
        // HOB creates a lead and submits it (no interview decision yet)
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesLead(lead), "Lead creation");
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        leadPage.openLead(lead.nic());

        // Change the contact number and SAVE
        String newContact = "0712345678";
        leadPage.setContact(newContact);
        leadPage.clickSave();
        String message = leadPage.closeAllPopups();

        // Open the lead again and read the saved value
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        leadPage.openLead(lead.nic());
        String saved = leadPage.fieldValue("contact");

        actual("Save pop-up: '" + message + "'. Value after reopening: " + saved);
        Assert.assertEquals(saved, newContact, "Change was not saved");
    }
}
