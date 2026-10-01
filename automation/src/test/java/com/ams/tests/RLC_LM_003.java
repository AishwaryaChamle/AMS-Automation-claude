package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_LM_003: Verify invalid NIC validation.
 * Expected: System rejects the invalid NIC and displays appropriate validation.
 */
public class RLC_LM_003 extends BaseTest {

    @Test
    public void invalidNicRejected() throws Exception {
        Lead lead = TestData.withNic(TestData.adultLead(), "12345ABC");
        flows.startLeadWithoutChecks(lead);
        leadPage.clickSubmit();
        String outcome = leadPage.actionOutcome();

        actual("NIC '12345ABC': DOB derived = '" + leadPage.derivedDob() + "'. Outcome: " + outcome);
        Assert.assertFalse(outcome.contains("Initiated"), "Lead was created with an invalid NIC");
        Assert.assertTrue(outcome.toUpperCase().contains("NIC"), "No NIC validation message shown");
    }
}
