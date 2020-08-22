import sun.plugin.javascript.navig.Array;

import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class OperationPriority {
    public int lvlPrior;
    public ArrayList<String> operations;

    public OperationPriority(int lvlPrior, ArrayList<String> operations) {
        this.lvlPrior = lvlPrior;
        this.operations = operations;
    }
}

public class Arithmetic {

    public static ArrayList<String> firstLvlPrior = new ArrayList<>(Arrays.asList
            ("(")
    );
    public static ArrayList<String> secondLvlPrior = new ArrayList<>(Arrays.asList
            ("+", "-")
    );
    public static ArrayList<String> thirdLvlPrior = new ArrayList<>(Arrays.asList
            ("*", "/")
    );
    public static ArrayList<String> fourthLvlPrior = new ArrayList<>(Arrays.asList
            ("^")
    );
    public static ArrayList<String> fifthLvlPrior = new ArrayList<>(Arrays.asList
            ("ln", "sin")
    );
    public static ArrayList<String> operations = new ArrayList<>(Arrays.asList
            ("+", "-", "*", "/", "^", "(", ")", ";")
    );
    public static OperationPriority secondPriorLvl = new OperationPriority(2, new ArrayList<>(Arrays.asList("+", "-")));
    public static OperationPriority thirdPriorLvl = new OperationPriority(3, new ArrayList<>(Arrays.asList("*", "/")));
    public static OperationPriority fourthPriorLvl = new OperationPriority(3, new ArrayList<>(Arrays.asList("^")));
    public static OperationPriority[] priorities = {secondPriorLvl, thirdPriorLvl, fourthPriorLvl};

    public static void main(String[] args) {

        System.out.println("qwe".matches("[-]?\\d+[.,]?\\d*|[a-zA-Z]+"));
        String path = "C:\\Temp\\ALM3.txt";
        String arithmeticExpression = fetchLineFromFile(path);
        if (arithmeticExpression != null) {
            arithmeticExpression = arithmeticExpression.replaceAll("\\s+", "");
            System.out.println(arithmeticExpression);
        } else {
            System.out.println("Empty line. Recheck it.");
            return;
        }
        ArrayList<String> expressionComponents = parseExpression(arithmeticExpression);
        System.out.println(makeRPN(expressionComponents));

    }

    public static String makeRPN(ArrayList<String> partsOfExpression) {
//        StringBuilder revPolNot = new StringBuilder("");
        ArrayList<String> revPolNot= new ArrayList<>();
        Stack<String> stackOfOper = new Stack<String>();
        for (String elem : partsOfExpression) {
            // if elem is a number than add it to the revPolNot
            if (elem.matches("[-]?\\d+[.,]?\\d*|[a-zA-Z]+")) {
                revPolNot.add(elem);
            } else {
                if (stackOfOper.empty() || elem.equals("(")) {
                    stackOfOper.push(elem);
                    continue;
                }
                else if (elem.equals(")")){
                    while(true){
                        revPolNot.add(stackOfOper.pop());
                        if (stackOfOper.lastElement().equals("(")){
                            stackOfOper.pop();
                            break;
                        }

                    }
                }
                else {
                    if(exprPriorLvlIsTopPrior(elem, stackOfOper.lastElement())){
                        stackOfOper.push(elem);
                    }else{
                        revPolNot.add(stackOfOper.pop());
                        stackOfOper.push(elem);
                    }
                }
            }
        }
        // make the stack empty
        while (!stackOfOper.isEmpty()){
            revPolNot.add(stackOfOper.pop());
        }
        return revPolNot.toString();
    }

    public static boolean exprPriorLvlIsTopPrior(String expressionOperation, String stackOperation) {
        int exprOperPrior = 0;
        int stackOperPrior = 0;
        for (OperationPriority op : priorities) {
            if (op.operations.contains(expressionOperation))
                exprOperPrior = op.lvlPrior;
        }
        for (OperationPriority op : priorities) {
            if (op.operations.contains(stackOperation))
                stackOperPrior = op.lvlPrior;
        }
        return exprOperPrior > stackOperPrior;
    }

    public static ArrayList<String> parseExpression(String expression) {
        char[] expressionByChar = expression.toCharArray();
        ArrayList<String> partsOfExpression = new ArrayList<>();
        StringBuilder operandOrFun = new StringBuilder("");
        for (char s : expressionByChar) {
            // if s is an operation
            if (operations.contains(Character.toString(s))) {
                if (operandOrFun.length() != 0) {
                    partsOfExpression.add(operandOrFun.toString());
                    operandOrFun.setLength(0);
                }
                partsOfExpression.add(Character.toString(s));
            } else {
                operandOrFun.append(s);
            }
        }
        if (operandOrFun.length() != 0) {
            partsOfExpression.add(operandOrFun.toString());
            operandOrFun.setLength(0);
        }
        System.out.println(partsOfExpression);
        return partsOfExpression;
    }

    public static String fetchLineFromFile(String path) {
        File file = new File(path);
        Scanner scanner;
        String string;
        try {
            scanner = new Scanner(file);
            string = scanner.nextLine();
        } catch (FileNotFoundException | NoSuchElementException e) {
            return null;
        }
        return string;
    }
}
