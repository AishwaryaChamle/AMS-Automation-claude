package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_016: Verify invalid structure.
 * Expected: File is rejected and an appropriate structure error is displayed.
 */
public class RLC_SLII_016 extends BaseTest {

    @Test
    public void invalidStructureRejected() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("SLII Exam Management");

        String message = uploadPage.upload(UploadFiles.sliiWithoutColumn(TestData.uniqueFile("slii_no_admission", "xlsx"), TestData.adultLead().nic(), "Admission No"));

        actual("Upload of a file with a missing column: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        Assert.assertTrue(message.toLowerCase().matches("(?s).*(missing|invalid|required|failed).*"), "Expected error message not shown: " + message);
    }
}
