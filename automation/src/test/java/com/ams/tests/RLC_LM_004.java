package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_LM_004: Verify invalid contact number validation.
 * Expected: System rejects the invalid contact number and displays appropriate validation.
 */
public class RLC_LM_004 extends BaseTest {

    @Test
    public void invalidContactNumberRejected() throws Exception {
        Lead lead = TestData.withContact(TestData.adultLead(), "12345");
        flows.startLeadWithoutChecks(lead);
        leadPage.clickSubmit();
        String outcome = leadPage.actionOutcome();

        actual("Contact '12345'. Outcome: " + outcome);
        Assert.assertFalse(outcome.contains("Initiated"), "Lead was created with an invalid contact number");
        Assert.assertTrue(outcome.toLowerCase().matches("(?s).*(contact|digit|number).*"), "No contact validation message shown");
    }
}
