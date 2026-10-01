package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_017: Verify supporting-document validation.
 * Expected: Required supporting-document validation prevents the applicable progression.
 */
public class RLC_HA_017 extends BaseTest {

    @Test
    public void supportingDocumentRequiredForYoungCandidate() throws Exception {
        // Candidate under 22 without any supporting document
        Lead lead = TestData.youngLead();
        flows.startLead(Config.hob(), flows.sharedCampaign(), lead);
        leadPage.setInterview(Flows.AGREED, TestData.today(), "No document - automation");
        boolean approveEnabled = leadPage.isApprovedEnabled();
        String outcome = "APPROVED button disabled";
        if (approveEnabled) {
            leadPage.clickApproved();
            outcome = leadPage.actionOutcome();
        }

        actual("Age " + lead.age() + ", no document. APPROVED enabled: " + approveEnabled + ". Outcome: " + outcome);
        Assert.assertFalse(outcome.contains("Lead Approved Successfully"), "Lead was approved without the required supporting document");
    }
}
