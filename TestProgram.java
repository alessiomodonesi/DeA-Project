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
    private double alpha;
    private Random rand;
    private List<List<MyEntry>> skipList;

    /**
     * @author Alessio Modonesi
     */
    public SkipListPQ(double alpha) {
        this.alpha = alpha;
        this.rand = new Random();
        this.skipList = new ArrayList<>();

        // Sh contains only the two sentinels
        List<MyEntry> Sh = new ArrayList<>();
        Sh.add(new MyEntry(Integer.MIN_VALUE, "-inf"));
        Sh.add(new MyEntry(Integer.MAX_VALUE, "+inf"));
        skipList.add(Sh);
    }

    /**
     * @author Alessio Modonesi
     */
    public int size() {
        return skipList.get(0).size() - 2;
    }

    /**
     * @author Alessio Modonesi
     */
    public MyEntry min() {
        if (this.size() != 0)
            return skipList.get(0).get(1); // 1a entry di S0
        return null;
    }

    /**
     * @author Alessio Modonesi
     */
    public int insert(int key, String value) {
        MyEntry e = new MyEntry(key, value); // crea una nuova entry con chiave e valore specificati
        int height = generateEll(alpha, key); // genera l'altezza della entry

        // aggiunge nuovi livelli se l'altezza generata è maggiore
        // dell'altezza corrente della skip list
        while ((skipList.size() - 1) <= height) {
            List<MyEntry> newLevel = new ArrayList<>(); // crea un nuovo livello vuoto
            newLevel.add(new MyEntry(Integer.MIN_VALUE, "-inf")); // aggiunge la sentinella sinistra
            newLevel.add(new MyEntry(Integer.MAX_VALUE, "+inf")); // aggiunge la sentinella destra
            skipList.add(newLevel); // aggiunge il nuovo livello alla skip list
        }

        int i = 0; // inizia dal livello superiore Sh
        while (i <= height) { // scende progressivamente attraverso i livelli fino all'altezza della entry
            int position = 1; // posizione iniziale nel livello Si, saltandola sentinella sinistra

            // trova la posizione corretta per inserire la entry nel livello corrente Si
            while (key >= skipList.get(i).get(position).getKey())
                position++;

            skipList.get(i).add(position, e); // inserisce la nuova entry nella posizione corretta nel livello Si
            i++; // passa al livello inferiore
        }
        return 0;
    }

    private int generateEll(double alpha_, int key) {
        int level = 0; // inizializza il livello a 0

        // verifica se il parametro alpha_ appartiene a [0, 1)
        if (alpha_ >= 0. && alpha_ < 1) {
            // genera livelli in base a un criterio probabilistico
            while (rand.nextDouble() < alpha_)
                level += 1; // incrementa il livello finché rand.nextDouble() è minore di alpha_
        } else {
            // calcola i livelli in modo deterministico se alpha_ non è nel range [0, 1)
            while (key != 0 && key % 2 == 0) {
                key = key / 2; // divide la chiave per 2 finché è pari
                level += 1; // incrementa il livello per ogni divisione riuscita
            }
        }

        return level; // restituisce il livello calcolato
    }

    /**
     * @author Alessio Modonesi
     */
    public MyEntry removeMin() {
        MyEntry e = min(); // recupera la entry con chiave minima
        if (e == null)
            return e;

        int i = 0; // parte dal livello superiore Sh
        boolean check = skipList.get(i).remove(e); // rimuove la entry dal livello corrente (Sh)
        while (check) { // continua a rimuovere la entry finché è presente nei livelli inferiori
            i++; // scende al livello successivo
            check = skipList.get(i).remove(e); // prova a rimuovere la entry nel livello corrente
        }

        // rimuove i livelli vuoti dalla skip list per mantenerla compatta
        while (skipList.size() > 1 && skipList.get(skipList.size() - 2).size() == 2)
            skipList.remove(skipList.size() - 1);

        return e; // restituisce la entry rimossa
    }

    /**
     * @author Alessio Modonesi
     */
    public void print() {
        StringBuilder stdout = new StringBuilder();
        int position = 1; // inizializza l'indice per attraversare S0

        // ciclo per iterare attraverso tutti gli elementi di S0, escluse le sentinelle
        while (position < skipList.get(0).size() - 1) {
            MyEntry e = skipList.get(0).get(position); // ottiene l'elemento alla posizione i in S0

            int i = 1; // inizializza il livello corrente
            while ((i < skipList.size()) && skipList.get(i).contains(e)) // numero di livelli in cui la entry è presente
                i++;

            // aggiunge la entry e il numero di livelli in cui è presente
            stdout.append(e).append(" ").append(i).append(", ");
            position++; // passa all'elemento successivo in S0
        }

        System.out.println(stdout.substring(0, stdout.length() - 2)); // rimuove ", " a fine output
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

                /**
                 * @author Alessio Modonesi
                 */
                switch (operation) {
                    case 0:
                        MyEntry e = skipList.min(); // recupera l'elemento con chiave minima nella skip list
                        if (e != null)
                            System.out.println(e); // stampa l'elemento minimo
                        break;
                    case 1:
                        skipList.removeMin(); // rimuove l'elemento con chiave minima dalla skip list
                        break;
                    case 2:
                        int key = Integer.parseInt(line[1]); // converte la chiave da stringa a intero
                        String value = line[2]; // recupera il valore associato alla chiave
                        skipList.insert(key, value); // inserisce una nuova entry (key, value) nella skip list
                        break;
                    case 3:
                        skipList.print(); // stampa tutte le entry della skip list
                        break;
                    default:
                        System.out.println("Invalid operation code");
                        return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}