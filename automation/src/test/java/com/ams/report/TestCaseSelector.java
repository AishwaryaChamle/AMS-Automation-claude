package com.ams.report;

import com.ams.base.Config;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Runs only the test cases chosen with -Dtestcases (class name = TestCase ID):
 *   mvn test -Dtestcases=TC_ONB_001                       one test case
 *   mvn test -Dtestcases="TC_ONB_001, TC_ONB_005"         several test cases
 *   mvn test                  (or -Dtestcases=all)        the test cases whose Priority is chosen at the prompt ("priorities")
 *                                                          (config.properties, e.g. P1,P2,P3; -Dpriorities=all for every priority)
 *   mvn test -Dtestcases=none                             no test, only rebuild Test_Execution_Result.xlsx
 */
public class TestCaseSelector implements IMethodInterceptor {

    /** Number of test cases selected for this run (shown on the live status page). */
    static volatile int plannedCount;

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        // "none": execute nothing, only rebuild Test_Execution_Result.xlsx from the saved results
        if (Config.get("testcases").equalsIgnoreCase("none")) {
            System.out.println("No test case executed: test_execution_result.xls rebuilt from the saved results");
            return List.of();
        }
        // Execute in the order the classes are listed in testng.xml (Sr No 1, 2, 3 ...)
        List<String> listed = context.getCurrentXmlTest().getXmlClasses().stream().map(c -> c.getName()).toList();
        methods = methods.stream()
                .sorted(java.util.Comparator.comparingInt(m -> {
                    int position = listed.indexOf(m.getMethod().getRealClass().getName());
                    return position < 0 ? Integer.MAX_VALUE : position;
                }))
                .toList();

        // A test case listed more than once (e.g. twice in testng.xml) is executed only once
        Set<String> seen = new LinkedHashSet<>();
        methods = methods.stream()
                .filter(m -> seen.add(m.getMethod().getRealClass().getSimpleName().toUpperCase() + "#" + m.getMethod().getMethodName()))
                .toList();

        Set<String> selected = selectedTestCases();
        if (selected.isEmpty()) {
            // No test case IDs given: execute the test cases whose Priority is selected (priorities=P1,P2,P3)
            List<IMethodInstance> byPriority = methods.stream()
                    .filter(m -> TestCaseBook.hasSelectedPriority(m.getMethod().getRealClass().getSimpleName()))
                    .toList();
            System.out.println("Test cases with priority " + Config.get("priorities") + ": " + byPriority.size()
                    + " of " + methods.size() + " in the suite");
            plannedCount = byPriority.size();
            return byPriority;
        }
        List<IMethodInstance> toRun = methods.stream()
                .filter(m -> selected.contains(m.getMethod().getRealClass().getSimpleName().toUpperCase()))
                .toList();

        // Tell the tester about IDs that are not in the suite (typing mistakes)
        Set<String> found = toRun.stream().map(m -> m.getMethod().getRealClass().getSimpleName().toUpperCase()).collect(Collectors.toSet());
        Set<String> unknown = new LinkedHashSet<>(selected);
        unknown.removeAll(found);
        System.out.println("Selected test cases to execute: " + found.size() + " of " + selected.size() + " requested"
                + (unknown.isEmpty() ? "" : ". Not found in this suite: " + unknown));
        plannedCount = toRun.size();
        return toRun;
    }

    /** IDs from -Dtestcases, e.g. "TC_001, TC_005"; empty means "run everything". */
    static Set<String> selectedTestCases() {
        String value = Config.get("testcases");
        if (value.isBlank() || value.equalsIgnoreCase("all")) {
            return Set.of();
        }
        return Arrays.stream(value.split("[,\\s]+"))
                .map(s -> s.trim())
                .filter(id -> !id.isEmpty())
                .map(s -> s.toUpperCase())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
