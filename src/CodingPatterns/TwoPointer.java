package CodingPatterns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwoPointer {

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }

    public static void main(String[] args) {
        // 1. Pair Sum of K sorted array
        int[] nums1 = {1, 2, 4, 7, 11, 15};
        pairSum(nums1, 15);

        // 2. Remove Duplicates from sorted array
        int[] nums2 = {1, 1, 2, 2, 3, 4, 4};
        System.out.println("remove duplicate from sorted array: " + Arrays.asList(removeDuplicates(nums2)));

        // 3. Merge Two Sorted Arrays
        int[] arr1 = {1, 3, 5};
        int[] arr2 = {2, 4, 6};
        System.out.println("sorted merge array: " + printArray(mergeSortedArrays(arr1, arr2)));

        // 4. Linked List Cycle Detection
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = head.next; // cycle

        System.out.println("Cycle detected? " + hasCycle(head));

    }

    private static boolean hasCycle(ListNode head) {
        return false;
    }

    private static int[] mergeSortedArrays(int[] arr1, int[] arr2) {
        int i, j;
        int[] result = new int[arr1.length + arr2.length];
        i = 0;
        j = 0;
        int k = 0;
        while (i < arr1.length && j < arr2.length) {
            if (arr1[i] < arr2[j]) {
                result[k] = arr1[i];
                i++;
            } else {
                result[k] = arr2[j];
                j++;
            }
            k++;
        }
        while (i<arr1.length) {
            result[k++] = arr1[i++];
        }
        while (j<arr2.length) {
            result[k++] = arr2[j++];
        }
        return result;
    }


    private static Integer[] removeDuplicates(int[] arr) {
        List<Integer> list = new ArrayList<>();
        list.add(arr[0]);
        for (int i = 1; i < arr.length; i++) {
            if (arr[i - 1] != arr[i]) {
                list.add(arr[i]);
            }
        }
        return list.toArray(list.toArray(new Integer[0]));
    }

    private static String printArray(int[] dijkstra) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i< dijkstra.length; i++) {
            builder.append(dijkstra[i]).append(" ");
        }
        return builder.toString();
    }
    private static void pairSum(int[] arr, int k) {
        // sorted array with sum k
        int left = 0;
        int right = arr.length - 1;
        int product;
        while (left < right) {
            product = arr[left] * arr[right];
            if (k == product) {
                System.out.println("pair found " + arr[left] + "," + arr[right]);
                left++;
                right--;
            } else if (k < product) {
                right--;
            } else {
                left++;
            }
        }
        System.out.println("no more pairs.");
    }

}
