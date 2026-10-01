package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_AT_006: Verify approval/rejection access restriction.
 * Expected: Approval/rejection controls are unavailable or the action is denied.
 */
public class RLC_AT_006 extends BaseTest {

    @Test
    public void unauthorizedUserCannotApproveOrReject() throws Exception {
        // HOB creates a lead (candidate above 22) and approves it for interview
        Lead lead = TestData.adultLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // The lead is pending with the Supervisor; the AS must not be able to act on it
        flows.findInApprovalTray(Config.agencySupervisor(), "In Progress", lead.fullName());
        boolean shownToAs = approvalTray.isListed(lead.fullName());

        actual("Lead pending with Supervisor shown in AS Approval Tray: " + shownToAs);
        Assert.assertFalse(shownToAs, "AS can approve / reject a lead that is not assigned to him");
    }
}
