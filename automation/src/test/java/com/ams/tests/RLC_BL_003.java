package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_BL_003: Verify blacklist upload instructions.
 * Expected: Applicable blacklist upload instructions are displayed.
 */
public class RLC_BL_003 extends BaseTest {

    @Test
    public void uploadInstructionsDisplayed() throws Exception {
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Blacklisting");
        String instruction = uploadPage.uploadInstruction();

        actual("Instruction shown: " + instruction);
        Assert.assertTrue(instruction.toLowerCase().contains("xlsx") && instruction.toLowerCase().contains("csv"), "Upload instructions not displayed");
    }
}
