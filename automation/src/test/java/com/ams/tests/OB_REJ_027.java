package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_027: Verify repeated rejection/resubmission for an ARI > 30 request follows LOP -> AA approval flow without involving SMBD.
 * Expected: Every corrected resubmission goes to LOP first and then AA after LOP approval.
 */
public class OB_REJ_027 extends BaseTest {

    @Test
    public void ari30OrMoreResubmissionFollowsLopThenAgencyAdmin() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);

        onboarding.reject(Config.lop(), lead, "Rejected by LOP - automation");
        onboarding.resubmit(Config.hob(), lead);
        boolean srMbdAfterResubmit = onboarding.isPendingWith(OnboardingFlows.smbd(), lead);
        onboarding.approve(Config.lop(), lead);
        boolean withAgencyAdmin = onboarding.isPendingWith(Config.agencyAdmin(), lead);

        actual("Resubmitted request with Sr MBD: " + srMbdAfterResubmit + ". After LOP approval with Agency Admin: " + withAgencyAdmin);
        Assert.assertFalse(srMbdAfterResubmit, "Sr MBD was involved in an ARI >= 30 request");
        Assert.assertTrue(withAgencyAdmin, "Request did not move from LOP to Agency Admin");
    }
}
