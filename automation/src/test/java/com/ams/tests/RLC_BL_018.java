package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_018: Verify prevented blacklist upload.
 * Expected: Upload is prevented and an appropriate error message is displayed.
 */
public class RLC_BL_018 extends BaseTest {

    @Test
    public void uploadNotPossibleWithoutFile() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Blacklisting");
        boolean enabled = uploadPage.isUploadEnabled();

        actual("UPLOAD button enabled with no file selected: " + enabled);
        Assert.assertFalse(enabled, "Upload can be started without a file");
    }
}
