package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * TC_ONB_022: Verify the mandatory document page contains AS Regstraion field and CRIB Report field.
 * Expected: When we choose the designaition Agency Head(In the personal details page) In the mandatry document page "AS Registration" field and "CRIB Report" should be available to upload the document.
 */
public class TC_ONB_022 extends BaseTest {

    @Test
    public void agencyHeadDesignationRequiresAsRegistrationAndCribReport() throws Exception {
        Lead lead = onboarding.newLead();

        // HOB chooses the designation on the first page and opens the mandatory documents page
        onboarding.openInSummary(Config.hob(), lead);
        onboardingPage.edit(lead.fullName());
        onboardingPage.fillApplicantDetails("HOB", "Agency Head");
        onboardingPage.next();
        List<String> fields = onboardingPage.documentFields();

        actual("Designation Agency Head: document fields " + fields);
        Assert.assertTrue(fields.stream().anyMatch(f -> f.contains("AS Registration")), "'AS Registration' upload field is missing");
        Assert.assertTrue(fields.stream().anyMatch(f -> f.contains("CRIB Report")), "'CRIB Report' upload field is missing");
    }
}
