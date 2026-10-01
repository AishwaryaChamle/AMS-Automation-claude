package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData.Lead;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * TC_ONB_021: Verify the mandatory document page contains AS Regstraion field
 * Expected: When we choose the designaition Agency Supervisor (In the personal details page) In the mandatry document page "AS Registration" field should be available to upload the document.
 */
public class TC_ONB_021 extends BaseTest {

    @Test
    public void agencySupervisorDesignationRequiresAsRegistration() throws Exception {
        Lead lead = onboarding.newLead();

        // HOB chooses the designation on the first page and opens the mandatory documents page
        onboarding.openInSummary(Config.hob(), lead);
        onboardingPage.edit(lead.fullName());
        onboardingPage.fillApplicantDetails("Agency Supervisor", "Agency Supervisor");
        onboardingPage.next();
        List<String> fields = onboardingPage.documentFields();

        actual("Designation Agency Supervisor: document fields " + fields);
        Assert.assertTrue(fields.stream().anyMatch(f -> f.contains("AS Registration")), "'AS Registration' upload field is missing");
    }
}
