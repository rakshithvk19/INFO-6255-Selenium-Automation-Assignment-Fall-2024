# Northeastern University Web Applications Automation

## Project Description
This project contains a suite of automated tests for various Northeastern University web applications using Selenium WebDriver. It covers scenarios such as downloading transcripts, managing Canvas calendar events, interacting with the classroom dashboard, accessing the digital repository, and updating the academic calendar.

## Prerequisites
- Java JDK 11 or higher
- Maven
- Chrome browser (latest version)
- ChromeDriver (compatible with your Chrome version)

## Setup
1. Clone this repository:
   ```
   git clone
   ```
2. Navigate to the project directory:
```
cd northeastern-selenium-automation
```
3. Install dependencies:
```
mvn clean install
```

## Configuration
1. Update `config.properties` file with your Northeastern credentials (if applicable).
2. Ensure ChromeDriver path is set in your system PATH or update it in the code.

## Running Tests
To run all tests:
```
mvn test
```

To run a specific test class:
```
mvn test -Dtest=Scenario1Test
```

## Project Structure
- `src/main/java/com/northeastern/selenium/`: Contains utility classes and helpers
- `src/test/java/com/northeastern/selenium/tests/`: Contains test classes for each scenario
- `src/test/resources/`: Contains test data and configuration files

## Scenarios
1. **Scenario1Test**: Download the latest transcript
2. **Scenario2Test**: Add two To-Do tasks in Canvas calendar
3. **Scenario3Test**: Attempt to download a classroom guide (negative test)
4. **Scenario4Test**: Download a dataset from the digital repository
5. **Scenario5Test**: Update the Academic calendar

## Reporting
- HTML reports are generated after test execution in the `test-output` directory.
- Screenshots are saved in the `screenshots` directory, organized by scenario.

## Notes
- The project uses data-driven approach. Ensure all test data is updated in the respective Excel files.
- For two-factor authentication, one manual intervention is allowed during the test run.

## Troubleshooting
- If tests fail due to timing issues, adjust the wait times in the test classes.
- Ensure you have the latest ChromeDriver compatible with your Chrome browser version.
