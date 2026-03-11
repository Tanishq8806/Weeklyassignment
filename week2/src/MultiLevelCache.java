import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String id, String content) {
        this.videoId = id;
        this.content = content;
    }
}

public class MultiLevelCache {

    // L1 Cache (Memory) with LRU
    private LinkedHashMap<String, VideoData> L1;

    // L2 Cache (SSD simulation)
    private LinkedHashMap<String, VideoData> L2;

    // L3 Database
    private HashMap<String, VideoData> database;

    // Access count for promotion
    private HashMap<String, Integer> accessCount;

    // Statistics
    private int l1Hits = 0;
    private int l2Hits = 0;
    private int l3Hits = 0;

    public MultiLevelCache() {

        L1 = new LinkedHashMap<String, VideoData>(10000, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> e) {
                return size() > 10000;
            }
        };

        L2 = new LinkedHashMap<String, VideoData>(100000, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> e) {
                return size() > 100000;
            }
        };

        database = new HashMap<>();
        accessCount = new HashMap<>();

        // sample database videos
        database.put("video_123", new VideoData("video_123", "Movie A"));
        database.put("video_999", new VideoData("video_999", "Movie B"));
        database.put("video_555", new VideoData("video_555", "Movie C"));
    }

    public VideoData getVideo(String videoId) {

        long start = System.nanoTime();

        // L1 lookup
        if (L1.containsKey(videoId)) {

            l1Hits++;

            System.out.println("L1 Cache HIT (0.5ms)");

            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        // L2 lookup
        if (L2.containsKey(videoId)) {

            l2Hits++;

            System.out.println("L2 Cache HIT (5ms)");

            VideoData video = L2.get(videoId);

            // promote to L1
            L1.put(videoId, video);

            System.out.println("Promoted to L1");

            return video;
        }

        System.out.println("L2 Cache MISS");

        // L3 database
        if (database.containsKey(videoId)) {

            l3Hits++;

            System.out.println("L3 Database HIT (150ms)");

            VideoData video = database.get(videoId);

            L2.put(videoId, video);

            accessCount.put(videoId,
                    accessCount.getOrDefault(videoId, 0) + 1);

            System.out.println("Added to L2");

            return video;
        }

        System.out.println("Video not found");

        long end = System.nanoTime();

        System.out.println("Total Time: " + (end - start) / 1000000.0 + "ms");

        return null;
    }

    public void getStatistics() {

        int total = l1Hits + l2Hits + l3Hits;

        System.out.println("\nCache Statistics:");

        if (total == 0) return;

        System.out.println("L1 Hit Rate: " + (l1Hits * 100.0 / total) + "%");
        System.out.println("L2 Hit Rate: " + (l2Hits * 100.0 / total) + "%");
        System.out.println("L3 Hit Rate: " + (l3Hits * 100.0 / total) + "%");

        System.out.println("Overall Requests: " + total);
    }

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.getVideo("video_123");
        System.out.println();

        cache.getVideo("video_123");
        System.out.println();

        cache.getVideo("video_999");
        System.out.println();

        cache.getStatistics();
    }
}