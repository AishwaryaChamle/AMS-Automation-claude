package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_DM_001: Verify supported image upload.
 * Expected: Supported image is uploaded successfully and associated with the relevant lead/candidate.
 */
public class RLC_DM_001 extends BaseTest {

    @Test
    public void supportedImageUploadedForLead() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        flows.findInApprovalTray(Config.supervisor(), "In Progress", lead.fullName());
        approvalTray.openDetails(lead.fullName());
        List<String> documents = leadPage.documentNames();
        boolean opened = documents.stream().anyMatch(d -> d.equals("Document 1"))
                && leadPage.viewDocument("Document 1");

        actual("Documents attached to the lead (Supervisor view): " + documents + ". VIEW opened the image: " + opened);
        Assert.assertTrue(documents.stream().anyMatch(d -> d.equals("Document 1")), "Uploaded image is not attached to the lead");
        Assert.assertTrue(opened, "Uploaded image could not be opened with VIEW");
    }
}
