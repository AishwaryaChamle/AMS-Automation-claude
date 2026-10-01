package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * OB_REJ_007: Verify LOP rejection for an onboarding request with ARI < 30 after successful SMBD approval.
 * Expected: Request returns to the original HOB/AS/LOP requester for correction and resubmission.
 */
public class OB_REJ_007 extends BaseTest {

    @Test
    public void lopRejectionAfterSrMbdApprovalReturnsToRequester() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        Assert.assertTrue(ariTotal < 30, "Test data should give ARI < 30 but gave " + ariTotal);
        onboarding.approve(OnboardingFlows.smbd(), lead);

        onboarding.reject(Config.lop(), lead, "Rejected by LOP - automation");
        String status = onboarding.statusFor(Config.hob(), lead);
        List<String> actions = onboardingPage.actions(lead.fullName());

        actual("LOP rejected after Sr MBD approval. Status for HOB: " + status + ", actions: " + actions);
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Request did not return to HOB");
        Assert.assertTrue(actions.contains("Edit"), "HOB cannot correct the rejected request");
    }
}
