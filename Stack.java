
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
f. STACK IMPLEMENTATION.
Self Explanatory
*/

public class Stack<Item> implements Iterable<Item> {
    private int N;                // size of the stack
    private Node<Item> first;     // top of stack
    
    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
    }
    
    public Stack() {
        first = null;
        N = 0;
    }
    
    public boolean isEmpty() {
        return first == null;
    }
    
    public int size() {
        return N;
    }
    
    public void push(Item item) throws IOException {
        if(N>=7){
            ErrorHandler.throwError(ErrorHandler.ER_STACK_OVERFLOW);
        }
        Node<Item> oldfirst = first;
        first = new Node<Item>();
        first.item = item;
        first.next = oldfirst;
        N++;
    }
    
    public Item pop() throws IOException {
        if (isEmpty()) ErrorHandler.throwError(ErrorHandler.ER_STACK_UNDERFLOW);
        Item item=null;
        try{
        item = first.item;        // save item to return
        first = first.next;            // delete first node
        N--;
        }catch(Exception e){
            ErrorHandler.throwError(ErrorHandler.ER_STACK_UNDERFLOW);
        }
        return item;                   // return the saved item
    }
    
    public Item peek() throws IOException {
        if (isEmpty()) ErrorHandler.throwError(ErrorHandler.ER_STACK_UNDERFLOW);
        return first.item;
    }
    
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Item item : this)
            s.append(item + " ");
        return s.toString();
    }
    
    public Iterator<Item> iterator() {
        return new ListIterator<Item>(first);
    }
    
    private class ListIterator<Item> implements Iterator<Item> {
        private Node<Item> current;
        
        public ListIterator(Node<Item> first) {
            current = first;
        }
        
        public boolean hasNext()  { return current != null;                     }
        
        public void remove()      { throw new UnsupportedOperationException();  }
        
        public Item next() {
            if (!hasNext())
                try {
                    ErrorHandler.throwError(ErrorHandler.ER_STACK_UNDERFLOW);
                } catch (IOException ex) {
                    Logger.getLogger(Stack.class.getName()).log(Level.SEVERE, null, ex);
                }
            Item item = current.item;
            current = current.next;
            return item;
        }
    }
    
}