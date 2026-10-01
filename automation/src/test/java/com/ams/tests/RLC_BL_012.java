package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_012: Verify non-blacklisted candidate.
 * Expected: Candidate is not identified as blacklisted based on the uploaded blacklist.
 */
public class RLC_BL_012 extends BaseTest {

    @Test
    public void nonBlacklistedCandidateProceeds() throws Exception {
        Lead lead = TestData.adultLead();
        String message = flows.hobCreatesLead(lead);

        actual("Lead with a NIC that is not blacklisted: '" + message + "'");
        Assert.assertTrue(message.contains("New Lead Initiated"), "Non-blacklisted candidate could not be created: " + message);
    }
}
