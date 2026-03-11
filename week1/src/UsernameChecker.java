import java.util.*;

public class UsernameChecker {

    // username -> userId
    private HashMap<String, Integer> users = new HashMap<>();

    // username -> attempt count
    private HashMap<String, Integer> attempts = new HashMap<>();

    // constructor with some existing users
    public UsernameChecker() {
        users.put("john_doe", 1);
        users.put("admin", 2);
        users.put("alex", 3);
        users.put("nik", 4);
    }

    // check username availability
    public boolean checkAvailability(String username) {

        // increase attempt count
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);

        return !users.containsKey(username);
    }

    // suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        suggestions.add(username + "1");
        suggestions.add(username + "2");
        suggestions.add(username.replace("_", "."));
        suggestions.add(username + "123");

        return suggestions;
    }

    // find most attempted username
    public String getMostAttempted() {

        String maxUser = "";
        int maxCount = 0;

        for (String user : attempts.keySet()) {
            int count = attempts.get(user);

            if (count > maxCount) {
                maxCount = count;
                maxUser = user;
            }
        }

        return maxUser + " (" + maxCount + " attempts)";
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UsernameChecker checker = new UsernameChecker();

        System.out.print("Enter username to check: ");
        String username = sc.nextLine();

        boolean available = checker.checkAvailability(username);

        if (available) {
            System.out.println(username + " → Available");
        } else {
            System.out.println(username + " → Already taken");

            System.out.println("Suggested usernames:");
            List<String> suggestions = checker.suggestAlternatives(username);

            for (String s : suggestions) {
                System.out.println(s);
            }
        }

        System.out.println("\nMost attempted username:");
        System.out.println(checker.getMostAttempted());
    }
}