package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_DM_006: Verify document viewing without download.
 * Expected: Documents can be viewed; document download is not expected as supported functionality.
 */
public class RLC_DM_006 extends BaseTest {

    @Test
    public void documentViewableButNotDownloadable() throws Exception {
        // HOB creates a lead (candidate under 22) with a supporting document and approves it
        Lead lead = TestData.youngLead();
        Flows.expectSuccess(flows.hobCreatesAndApprovesLead(lead), "HOB approval");

        flows.findInApprovalTray(Config.supervisor(), "In Progress", lead.fullName());
        approvalTray.openDetails(lead.fullName());
        // The document is checked with its VIEW button
        boolean documentShown = !leadPage.documentNames().isEmpty() && leadPage.viewDocument("Document 1");
        boolean downloadOffered = !driver.findElements(By.xpath("//main//a[@download] | //main//button[contains(translate(.,'DOWNLOAD','download'),'download')]")).isEmpty();

        actual("Document opened with VIEW: " + documentShown + "; download option offered: " + downloadOffered);
        Assert.assertTrue(documentShown, "Document is not viewable");
        Assert.assertFalse(downloadOffered, "A download option is offered");
    }
}
