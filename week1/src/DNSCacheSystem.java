import java.util.*;

public class DNSCacheSystem {

    // Entry class
    static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, int ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // LRU Cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache;
    private int capacity;

    // statistics
    private int hits = 0;
    private int misses = 0;

    public DNSCacheSystem(int capacity) {

        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCacheSystem.this.capacity;
            }
        };
    }

    // simulate upstream DNS lookup
    private String queryUpstreamDNS(String domain) {

        // fake IP generator
        Random r = new Random();
        return "172.217.14." + (r.nextInt(200) + 1);
    }

    // resolve domain
    public synchronized String resolve(String domain) {

        long start = System.nanoTime();

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                long end = System.nanoTime();

                System.out.println("Cache HIT → " + entry.ipAddress +
                        " (retrieved in " + (end - start) / 1000000.0 + " ms)");

                return entry.ipAddress;
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED");
            }
        }

        misses++;

        System.out.println("Cache MISS → Querying upstream DNS...");

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, ip, 5)); // TTL = 5 seconds

        System.out.println(domain + " → " + ip + " (TTL: 5s)");

        return ip;
    }

    // remove expired entries
    public void cleanExpiredEntries() {

        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, DNSEntry> e = it.next();

            if (e.getValue().isExpired()) {
                it.remove();
            }
        }
    }

    // cache statistics
    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;

        System.out.println("\nCache Statistics:");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws InterruptedException {

        DNSCacheSystem dns = new DNSCacheSystem(5);

        dns.resolve("google.com");
        dns.resolve("google.com");

        Thread.sleep(6000); // wait for TTL expiry

        dns.resolve("google.com");

        dns.getCacheStats();
    }
}
