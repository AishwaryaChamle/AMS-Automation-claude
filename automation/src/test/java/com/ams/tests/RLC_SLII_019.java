package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_019: Verify no file selected.
 * Expected: Upload is not performed and an appropriate no-file-selected message is displayed.
 */
public class RLC_SLII_019 extends BaseTest {

    @Test
    public void uploadNotPossibleWithoutFile() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("SLII Exam Management");
        boolean enabled = uploadPage.isUploadEnabled();

        actual("UPLOAD button enabled with no file selected: " + enabled);
        Assert.assertFalse(enabled, "Upload can be started without a file");
    }
}
