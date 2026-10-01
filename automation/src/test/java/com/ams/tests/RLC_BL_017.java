package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_017: Verify blacklist upload failure.
 * Expected: Upload fails and an appropriate error message is displayed.
 */
public class RLC_BL_017 extends BaseTest {

    @Test
    public void uploadFailureShowsErrorMessage() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Blacklisting");

        String message = uploadPage.upload(UploadFiles.corruptedExcel(TestData.uniqueFile("blacklist_corrupted", "xlsx")));

        actual("Upload of a corrupted file: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        Assert.assertTrue(message.toLowerCase().matches("(?s).*(failed|error|missing|invalid|required).*"), "Expected error message not shown: " + message);
    }
}
