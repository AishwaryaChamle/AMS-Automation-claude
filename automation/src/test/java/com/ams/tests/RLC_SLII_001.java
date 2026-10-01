package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_SLII_001: Verify Agency Admin section access.
 * Expected: Agency Admin can access the section.
 */
public class RLC_SLII_001 extends BaseTest {

    @Test
    public void agencyAdminCanAccessSection() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("SLII Exam Management");
        String instruction = uploadPage.uploadInstruction();

        actual("Agency Admin opened SLII Exam Management. Instruction: " + instruction);
        Assert.assertTrue(uploadPage.isUploaderShown(), "Upload section is not displayed");
    }
}
