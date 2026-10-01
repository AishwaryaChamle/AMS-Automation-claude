package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.base.Config;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;

/**
 * AA_SLII_006: Verify uploaded SLII exam data is correctly retrieved during onboarding for the corresponding agent.
 * Expected: System retrieves the applicable SLII exam information and uses it during agent onboarding.
 */
public class AA_SLII_006 extends BaseTest {

    @Test
    public void sliiResultsFilledIntoOnboardingForm() throws Exception {
        Lead lead = onboarding.newLead();

        // Agency Admin uploads the SLII exam result of the candidate's NIC
        Object[] result = UploadFiles.sliiRow(lead.nic(), 72);
        Path file = UploadFiles.slii(TestData.uniqueFile("slii_onboarding", "xlsx"), List.<Object[]>of(result));
        String upload = flows.uploadAsAgencyAdmin("SLII Exam Management", file);
        Assert.assertTrue(upload.contains("File Uploaded Successfully"), "SLII upload failed: " + upload);

        // HOB opens the onboarding form and goes to the qualification page
        onboarding.openInSummary(Config.hob(), lead);
        onboardingPage.edit(lead.fullName());
        onboardingPage.fillApplicantDetails("SC", "Life Insurance Advisor");
        onboardingPage.next();
        onboardingPage.uploadMandatoryDocuments();
        onboardingPage.next();
        onboardingPage.fillPersonalDetails();
        onboardingPage.next();
        onboardingPage.fillAddressDetails();
        onboardingPage.next();
        boolean filled = onboardingPage.waitForIbslData();
        String admissionNo = onboardingPage.ibslValue("ibslAdmissionNo");
        String marks = onboardingPage.ibslValue("ibslMarks");

        actual("Uploaded SLII result: admission " + result[4] + ", marks " + result[1] + ". IBSL data on the onboarding form: admission '"
                + admissionNo + "', marks '" + marks + "'");
        Assert.assertTrue(filled, "IBSL examination data was not filled from the SLII results");
        Assert.assertEquals(admissionNo, result[4], "Admission number");
        Assert.assertEquals(marks, String.valueOf(result[1]), "Marks");
    }
}
