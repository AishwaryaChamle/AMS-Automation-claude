package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_018: Verify empty file.
 * Expected: Upload is rejected with an appropriate empty-file error.
 */
public class RLC_SLII_018 extends BaseTest {

    @Test
    public void emptyFileRejected() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("SLII Exam Management");

        String message = uploadPage.upload(UploadFiles.headersOnly("SLII-exam-results-upload-template.xlsx", TestData.uniqueFile("slii_empty", "xlsx")));

        actual("Upload of a file without data rows: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        Assert.assertTrue(message.contains("No data rows"), "Expected error message not shown: " + message);
    }
}
