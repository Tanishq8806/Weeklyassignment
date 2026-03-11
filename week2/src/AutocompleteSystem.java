import java.util.*;

public class AutocompleteSystem {

    // Trie Node
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;
    }

    private TrieNode root = new TrieNode();

    // query -> frequency
    private HashMap<String, Integer> frequencyMap = new HashMap<>();

    // insert query into trie
    public void insert(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());

            node = node.children.get(c);
        }

        node.isEnd = true;
    }

    // update frequency when query searched
    public void updateFrequency(String query) {

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);

        insert(query);
    }

    // collect queries from trie
    private void collectQueries(TrieNode node, String prefix, List<String> results) {

        if (node.isEnd) {
            results.add(prefix);
        }

        for (char c : node.children.keySet()) {

            collectQueries(node.children.get(c), prefix + c, results);
        }
    }

    // get suggestions
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }

            node = node.children.get(c);
        }

        List<String> queries = new ArrayList<>();

        collectQueries(node, prefix, queries);

        // top 10 by frequency
        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) ->
                        frequencyMap.get(a) - frequencyMap.get(b));

        for (String q : queries) {

            pq.offer(q);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<String> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }

        Collections.reverse(result);

        return result;
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        // sample search history
        system.updateFrequency("java tutorial");
        system.updateFrequency("javascript");
        system.updateFrequency("java download");
        system.updateFrequency("java tutorial");
        system.updateFrequency("java 21 features");
        system.updateFrequency("java tutorial");

        System.out.println("Search suggestions for 'jav':");

        List<String> suggestions = system.search("jav");

        int rank = 1;

        for (String s : suggestions) {

            System.out.println(rank + ". " + s +
                    " (" + system.frequencyMap.get(s) + " searches)");

            rank++;
        }
    }
}