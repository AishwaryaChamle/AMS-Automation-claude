package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_010: Verify blacklist detection during recruitment.
 * Expected: Candidate is identified as blacklisted during recruitment.
 */
public class RLC_BL_010 extends BaseTest {

    @Test
    public void blacklistedCandidateDetectedDuringRecruitment() throws Exception {
        // Agency Admin blacklists the candidate's NIC
        Lead lead = TestData.adultLead();
        String fileName = TestData.uniqueFile("blacklist_detect", "xlsx");
        Flows.expectSuccess(flows.uploadAsAgencyAdmin("Blacklisting",
                UploadFiles.blacklist(fileName, List.<Object[]>of(UploadFiles.blacklistRow(lead.lastName(), lead.nic())))), "Blacklist upload");

        // HOB tries to create a lead for that candidate
        flows.startLeadWithoutChecks(lead);
        String outcome;
        // The NIC is checked against the blacklist in the background, so give the message time to appear
        if (leadPage.waitForText("lacklist")) {
            outcome = "Shown on the form: " + String.join("; ", leadPage.validationMessages()) + " " + leadPage.pageText().replaceAll("(?s).*(?i)(.{0,80}blacklist.{0,80}).*", "$1");
        } else {
            leadPage.clickSubmit();
            outcome = leadPage.actionOutcome();
        }

        actual("Lead with blacklisted NIC " + lead.nic() + ": " + outcome);
        Assert.assertTrue(outcome.toLowerCase().contains("blacklist"), "Candidate was not identified as blacklisted");
        Assert.assertFalse(outcome.contains("Initiated"), "Blacklisted candidate was created as a lead");
    }
}
