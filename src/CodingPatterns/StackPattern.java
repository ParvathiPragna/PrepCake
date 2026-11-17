package CodingPatterns;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class StackPattern {
    public static void main(String[] args) {
        System.out.println(isBalanced("(("));
        System.out.println(isBalanced(")()"));
        System.out.println(isBalanced("()()"));
        System.out.println(isBalanced("((())())"));

        // reverse string using stack
        System.out.println(reverseString("hiiii"));
        // delete middle element of the stack without using additional data structure
        Stack<Integer> st = new Stack<>();
        List<Integer> arr = Arrays.asList(1, 2, 3, 4, 5, 6);
        st.addAll(arr);
        System.out.println(deleteMiddleOfStack(st));


//        //postfix to prefix
        System.out.println(postFixToPrefix("AB+CD-*"));
//        //postfix to infix
        System.out.println(postFixToInfix("AB+CD-*"));
//        //evaluate postfix
//        System.out.println(evalPostfix(Arrays.asList("2", "3", "1", "*", "+", "9", "-")));
//        System.out.println(evalPostfix(Arrays.asList("2", "3", "^", "1", "+")));
        // next greater element in the array
        System.out.println(Arrays.toString(nextGreaterElem(Arrays.asList(6, 8, 0, 1, 3).toArray(new Integer[0]))));
//        //next smaller
//        System.out.println(Arrays.toString(nextSmallerElem(Arrays.asList(6, 8, 0, 1, 3).toArray(new Integer[0]))));
//        //Given array of integer, find the next smaller of next greater element of every element in array
        System.out.println(Arrays.toString(nextSmallerOfNextGreaterElem(Arrays.asList(5, 1, 9, 2, 5, 1, 7).toArray(new Integer[0]))));

        //celebrity problem
        int[][] mat = {{1, 1, 0}, {0, 1, 0}, {0, 1, 1}};
        System.out.println(celebrity(mat));

        // max of min's
        int[] array = {10, 20, 30, 50, 10, 70, 30};
        System.out.println(maxOfMins(array));

        //Check Redundant Brackets
        System.out.println("has redundant brackets: ((a+b)) " + checkRedundantBrackets("((a+b))"));
        System.out.println("has redundant brackets: (a+b)/c " + checkRedundantBrackets("(a+b)/c"));
        System.out.println("has redundant brackets: ((a)+b) " + checkRedundantBrackets("((a)+b)"));


    }

    private static int[] nextSmallerOfNextGreaterElem(Integer[] arr) {
        int n = arr.length;
        int[] greater = new int[n];
        int[] smaller = new int[n];
        Stack<Integer> st = new Stack<>();
        Arrays.fill(greater, -1);
        //  next greater - store indexes
        for (int i = arr.length - 1; i >= 0; i--) {
            while (!st.isEmpty() && arr[st.peek()] <= arr[i]) {
                st.pop();
            }
            if (!st.isEmpty()) {
                greater[i] = st.peek();
            }
            st.push(i);
        }
        st.clear();
        Arrays.fill(smaller, -1);
        // next smaller of next greater
        for (int i = arr.length - 1; i >= 0; i--) {
            while (!st.isEmpty() && arr[st.peek()] >= arr[i]) {
                st.pop();
            }
            if (!st.isEmpty()) {
                smaller[i] = st.peek();
            }
            st.push(i);
        }
        int[] result = new int[n];
        Arrays.fill(result, -1);
        for (int i = 0; i < n; i++) {
            if (greater[i] != -1) {
                result[i] = smaller[greater[i]] == -1? -1: arr[smaller[greater[i]]];
            }
        }
        return result;
    }


    private static String postFixToPrefix(String s) {
        Stack<String> st = new Stack<>();
        char[] arr = s.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (isOperator(arr[i])) {
                var op2 = st.pop();
                var op1 = st.pop();
                var res = arr[i] + op1 + op2;
                st.push(res);
            } else {
                st.push(String.valueOf(arr[i]));
            }
        }
        return st.pop();
    }

    private static String postFixToInfix(String s) {
        Stack<String> st = new Stack<>();
        char[] arr = s.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (isOperator(arr[i])) {
                var op2 = st.pop();
                var op1 = st.pop();
                var res = op1 + arr[i] + op2;
                st.push(res);
            } else {
                st.push(arr[i] + "");
            }
        }
        return st.pop();
    }

    private static boolean checkRedundantBrackets(String s) {
        Stack<Character> st = new Stack<>();
        int l = s.length();
        for (int i = 0; i < l; i++) {
            if (s.charAt(i) == ')') {
                if (st.peek() == '(') {
                    return true; // no op inside
                } else if (isOperator(st.peek())) {
                    st.pop();
                    if (st.peek() == '(') {
                        st.pop(); // clear the pair
                    }

                }

            } else if (isOperator(s.charAt(i)) || s.charAt(i) == '(') {
                st.push(s.charAt(i));
            }
        }
        return st.isEmpty();
    }

    private static boolean isOperator(Character character) {
        return character == '+' || character == '-' || character == '*' || character == '/';
    }

    private static int maxOfMins(int[] array) {
        return 0;
    }

    private static Integer[] nextGreaterElem(Integer[] arr) {
        Integer[] result = new Integer[arr.length];
        Arrays.fill(result, -1);
        Stack<Integer> st = new Stack<>();
        for (int i = 0; i < arr.length; i++) {
            while (!st.isEmpty() && arr[st.peek()] < arr[i]) {
                result[st.pop()] = arr[i];
            }
            st.push(i);
        }
        return result;
    }

    private static Stack<Integer> deleteMiddleOfStack(Stack<Integer> st) {
        return deleteMiddleOfStackUtil(st, 0, st.size() / 2);
    }

    private static Stack<Integer> deleteMiddleOfStackUtil(Stack<Integer> st, int i, int mid) {
        if (mid == i) {
            st.pop();
            return st;
        }
        int x = st.pop();
        deleteMiddleOfStackUtil(st, i + 1, mid);
        st.push(x);
        return st;
    }

    private static String reverseString(String s) {
        Stack<Character> st = new Stack<>();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            st.push(s.charAt(i));
        }
        StringBuilder sb = new StringBuilder();
        while (!st.isEmpty()) {
            sb.append(st.pop());
        }
        return sb.toString();
    }

    private static boolean isBalanced(String s) {
        Stack<Character> st = new Stack<>();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == '(') {
                st.push(s.charAt(i));
            } else {
                if (st.isEmpty()) {
                    return false;
                }
                st.pop();
            }
        }
        return st.isEmpty();
    }

    private static Integer celebrity(int[][] mat) {
        Stack<Integer> st = new Stack<>();
        int n = mat.length;
        for (int i = 0; i < n; i++) {
            st.push(i);
        }
        while (st.size() > 1) {
            var a = st.pop();
            var b = st.pop();
            if (mat[a][b] == 1) {
                st.push(b);
            } else {
                st.push(a);
            }
        }
        int celeb = st.pop();
        for (int i = 0; i < n; i++) {
            if (i != celeb && mat[celeb][i] == 1) { // if someones is known by celeb then wrong celeb
                return -1;
            }
        }
        return celeb;
    }
}
