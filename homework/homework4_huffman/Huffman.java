package huffman;

import java.util.*;
import java.io.ByteArrayOutputStream;

/**
 * Huffman instances provide reusable Huffman Encoding Maps for compressing and
 * decompressing text corpi with comparable distributions of characters.
 */
public class Huffman {

    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    private TreeMap<Character, String> encodingMap;
    private Queue<HuffNode> pq;

    /**
     * Creates the Huffman Trie and Encoding Map using the character distributions
     * in the given text corpus
     * 
     * @param corpus A String representing a message / document corpus with
     *               distributions over characters that are implicitly used
     *               throughout the methods that follow. Note: this corpus ONLY
     *               establishes the Encoding Map; later compressed corpi may
     *               differ.
     */
    Huffman(String corpus) {
        this.pq = createQueue(corpus);
        this.trieRoot = createTree(pq);
        this.encodingMap = new TreeMap<Character, String>();
        createEncodingMap(this.trieRoot, "");
    }

    /**
     * Creates the priority queue based on the frequencies of characters in the
     * passed in corpus.
     * 
     * @param corpus
     * @return Queue<HuffNode>
     */
    private Queue<HuffNode> createQueue(String corpus) {
        HashMap<Character, Integer> freq = new HashMap<Character, Integer>();
        Queue<HuffNode> pq = new PriorityQueue<HuffNode>();

        for (char c : corpus.toCharArray()) {
            if (!freq.containsKey(c)) { freq.put(c, 1); } 
            else { freq.replace(c, freq.get(c) + 1); }
        }
        for (Map.Entry<Character, Integer> character : freq.entrySet()) {
            HuffNode current = new HuffNode(character.getKey(), character.getValue());
            pq.add(current);
        }
        return pq;
    }

    /**
     * Creates a tree structure using the priority queue that organizes the
     * characters based on frequencies.
     * 
     * @param pq
     * @return HuffNode (root that created from priority queue)
     */
    private HuffNode createTree(Queue<HuffNode> pq) {
        while (pq.size() > 1) {
            HuffNode first = pq.poll();
            HuffNode second = pq.poll();
            HuffNode parent = new HuffNode('\u0000', first.count + second.count);
            parent.right = second;
            parent.left = first;
            pq.add(parent);
        }
        return pq.poll();
    }

    /**
     * Creates the encoding map recursively by navigating the "tree" created from
     * the node left and right references.
     * 
     * @param start
     * @param map
     * @param compressed (binary code generated from traversing left and right)
     */
    private void createEncodingMap(HuffNode start, String compressed) {
        if (start.isLeaf()) {
            this.encodingMap.put(start.character, compressed);
            return;
        }
        createEncodingMap(start.left, compressed + "0");
        createEncodingMap(start.right, compressed + "1");
    }

    // -----------------------------------------------
    // Compression
    // -----------------------------------------------

    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap field
     * generated during construction for this purpose.
     * 
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the Huffman
     *         coded bytecode. Formatted as 3 components: (1) the first byte
     *         contains the number of characters in the message, (2) the bitstring
     *         containing the message itself, (3) possible 0-padding on the final
     *         byte.
     */
    public byte[] compress(String message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(128);
        String temp = "";
        byte addBit;

        stream.write(message.length());
        for (char c : message.toCharArray()) {
            temp += this.encodingMap.get(c);
            while (temp.length() >= 8) {
                addBit = addByte(temp, stream);
                temp = temp.substring(8);
            }
        }

        while (temp.length() % 8 != 0) { temp += "0"; }

        addBit = addByte(temp, stream);
        return stream.toByteArray();
    }

    /**
     * Creates a byte with designated string to be added, adds it to passed in
     * stream.
     * 
     * @param toAdd, stream
     * @return byte
     */
    private byte addByte(String toAdd, ByteArrayOutputStream stream) {
        byte result = (byte) Integer.parseInt(toAdd.substring(0, 8), 2);
        stream.write(result);
        return result;
    }

    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------

    /**
     * Decompresses the given compressed array of bytes into their original, String
     * representation. Uses the trieRoot field (the Huffman Trie) that generated the
     * compressed message during decoding.
     * 
     * @param compressedMsg {@code byte[]} representing the compressed corpus with
     *                      the Huffman coded bytecode. Formatted as 3 components:
     *                      (1) the first byte contains the number of characters in
     *                      the message, (2) the bitstring containing the message
     *                      itself, (3) possible 0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode
     *         message.
     */
    public String decompress(byte[] compressedMsg) {
        String result = "";
        String bitString = makeBinaryString(compressedMsg[1]);
        int length = compressedMsg[0];

        if (compressedMsg.length == 3) { bitString += makeBinaryString(compressedMsg[2]); }

        HuffNode current = this.trieRoot;

        for (int i = 0; i < bitString.length(); i++) {
            if (current.isLeaf()) {
                if (result.length() < length) { result += current.character;}
                current = this.trieRoot;
            }
            if (bitString.charAt(i) == '0') { current = current.left; } 
            else if (bitString.charAt(i) == '1') { current = current.right; }
            if (result.length() == length) { return result; }
        }
        return result;
    }

    /**
     * Converts the given byte to a string of bits.
     * 
     * @param b
     * @return String
     */
    private String makeBinaryString(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------

    /**
     * Huffman Trie Node class used in construction of the Huffman Trie. Each node
     * is a binary (having at most a left and right child), contains a character
     * field that it represents (in the case of a leaf, otherwise the null character
     * \0), and a count field that holds the number of times the node's character
     * (or those in its subtrees, in the case of inner nodes) appear in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {

        HuffNode left, right;
        char character;
        int count;

        HuffNode(char character, int count) {
            this.count = count;
            this.character = character;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        public int compareTo(HuffNode other) {
            return this.count - other.count;
        }
    }

}