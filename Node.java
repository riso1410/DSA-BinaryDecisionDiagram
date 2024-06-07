public class Node {

    private String bfunkcia;
    private Node left;
    private Node right;
    private String var;
    private Node parent;

    public Node(String bfunkcia, Node left, Node right) {
        this.bfunkcia = bfunkcia;
        this.left = left;
        this.right = right;
    }

    public String getBfunkcia() {return bfunkcia;}
    public void setBfunkcia(String bfunkcia) {
        this.bfunkcia = bfunkcia;
    }
    public Node getLeft() {return left;}
    public String getVar() {return var;}
    public void setVar(String var) {this.var = var;}
    public Node getRight() {
        return right;
    }
    public Node getParent() {
        return parent;
    }
    public void setLeft(Node left) {
        this.left = left;
    }
    public void setRight(Node right) {
        this.right = right;
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }
}
