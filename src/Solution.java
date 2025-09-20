public class Solution {
    public static void main(String[] args) {
        System.out.println();
        Node head1 = new Node(1);
        head1.next = new Node(2);
        head1.next.next = new Node(3);
        head1.next.next.next = new Node(4);
        head1.next.next.next.next = new Node(5);

        head1 = deleteNthNodeFromEnd(head1, 5);

        Node curr = head1;
        while (curr != null) {
            System.out.print(curr.data + " ");
            curr = curr.next;
        }

        Node head = new Node(1);
        head.next = new Node(3);
        head.next.next = new Node(4);

        head.next.next.next = head.next;

        if (detectLoop(head))
            System.out.println("true");
        else
            System.out.println("false");

        System.out.println();

    }

    public static boolean detectLoop(Node head) {
        Node slow = head, fast = head;
        while(fast.next != null && slow != null) {
            fast = fast.next.next;
            slow = slow.next;
            if(fast == slow) {
                return true;
            }
        }
        return false;
    }

    public static Node deleteNthNodeFromEnd(Node head, int n) {
        int len = 0;
        if(head == null) {
            return head;
        }
        Node node = head;

        while(node != null) {
            len++;
            node = node.next;
        }
        if(n> len) {
            return  null;
        }
        int k = len - n - 1;
        node = head;
        if(k==-1) {
            return head.next;
        }
        while(k != 0) {
            node = node.next;
            k--;
        }
        //node is prev


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