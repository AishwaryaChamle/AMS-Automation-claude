package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_HA_019: Verify age threshold for exactly 22.
 * Expected: System applies the requirement's defined age-threshold workflow consistently to exactly 22 without treating the candidate as above 22.
 */
public class RLC_HA_019 extends BaseTest {

    // Age-threshold rule confirmed by the business: supporting documents are required only for candidates
    // UNDER 22, so a candidate aged exactly 22 follows the normal (22 and above) workflow.
    @Test
    public void candidateAgedExactly22FollowsTwentyTwoAndAboveWorkflow() throws Exception {
        // Candidate who turns 22 today
        Lead lead = TestData.leadAgedExactly22();
        flows.startLead(Config.hob(), flows.sharedCampaign(), lead);
        String age = leadPage.derivedAge();
        boolean documentsAsked = leadPage.isSupportingDocumentSectionShown();

        actual("Age shown: " + age + "; supporting-document section shown (under-22 workflow): " + documentsAsked);
        Assert.assertEquals(age, "22", "Age derived from NIC is not 22");
        Assert.assertFalse(documentsAsked, "Candidate aged exactly 22 is asked for under-22 supporting documents");
    }
}
