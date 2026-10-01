package com.ams.tests;

import com.ams.base.Blocked;
import org.testng.annotations.Test;

/**
 * RLC_HA_012: Verify Reschedule Interview status.
 * Expected: Candidate status changes to Reschedule Interview.
 * Marked Invalid by the tester: the application has no "Reschedule" interview status
 * (the lead offers only Agreed for the interview, Cannot be contacted, Not available for the interview).
 */
public class RLC_HA_012 {

    @Test
    public void candidateStatusRescheduleInterview() {
        Blocked.invalid("Marked Invalid by the tester: the application has no 'Reschedule' interview status "
                + "(the lead offers only Agreed for the interview, Cannot be contacted, Not available for the interview).");
    }
}
