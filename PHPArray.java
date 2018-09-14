import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class PHPArray<V> implements Iterable<V> {

    private int numberOfPairs; //basically how many things are added to the hash table, useful for determining when half full
    private int tableSize; //size of the hash table
    private Node[] hashTable; //a hashtable made out of nodes that contain key, value, next, previous
    private Node start = new Node();
    private Node end = new Node();
    private Node eachCurr = start;

    //default constructor which sets the table size to 11
    public PHPArray() {
        this(11);
        this.hashTable = new Node[11];
    }

    public PHPArray(int initSize) {
        //set the global variable to the initial size
        tableSize = initSize;
        //create a hashtable of nodes from that table size
        this.hashTable = new Node[tableSize];
    }

    void put(String key, V value) {
        int hashCode = hash(key);

        //table reszie
        if (numberOfPairs >= tableSize / 2) {
            System.out.println("\t\tSize: " + numberOfPairs + " -- resizing array from " + tableSize + " to " + 2 * tableSize);
            resize(2 * tableSize);
        }

        //check to see if already present
        for (int i = hashCode; hashTable[i] != null; i = (i + 1) % tableSize) {
            if (hashTable[i].key.equals(key)) {
                hashTable[hashCode].value = value;
                return;
            }
        }

        Node newNode = new Node(key, value);

        if (numberOfPairs == 0) {
            //placeing the new node into the table via linear probing
            hashTable[hashCode] = newNode;

            //LINKED LIST: adding the first value
            start.next = newNode;
            end.previous = newNode;
            newNode.next = end;
            newNode.previous = start;
        } else {
            int i = hashCode;
            for (i = hashCode; hashTable[i] != null; i = (i + 1) % tableSize) {
                if (hashTable[i].key.equals(key)) {
                    hashTable[i].value = value;
                    return;
                }
            }
            //placeing the new node into the table via linear probing
            hashTable[i] = newNode;

            //LINKED LIST: add all other values
            Node last = end.previous;
            last.next = newNode;
            newNode.previous = last;
            end.previous = newNode;
        }

        numberOfPairs++;
    }

    //still store the key here as a string
    void put(int keyInt, V value) {
        //change the key from an int to a string
        String key = Integer.toString(keyInt);

        int hashCode = hash(key);

        //table reszie
        if (numberOfPairs >= tableSize / 2) {
            System.out.println("\t\tSize: " + numberOfPairs + " -- resizing array from " + tableSize + " to " + 2 * tableSize);
            resize(2 * tableSize);
        }

        //check to see if already present
        for (int i = hashCode; hashTable[i] != null; i = (i + 1) % tableSize) {
            if (hashTable[i].key.equals(key)) {
                hashTable[hashCode].value = value;
                return;
            }
        }

        Node newNode = new Node(key, value);

        //this checks for a key already present
        //if it is present and not null it replaces the current value with the new one
        if (numberOfPairs == 0) {
            //placeing the new node into the table via linear probing
            hashTable[hashCode] = newNode;

            //LINKED LIST: adding the first value
            start.next = newNode;
            end.previous = newNode;
            newNode.next = end;
            newNode.previous = start;
        } else {
            int i = hashCode;
            for (i = hashCode; hashTable[i] != null; i = (i + 1) % tableSize) {
                if (hashTable[i].key.equals(key)) {
                    hashTable[i].value = value;
                    return;
                }
            }
            //placeing the new node into the table via linear probing
            hashTable[i] = newNode;

            //LINKED LIST: add all other values
            Node last = end.previous;
            last.next = newNode;
            newNode.previous = last;
            end.previous = newNode;
        }

        numberOfPairs++;
    }

    Pair<V> each() {
        eachCurr = eachCurr.next;
        if (eachCurr != null) {
            Pair pair = new Pair(eachCurr);
            return pair;
        } else {
            return null;
        }
    }

    ArrayList<String> keys() {
        ArrayList<String> list = new ArrayList<String>();
        Node current = start.next;
        while (current != null) {
            list.add(current.key);
            current = current.next;
        }
        return list;
    }

    ArrayList<V> values() {
        ArrayList<V> list = new ArrayList<V>();
        Node current = start.next;
        while (current != null) {
            list.add((V) current.value);
            current = current.next;
        }
        return list;
    }

    void showTable() {
        System.out.println("\tRaw Hash Table Contents:");
        for (int i = 0; i < tableSize; i++) {
            if (hashTable[i] == null) {
                System.out.println(i + ": null");
            } else {
                System.out.println(i + ": Key: " + hashTable[i].key + " Value: " + hashTable[i].value.toString());
            }
        }
    }

    V get(String key) {
        int hashCode = hash(key);

        for (int i = hashCode; hashTable[i] != null; i = (i + 1) % tableSize) {
            if (hashTable[i].key.equals(key)) {
                return (V) hashTable[hashCode].value;
            }
        }
        return null;
    }

    V get(int keyInt) {
        String key = Integer.toString(keyInt);
        int hashCode = hash(key);

        for (int i = hashCode; hashTable[i] != null; i = (i + 1) % tableSize) {
            if (hashTable[i].key.equals(key)) {
                return (V) hashTable[hashCode].value;
            }
        }
        return null;
    }

    int length() {
        return numberOfPairs;
    }

    private boolean contains(String key) {
        return get(key) != null;
    }

    void unset(String key) {
        if (!contains(key)) {
            return;
        }

        int i = hash(key);
        while (!key.equals(hashTable[i].key)) {
            i = (i + 1) % tableSize;
        }

        //node from the linked list
        Node previous = hashTable[i].previous;
        Node next = hashTable[i].next;
        //checks if a previous or next is null
        if (previous != null) {
            previous.next = next;
        }
        if (next != null) {
            next.previous = previous;
        }
        //delete the key and associated value
        hashTable[i] = null;
        numberOfPairs--;

        //rehash all keys in the cluster
        i = (i + 1) % tableSize;
        while (hashTable[i] != null) {
            Node nodeToRehash = hashTable[i];
            System.out.println("\t\tKey " + hashTable[i].key + " rehashed...\n");
            hashTable[i] = null;
            numberOfPairs--;
            reput(nodeToRehash);
            i = (i + 1) % tableSize;
        }
    }

    void unset(int keyInt) {
        String key = Integer.toString(keyInt);
        if (!contains(key)) {
            return;
        }

        int i = hash(key);
        while (!key.equals(hashTable[i].key)) {
            i = (i + 1) % tableSize;
        }

        //node from the linked list
        Node previous = hashTable[i].previous;
        Node next = hashTable[i].next;
        //checks if a previous or next is null
        if (previous != null) {
            previous.next = next;
        }
        if (next != null) {
            next.previous = previous;
        }
        //delete the key and associated value
        hashTable[i] = null;
        numberOfPairs--;

        //rehash all keys in the cluster
        i = (i + 1) % tableSize;
        while (hashTable[i] != null) {
            Node nodeToRehash = hashTable[i];
            System.out.println("\t\tKey " + hashTable[i].key + " rehashed...\n");
            hashTable[i] = null;
            numberOfPairs--;
            reput(nodeToRehash);
            i = (i + 1) % tableSize;
        }
    }

    void reset() {
        eachCurr = start;
    }

    void sort() {
        ArrayList sortedList = new ArrayList(numberOfPairs);

        //put everything into the array
        int k = 0;
        for (int i = 0; i < tableSize; i++) {
            if (hashTable[i] != null) {
                sortedList.add(k, (V) hashTable[i].value);
                k++;
            }
        }
        //actually sort the data
        Collections.sort(sortedList);

        //create a temp hashTable
        Node[] temp = new Node[tableSize];
        Node current = start;
        for (int i = 0; i < numberOfPairs; i++) {
            String key = Integer.toString(i);
            Node newNode = new Node(key, sortedList.get(i));
            current.next = newNode;
            newNode.previous = current;
            int hashcode = hash(key);
            temp[hashcode] = newNode;
            current = current.next;
        }
        current.next = null;
        this.hashTable = temp;

    }

    void asort() {
        ArrayList<Node> sortedList = new ArrayList<Node>(numberOfPairs);

        //put everything into the array
        int k = 0;
        for (int i = 0; i < tableSize; i++) {
            if (hashTable[i] != null) {
                sortedList.add(k, hashTable[i]);
                k++;
            }
        }
        Collections.sort(sortedList, new cmprtr());
        
        //create a temp hashTable
        Node[] tempHashTable = new Node[tableSize];
        Node current = start;
        for (int i = 0; i < numberOfPairs; i++) {
            Node newNode = sortedList.get(i);
            current.next = newNode;
            newNode.previous = current;
            newNode.next = end;
            end.previous = newNode;
            int hashcode = hash(newNode.key);
            //linear probing
            while(tempHashTable[hashcode] != null){
                hashcode++;
            }
            tempHashTable[hashcode] = newNode;
            current = current.next;
        }
        current.next = null;
        this.hashTable = tempHashTable;
    }

    PHPArray<String> array_flip() throws ClassCastException {
        reset();
        
        //attempt to check for multiple of the same values
//        PHPArray<String> newArray = new PHPArray<String>(tableSize);
//        
//        Node curr = start;
//        newArray.numberOfPairs = 0;
//        while(curr.next != null){
//            String key = curr.key;
//            V value = (V) curr.value;
//            flipPut(newArray, key, value);
//        }
        
        PHPArray<String> newArray = (PHPArray<String>) this;
        
        for(int i = 0; i < tableSize; i++){
            if(this.hashTable[i] != null){
                String key = this.hashTable[i].key;
                V value = (V) this.hashTable[i].value;
                //attempt to check for multiple same values
//                int hashCode = hash(key);
//                if(newArray.hashTable[hashCode].equals(newArray.hashTable[i].value)){
//                    newArray.hashTable[hashCode].key = (String) newArray.hashTable[i].value;
//                } else {
                    newArray.hashTable[i].key = (String) newArray.hashTable[i].value;
                //}
                newArray.hashTable[i].value = key;
            }
        }
        return newArray;
    }
    
    private void flipPut(PHPArray newArray, String key, V value) {
        int hashCode = hash(key);

        //check to see if already present
        for (int i = hashCode; newArray.hashTable[i] != null; i = (i + 1) % newArray.tableSize) {
            if (newArray.hashTable[i].key.equals(key)) {
                newArray.hashTable[hashCode].value = value;
                return;
            }
        }

        Node newNode = new Node((String) value, key);

        if (newArray.numberOfPairs == 0) {
            //placeing the new node into the table via linear probing
            newArray.hashTable[hashCode] = newNode;

            //LINKED LIST: adding the first value
            newArray.start.next = newNode;
            newArray.end.previous = newNode;
            newNode.next = newArray.end;
            newNode.previous = newArray.start;
        } else {
            int i = hashCode;
            for (i = hashCode; newArray.hashTable[i] != null; i = (i + 1) % newArray.tableSize) {
                if (newArray.hashTable[i].key.equals(key)) {
                    newArray.hashTable[i].value = value;
                    return;
                }
            }
            //placeing the new node into the table via linear probing
            newArray.hashTable[i] = newNode;

            //LINKED LIST: add all other values
            Node last = newArray.end.previous;
            last.next = newNode;
            newNode.previous = last;
            newArray.end.previous = newNode;
        }
        
        newArray.numberOfPairs++;
    }

    public Iterator<V> iterator() {
        return new MyIterator<V>();
    }

    private class cmprtr implements Comparator<Node> {

        @Override
        public int compare(Node t, Node t1) {
            String value1 = (String) t.value;
            String value2 = (String) t1.value;
            
            
            return value1.compareTo(value2);
        }
    }
    
    public class MyIterator<V> implements Iterator<V> {

        private Node currentNode = start;

        public boolean hasNext() {
            return currentNode.next != null;
        }

        public V next() {
            if (currentNode.next == null) {
                throw new NoSuchElementException();
            }
            currentNode = currentNode.next;
            return (V) currentNode.value;
        }

    }

    private int hash(String key) {
        return (key.hashCode() & 0x7fffffff) % tableSize;
    }

    //resize the array if it gets to half capacity or over
    private void resize(int capacity) {
        PHPArray<V> temp = new PHPArray<V>(capacity);
        for (int i = 0; i < tableSize; i++) {
            if (hashTable[i] != null) {
                //unsure if the casting of value is correct but it got rid of the error
                temp.reput(hashTable[i]);
            }
        }
        hashTable = temp.hashTable;
        tableSize = temp.tableSize;
    }

    //reput is only for rehashing 
    //this prevents the changing of the linked list
    private void reput(Node node) {
        int hashCode = hash(node.key);

        Node newNode = node;

        int i = hashCode;
        for (i = hashCode; hashTable[i] != null; i = (i + 1) % tableSize) {
            continue;
        }
        //placeing the new node into the table via linear probing
        hashTable[i] = newNode;

        numberOfPairs++;
    }

    private static class Node<V> {

        String key;
        V value;
        //this is the linked list part
        //it keeps the nodes in order by chaining them
        Node previous;
        Node next;

        public Node() {
            key = null;
            value = null;
            previous = null;
            next = null;
        }

        public Node(String key, V value) {
            this.key = key;
            this.value = value;
            this.previous = null;
            this.next = null;
        }
    }

    public static class Pair<V> {

        String key;
        V value;

        public Pair(Node node) {
            this.key = node.key;
            this.value = (V) node.value;
        }
    }

}
