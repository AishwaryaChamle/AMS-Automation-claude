
## Objective

Automate the test cases defined in `Test_Cases_UAT_Sept_2026` & `Test_Cases_UAT_Sept_2026_Based on Test Scenario` using:

- Java
- Selenium WebDriver
- TestNG
- Maven

 Use `Recruitment_Leads_Mgmt.md` & `ASPL_Test Scenarios for Recruitment Lead Capturing System_0708` as the functional source of truth.

 The complete workflow must be:

Flow.md
   ↓
TestCase_Suits.xls
   ↓
Java + Selenium + TestNG Automation
   ↓
Execute Test Cases
   ↓
Executed_Suits.xls
   ↓
Open Executed_Suits.xls
```

 ## 1\. Read and Understand the Inputs

 Before writing any code:

1. Read the complete `Flow.md`.
2. Read the complete `TestCase_Suits.xls`.
3. Understand every test case in `TestCase_Suits.xls`.
4. Use `Flow.md` to understand the application flow and expected behavior.
5. Inspect the existing Maven project structure.
6. Inspect existing Java classes, Page Objects, utilities, configuration, and TestNG setup.
7. Reuse existing code where appropriate.
8. Do not invent functionality that is not supported by `Flow.md` or the test cases.
9. Ask questions/Confirm if something blocked or do not understand 

 ## 2\. Technology Requirements

 The automation must use:

```
Java
Selenium WebDriver
TestNG
Maven
```

 Use the existing project versions and dependencies if they are already configured.

 Do not introduce another automation framework.

 ## 3\. Test Case Class Naming

 Create an automated test class for each test case.

 The **class name must exactly match the `TestCase_id` from `TestCase_Suits.xls`.**

 For example:

```
TestCase_id = TC_001
Class name  = TC_001
```

 Another example:

```
TestCase_id = TC_002
Class name  = TC_002
```

 Do not use names such as:

```
LoginTest
OrangeHRMLoginTest
TestCase001
TC001LoginTest
```

 The class name must be exactly the TestCase ID.

 ## 4\. Test Method Naming

 The TestNG test method should represent the feature/scenario being tested.

 For example:

```
@Test
public void validUserLogin() {
    // test implementation
}
```

 For another test case:

```
@Test
public void invalidPasswordLogin() {
    // test implementation
}
```

 Do not use the TestCase ID as the method name.

 Maintain this relationship:

```
TestCase ID → Test Class → Test Method

TC_001 → TC_001 → validUserLogin()
TC_002 → TC_002 → invalidPasswordLogin()
TC_003 → TC_003 → emptyUsernameValidation()
```

 ## 5\. Code Quality

 Keep the automation code:

 - Simple
- Clean
- Easy to understand
- Easy to maintain
- Beginner-friendly

 Add useful comments explaining important sections of the code.

 Do not add unnecessary complexity.

 Avoid:

 - Over-engineering
- Unnecessary abstraction
- Duplicate code
- Hard-coded waits
- Unnecessary dependencies
- Complex design patterns unless the existing project already uses them

 Use meaningful variable and method names.

 Example:

```
// Enter valid username
usernameField.sendKeys("Admin");

// Enter valid password
passwordField.sendKeys("admin123");

// Click Login
loginButton.click();
```

 ## 6\. Selenium Automation

 Use Selenium WebDriver for browser automation.

 For each test case:

 1. Navigate to the required application URL.
2. Perform the steps defined in `TestCase_Suits.xls`.
3. Use reliable Selenium locators.
4. Use appropriate explicit waits where required.
5. Perform the required actions.
6. Validate the expected result using TestNG assertions.

 Do not use `Thread.sleep()` unless there is a specific technical reason and no suitable explicit wait can be used.

 ## 7\. TestNG

 Use TestNG for test execution.

 Each test case should contain an appropriate:

```
@Test
```

 method.

 Use `@BeforeMethod`, `@AfterMethod`, `@BeforeClass`, or `@AfterClass` only where required by the existing project structure.

 Ensure the browser is properly initialized and closed.

 ## 8\. Maven

 The project must be executable using Maven.

 Verify the existing `pom.xml`.

 Run the appropriate Maven test command, for example:

```
mvn test
```

 Do not change the existing Maven configuration unnecessarily.

 If dependencies are missing, add only the dependencies required for Java, Selenium, TestNG, and the existing project structure.

 ## 9\. TestCase\_Suits.xls

 Use:

```
TestCase_Suits.xls
```

 as the source for the tests to automate.

 Do not modify the original `TestCase_Suits.xls`.

 Read the test cases and their:

 - TestCase\_id
- Testcase\_description
- Test\_data
- Test\_Step
- Expected\_Result

 Use these values to implement the automation.

 ## 10\. Execute the Tests

 After implementing all applicable test cases:

 1. Build the Maven project.
2. Execute the TestNG tests.
3. Capture the actual result of each test.
4. Determine whether each test passed or failed based on the implemented assertions.
5. Capture relevant failure information.
6. Fix automation issues caused by the implementation.
7. Re-run the tests after fixing them.
8. Do not change the expected behavior simply to make a test pass.

 ## 11\. Generate Executed\_Suits.xls

 After test execution is complete, create:

```
Executed_Suits.xls
```

 Use `TestCase_Suits.xls` as the template.

 Preserve:

 - All existing test cases
- All existing columns
- Column order
- TestCase IDs
- Test descriptions
- Test data
- Test steps
- Expected results

 Fill the previously blank execution columns with the actual execution information:

```
Actual_Result
Status
Remark/evidence
Execution_Date
tester
```

 ### Status

 Use:

```
Pass
Fail
```

 based on the actual automated test result.

 Do not mark a test as `Pass` unless the expected result was successfully verified by an assertion.

 ### Actual\_Result

 Record what actually happened during execution.

 Example:

```
User was successfully logged in and redirected to the dashboard.
```

 For a failure:

```
Login failed because the dashboard element was not displayed.
```

 ### Remark/evidence

 Include useful evidence such as:

 - Assertion failure
- Error message
- Screenshot path
- Relevant execution information

 Example:

```
Passed. Dashboard displayed successfully.
```

 or:

```
Failed. Assertion error: Dashboard element was not displayed. Screenshot: screenshots/TC_001.png
```

 ### Execution\_Date

 Record the actual execution date.

 ### tester

 If a tester name is configured in the project, use it.

 If no tester name is available, leave this field blank.

 ## 12\. Excel Formatting

 `Executed_Suits.xls` must be readable and maintain the same structure as `TestCase_Suits.xls`.

 Preserve the original columns and data.

 Do not remove or rename columns.

 ## 13\. Final Verification

 Before completing the task, verify:

 - `Flow.md` was read.
- `TestCase_Suits.xls` was read.
- Every applicable test case was automated.
- Every test class name exactly matches its `TestCase_id`.
- Test methods use meaningful feature/scenario names.
- Selenium is used for browser automation.
- TestNG is used for test execution.
- Maven is used to build/execute the project.
- Assertions validate the expected results.
- Tests were actually executed.
- `Executed_Suits.xls` was generated.
- Execution fields contain accurate results.
- Original `TestCase_Suits.xls` was not modified.

 ## 14\. Open the Final Excel File

 Once execution is complete and `Executed_Suits.xls` has been generated successfully:

 **Open `Executed_Suits.xls` using the available system/application mechanism.**

 Do not open the original `TestCase_Suits.xls`.

 The final file to open must be:

```
Executed_Suits.xls
```

 ## 15\. Final Response

 After completing everything, provide a concise summary:

```
Automation Framework: Java + Selenium + TestNG + Maven

Test Cases:
Total: <count>
Executed: <count>
Passed: <count>
Failed: <count>

Input:
TestCase_Suits.xls

Output:
Executed_Suits.xls

Execution:
Completed

Final Excel:
Yes. Add **individual test case execution scope**.

### Updated requirement

1. Create `test_execution_result.xls` **before execution**.
2. Select **individual test case(s)** to execute — not necessarily the full suite.
3. Execute only the selected test case(s).
4. After **each test case**, immediately update and save:

   * Actual Result
   * Status — Pass/Fail
   * Remark/Evidence
   * Execution Date
   * Tester
5. Previous results must remain saved.
6. Support both:

   * **Single test case:** `TC_001`
   * **Multiple selected cases:** `TC_001, TC_005, TC_010`
   * **Full suite:** execute all cases
7. Original `TestCase_Suits.xls` must remain unchanged.
8. Final report: **`test_execution_result.xls`**.


 If any test case could not be automated or executed, clearly state the TestCase ID and reason.

Once done open the  Executed_Suits.xls
 