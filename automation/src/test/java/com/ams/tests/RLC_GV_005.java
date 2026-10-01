package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_005: Verify protected screen access.
 * Expected: Access to protected screens is denied or unavailable.
 */
public class RLC_GV_005 extends BaseTest {

    @Test
    public void protectedScreensDeniedToUnauthorizedUser() throws Exception {
        // Agency Admin paths of the protected screens
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Blacklisting");
        String blacklistPath = uploadPage.currentPath();
        menu.openRecruitmentCard("SLII Exam Management");
        String sliiPath = uploadPage.currentPath();

        // Immediate Supervisor opens them directly
        loginPage.loginAs(Config.supervisor());
        uploadPage.openPath(blacklistPath);
        boolean blacklistShown = uploadPage.isUploaderShown();
        uploadPage.openPath(sliiPath);
        boolean sliiShown = uploadPage.isUploaderShown();

        actual("Supervisor direct access - Blacklisting: " + blacklistShown + ", SLII Exam Management: " + sliiShown);
        Assert.assertFalse(blacklistShown, "Blacklisting is accessible to the Supervisor");
        Assert.assertFalse(sliiShown, "SLII Exam Management is accessible to the Supervisor");
    }
}
