package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.pages.OnboardingPage;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * TC_ONB_014: Verify Medical Report option availability for candidate age >= 50 in the LOP approval tray
 * Expected: Medical Report upload option should be displayed on the document upload page in the LOP approval tray
 */
public class TC_ONB_014 extends BaseTest {

    @Test
    public void medicalReportOptionShownToLopForCandidateAged50OrMore() throws Exception {
        Lead lead = onboarding.newLead(TestData.leadAged(52));
        onboarding.raiseRequest(Config.hob(), lead, TestData.LOW_ARI);

        // LOP opens the request from the Approval Tray and looks at the document upload page
        flows.findInApprovalTray(Config.lop(), "In Progress", lead.fullName());
        approvalTray.openOnboardingDetails(lead.fullName());
        onboardingPage.nextUntil(OnboardingPage.DOCUMENTS_STEP);
        List<String> fields = onboardingPage.documentFields();

        actual("Candidate aged " + lead.age() + ". Document fields shown to LOP: " + fields);
        Assert.assertTrue(fields.stream().anyMatch(f -> f.contains("Medical Report")), "Medical Report upload option is not shown to LOP");
    }
}
