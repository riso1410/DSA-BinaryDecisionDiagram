import java.io.*;
import java.util.*;
/*
TESTOVANIE: 500 formulí pre BDD_create a pre best_order,
 porovnat v grafe pocet nodov, pocet po zredukovanie,% zredukovania, cas vytvorenie
 pre rozne pocty premennych (10,11,12,13,14,15)
 */
public class Main {
    private static Node root;

    public static void main(String[] args) throws IOException {

        ArrayList<String> inputs = new ArrayList<>();
        List<String> bFunctions = generateBfunctions();

        for (String bFunction : bFunctions) {

            String order = getUniqueCharacters(bFunction);

            long startTime = System.nanoTime();
            BDD bdd = new BDD();
            bdd.BDD_create(bFunction, order);
            long endTime = System.nanoTime();
            root = bdd.root;
            long totalTime = (endTime - startTime) / 1000; //microseconds

            System.out.println(totalTime + " " + (int) bdd.numberOfNodes + " " + (int) bdd.reducedNumberOfNodes + " " + Math.round(((bdd.numberOfNodes - bdd.reducedNumberOfNodes) / bdd.numberOfNodes) * 100 * 100.0) / 100.0);

            //kontrola vsetkych moznych vstupov pre korektnost stromu (vsetky vstupy su unikatne) 2^n
            generateStrings("", order.length(), inputs);

            for (String input : inputs) {
                String result = bdd.BDD_use(bdd, input);
                if (result.equals("-1")) {
                    System.out.println("Chyba");
                }
            }
            inputs.clear();
        }

        System.out.println("Best order: ");
        for (String bFunction : bFunctions) {

            BDD bdd;
            String order = getUniqueCharacters(bFunction);

            bdd = BDD_create_with_best_order(bFunction);

            System.out.println(bdd.time + " " + (int) bdd.numberOfNodes + " " + (int) bdd.reducedNumberOfNodes + " " + Math.round(((bdd.numberOfNodes - bdd.reducedNumberOfNodes) / bdd.numberOfNodes) * 100 * 100.0) / 100.0);

            //kontrola vsetkych moznych vstupov pre korektnost stromu (vsetky vstupy su unikatne) 2^n
            generateStrings("", order.length(), inputs);

            for (String input : inputs) {
                String result = bdd.BDD_use(bdd, input);
                if (result.equals("-1")) {
                    System.out.println("Chyba");
                }
            }
            inputs.clear();
        }

//            System.out.println("\nFormula: " +bFunction + "\nPostupnosť: " + order + "\nVstup: " + input + "\nVýsledok: " + result+"\nRedukcia: " +
//                    Math.round(((bdd.numberOfNodes-bdd.reducedNumberOfNodes)/bdd.numberOfNodes)*100 * 100.0)/100.0 + "%" + "\nPočet uzlov: " +
//                    (int) bdd.numberOfNodes + "\nPočet redukovaných uzlov: " + (int) bdd.reducedNumberOfNodes);
//


//        String order = "TAWYGEJOKHISV";
//        bdd = BDD_create_with_best_order("!A!J!K!TWY+!I!KS!W+!AG!H!T!Y+E!HI!J!K!V!W+E!GO!V+HJO!T!WY");
//        root = bdd.root;
//        System.out.println(bdd.BDD_use(bdd,inputGenerator(order)));
        printAll();
    }

    static void printAll() throws FileNotFoundException {
        BinaryTreePrinter printer = new BinaryTreePrinter(root);
        printer.print(new PrintStream("C:\\Users\\riso\\IdeaProjects\\DSA2\\src\\Output.txt"));
    }


    private static void generateStrings(String currentString, int remainingLength, ArrayList<String> inputs) {
        if (remainingLength == 0) {
            inputs.add(currentString);
        } else {
            generateStrings(currentString + "0", remainingLength - 1, inputs);
            generateStrings(currentString + "1", remainingLength - 1, inputs);
        }
    }

    public static List<String> generateBfunctions() {

        List<String> bFunctions = new ArrayList<>();
        Random random = new Random();
        String[] chars = new String[10];
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        HashSet<String> uniqueChars = new HashSet<>();

        for (int l = 0; l < chars.length;) {
            int index = random.nextInt(alphabet.length());
            String c = alphabet.substring(index, index + 1);
            if (!uniqueChars.contains(c)) {
                chars[l] = c;
                uniqueChars.add(c);
                l++;
            }
        }

        for (int i = 0; i < 200; i++) {

            Set<String> clauses = new HashSet<>();
            int clauseCount = random.nextInt(5, 13);

            for (int j = 0; j < clauseCount; j++) {
                int variableCount = random.nextInt(5,13);
                Set<Character> variables = new TreeSet<>();

                for (int k = 0; k < variableCount; k++) {
                    String variable = chars[random.nextInt(chars.length)];
                    variables.add(variable.charAt(0));
                }

                StringBuilder clauseBuilder = new StringBuilder();
                for (Character variable : variables) {
                    if (random.nextBoolean()) {
                        clauseBuilder.append("!");
                    }
                    clauseBuilder.append(variable);
                }

                String clause = clauseBuilder.toString();
                clauses.add(clause);
            }
            String formula = String.join("+", clauses);
            if (!bFunctions.contains(formula)) {
                bFunctions.add(formula);
            } else {
                i--;
            }
        }
        return bFunctions;
    }

    public static String inputGenerator(String order){
        int length = order.length();
        Random random = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(random.nextInt(2));
        }
        return s.toString();
    }

    public static String getUniqueCharacters(String input) {
        ArrayList<Character> chars = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c != '+' && c != '!' && !chars.contains(c)) {
                chars.add(c);
            }
        }

        Collections.shuffle(chars);

        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            sb.append(c);
        }

        return sb.toString();
    }
    public static BDD BDD_create_with_best_order(String bfunc) {

        double bestNumberOfNodes = 0;
        BDD bdd = null;
        BDD bestBDD = null;
        ArrayList<String> usedOrders = new ArrayList<>();
        long totalTime = 0;

        for (int i = 0; i < 100; i++) {

            String order = getUniqueCharacters(bfunc);
            if (!usedOrders.contains(order)) {
                usedOrders.add(order);
                long startTime = System.nanoTime();
                bdd = new BDD();
                bdd.BDD_create(bfunc, order);
                long endTime = System.nanoTime();
                totalTime = (endTime - startTime) / 1000; //microseconds

            } else {
                i--;
            }

            if (bestNumberOfNodes == 0 || bdd.reducedNumberOfNodes < bestNumberOfNodes) {
                bestNumberOfNodes = bdd.reducedNumberOfNodes;
                bestBDD = bdd;
                bdd.time = totalTime;
            }
        }

        return bestBDD;
    }
}