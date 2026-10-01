package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_002: Verify restricted section access.
 * Expected: Access is denied or the section is unavailable.
 */
public class RLC_SLII_002 extends BaseTest {

    @Test
    public void otherRolesCannotAccessSection() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("SLII Exam Management");
        String path = uploadPage.currentPath();

        // HOB opens the same address directly
        loginPage.loginAs(Config.hob());
        uploadPage.openPath(path);
        boolean shownToHob = uploadPage.isUploaderShown();

        actual("SLII Exam Management path " + path + " opened by HOB shows the uploader: " + shownToHob);
        Assert.assertFalse(shownToHob, "HOB can access SLII Exam Management");
    }
}
