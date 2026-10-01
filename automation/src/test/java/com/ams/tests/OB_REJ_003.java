package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_003: Verify LOP can resubmit an onboarding request after SMBD rejects an applicant with ARI < 30.
 * Expected: Request returns to LOP, and LOP can resubmit the corrected request.
 */
public class OB_REJ_003 extends BaseTest {

    @Test
    public void lopResubmitsAfterSrMbdRejection() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.lop(), lead, TestData.LOW_ARI);
        Assert.assertTrue(ariTotal < 30, "Test data should give ARI < 30 but gave " + ariTotal);

        // Sr MBD rejects: the request returns to the LOP
        onboarding.reject(OnboardingFlows.smbd(), lead, "Please correct the bank account number - automation");
        String status = onboarding.statusFor(Config.lop(), lead);

        // LOP corrects the application and raises it again
        String resubmitted = onboarding.resubmit(Config.lop(), lead);
        boolean backWithSrMbd = onboarding.isPendingWith(OnboardingFlows.smbd(), lead);

        actual("Status for LOP after Sr MBD rejection: " + status + ". Resubmission pop-up: '" + resubmitted
                + "'. In Sr MBD tray again: " + backWithSrMbd);
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Request did not return to the LOP");
        Assert.assertTrue(resubmitted.contains(OnboardingFlows.REQUEST_RAISED), "Corrected request was not raised again: " + resubmitted);
        Assert.assertTrue(backWithSrMbd, "Resubmitted request is not with the Sr MBD");
    }
}
