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
 * RLC_DM_003: Verify previously uploaded document.
 * Expected: Previously uploaded document remains visible.
 */
public class RLC_DM_003 extends BaseTest {

    @Test
    public void previouslyUploadedDocumentRemainsVisible() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor approves, so the lead moves to Agency Admin
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Immediate Supervisor approval");

        // At the next stage (Agency Admin) the document must still be there
        flows.findInApprovalTray(Config.agencyAdmin(), "In Progress", lead.fullName());
        approvalTray.openDetails(lead.fullName());
        List<String> documents = leadPage.documentNames();
        boolean opened = documents.stream().anyMatch(d -> d.equals("Document 1"))
                && leadPage.viewDocument("Document 1");

        actual("Documents visible to Agency Admin: " + documents + ". VIEW opened the document: " + opened);
        Assert.assertTrue(documents.stream().anyMatch(d -> d.equals("Document 1")), "Previously uploaded document is not visible");
        Assert.assertTrue(opened, "Previously uploaded document could not be opened with VIEW");
    }
}
