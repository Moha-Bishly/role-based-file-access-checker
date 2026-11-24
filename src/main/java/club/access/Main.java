package club.access;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("DENY_INVALID - expected role, action, request time and expiry time");
            return;
        }
        System.out.println(new AccessChecker().check(args[0], args[1], args[2], args[3]));
    }
}
