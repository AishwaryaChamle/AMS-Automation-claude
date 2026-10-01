package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.OnboardingFlows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OB_REJ_009: Verify LOP rejection does not route the request directly to Agency Admin.
 * Expected: Request is returned to the original requester and does not proceed to AA until LOP approves it.
 */
public class OB_REJ_009 extends BaseTest {

    @Test
    public void lopRejectionDoesNotRouteRequestToAgencyAdmin() throws Exception {
        Lead lead = onboarding.newLead();
        onboarding.raiseRequest(Config.hob(), lead, TestData.HIGH_ARI);

        onboarding.reject(Config.lop(), lead, "Rejected by LOP - automation");
        boolean inAgencyAdminTray = onboarding.isPendingWith(Config.agencyAdmin(), lead);
        String status = onboarding.statusFor(Config.hob(), lead);

        actual("After LOP rejection - in Agency Admin tray: " + inAgencyAdminTray + ", status for HOB: " + status);
        Assert.assertFalse(inAgencyAdminTray, "Rejected request went to Agency Admin");
        Assert.assertEquals(status, OnboardingFlows.STATUS_REJECTED, "Request did not return to HOB");
    }
}
