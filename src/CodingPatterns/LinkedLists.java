package CodingPatterns;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class LinkedLists {
    public static void main(String[] args) {
        System.out.println();
        Node head1 = new Node(1);
        head1.next = new Node(2);
        head1.next.next = new Node(3);
        head1.next.next.next = new Node(4);
        head1.next.next.next.next = new Node(5);

        head1 = deleteNthNodeFromEnd2(head1, 5);

        Node curr = head1;
        while (curr != null) {
            System.out.print(curr.data + " ");
            curr = curr.next;
        }
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        head.next.next.next.next = new Node(5);
        head.next.next.next.next.next = new Node(6);

        head.next.next.next.next.next = head.next.next;

        if (detectLoop(head))
            System.out.println("true");
        else
            System.out.println("false");
        // Find First CodingPatterns.Node of Loop in Linked List
        Node node = getFirstNodeInLoop(head);

        System.out.println("data of fist node in loop :" + node.data);


        //merge k linkedlists
        List<Node> arr = new java.util.ArrayList<>();

        arr.add(new Node(1));
        arr.get(0).next = new Node(3);
        arr.get(0).next.next = new Node(5);
        arr.get(0).next.next.next = new Node(7);

        arr.add(new Node(2));
        arr.get(1).next = new Node(4);
        arr.get(1).next.next = new Node(6);
        arr.get(1).next.next.next = new Node(8);

        arr.add(new Node(0));
        arr.get(2).next = new Node(9);
        arr.get(2).next.next = new Node(10);
        arr.get(2).next.next.next = new Node(11);

        Node head2 = mergeKLists(arr);

        printList(head2);

    }

    //list of heads are givens
    public static Node mergeKLists(List<Node> arr) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(o -> o.data));

        for (int i = 0; i < arr.size(); i++) { // add all heads to pq
            pq.add(arr.get(i));
        }
        Node head = pq.poll();
        Node node = head;
        while (!pq.isEmpty() && node != null) {
            node.next = pq.poll();
            node = node.next;
            if(node.next!=null){
                pq.add(node.next);
            }
        }
        return head;
    }

    static void printList(Node node) {
        while (node != null) {
            System.out.print(node.data);
            if (node.next != null) {
                System.out.print(" -> ");
            }
            node = node.next;
        }
    }

    private static Node getFirstNodeInLoop(Node head) {
        Node slow, fast;
        slow = head;
        fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next; // 2steps

            if (slow == fast) {
                System.out.println("loop exists");
                slow = head;
                //lest find node
                while (slow != fast) {
                    slow = slow.next;
                    fast = fast.next; // 1 step only
                    System.out.println(".");

                }
                return slow;
            }
        }
        return null;
    }

    public static boolean detectLoop(Node head) {
        Node slow = head, fast = head;
        while (fast.next != null && slow != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) {
                return true;
            }
        }
        return false;
    }

    /**
     * two traversals
     */
    public static Node deleteNthNodeFromEnd(Node head, int n) {
        int len = 0;
        if (head == null) {
            return head;
        }
        Node node = head;

        while (node != null) {
            len++;
            node = node.next;
        }
        if (n > len) {
            return null;
        }
        int k = len - n - 1;
        node = head;
        if (k == -1) {
            return head.next;
        }
        while (k != 0) {
            node = node.next;
            k--;
        }
        //node is prev


        return head;
    }

    /*
    single traversal
     */
    public static Node deleteNthNodeFromEnd2(Node head, int n) {
        int len = 0;
        if (head == null) {
            return head;
        }
        Node node = head;

        while (node != null && len == n - 1) {
            len++;
            node = node.next;
        }
        System.out.println(node.data);

        Node prev = node;

        while (node.next != null) {
            prev = prev.next;
            node = node.next;
        }
        //node is prev

        node = prev.next;
        if (node != null)
            prev.next = node.next;

        return head;
    }

}

class Node {
    int data;
    Node next;

    Node(int x) {
        this.data = x;
        this.next = null;
    }
}