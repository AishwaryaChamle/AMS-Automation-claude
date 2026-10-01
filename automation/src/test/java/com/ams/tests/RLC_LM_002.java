package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_LM_002: Verify mandatory-field validation.
 * Expected: Lead is not created and mandatory-field validation is displayed.
 */
public class RLC_LM_002 extends BaseTest {

    @Test
    public void mandatoryFieldValidationOnCreateLead() throws Exception {
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        leadPage.clickCreateLead();
        leadPage.clickSubmit();
        String outcome = leadPage.actionOutcome();

        actual("SUBMIT with an empty form: " + outcome);
        Assert.assertTrue(outcome.startsWith("VALIDATION"), "Lead was submitted without mandatory fields");
        Assert.assertTrue(outcome.contains("Title is required"), "Mandatory-field message missing");
    }
}
