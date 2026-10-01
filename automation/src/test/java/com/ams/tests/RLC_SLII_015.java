package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_015: Verify invalid template.
 * Expected: Upload is rejected with an appropriate template validation message.
 */
public class RLC_SLII_015 extends BaseTest {

    @Test
    public void invalidTemplateRejected() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("SLII Exam Management");

        String message = uploadPage.upload(UploadFiles.wrongTemplate(TestData.uniqueFile("slii_wrong_template", "xlsx")));

        actual("Upload of a file with a wrong template: '" + message + "'");
        Assert.assertFalse(message.contains("File Uploaded Successfully"), "The file was accepted");
        Assert.assertTrue(message.toLowerCase().matches("(?s).*(missing|invalid|required|failed).*"), "Expected error message not shown: " + message);
    }
}
