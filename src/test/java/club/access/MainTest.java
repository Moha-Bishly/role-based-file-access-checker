package club.access;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {
    private void checkOutput(String id, String expected, String... args) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            Main.main(args);
        } finally {
            System.setOut(original);
        }
        String actual = bytes.toString(StandardCharsets.UTF_8).stripTrailing();
        System.out.println(id + "|" + actual);
        assertEquals(expected, actual, id);
    }

    @Test
    void printsDecisionForFourArguments() {
        checkOutput("C01", "ALLOW", "Editor", "EDIT", "2026-11-20T14:59", "2026-11-20T15:00");
    }

    @Test
    void explainsMissingArguments() {
        checkOutput("C02", "DENY_INVALID - expected role, action, request time and expiry time",
                    "Owner", "DELETE");
    }
}
