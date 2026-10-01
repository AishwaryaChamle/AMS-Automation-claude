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
 * RLC_DM_004: Verify document history.
 * Expected: Previously uploaded document records/history are displayed.
 */
public class RLC_DM_004 extends BaseTest {

    @Test
    public void documentHistoryDisplayed() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        // Immediate Supervisor approves, so the lead moves to Agency Admin
        Flows.expectSuccess(flows.approveInTray(Config.supervisor(), lead), "Immediate Supervisor approval");

        flows.findInApprovalTray(Config.agencyAdmin(), "In Progress", lead.fullName());
        approvalTray.openDetails(lead.fullName());
        String page = leadPage.pageText();
        List<String> documents = leadPage.documentNames();
        boolean opened = !documents.isEmpty() && leadPage.viewDocument("Document 1");

        actual("Document record shown with the lead history (HOB and Supervisor interview sections present: "
                + (page.contains("HOB Interview") && page.contains("Supervisor Interview")) + "): " + documents
                + ". VIEW opened the document: " + opened);
        Assert.assertFalse(documents.isEmpty(), "No document records shown");
        Assert.assertTrue(opened, "Document record could not be opened with VIEW");
    }
}
