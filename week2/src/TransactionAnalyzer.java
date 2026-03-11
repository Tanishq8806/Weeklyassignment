import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time;

    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class TransactionAnalyzer {

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two-Sum
    public void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        System.out.println("Two-Sum Results:");

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction prev = map.get(complement);

                System.out.println("(" + prev.id + ", " + t.id + ") → "
                        + prev.amount + " + " + t.amount);
            }

            map.put(t.amount, t);
        }
    }

    // Two-Sum with 1 hour window
    public void findTwoSumTimeWindow(int target, long windowMillis) {

        System.out.println("\nTwo-Sum within Time Window:");

        for (int i = 0; i < transactions.size(); i++) {

            for (int j = i + 1; j < transactions.size(); j++) {

                Transaction t1 = transactions.get(i);
                Transaction t2 = transactions.get(j);

                if (Math.abs(t1.time - t2.time) <= windowMillis
                        && t1.amount + t2.amount == target) {

                    System.out.println("(" + t1.id + ", " + t2.id + ")");
                }
            }
        }
    }

    // Duplicate detection
    public void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());

            map.get(key).add(t);
        }

        System.out.println("\nDuplicate Transactions:");

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.print(key + " → Accounts: ");

                for (Transaction t : list) {
                    System.out.print(t.account + " ");
                }

                System.out.println();
            }
        }
    }

    // K-Sum (recursive)
    public void findKSum(int k, int target) {

        System.out.println("\nK-Sum Results:");

        kSumHelper(0, k, target, new ArrayList<>());
    }

    private void kSumHelper(int index, int k, int target, List<Transaction> path) {

        if (k == 0 && target == 0) {

            System.out.print("Match: ");

            for (Transaction t : path) {
                System.out.print(t.id + " ");
            }

            System.out.println();

            return;
        }

        if (k == 0 || index >= transactions.size()) {
            return;
        }

        Transaction t = transactions.get(index);

        path.add(t);

        kSumHelper(index + 1, k - 1, target - t.amount, path);

        path.remove(path.size() - 1);

        kSumHelper(index + 1, k, target, path);
    }

    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        analyzer.addTransaction(new Transaction(1, 500, "Store A", "acc1", 1000));
        analyzer.addTransaction(new Transaction(2, 300, "Store B", "acc2", 1100));
        analyzer.addTransaction(new Transaction(3, 200, "Store C", "acc3", 1200));
        analyzer.addTransaction(new Transaction(4, 500, "Store A", "acc4", 1300));

        analyzer.findTwoSum(500);

        analyzer.findTwoSumTimeWindow(500, 3600000); // 1 hour

        analyzer.detectDuplicates();

        analyzer.findKSum(3, 1000);
    }
}