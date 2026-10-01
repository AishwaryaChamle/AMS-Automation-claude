package com.ams.report;

/** The outcome of one test case, written to Executed_Suits.xls. */
public record ExecutionResult(String testCaseId, String status, String actualResult, String remark, String executionDate) {
}
