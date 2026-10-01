package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_DM_002: Verify document viewing.
 * Expected: Document can be viewed successfully.
 */
public class RLC_DM_002 extends BaseTest {

    @Test
    public void uploadedDocumentCanBeViewed() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        flows.findInApprovalTray(Config.supervisor(), "In Progress", lead.fullName());
        approvalTray.openDetails(lead.fullName());
        // Check the document with its VIEW button: it opens in a new tab or in a viewer on the page
        boolean opened = leadPage.viewDocument("Document 1");

        actual("Clicking VIEW opened the document (viewer / new tab): " + opened);
        Assert.assertTrue(opened, "Document could not be viewed");
    }
}
