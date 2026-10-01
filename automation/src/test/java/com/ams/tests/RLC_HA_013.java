package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_013: Verify Reject Interview status.
 * Expected: Candidate status changes to Reject Interview.
 */
public class RLC_HA_013 extends BaseTest {

    @Test
    public void candidateStatusRejectInterview() throws Exception {
        Lead lead = TestData.adultLead();
        String message = flows.hobCreatesAndRejectsLead(lead);
        flows.openLeadList(Config.hob(), flows.sharedCampaign());
        String row = leadPage.rowText(lead.nic());

        actual("Pop-up: '" + message + "'. Lead row: " + row);
        Assert.assertTrue(message.contains("Rejected Successfully"), "Reject was not accepted: " + message);
        Assert.assertTrue(row.contains("Rejected"), "Candidate status did not change to rejected");
    }
}
