package club.access;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AccessCheckerTest {
    @ParameterizedTest(name = "{0}: {1} {2} expects {5}")
    @CsvFileSource(resources = "/cases.csv", numLinesToSkip = 1, nullValues = "<null>")
    void checksPolicy(String id, String role, String action, String request, String expiry,
                      AccessChecker.Decision expected) {
        AccessChecker.Decision actual = new AccessChecker().check(role, action, request, expiry);
        System.out.println(id + "|" + actual);
        assertEquals(expected, actual, id);
    }
}
