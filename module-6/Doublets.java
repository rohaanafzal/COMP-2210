import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

import java.util.stream.Collectors;

/**
 * Provides an implementation of the WordLadderGame interface.
 *
 * @author Your Name (you@auburn.edu)
 */
public class Doublets implements WordLadderGame {

    /** Lexicon to store all valid words from dictionary */
    private TreeSet<String> lexicon = new TreeSet<String>();

    /**
     * Constructor: loads words from input stream into lexicon.
     */
    public Doublets(InputStream in) {
        try {
            Scanner s = new Scanner(new BufferedReader(new InputStreamReader(in)));
            while (s.hasNext()) {
                String str = s.next();
                lexicon.add(str.toLowerCase());
                s.nextLine(); // skip rest of line if any
            }
            in.close();
        } catch (java.io.IOException e) {
            System.err.println("Error reading from InputStream.");
            System.exit(1);
        }
    }

    @Override
    public int getWordCount() {
        return lexicon.size();
    }

    @Override
    public boolean isWord(String str) {
        return lexicon.contains(str.toLowerCase());
    }

    @Override
    public int getHammingDistance(String str1, String str2) {
        if (str1 == null || str2 == null || str1.length() != str2.length()) {
            return -1;
        }

        int distance = 0;
        for (int i = 0; i < str1.length(); i++) {
            if (Character.toLowerCase(str1.charAt(i)) != Character.toLowerCase(str2.charAt(i))) {
                distance++;
            }
        }
        return distance;
    }

    @Override
    public List<String> getNeighbors(String word) {
        List<String> neighbors = new ArrayList<String>();
        for (String current : lexicon) {
            if (getHammingDistance(word, current) == 1) {
                neighbors.add(current);
            }
        }
        return neighbors;
    }

    @Override
    public boolean isWordLadder(List<String> sequence) {
        if (sequence == null || sequence.isEmpty()) {
            return false;
        }

        if (sequence.size() == 1) {
            return isWord(sequence.get(0));
        }

        for (int i = 0; i < sequence.size() - 1; i++) {
            String current = sequence.get(i);
            String next = sequence.get(i + 1);

            if (!isWord(current) || !isWord(next) || getHammingDistance(current, next) != 1) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<String> getMinLadder(String start, String end) {
        List<String> empty = new ArrayList<>();
        List<String> ladder = new ArrayList<>();

        if (start == null || end == null) return empty;

        start = start.toLowerCase();
        end = end.toLowerCase();

        if (!isWord(start) || !isWord(end) || getHammingDistance(start, end) == -1) {
            return empty;
        }

        if (start.equals(end)) {
            ladder.add(start);
            return ladder;
        }

        ArrayDeque<Node> queue = new ArrayDeque<>();
        HashSet<String> visited = new HashSet<>();

        Node startNode = new Node(start, null);
        queue.add(startNode);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.removeFirst();
            List<String> neighbors = getNeighbors(current.position);

            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    Node neighborNode = new Node(neighbor, current);
                    visited.add(neighbor);
                    queue.add(neighborNode);

                    if (neighbor.equals(end)) {
                        return buildPath(new Node(neighbor, current));
                    }
                }
            }
        }

        return empty;
    }

    /** Reconstructs the path from end to start using predecessor links */
    private List<String> buildPath(Node endNode) {
        List<String> path = new ArrayList<>();
        for (Node node = endNode; node != null; node = node.predecessor) {
            path.add(0, node.position);
        }
        return path;
    }

    /** Inner class to track BFS search with backtracking */
    private class Node {
        String position;
        Node predecessor;

        public Node(String position, Node predecessor) {
            this.position = position;
            this.predecessor = predecessor;
        }
    }
}
