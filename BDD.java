import java.util.*;
public class BDD {

    double numberOfNodes = 1;
    double reducedNumberOfNodes = 0;
    String order;
    HashMap<String, Node> nodeMap = new HashMap<>();
    Node root;
    long time;
    public void BDD_create(String bfunc, String order) {

        Node trueNode = new Node("1",null,null);
        Node falseNode = new Node("0", null,null);
        this.order = order;
        nodeMap.put("1", trueNode);
        nodeMap.put("0", falseNode);
        root = new Node(bfunc, null, null);
        String[] vars = order.split("");
        decompose(root, vars, 0);
        reduce_S(root);
    }

    public void decompose(Node node, String[] vars, int i) {

        //kontrola dokym sa neprejde cely order
        if (i < 0 || i == vars.length) {
            return;
        }

        String[] clauses = node.getBfunkcia().split("\\+");
        String var = vars[i];
        String negVar = "!" + var;
        ArrayList<String> leftClause = new ArrayList<>();
        ArrayList<String> rightClause = new ArrayList<>();

        //cyklus na mazanie premennych
        for (String clause : clauses) {

            if (clause.equals("0")) {
                continue;
            }

            if (clause.contains(var)) {
                if (clause.contains("!" + var)) {
                    if (clause.equals(negVar)) {
                        leftClause.add(clause.replace(negVar, "1"));
                        if (clauses.length == 1) {
                            rightClause.add(clause.replace(negVar, "0"));
                        }
                    } else {
                        String temp = clause.replace(negVar, "");
                        if (!leftClause.contains(temp)) {
                            leftClause.add(temp);
                        }
                    }
                } else {
                    if (clause.equals(var)) {
                        String temp = clause.replace(var, "1");
                        rightClause.add(temp);

                        if (clauses.length == 1) {
                            leftClause.add(clause.replace(var, "0"));
                        }
                    } else {
                        String temp = clause.replace(var, "");
                        if (!rightClause.contains(temp)) {
                            rightClause.add(temp);
                        }

                        if (clauses.length == 1) {
                            leftClause.clear();
                            leftClause.add("0");
                        }
                    }
                }
            }
            //ak sa premenna nenachadza v klauzule nakopiruje je celu do left aj do right nody
            else {
                leftClause.add(clause);
                rightClause.add(clause);
            }
        }

        //vymazanie duplikatov z klauzul
        HashSet<String> set = new HashSet<>(leftClause);
        leftClause.clear();
        leftClause.addAll(set);
        HashSet<String> set2 = new HashSet<>(rightClause);
        rightClause.clear();
        rightClause.addAll(set2);

        //kontrola ci sa v klauzule nachadza 1 alebo 0 ak ano tak sa vyhodnoti cela funkcia tak
        if (leftClause.contains("1") || checkNeg(leftClause)) {
            leftClause.clear();
            leftClause.add("1");
        } else if ((leftClause.contains("0") && leftClause.size() == 1) || leftClause.isEmpty()) {
            leftClause.clear();
            leftClause.add("0");
        }

        if (rightClause.contains("1") || checkNeg(rightClause)) {
            rightClause.clear();
            rightClause.add("1");
        } else if ((rightClause.contains("0") && rightClause.size() == 1) || rightClause.isEmpty()) {
            rightClause.clear();
            rightClause.add("0");
        }

        //tvorenie novych nodov
        if (!node.getBfunkcia().equals("0") && !node.getBfunkcia().equals("1")) {
            if (!leftClause.contains("1") && !leftClause.contains("0")) {
                if (node.getLeft() == null) {
                    numberOfNodes++;
                    Node leftChild = new Node(String.join("+", leftClause), null, null);
                    node.setVar(var);
                    node.setLeft(leftChild);
                    leftChild.setParent(node);
                    decompose(leftChild, vars, i + 1);
                }
            }

            if (!rightClause.contains("1") && !rightClause.contains("0")) {
                if (node.getRight() == null) {
                    numberOfNodes++;
                    Node rightChild = new Node(String.join("+", rightClause), null, null);
                    node.setVar(var);
                    node.setRight(rightChild);
                    rightChild.setParent(node);
                    decompose(rightChild, vars, i + 1);
                }
            }

            //ak je vyraz 1 alebo 0 tak sa nastavi true alebo false node
            if (leftClause.contains("1")) {
                node.setLeft(nodeMap.get("1"));
                node.setVar(var);
            } else if (leftClause.contains("0")) {
                node.setLeft(nodeMap.get("0"));
                node.setVar(var);
            }

            if (rightClause.contains("1")) {
                node.setRight(nodeMap.get("1"));
                node.setVar(var);
            } else if (rightClause.contains("0")) {
                node.setRight(nodeMap.get("0"));
                node.setVar(var);
            }
        }

        //volanie funkcie na redukciu nodov
        node = reduce(node);
        //kontrola ci sa node uz nenachadza v mape ak nie tak sa prida
        if (!nodeMap.containsKey(node.getBfunkcia())) {
            reducedNumberOfNodes++;
            nodeMap.put(node.getBfunkcia(), node);
        }
    }

    //kontrola ci sa v klauzule nenachadza premenna aj negovana premenna
    public boolean checkNeg(ArrayList<String> list) {
        Set<String> variables = new HashSet<>();

        for (String clause : list) {
            if (clause.length() < 3) {
                if (clause.startsWith("!")) {
                    String variable = clause.substring(1);
                    if (variables.contains(variable)) {
                        return true;
                    }
                } else {
                    if (variables.contains("!" + clause)) {
                        return true;
                    }
                }
                variables.add(clause);
            }
        }

        return false;
    }

    //redukcia I, ak maju dva nody rovnaku funkciu tak sa druheho pointer nastavi na miesto prveho
    public Node reduce(Node node){

        Node childNode = nodeMap.get(node.getBfunkcia());

        if (childNode != null) {

            if (node.getParent().getLeft() == node){
                node.getParent().setLeft(childNode);
                return node.getParent().getLeft();
            } else if (node.getParent().getRight() == node){
                node.getParent().setRight(childNode);
                return node.getParent().getRight();
            }
        }
        return node;
    }

    public void reduce_S(Node node){

        if (node != null && node.getLeft() != null && node.getRight() != null && node.getLeft() == node.getRight()){
            if (node.getParent() == null){
                this.root = node.getLeft();
                reducedNumberOfNodes--;

            } else if (node.getParent().getLeft() == node){
                node.getParent().setLeft(node.getLeft());
                reducedNumberOfNodes--;

            } else if (node.getParent().getRight() == node){
                node.getParent().setRight(node.getLeft());
                reducedNumberOfNodes--;

            }
        }
        if (node != null){
            reduce_S(node.getLeft());
            reduce_S(node.getRight());
        }
    }

    public String BDD_use(BDD bdd, String input) {
        //pocitanie spravneho vysledku
        order = bdd.order;

        String flag = "-1";
        String[] vars = order.split("");
        String clause = root.getBfunkcia();
        Node node = root;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '0') {
                clause = clause.replace("!" + vars[i], "1");
                clause = clause.replace(vars[i], "0");
            } else if (input.charAt(i) == '1') {
                clause = clause.replace("!" + vars[i], "0");
                clause = clause.replace(vars[i], "1");
            }
        }

        String[] result = clause.split("\\+");

        for (String s : result) {
            if (!s.contains("0")) {
                flag = "1";
                break;
            } else {
                flag = "0";
            }
        }
        ////////////////////////////////////////////////////////////////////
        //Pocitanie mojho vysledku


        String myFlag = "-2";
        for (int i = 0; i < input.length(); i++) {

            char var = input.charAt(order.indexOf(node.getVar()));

            if (var == '0') {
                if (node.getLeft().getBfunkcia().equals("1")) {
                    myFlag = "1";
                } else if (node.getLeft().getBfunkcia().equals("0")) {
                    myFlag = "0";
                } else {
                    node = node.getLeft();
                }
            } else if (var == '1') {
                if (node.getRight().getBfunkcia().equals("1")) {
                    myFlag = "1";
                } else if (node.getRight().getBfunkcia().equals("0")) {
                    myFlag = "0";
                } else {
                    node = node.getRight();
                }
            }
        }

        ////////////////////////////////////////////////////////////////////
        //Porovnanie vysledkov
        if (flag.equals(myFlag) && flag.equals("1")) {
            return "1";
        }
        else if (flag.equals(myFlag) && flag.equals("0")) {
            return "0";
        } else {
            return "-1";
        }
    }
}