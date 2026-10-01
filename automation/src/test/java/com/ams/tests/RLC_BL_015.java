package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_015: Verify invalid blacklist structure.
 * Expected: Upload is rejected with an appropriate structure validation error.
 */
public class RLC_BL_015 extends BaseTest {

    @Test
    public void invalidStructureRejected() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Blacklisting");

        String message = uploadPage.upload(UploadFiles.blacklistWithoutColumn(TestData.uniqueFile("blacklist_no_status", "xlsx"), TestData.adultLead().nic(), "STATUS"));

        actual("Upload of a file with a missing column: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        Assert.assertTrue(message.toLowerCase().matches("(?s).*(missing|invalid|required|failed).*"), "Expected error message not shown: " + message);
    }
}
