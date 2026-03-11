import java.util.*;

public class RateLimiterSystem {

    // TokenBucket class
    static class TokenBucket {

        int tokens;
        int maxTokens;
        double refillRate; // tokens per second
        long lastRefillTime;

        public TokenBucket(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.refillRate = refillRate;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // refill tokens based on elapsed time
        private void refill() {

            long now = System.currentTimeMillis();
            double seconds = (now - lastRefillTime) / 1000.0;

            int tokensToAdd = (int) (seconds * refillRate);

            if (tokensToAdd > 0) {
                tokens = Math.min(maxTokens, tokens + tokensToAdd);
                lastRefillTime = now;
            }
        }

        // check request
        public synchronized boolean allowRequest() {

            refill();

            if (tokens > 0) {
                tokens--;
                return true;
            }

            return false;
        }

        public int getRemainingTokens() {
            return tokens;
        }
    }

    // clientId -> TokenBucket
    private HashMap<String, TokenBucket> clients = new HashMap<>();

    private int MAX_REQUESTS = 1000;
    private double REFILL_RATE = 1000.0 / 3600.0; // per second

    // check rate limit
    public String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId, new TokenBucket(MAX_REQUESTS, REFILL_RATE));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {

            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        }
        else {

            return "Denied (0 requests remaining, retry later)";
        }
    }

    // get rate limit status
    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            System.out.println("Client not found");
            return;
        }

        int used = bucket.maxTokens - bucket.tokens;

        System.out.println("{used: " + used +
                ", limit: " + bucket.maxTokens +
                ", remaining: " + bucket.tokens + "}");
    }

    public static void main(String[] args) {

        RateLimiterSystem limiter = new RateLimiterSystem();

        String client = "abc123";

        // simulate requests
        for (int i = 0; i < 5; i++) {
            System.out.println(limiter.checkRateLimit(client));
        }

        limiter.getRateLimitStatus(client);
    }
}