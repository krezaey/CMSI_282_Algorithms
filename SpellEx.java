package spellex;

import java.util.*;

public class SpellEx {
    
    // Note: Not quite as space-conscious as a Bloom Filter,
    // nor a Trie, but since those aren't in the JCF, this map 
    // will get the job done for simplicity of the assignment
    private Map<String, Integer> dict;
    
    // For your convenience, you might need this array of the
    // alphabet's letters for a method
    private static final char[] LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Constructs a new SpellEx spelling corrector from a given
     * "dictionary" of words mapped to their frequencies found
     * in some corpus (with the higher counts being the more
     * prevalent, and thus, the more likely to be suggested)
     * @param words The map of words to their frequencies
     */
    SpellEx(Map<String, Integer> words) {
        dict = new HashMap<>(words);
    }
        
    /**
     * Makes a table for the memoization structure with gutters for base cases.
     * @param rows
     * @param cols
     * @return int[][]
     */
    private static int[][] makeTable(int rows, int cols) {
        int[][] table = new int[rows][cols];
                        
        for (int i = 0; i < rows; i++) {
            table[i][0] = i;
        }
        for (int j = 0; j < cols; j++) {
            table[0][j] = j;
        }
        return table;
    }
    
    /**
     * Returns minimum of 3 integers
     * @param a
     * @param b
     * @param c
     * @return int min
     */
    private static int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }
    
    /**
     * Returns the edit distance between the two input Strings
     * s0 and s1 based on the minimal number of insertions, deletions,
     * replacements, and transpositions required to transform s0
     * into s1
     * @param s0 A "start" String
     * @param s1 A "destination" String
     * @return The minimal edit distance between s0 and s1
     */
    public static int editDistance (String s0, String s1) {
        String colString = s0;
        String rowString = s1;
        int cols = colString.length() + 1;
        int rows = rowString.length() + 1; 
        int[][] table = makeTable(rows, cols);
       
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                if (rowString.substring(0, r).contentEquals(colString.substring(0, c))) { table[r][c] = 0; }
                else {
                    if (r >= 1) { table[r][c] = table[r][c-1] + 1; }
                    if (c >= 1) { table[r][c] = table[r-1][c] + 1; }
                    if (r >= 1 && c >= 1) {
                        table[r][c] = (colString.charAt(c-1) != rowString.charAt(r-1)) ? min(table[r-1][c] + 1, table[r][c-1] + 1, table[r-1][c-1] + 1) :
                                                                                         min(table[r-1][c] + 1, table[r][c-1] + 1, table[r-1][c-1]);
                    }
                    if ((r >= 2 && c >= 2) && (colString.charAt(c-2) == rowString.charAt(r-1)) && (colString.charAt(c-1) == rowString.charAt(r-2))) {
                        table[r][c] = (colString.charAt(c-1) != rowString.charAt(r-1)) ? min(table[r-1][c] + 1, table[r][c-1] + 1, Math.min(table[r-1][c-1] + 1, table[r-2][c-2] + 1)) :
                                                                                         min(table[r-1][c] + 1, table[r][c-1] + 1, Math.min(table[r-1][c-1], table[r-2][c-2] + 1));
                    }
                }
            }
        }  
        return table[rows-1][cols-1];
    }
        
    /**
     * Returns the n closest words in the dictionary to the given word,
     * where "closest" is defined by:
     * <ul>
     *   <li>Minimal edit distance (with ties broken by:)
     *   <li>Largest count / frequency in the dictionary (with ties broken by:)
     *   <li>Ascending alphabetic order
     * </ul>
     * @param word The word we are comparing against the closest in the dictionary
     * @param n The number of least-distant suggestions desired
     * @return A set of up to n suggestions closest to the given word
     */
    public Set<String> getNLeastDistant (String word, int n) {
        HashSet<String> results = new HashSet<String>(n);
        PriorityQueue<CandidateWord> pq = new PriorityQueue<CandidateWord>(); 
        
        for (Map.Entry<String, Integer> candidate: dict.entrySet()) {
            CandidateWord cw = new CandidateWord(candidate.getKey(), word, true);
            pq.add(cw);
        }
        for (int i = 0; i < n; i++) {
            results.add(pq.poll().word);
        }
        return results;
    }
    
    /**
     * Returns a combinatoric candidate set of words based on the insertion action.
     * @param word
     * @return Set<String>
     */
    private Set<String> insertion(String word) {
        HashSet<String> results = new HashSet<String>();
        String beg;
        String end;
        String finalWord;
        
        for (int stringIndex = 0; stringIndex <= word.length(); stringIndex++) {
            for (char letter : LETTERS) {
                beg = word.substring(0, stringIndex);
                end = word.substring(stringIndex);
                finalWord = beg + letter + end;
                results.add(finalWord);
            }
        }
        return results;
    }
    
    /**
     * Returns a combinatoric set of words based on the deletion action.
     * @param word
     * @return Set<String>
     */
    private Set<String> deletion(String word) {
        HashSet<String> results = new HashSet<String>();
        String beg;
        String end;
        String finalWord;
        
        for (int stringIndex = 0; stringIndex < word.length(); stringIndex++) {
            if (stringIndex == 0) {
                beg = "";
                end = word.substring(stringIndex+1);
            }
            else if (stringIndex == word.length()-1) {
                beg = word.substring(0, stringIndex);
                end = "";
            }
            else {
                beg = word.substring(0, stringIndex);
                end = word.substring(stringIndex + 1);
            }
            finalWord = beg + end;
            results.add(finalWord);
        }
        return results;
    }
    
    /**
     * Returns a combinatoric set of words based on the replacement action.
     * @param word
     * @return Set<String>
     */
    private Set<String> replacement(String word) {
        HashSet<String> results = new HashSet<String>();
        String beg;
        String end;
        String finalWord;
        
        for (int stringIndex = 0; stringIndex < word.length(); stringIndex++) {
            for (char letter : LETTERS) {
                if (stringIndex == 0) {
                    beg = "";
                    end = word.substring(stringIndex+1);
                }
                else if (stringIndex == word.length()-1) {
                    beg = word.substring(0, stringIndex);
                    end = "";
                }
                else {
                    beg = word.substring(0, stringIndex);
                    end = word.substring(stringIndex + 1);
                }
                finalWord = beg + letter + end;
                results.add(finalWord);
            }
        }
        return results;
    }
    
    /**
     * Returns a combinatoric set of words based on the transposition action.
     * @param word
     * @return Set<String>
     */
    private Set<String> transposition(String word) {
        HashSet<String> results = new HashSet<String>();
        
        for (int stringIndex = 0; stringIndex+1 < word.length(); stringIndex++) {
            char[] charWord = word.toCharArray();
            char temp = charWord[stringIndex];
            charWord[stringIndex] = charWord[stringIndex+1];
            charWord[stringIndex+1] = temp;
            results.add(charWord.toString());
        }
        return results;
    }
    
    /**
     * Generates a comprehensive combinatoric set of words based on all possible edit actions.
     * @param word
     * @return Set<String>
     */
    private Set<String> generateWords(String word) {
        HashSet<String> results = new HashSet<String>();
        results.addAll(insertion(word));
        results.addAll(deletion(word));
        results.addAll(replacement(word));
        results.addAll(transposition(word));
        return results;
    }
    
    /**
     * Returns the set of n most frequent words in the dictionary to occur with
     * edit distance distMax or less compared to the given word. Ties in
     * max frequency are broken with ascending alphabetic order.
     * @param word The word to compare to those in the dictionary
     * @param n The number of suggested words to return
     * @param distMax The maximum edit distance (inclusive) that suggested / returned 
     * words from the dictionary can stray from the given word
     * @return The set of n suggested words from the dictionary with edit distance
     * distMax or less that have the highest frequency.
     */
    public Set<String> getNBestUnderDistance (String word, int n, int distMax) {
        HashSet<String> initialGen = new HashSet<String>();
        HashSet<String> nextGen = new HashSet<String>();
        HashSet<String> results = new HashSet<String>();
        PriorityQueue<CandidateWord> pq = new PriorityQueue<CandidateWord>();
        
        initialGen.addAll(generateWords(word));
        for (int i = 1; i < distMax; i++) {
            for (String oldWord : initialGen) { nextGen.addAll(generateWords(oldWord)); }
            initialGen.addAll(nextGen);
        }
        for (String oldWord : initialGen) {
            if (dict.containsKey(oldWord)) { 
                CandidateWord cw = new CandidateWord(oldWord, word, false);
                pq.add(cw);
            }
        }
        for (int i = 0; i < n; i++) {
            if(!pq.isEmpty()) { results.add(pq.poll().word); }
        }
        return results;
    }
    
    /**
     * Private class to store potential word's frequency, edit distance.
     * Implements Comparable<CandidateWord> for priority queue's usage.
     */
    private class CandidateWord implements Comparable<CandidateWord> {
        private String word;
        private String targetWord;
        private int frequency; 
        private int editDistance;
        
        /**
         * Constructor
         * Creates candidate that computes edit distance from inputed
         * target word and takes frequency from provided corpus. 
         * @param word
         * @param targetWord
         */
        CandidateWord(String word, String targetWord, boolean useEditDistance) {
            this.word = word;
            this.targetWord = targetWord;
            this.frequency = dict.get(word);
            this.editDistance = useEditDistance ? editDistance(this.targetWord, this.word) : 0;
        }

        /**
         * Also for comparison between CandidateWord objects.
         * @param CandidateWord other
         * @return int -1 if this < other, 1 if this > other where less than is preceding
         */
        @Override
        public int compareTo(CandidateWord other) {
            if (this.editDistance < other.editDistance) {
                return -1;
            }
            else if (this.editDistance == other.editDistance) {
                if (this.frequency > other.frequency) {
                    return -1;
                }
                else if (this.frequency == other.frequency) {
                    if (this.word.compareTo(other.word) < 0) {
                        return -1;
                    }
                }
            }
            return 1;
        }  
    }
}