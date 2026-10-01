package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_008: Verify HOB/AS/LOP can resubmit an onboarding request after LOP rejection.
 * Expected: Original requester can correct the request and raise it again; it follows the applicable approval flow again.
 */
public class OB_REJ_008 extends BaseTest {

    @Test
    public void resubmissionAfterLopRejectionFollowsApprovalFlowAgain() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);
        onboarding.approve(OnboardingFlows.smbd(), lead);
        onboarding.reject(Config.lop(), lead, "Rejected by LOP - automation");

        // HOB corrects and raises again: an ARI < 30 request starts again with the Sr MBD
        String resubmitted = onboarding.resubmit(Config.hob(), lead);
        boolean withSrMbd = onboarding.isPendingWith(OnboardingFlows.smbd(), lead);

        actual("Resubmission after LOP rejection: '" + resubmitted + "'. In Sr MBD tray: " + withSrMbd);
        Assert.assertTrue(resubmitted.contains(OnboardingFlows.REQUEST_RAISED), "Resubmission failed: " + resubmitted);
        Assert.assertTrue(withSrMbd, "Resubmitted request does not follow the approval flow (not with Sr MBD)");
    }
}
