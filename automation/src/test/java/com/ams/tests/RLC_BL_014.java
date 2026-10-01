package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_014: Verify invalid blacklist template.
 * Expected: Upload is rejected with an appropriate template error.
 */
public class RLC_BL_014 extends BaseTest {

    @Test
    public void invalidTemplateRejected() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Blacklisting");

        String message = uploadPage.upload(UploadFiles.wrongTemplate(TestData.uniqueFile("blacklist_wrong_template", "xlsx")));

        actual("Upload of a file with a wrong template: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        Assert.assertTrue(message.toLowerCase().matches("(?s).*(missing|invalid|required|failed).*"), "Expected error message not shown: " + message);
    }
}
