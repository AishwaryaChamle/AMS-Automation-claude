package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_022: Verify blacklist upload restriction.
 * Expected: Upload action is unavailable or denied.
 */
public class RLC_BL_022 extends BaseTest {

    @Test
    public void unauthorizedUserCannotUploadBlacklist() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Blacklisting");
        String path = uploadPage.currentPath();

        // HOB opens the same address directly
        loginPage.loginAs(Config.hob());
        uploadPage.openPath(path);
        boolean shownToHob = uploadPage.isUploaderShown();

        actual("Blacklisting path " + path + " opened by HOB shows the uploader: " + shownToHob);
        Assert.assertFalse(shownToHob, "HOB can access Blacklisting");
    }
}
