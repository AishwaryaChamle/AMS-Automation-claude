package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.utils.Flows;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_009: Verify multiple exam attempts.
 * Expected: Multiple exam attempts are retained and distinguishable through the available upload records.
 */
public class RLC_SLII_009 extends BaseTest {

    @Test
    public void multipleExamAttemptsDistinguishable() throws Exception {
        // The same candidate (NIC) appears in two uploads = two exam attempts
        String nic = TestData.adultLead().nic();
        String firstFile = TestData.uniqueFile("slii_attempt1", "xlsx");
        Flows.expectSuccess(flows.uploadAsAgencyAdmin("SLII Exam Management", UploadFiles.slii(firstFile, List.<Object[]>of(UploadFiles.sliiRow(nic, 38)))), "First attempt");
        String secondFile = TestData.uniqueFile("slii_attempt2", "xlsx");
        Flows.expectSuccess(flows.uploadAgain(UploadFiles.slii(secondFile, List.<Object[]>of(UploadFiles.sliiRow(nic, 71)))), "Second attempt");

        String first = uploadPage.viewUploadedFile(firstFile);
        uploadPage.closeViewPopup();
        String second = uploadPage.viewUploadedFile(secondFile);

        actual("Attempt 1: " + first.replace("\n", " | ") + " || Attempt 2: " + second.replace("\n", " | "));
        Assert.assertTrue(first.contains(nic) && first.contains("38"), "First attempt not retained");
        Assert.assertTrue(second.contains(nic) && second.contains("71"), "Second attempt not retained");
    }
}
