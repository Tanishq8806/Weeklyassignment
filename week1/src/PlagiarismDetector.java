import java.util.*;

public class PlagiarismDetector {

    // n-gram -> set of document IDs
    private HashMap<String, Set<String>> index = new HashMap<>();

    // documentId -> list of ngrams
    private HashMap<String, List<String>> documentNgrams = new HashMap<>();

    private int N = 5; // 5-gram

    // generate ngrams
    private List<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");

        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    // add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            index.putIfAbsent(gram, new HashSet<>());

            index.get(gram).add(docId);
        }
    }

    // analyze document for plagiarism
    public void analyzeDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (index.containsKey(gram)) {

                for (String existingDoc : index.get(gram)) {

                    matchCount.put(existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        for (String doc : matchCount.keySet()) {

            int matches = matchCount.get(doc);

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println(
                    "Found " + matches +
                            " matching n-grams with \"" + doc + "\"");

            System.out.println(
                    "Similarity: " +
                            String.format("%.2f", similarity) + "%");

            if (similarity > 50) {
                System.out.println("⚠ PLAGIARISM DETECTED\n");
            } else if (similarity > 10) {
                System.out.println("⚠ Suspicious similarity\n");
            }
        }
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        // existing essays
        detector.addDocument(
                "essay_089.txt",
                "machine learning is a field of artificial intelligence that allows systems to learn from data"
        );

        detector.addDocument(
                "essay_092.txt",
                "machine learning is a field of artificial intelligence that allows systems to learn from data and improve performance automatically"
        );

        // new essay
        String newEssay =
                "machine learning is a field of artificial intelligence that allows systems to learn from data and improve performance automatically in many applications";

        detector.analyzeDocument("essay_123.txt", newEssay);
    }
}
