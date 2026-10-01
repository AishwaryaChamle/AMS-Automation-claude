package com.ams.base;

import org.testng.SkipException;

/**
 * Used by test cases that cannot be executed in this environment
 * (for example missing user credentials). The test is reported as "Blocked"
 * with the reason, instead of pretending it passed or failed.
 * A test case marked 'Not in scope' by ASPL is reported as "Invalid" instead (see {@link #invalid}).
 */
public final class Blocked {

    public static final String PREFIX = "BLOCKED: ";
    public static final String INVALID_PREFIX = "INVALID: ";

    // Reasons shared by several test cases
    public static final String NO_LOP_USER =
            "No LOP user credentials are provided in Recruitment_Leads_Mgmt.md, so the LOP approval stage cannot be executed.";
    public static final String NO_MARKETING_USER =
            "No Marketing Team user credentials are provided in Recruitment_Leads_Mgmt.md.";
    public static final String ONE_USER_PER_ROLE =
            "Only one user per role is provided in Recruitment_Leads_Mgmt.md; a second user with the same role is required.";
    public static final String OUT_OF_SCOPE =
            "Marked 'Not in scope' by ASPL in the test scenario document; there is no functional behaviour to automate.";

    private Blocked() {
    }

    public static void because(String reason) {
        throw new SkipException(PREFIX + reason);
    }

    /** The test case is not valid for this release (e.g. 'Not in scope'): reported as "Invalid". */
    public static void invalid(String reason) {
        throw new SkipException(INVALID_PREFIX + reason);
    }
}
