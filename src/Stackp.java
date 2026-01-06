import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Stackp {
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
        st.add(2); st.add(3); st.add(9);
        System.out.println(sortStack(st));


        //postfix to prefix
        System.out.println(postFixToPrefix("AB+CD-*"));
        //postfix to infix
        System.out.println(postFixToInfix("AB+CD-*"));
        //evaluate postfix
        System.out.println(evalPostfix(Arrays.asList("2", "3", "1", "*", "+", "9", "-")));
        System.out.println(evalPostfix(Arrays.asList("2", "3", "^", "1", "+")));
        // next greater element in the array
        System.out.println(Arrays.toString(nextGreaterElem(Arrays.asList(6,8,0,1,3).toArray(new Integer[0]))));
        //next smaller
        System.out.println(Arrays.toString(nextSmallerElem(Arrays.asList(6,8,0,1,3).toArray(new Integer[0]))));
        //Given array of integer, find the next smaller of next greater element of every element in array
        System.out.println(Arrays.toString(nextSmallerOfNextGreaterElem(Arrays.asList(5, 1, 9, 2, 5, 1, 7).toArray(new Integer[0]))));

        //celebrity problem 
        int[][] mat = { { 1, 1, 0 },
                { 0, 1, 0 },
                { 0, 1, 1 } };
        System.out.println(celebrity(mat));

        // max of min's
        int[] array = {10, 20, 30, 50, 10, 70, 30};
        System.out.println(maxOfMins(array));
    }
/*
find the maximum among minimums of all window sizes
 */
    private static int[] maxOfMins(int[] array) {
        Stack<Integer> st = new Stack<>();
        
        return new int[0];
    }

    private static int celebrity(int[][] mat) {
        Stack<Integer> st = new Stack<>();
        for(int i =0; i< mat.length; i++)
            st.push(i);
        while (st.size()>1) {
            // enter if number of elements are at least 2;  so 2 pops are fine
          Integer a = st.pop();
          Integer b = st.pop();
          if(mat[a][b] == 1) {
              st.push(b);
          }
          else {
              st.push(a);
          }
        }
        int c = st.peek();
        //validate if the only element doesn't know anyone
        for(int i = 0 ;i < mat.length ; i++) {
            if(i != c) {
                if(mat[c][i] == 1) {
                    return 0;
                }
            }
        }
        return c+1 ;
    }

    private static Stack<Integer> sortStack(Stack<Integer> st) {
        Stack<Integer> temp = new Stack<>();
        Integer ele;
        while(!st.isEmpty()) {
            ele = st.pop();
            while(!temp.isEmpty() && temp.peek() > ele) {
                st.push(temp.pop());
            }
            temp.push(ele);
        }

        return temp;
    }

    private static Integer[] nextSmallerOfNextGreaterElem(Integer[] arr) {
        // store next greater in result array but instead of elem store the index
        Stack<Integer> st = new Stack<>();
        Integer[] gresult = new Integer[arr.length];
        Arrays.fill(gresult, -1);
        for(int i = arr.length-1; i>=0 ; i--) {
            while (!st.isEmpty() && arr[st.peek()] <= arr[i]) {
                st.pop();
            }
            if(!st.isEmpty()){
                gresult[i] = st.peek();
            }
            st.push(i);
        }
        //store next smaller in sresult store elem
        st.clear();
        Integer[] sresult = new Integer[arr.length];
        Arrays.fill(sresult, -1);
        for(int i = arr.length-1; i>=0 ; i--) {
            while (!st.isEmpty() && st.peek() >= arr[i]) {
                st.pop();
            }
            if(!st.isEmpty()){
                sresult[i] = st.peek();
            }
            st.push(arr[i]);
        }
        Integer[] result = new Integer[arr.length];
        for(int i = 0;i<arr.length; i++) {
            if(gresult[i] != -1){
                result[i] = sresult[gresult[i]];
            }
            else{
                result[i] = -1;
            }
        }
        return result;
    }

    private static Integer[] nextSmallerElem(Integer[] arr) {
        Stack<Integer> st = new Stack<>();
        Integer[] result = new Integer[arr.length];
        Arrays.fill(result, -1);
        for (int i = arr.length - 1; i >= 0; i--) {
            while (!st.isEmpty() && st.peek() >= arr[i]) {
                st.pop();
            } // maintain monotonic increasing stack
            if(!st.isEmpty()) {
                result[i] = st.peek();
            }
            st.push(arr[i]);
        }
        return result;
    }

    private static Integer[] nextGreaterElem(Integer[] arr) {
        Stack<Integer> st = new Stack<>();
        Integer[] result = new Integer[arr.length];
        Arrays.fill(result, -1);
        for (int i = arr.length - 1; i >= 0; i--) {
            while (!st.isEmpty() && st.peek() <= arr[i]) {
                st.pop();
            }
            if(!st.isEmpty()) {
                result[i] = st.peek();
            }
            st.push(arr[i]);
        }
        return result;
    }

    private static Integer evalPostfix(List<String> list) {
        Stack<String> st = new Stack<>();
        int n = list.size();
        for (int i = 0; i < n; i++) {
            if (isOperator(list.get(i).charAt(0))) {
                char op = list.get(i).charAt(0);
                String op2 = st.pop();
                String op1 = st.pop();
                st.push(evalExp(Integer.valueOf(op1), op, Integer.valueOf(op2)));
            } else {
                st.push(list.get(i));
            }
        }

        return Integer.valueOf(st.pop());
    }

    private static String evalExp(Integer i1, char op, Integer i2) {
        if (op == '+') {
            return Integer.valueOf(i1 + i2).toString();
        }
        if (op == '-') {
            return Integer.valueOf(i1 - i2).toString();
        }
        if (op == '*') {
            return Integer.valueOf(i1 * i2).toString();
        }
        if (op == '^') {
            return Integer.valueOf(Double.valueOf(Math.pow(i1, i2)).intValue()).toString();
        }
        return Integer.valueOf(i1 / i2).toString();
    }


    private static String postFixToInfix(String s) {
        int n = s.length();
        Stack<String> st = new Stack<>();
        for (int i = 0; i < n; i++) {
            if (isOperator(s.charAt(i))) {
                char op = s.charAt(i);
                String op2 = st.pop();
                String op1 = st.pop();
                st.push(op1 + op + op2);
            } else {
                st.push(s.charAt(i) + "");
            }
        }
        StringBuilder res = new StringBuilder();
        for (String i : st) {
            res.append(i);
        }
        return res.toString();
    }

    private static String postFixToPrefix(String s) {
        int n = s.length();
        Stack<String> st = new Stack<>();
        for (int i = 0; i < n; i++) {
            if (isOperator(s.charAt(i))) {
                char op = s.charAt(i);
                String op2 = st.pop();
                String op1 = st.pop();
                st.push(op + op1 + op2);
            } else {
                st.push(s.charAt(i) + "");
            }
        }
        StringBuilder res = new StringBuilder();
        for (String i : st) {
            res.append(i);
        }
        return res.toString();
    }

    private static boolean isOperator(char c) {
        if (c == '+' || c == '-' || c == '/' || c == '*' || c == '^') {
            return true;
        }
        return false;
    }

    private static Stack<Integer> deleteMiddleOfStack(Stack<Integer> st) {
        deleteMiddleOfStackUtil(st, st.size() / 2, 0);
        return st;
    }

    private static void deleteMiddleOfStackUtil(Stack<Integer> st, int mid, int curr) {
        if (curr == mid) {
            st.pop();
            return;
        }
        Integer x = st.pop();
        curr++;
        deleteMiddleOfStackUtil(st, mid, curr);
        st.push(x);
    }

    private static String reverseString(String s) {
        Stack<Character> st = new Stack<>();
        char[] arr = s.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            st.push(arr[i]);
        }
        StringBuilder res = new StringBuilder();
        while (!st.isEmpty()) {
            res.append(st.pop());
        }
        return res.toString();
    }

    private static boolean isBalanced(String s) {
        Stack<Character> st = new Stack<>();
        char[] arr = s.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '(') {
                st.push('(');
            } else {
                if (st.isEmpty()) {
                    return false;
                }
                st.pop();
            }
        }
        return st.isEmpty();
    }
}
