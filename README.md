# Role-Based File Access Checker

A Java 17 console application that checks a student club member's permission to read, edit, or delete a file. The project includes JUnit 5 tests and JaCoCo coverage reports.

## What it checks

This Java 17 console program models one student club file. A request contains a previously assigned role, an action, a request timestamp and a permission expiry timestamp. It computes an authorization decision. It does not authenticate a user or change a file.

| Role | READ | EDIT | DELETE |
|---|---|---|---|
| Viewer | Allow | Deny | Deny |
| Editor | Allow | Allow | Deny |
| Owner | Allow | Allow | Allow |

All inputs are required. Roles and actions are case-sensitive. Supply local timestamps, in the same timezone for both values, in the form 2026-11-20T14:59. Validation rejects unknown tokens, impossible dates and malformed times. The simulated grant is active strictly before expiry. There is no start time in this model.

## Build and run

Install JDK 17 and Maven 3.9 or use a machine with both available. Run these commands from the directory containing pom.xml. The first Maven build needs internet access to fetch dependencies.

```text
java -version
mvn -version
mvn clean verify
java -cp target/classes club.access.Main Editor EDIT 2026-11-20T14:59 2026-11-20T15:00
java -cp target/classes club.access.Main Editor EDIT 2026-11-20T15:00 2026-11-20T15:00
```

The two requests print ALLOW and DENY_EXPIRED, respectively. Maven compiles the code, runs JUnit and creates the coverage report at target/site/jacoco/index.html. Surefire writes test output to target/surefire-reports. For the recorded run, the tools were Amazon Corretto 17.0.21, Maven 3.9.9, JUnit Jupiter 5.12.2 and JaCoCo 0.8.13.

## How the code works

1. Main.main checks for four arguments. Wrong argument counts print DENY_INVALID and the expected arguments. For four arguments, it prints the decision returned by AccessChecker.check.
2. The checker validates the role and action against fixed sets. It parses dates using a strict minute-precision Java formatter. A missing timestamp or parsing error produces DENY_INVALID.
3. The checker returns DENY_EXPIRED whenever the request falls at or beyond expiry. This happens before looking up role permissions. Even a forbidden action receives the expired reason once a valid grant has expired.
4. The role rule produces ALLOW for a permitted operation and DENY_ROLE otherwise. The default arm represents Viewer because input validation has already accepted exactly three roles.
5. AccessCheckerTest reads the expected outcomes in src/test/resources/cases.csv. Each row becomes a separately reported JUnit invocation. MainTest exercises the same entry point used by a person running the application. Its capture of console output is restored after each test.
6. Cases D01-D09 test the role matrix. B01-B04 check times on either side of the cutoff, the cutoff itself, and a date change. P01-P02 test precedence when two denial reasons could apply. N01-N12 exercise invalid input classes. C01-C02 check the command line. B01 intentionally repeats D05 to make the three-value boundary set legible.
7. evidence/final-cases.csv records all 29 inputs, expected and observed outcomes, and verdicts. The literal <null> denotes a Java null input. Two single quotes in a data field denote an empty string. For the CLI cases, <omitted> means the argument was absent.
8. Open evidence/coverage/index.html in a browser. Click the package and class to inspect individual lines. The file evidence/coverage/jacoco.xml contains aggregate counters for independent verification.

## What the runs show

| Run | Implementation and suite | Outcome |
|---|---|---|
| Baseline | Active-grant policy only, D01-D09 | 9 passed |
| Fault experiment | Deliberately changed the cutoff to allow equality, full suite | 27 passed, B02 and P01 failed |
| Final regression | Corrected the cutoff and reran the entire suite | 29 passed |
| Coverage comparison | Faulty cutoff, B02 and P01 removed, all remaining tests rerun | 27 passed, all 23 measured branches covered |

The logs in evidence are extracts of real Maven output. The raw trial coverage counters are in coverage-comparison.csv. Both failures in the full fault experiment have the same cause. The editor got an unauthorized allowance at expiry. The viewer still got a denial but was given the wrong reason. A test that checked only a boolean would miss the second symptom.

## Reproducing the experiments

Use a separate copy so the reference implementation stays available.

1. To reproduce the seeded fault, replace `!request.isBefore(expiry)` with `request.isAfter(expiry)` in AccessChecker.java. Run `mvn clean test`. Confirm B02 and P01 fail. The build is expected to report failure during this experiment.
2. In that faulty copy only, remove the two rows beginning B02 and P01 from cases.csv. Run `mvn clean verify`. The remaining 27 tests pass. Inspect the branch and line counters in the coverage report.
3. Restore both CSV rows and the strict-before guard from the reference copy, then run `mvn clean verify`. All 29 tests should pass.
4. To recreate the earlier role-only baseline, use a fresh copy, omit the expiry comparison block, and keep only the CSV header and D01-D09. Run `mvn -Dtest=AccessCheckerTest clean test`. This run exercises only valid, active grants.

AI assistance was used for coding and report writing.
