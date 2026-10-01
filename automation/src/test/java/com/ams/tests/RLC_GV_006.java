package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RLC_GV_006: Verify equivalent-role access consistency.
 * Expected: Access is determined by role and is consistent for equivalent roles.
 */
public class RLC_GV_006 extends BaseTest {

    @Test
    public void equivalentRolesHaveConsistentAccess() throws Exception {
        // Both Agency Admin users must see the same screens
        loginPage.loginAs(Config.agencyAdmin());
        List<String> firstCards = menu.recruitmentCards();
        loginPage.loginAs(Config.agencyAdmin2());
        List<String> secondCards = menu.recruitmentCards();

        actual("Agency Admin 1 cards: " + firstCards + "; Agency Admin 2 cards: " + secondCards);
        Assert.assertEquals(secondCards, firstCards, "Access differs between users with the same role");
    }
}
