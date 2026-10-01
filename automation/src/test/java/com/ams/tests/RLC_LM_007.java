package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.pages.FunnelPage;
import com.ams.utils.Database;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Map;

/**
 * RLC_LM_007: Verify funnel stage counts.
 * Expected: Count at each funnel stage corresponds to the leads in that stage.
 * The count of each stage is checked against the database query for that stage (Database.md).
 */
public class RLC_LM_007 extends BaseTest {

    @Test
    public void funnelCountsMatchLeads() throws Exception {
        // Agency Admin opens Recruitment Leads Mgmt > Recruitment Funnel
        loginPage.loginAs(Config.agencyAdmin());
        menu.openRecruitmentCard("Recruitment Funnel");
        FunnelPage funnel = new FunnelPage(driver);

        // Other test runs may add leads meanwhile: read the database before and after the screen,
        // and repeat until both database readings are the same
        Map<String, Long> screen = null;
        Map<String, Long> database = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            Map<String, Long> before = Database.funnelCounts();
            driver.navigate().refresh();
            screen = funnel.stageCounts();
            database = Database.funnelCounts();
            if (before.equals(database)) {
                break;
            }
        }

        if (screen == null || database == null) {
            throw new IllegalStateException("Funnel counts were not read");
        }
        actual("Funnel on screen: " + screen + ". Database counts (Database.md queries): " + database);
        Assert.assertEquals(screen.keySet(), database.keySet(), "Funnel stages shown on the screen");
        SoftAssert stages = new SoftAssert();
        for (String stage : database.keySet()) {
            stages.assertEquals(screen.get(stage), database.get(stage), "Count of stage " + stage);
        }
        stages.assertAll();
    }
}
