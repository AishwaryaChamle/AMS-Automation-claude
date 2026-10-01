package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_LM_010: Verify duplicate lead/NIC.
 * Expected: Duplicate lead/NIC is detected and the system prevents or handles the duplicate according to the implemented validation.
 */
public class RLC_LM_010 extends BaseTest {

    @Test
    public void duplicateNicDetected() throws Exception {
        Lead first = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesLead(first), "First lead");

        // Second lead with the same NIC
        Lead duplicate = TestData.withNic(TestData.adultLead(), first.nic());
        flows.startLeadWithoutChecks(duplicate);
        leadPage.clickSubmit();
        String outcome = leadPage.actionOutcome();

        actual("Second lead with NIC " + first.nic() + ": " + outcome);
        Assert.assertFalse(outcome.contains("Initiated"), "Duplicate NIC was accepted as a new lead");
    }
}
