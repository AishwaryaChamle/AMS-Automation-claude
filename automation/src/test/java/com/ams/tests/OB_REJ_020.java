package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_020: Verify LOP can re-upload a corrected Agent Agreement after AA rejects the Agent Code Activation request.
 * Expected: LOP can upload the corrected agreement and resubmit the activation request.
 */
public class OB_REJ_020 extends BaseTest {

    @Test
    public void lopReUploadsAgreementAfterActivationRejection() throws Exception {
        Lead lead = onboarding.newLead();
        int ariTotal = onboarding.raiseRequest(Config.lop(), lead, TestData.HIGH_ARI);
        onboarding.approveAll(lead, ariTotal);
        onboarding.uploadAgreementAndRequestActivation(Config.lop(), lead);

        // Agency Admin rejects the activation; the LOP uploads the corrected agreement and raises it again
        onboarding.reject(Config.agencyAdmin(), lead, "Please upload the signed agreement - automation");
        String again = onboarding.uploadAgreementAndRequestActivation(Config.lop(), lead);
        boolean withAgencyAdmin = onboarding.isPendingWith(Config.agencyAdmin(), lead);

        actual("LOP re-uploaded the agreement after rejection: '" + again + "'. Activation request with Agency Admin: " + withAgencyAdmin);
        Assert.assertTrue(again.contains(OnboardingFlows.ACTIVATION_RAISED), "Activation request was not raised again: " + again);
        Assert.assertTrue(withAgencyAdmin, "Activation request is not with the Agency Admin");
    }
}
