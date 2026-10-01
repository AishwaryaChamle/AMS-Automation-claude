package com.ams.tests;

import com.ams.base.BaseTest;
import com.ams.pages.OnboardingPage;
import com.ams.utils.TestData;
import com.ams.utils.TestData.Lead;
import com.ams.utils.UploadFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * RLC_BL_011: Verify blacklist detection during onboarding.
 * Expected: Candidate is identified as blacklisted during onboarding.
 */
public class RLC_BL_011 extends BaseTest {

    @Test
    public void blacklistedCandidateDetectedDuringOnboarding() throws Exception {
        // Agency Admin blacklists a NIC
        Lead person = TestData.adultLead();
        String fileName = TestData.uniqueFile("blacklist_onboarding", "xlsx");
        String upload = flows.uploadAsAgencyAdmin("Blacklisting",
                UploadFiles.blacklist(fileName, List.<Object[]>of(UploadFiles.blacklistRow(person.lastName(), person.nic()))));
        Assert.assertTrue(upload.contains("File Uploaded Successfully"), "Blacklist upload failed: " + upload);

        // Agency Admin starts an onboarding application with that NIC
        onboardingPage.openSummary();
        onboardingPage.startNewApplication(person.nic());
        String message = onboardingPage.newApplicationMessage();
        String step = onboardingPage.currentStep();

        actual("New Application with blacklisted NIC " + person.nic() + ": message '" + message + "', page: " + step);
        Assert.assertTrue(message.toLowerCase().contains("blacklist"), "Candidate was not identified as blacklisted");
        Assert.assertNotEquals(step, OnboardingPage.APPLICANT_STEP, "Onboarding continued for a blacklisted NIC");
    }
}
