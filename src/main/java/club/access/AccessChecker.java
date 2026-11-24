package club.access;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Set;

public final class AccessChecker {
    private static final Set<String> ROLES = Set.of("Viewer", "Editor", "Owner");
    private static final Set<String> ACTIONS = Set.of("READ", "EDIT", "DELETE");
    private static final DateTimeFormatter MINUTES = DateTimeFormatter
            .ofPattern("uuuu-MM-dd'T'HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    public enum Decision {
        ALLOW, DENY_INVALID, DENY_EXPIRED, DENY_ROLE
    }

    public Decision check(String role, String action, String requestedAt, String expiresAt) {
        if (role == null || !ROLES.contains(role) || action == null || !ACTIONS.contains(action)) {
            return Decision.DENY_INVALID;
        }
        if (requestedAt == null || expiresAt == null) {
            return Decision.DENY_INVALID;
        }
        try {
            LocalDateTime request = LocalDateTime.parse(requestedAt, MINUTES);
            LocalDateTime expiry = LocalDateTime.parse(expiresAt, MINUTES);
            if (!request.isBefore(expiry)) {
                return Decision.DENY_EXPIRED;
            }
        } catch (DateTimeParseException invalidTime) {
            return Decision.DENY_INVALID;
        }
        boolean permitted = switch (role) {
            case "Owner" -> true;
            case "Editor" -> !action.equals("DELETE");
            default -> action.equals("READ"); // The only other validated role is Viewer.
        };
        return permitted ? Decision.ALLOW : Decision.DENY_ROLE;
    }
}
