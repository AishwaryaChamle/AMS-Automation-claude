package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_014: Verify unsupported file format.
 * Expected: Upload is rejected with an appropriate supported-format error.
 */
public class RLC_SLII_014 extends BaseTest {

    @Test
    public void unsupportedFileFormatRejected() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("SLII Exam Management");

        String message = uploadPage.upload(UploadFiles.textFile(TestData.uniqueFile("slii", "txt")));

        actual("Upload of a .txt file: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        Assert.assertTrue(message.contains("Unsupported file format"), "Expected error message not shown: " + message);
    }
}
