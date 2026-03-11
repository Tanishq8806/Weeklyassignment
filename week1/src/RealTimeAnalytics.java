import java.util.*;

public class RealTimeAnalytics {

    // page -> total visits
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // page -> unique users
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    private HashMap<String, Integer> sourceCount = new HashMap<>();

    // process incoming page view event
    public void processEvent(String url, String userId, String source) {

        // update page views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // update unique visitors
        uniqueVisitors.putIfAbsent(url, new HashSet<>());
        uniqueVisitors.get(url).add(userId);

        // update traffic source
        sourceCount.put(source, sourceCount.getOrDefault(source, 0) + 1);
    }

    // get top 10 pages
    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<Map.Entry<String, Integer>> topPages = new ArrayList<>();

        int count = 0;
        while (!pq.isEmpty() && count < 10) {
            topPages.add(pq.poll());
            count++;
        }

        return topPages;
    }

    // display dashboard
    public void getDashboard() {

        System.out.println("\n=== REAL-TIME ANALYTICS DASHBOARD ===");

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;
        for (Map.Entry<String, Integer> entry : topPages) {

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(
                    rank + ". " + page +
                            " - " + views + " views (" +
                            unique + " unique visitors)"
            );

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        for (Map.Entry<String, Integer> entry : sourceCount.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        // simulate events
        analytics.processEvent("/article/breaking-news", "user_123", "google");
        analytics.processEvent("/article/breaking-news", "user_456", "facebook");
        analytics.processEvent("/sports/championship", "user_789", "google");
        analytics.processEvent("/sports/championship", "user_999", "direct");
        analytics.processEvent("/sports/championship", "user_123", "google");
        analytics.processEvent("/article/breaking-news", "user_888", "direct");

        // dashboard update every 5 seconds
        for (int i = 0; i < 3; i++) {

            analytics.getDashboard();

            Thread.sleep(5000);
        }
    }
}