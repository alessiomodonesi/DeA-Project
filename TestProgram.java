import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class MyEntry {
    private Integer key;
    private String value;

    public MyEntry(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + " " + value;
    }
}

class SkipListPQ {

    private class Node {
        int key;
        String value;
        Node prev, next, above, below;

        public Node(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node head; // Nodo sentinella superiore sinistro
    private int height; // Altezza corrente della Skip List
    private int size; // Numero di elementi nella lista
    private double alpha;
    private Random rand;
    private int totalTraversedNodes; // Somma totale dei nodi attraversati
    private int totalInserts; // Numero totale di inserimenti

    public SkipListPQ(double alpha) {
        this.alpha = alpha;
        this.rand = new Random();
        this.head = new Node(Integer.MIN_VALUE, null); // Nodo sentinella -∞
        Node tail = new Node(Integer.MAX_VALUE, null); // Nodo sentinella +∞
        head.next = tail;
        tail.prev = head;
        this.height = 0;
        this.size = 0;
        this.totalTraversedNodes = 0;
        this.totalInserts = 0;
    }

    public int size() {
        return size;
    }

    public MyEntry min() {
        if (size == 0)
            return null;
        Node first = head;
        while (first.below != null) {
            first = first.below; // Scendi fino a S₀
        }
        first = first.next; // Il primo nodo valido
        return new MyEntry(first.key, first.value);
    }

    public int insert(int key, String value) {
        Node current = head;
        int traversedNodes = 0; // Contatore dei nodi attraversati

        // Trova la posizione corretta in S₀
        while (current.below != null) { // Scendi al livello inferiore
            traversedNodes++; // Conta il nodo attuale
            current = current.below;
            while (current.next.key < key) { // Muoviti orizzontalmente
                traversedNodes++; // Conta il nodo attraversato
                current = current.next;
            }
        }

        // Inserisci il nodo in S₀
        Node newNode = new Node(key, value);
        Node nextNode = current.next;
        current.next = newNode;
        newNode.prev = current;
        newNode.next = nextNode;
        nextNode.prev = newNode;

        // Costruisci la torre
        int level = generateEll(alpha, key);
        int currentLevel = 0;

        while (currentLevel < level) {
            // Crea un nuovo livello se necessario
            if (currentLevel >= height) {
                height++;
                Node newHead = new Node(Integer.MIN_VALUE, null);
                Node newTail = new Node(Integer.MAX_VALUE, null);
                newHead.next = newTail;
                newTail.prev = newHead;
                newHead.below = head;
                head.above = newHead;
                head = newHead;
            }

            // Trova il nodo sopra il current
            while (current.above == null) {
                current = current.prev;
                traversedNodes++; // Conta il nodo mentre ci muoviamo all'indietro
            }
            traversedNodes++; // Conta il nodo sopra
            current = current.above;

            // Inserisci il nuovo nodo nel livello superiore
            Node newUpperNode = new Node(key, value);
            newUpperNode.below = newNode;
            newNode.above = newUpperNode;

            Node upperNext = current.next;
            current.next = newUpperNode;
            newUpperNode.prev = current;
            newUpperNode.next = upperNext;
            upperNext.prev = newUpperNode;

            newNode = newUpperNode;
            currentLevel++;
        }

        size++;
        totalInserts++;
        totalTraversedNodes += traversedNodes; // Aggiorna il totale dei nodi attraversati
        return traversedNodes;
    }

    private int generateEll(double alpha_, int key) {
        int level = 0;
        if (alpha_ >= 0. && alpha_ < 1) {
            while (rand.nextDouble() < alpha_) {
                level += 1;
            }
        } else {
            while (key != 0 && key % 2 == 0) {
                key = key / 2;
                level += 1;
            }
        }
        return level;
    }

    public MyEntry removeMin() {
        if (size == 0)
            return null;

        Node current = head;
        while (current.below != null) {
            current = current.below;
        }
        current = current.next;

        MyEntry minEntry = new MyEntry(current.key, current.value);

        // Rimuovi il nodo e aggiorna i collegamenti
        while (current != null) {
            Node next = current.next;
            Node prev = current.prev;
            if (prev != null)
                prev.next = next;
            if (next != null)
                next.prev = prev;
            current = current.above;
        }

        // Rimuovi livelli vuoti
        while (head.below != null && head.next.key == Integer.MAX_VALUE) {
            head = head.below;
            head.above = null;
            height--;
        }

        size--;
        return minEntry;
    }

    public void print() {
        Node current = head;
        while (current.below != null) {
            current = current.below;
        }
        current = current.next;

        List<String> output = new ArrayList<>();
        while (current.key != Integer.MAX_VALUE) {
            int count = 1;
            Node temp = current;
            while (temp.above != null) {
                count++;
                temp = temp.above;
            }
            output.add(current.key + " " + current.value + " " + count);
            current = current.next;
        }
        System.out.println(String.join(", ", output));
    }

    public String statistics() {
        double avgTraversed = totalInserts > 0 ? (double) totalTraversedNodes / totalInserts : 0.0;
        return String.format("%.2f %d %d %.2f", alpha, size, totalInserts, avgTraversed);
    }
}

public class TestProgram {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TestProgram <file_path>");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String[] firstLine = br.readLine().split(" ");
            int N = Integer.parseInt(firstLine[0]);
            double alpha = Double.parseDouble(firstLine[1]);
            System.out.println(N + " " + alpha);

            SkipListPQ skipList = new SkipListPQ(alpha);

            for (int i = 0; i < N; i++) {
                String[] line = br.readLine().split(" ");
                int operation = Integer.parseInt(line[0]);

                switch (operation) {
                    case 0: // min
                        MyEntry minEntry = skipList.min();
                        if (minEntry != null)
                            System.out.println(minEntry);
                        break;
                    case 1: // removeMin
                        @SuppressWarnings("unused")
                        MyEntry removed = skipList.removeMin();
                        break;
                    case 2: // insert
                        int key = Integer.parseInt(line[1]);
                        String value = line[2];
                        skipList.insert(key, value);
                        break;
                    case 3: // print
                        skipList.print();
                        break;
                    default:
                        System.out.println("Invalid operation code");
                }
            }

            System.out.println(skipList.statistics());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}