package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_016: Verify empty blacklist file.
 * Expected: Upload is rejected with an appropriate empty-file error.
 */
public class RLC_BL_016 extends BaseTest {

    @Test
    public void emptyFileRejected() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Blacklisting");

        String message = uploadPage.upload(UploadFiles.headersOnly("Blacklisting-upload-template.xlsx", TestData.uniqueFile("blacklist_empty", "xlsx")));

        actual("Upload of a file without data rows: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        Assert.assertTrue(message.contains("No data rows"), "Expected error message not shown: " + message);
    }
}
