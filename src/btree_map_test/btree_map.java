package btree_map_test;

import java.util.ArrayList;
import javafx.util.Pair;

public class btree_map<K extends Comparable<K>, V> {
    private int HEIGHT = 0;
    public int t;
    private int NUM_OF_PAIRS = 0;  
    public Node root;
    
    private class Node {
        private int NUM_OF_ENTRIES = 0;
        private Pair<K, V> entry[] = new Pair[2 * t - 1];
        private ArrayList<Node> child = new ArrayList<Node>(2 * t);
        //Node child[] = new Node[2 * t];
        boolean leaf = true; 
                
       public Node(){
           for(int i = 0; i < 2 * t; i++){
               child.add(null);
           }
       }
    }
    
    public btree_map(int t) {
        this.t = t;
        this.root = new Node();
        this.root.NUM_OF_ENTRIES = 0;
        this.root.leaf = true;
    }
    
    public btree_map(btree_map MapToCopy){
        this.HEIGHT = MapToCopy.HEIGHT;
        this.NUM_OF_PAIRS = MapToCopy.NUM_OF_PAIRS;
        this.root = recursiveCopy(MapToCopy.root);
    }
    
    private void split(Node NodeToSplit, int pos, Node NewNode) {
        Node temp = new Node();
        temp.leaf = NewNode.leaf;
        temp.NUM_OF_ENTRIES = t - 1;
        for (int j = 0; j < t - 1; j++) {
            temp.entry[j] = NewNode.entry[j + t];
        }
        if (NewNode.leaf == false) {
            for (int j = 0; j < t; j++) {
                temp.child.set(j, NewNode.child.get(j + t));
            }
        }
        NewNode.NUM_OF_ENTRIES = t - 1;
        for (int j = NodeToSplit.NUM_OF_ENTRIES; j >= pos + 1; j--) {
            NodeToSplit.child.set(j + 1, NodeToSplit.child.get(j)); 
        }
        NodeToSplit.child.set(pos + 1, temp);
        for (int j = NodeToSplit.NUM_OF_ENTRIES - 1; j >= pos; j--) {
            NodeToSplit.entry[j + 1] = NodeToSplit.entry[j];
        }
        NodeToSplit.entry[pos] = NewNode.entry[t - 1];
        NodeToSplit.NUM_OF_ENTRIES = NodeToSplit.NUM_OF_ENTRIES + 1;
    }
    
    public void add(K key, V value) {
        Node rootNode = root;
        if (rootNode.NUM_OF_ENTRIES == 2 * t - 1) {
            Node s = new Node();
            root = s;
            s.leaf = false;
            s.NUM_OF_ENTRIES = 0;
            s.child.set(0, rootNode);
            HEIGHT++;
            split(s, 0, rootNode);
            addNode(s, key, value);
        } else {
            addNode(rootNode, key, value);
        }
        NUM_OF_PAIRS++;
    }
    
    private void addNode(Node node, K key, V value) {
        if (node.leaf == true) {
            int i = 0;
            for (i = node.NUM_OF_ENTRIES - 1; i >= 0 
                    && key.compareTo(node.entry[i].getKey()) < 0; i--) {
                node.entry[i + 1] = node.entry[i];
            }
            node.entry[i + 1] = new Pair(key, value);
            node.NUM_OF_ENTRIES = node.NUM_OF_ENTRIES + 1;
        } else {
            int i = 0;
            for (i = node.NUM_OF_ENTRIES - 1; i >= 0 
                    && key.compareTo(node.entry[i].getKey()) < 0; i--) {
            }
            i++;
            Node tmp = node.child.get(i);
            if (tmp.NUM_OF_ENTRIES == 2 * t - 1) {                
                split(node, i, tmp);        
                if (key.compareTo(node.entry[i].getKey()) > 0) {
                    i++;                
                }
            }
            addNode(node.child.get(i), key, value);
        }
    }
    
       public Pair searchpair(K key) {
        Node node = root;
        while (true) {
            int i = binarySearch(node, key);
            if (i == -1) {
                return new Pair (null, 0);
            }
            if (i < node.NUM_OF_ENTRIES && (key.compareTo(node.entry[i].getKey()) > 0) 
                    || (node.entry[i].getKey().compareTo(key) < 0)){
                return new Pair(node, i);
            }
            if (node.leaf) {
                return new Pair(null, 0);
            }
            else
            {
                node = node.child.get(i);
            }
        }
    }
    
    public boolean search(K key) {
        return searchpair(key).getKey() != null;
    }
    
    public void clear(){
        this.root = null;
    }

    public boolean isEmpty(){
        return  root == null || root.NUM_OF_ENTRIES == 0;
    }
    
    public int getHeight(){
        return this.HEIGHT;
    }
    
    public boolean heightTest(){
        return getHeight() <= (Math.log((NUM_OF_PAIRS + 1)/2))/(Math.log(t));
    }

    private void testNode(Node node){
        if (node != root && node.NUM_OF_ENTRIES < (t - 1) && node.NUM_OF_ENTRIES > (2 * t - 1)){
            System.exit(1);
        }
        else if (node == root && node.NUM_OF_ENTRIES < 1 && node.NUM_OF_ENTRIES > (2 * t)){
            System.exit(1);
        }
        if (node.leaf == false) {
            for (int i = 0; i <= node.NUM_OF_ENTRIES; i++){
                testNode(node.child.get(i));
            }
        }
    } 
    
    private int binarySearch(Node node, K key) {
            int left = 0;
            int right = node.NUM_OF_ENTRIES - 1;
            int search = -1;
            if (key.compareTo(node.entry[0].getKey()) < 0) {
                return 0;
            } else if (node.entry[node.NUM_OF_ENTRIES - 1].getKey().compareTo(key) < 0) {
                return node.NUM_OF_ENTRIES - 1;
            }
            while (left <= right) {  
                int mid = (left + right) / 2;
                if (key.equals(node.entry[mid].getKey())) {
                    search = mid;
                    break;
                } else if (node.entry[mid].getKey().compareTo(key) < 0 
                        && node.entry[mid + 1].getKey().compareTo(key) > 0) {  
                    search = mid;
                    break;
            } else if (node.entry[mid].getKey().compareTo(key) < 0)
                    left = mid + 1;
                    else
                    right = mid - 1;
            }
            return search;
        }
    
    private Node recursiveCopy(Node node) {
        Node NodeToCopy = new Node();
        NodeToCopy.leaf = node.leaf;
        NodeToCopy.NUM_OF_ENTRIES = node.NUM_OF_ENTRIES;
        for (int i = 0; i <= node.NUM_OF_ENTRIES; i++){
            NodeToCopy.entry[i] = new Pair(node.entry[i].getKey(),node.entry[i].getValue());
            if (node.leaf == false) {
                NodeToCopy.child.set(i, recursiveCopy(node.child.get(i)));
            }
        }
        return NodeToCopy;
    }
    
    public boolean nodeTest(){
        testNode(root);
        return true;
    }
}