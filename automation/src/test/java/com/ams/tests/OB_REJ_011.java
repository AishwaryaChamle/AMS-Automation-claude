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
 * OB_REJ_011: Verify Agency Admin rejection after LOP approval for an ARI < 30 onboarding request.
 * Expected: Request returns to the original HOB/AS/LOP requester for correction and resubmission.
 */
public class OB_REJ_011 extends BaseTest {

    @Test
    public void agencyAdminRejectionAfterLopApprovalReturnsToRequester() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        Assert.assertTrue(ariTotal < 30, "Test data should give ARI < 30 but gave " + ariTotal);
        onboarding.approve(OnboardingFlows.smbd(), lead);
        onboarding.approve(Config.lop(), lead);

        onboarding.reject(Config.agencyAdmin(), lead, "Rejected by Agency Admin - automation");
        String status = onboarding.statusFor(Config.hob(), lead);
        List<String> actions = onboardingPage.actions(lead.fullName());

        actual("Agency Admin rejected after Sr MBD and LOP approval. Status for HOB: " + status + ", actions: " + actions);
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Request did not return to HOB");
        Assert.assertTrue(actions.contains("Edit"), "HOB cannot correct the rejected request");
    }
}
