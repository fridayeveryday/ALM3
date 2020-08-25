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

    public static OperationPriority firstPriorLvl = new OperationPriority(1, new ArrayList<>(Arrays.asList("(")));
    public static OperationPriority secondPriorLvl = new OperationPriority(2, new ArrayList<>(Arrays.asList("+", "-")));
    public static OperationPriority thirdPriorLvl = new OperationPriority(3, new ArrayList<>(Arrays.asList("*", "/")));
    public static OperationPriority fourthPriorLvl = new OperationPriority(4, new ArrayList<>(Arrays.asList("^")));
    public static OperationPriority fifthPriorLvl = new OperationPriority(5, new ArrayList<>(Arrays.asList("ln", "sin")));
    public static OperationPriority[] priorities = {firstPriorLvl, secondPriorLvl, thirdPriorLvl, fourthPriorLvl, fifthPriorLvl};

    public static ArrayList<String> operations = new ArrayList<>();


    public static String anyNumberInRegex = "[-]?\\d+[.,]?\\d*";
    public static String anyLettersInRegex = "[a-zA-Z]+";

    public static void main(String[] args) {
        System.out.println(Double.parseDouble("+2"));

        //an initialization of operations
        for (OperationPriority op : priorities) {
            operations.addAll(op.operations);
        }
        operations.addAll(Arrays.asList(")"));

        String path = "C:\\Temp\\ALM3.txt";
        String arithmeticExpression = fetchLineFromFile(path);
        if (checkIsEmptyLine(arithmeticExpression)) {

        }
        //todo what if the first number is negative?
        ArrayList<String> expressionComponents = parseExpression(arithmeticExpression);
        if (!checkIsCorrectExpr(expressionComponents)) {
            System.out.println("Incorrect format of expression. Please recheck it.");
        }
        ArrayList<String> elemInRPN = makeRPN(expressionComponents);
        System.out.println(elemInRPN);
        Object res = solveExpressionInRPN(elemInRPN);
        if (res != null)
            System.out.println(res);

    }

    public static boolean checkIsCorrectExpr(ArrayList<String> elemsOfExpr) {

        for (String elem : elemsOfExpr) {
            if (!operations.contains(elem)) {
                try {
                    Double.parseDouble(elem);
                } catch (NumberFormatException exc) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkIsEmptyLine(String arithmeticExpression) {
        // Does the string exist?
        if (arithmeticExpression != null) {
            arithmeticExpression = arithmeticExpression.replaceAll("\\s+", "");
            System.out.println(arithmeticExpression);
            return false;
        } else {
            System.out.println("Empty line. Recheck it.");
            return true;
        }
    }


    public static Number solveExpressionInRPN(ArrayList<String> elemInRPN) {
        Stack<Double> stack4Solving = new Stack<>();
        for (String elem : elemInRPN) {
            if (elem.matches(anyNumberInRegex)) {
                stack4Solving.push(Double.parseDouble(elem));
            } else if (fifthPriorLvl.operations.contains(elem)) {
                double lastValInStack = stack4Solving.pop();
                double resOfFun = 0;
                switch (elem) {
                    case "ln":
                        resOfFun = Math.log(lastValInStack);
                        break;
                    case "sin":
                        resOfFun = Math.sin(lastValInStack);
                        break;
                }
                stack4Solving.push(resOfFun);
            } else if (secondPriorLvl.operations.contains(elem)) {
//                double lastValInStack;
//                if (elem.equals("-")) {
//                    stack4Solving.push(lastValInStack * -1);
//                }
                double lastValInStack = stack4Solving.pop();
                try {

                    Double second2LastValInStack = stack4Solving.pop();
                    double res = 0;
                    switch (elem) {
                        case "+":
                            res = second2LastValInStack + lastValInStack;
                            break;
                        case "-":
                            res = second2LastValInStack - lastValInStack;
                            break;
                    }
                    stack4Solving.push(res);
                } catch (EmptyStackException exc) {
                    if(elem.equals("-"))
                        stack4Solving.push(-1*lastValInStack);
                    else
                        stack4Solving.push(lastValInStack);
                }
            } else {
                double secondNum = stack4Solving.pop();
                double firstNum = stack4Solving.pop();
                double res = 0;
                switch (elem) {
//                    case "+":
//                        res = firstNum + secondNum;
//                        break;
//                    case "-":
//                        res = firstNum - secondNum;
//                        break;
                    case "/":
                        if (secondNum != 0)
                            res = firstNum / secondNum;
                        else {
                            System.out.println("Division by zero.");
                            return null;
                        }
                        break;

                    case "*":
                        res = firstNum * secondNum;
                        break;
                    case "^":
                        res = Math.pow(firstNum, secondNum);
                        break;
                }
                stack4Solving.push(res);
            }
        }
        System.out.println(stack4Solving);
        double last = stack4Solving.pop();
        double second2last = stack4Solving.pop();
        return last + second2last;

    }

    public static ArrayList<String> makeRPN(ArrayList<String> partsOfExpression) {
        ArrayList<String> revPolNot = new ArrayList<>();
        Stack<String> stackOfOper = new Stack<String>();
        for (String elem : partsOfExpression) {
            // if elem is a number than add it to the revPolNot
            if (elem.matches(anyNumberInRegex + "|" + anyLettersInRegex) && !fifthPriorLvl.operations.contains(elem)) {
                revPolNot.add(elem);
            } else {
                if ((stackOfOper.empty() || elem.equals("(")) && !elem.equals(")")) {
                    stackOfOper.push(elem);

                } else if (elem.equals(")")) {
                    while (true) {
                        if (stackOfOper.lastElement().equals("(")) {
                            stackOfOper.pop();
                            if (!stackOfOper.isEmpty() && (fifthPriorLvl.operations.contains(stackOfOper.lastElement()))) {
                                revPolNot.add(stackOfOper.pop());
                            }
                            break;
                        }
                        revPolNot.add(stackOfOper.pop());


                    }
                } else {
                    if (exprPriorLvlIsTopPrior(elem, stackOfOper.lastElement())) {
                        stackOfOper.push(elem);
                    } else {
                        while (!stackOfOper.isEmpty()) {
                            revPolNot.add(stackOfOper.pop());
                        }
                        stackOfOper.push(elem);
                    }
                }
            }
        }
        // make the stack empty
        while (!stackOfOper.isEmpty()) {
            revPolNot.add(stackOfOper.pop());
        }
        return revPolNot;
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
        ArrayList<String> preparedExpression = new ArrayList<>();
        StringBuilder concatTempStr = new StringBuilder();
//        for (int i = 0; i < partsOfExpression.size(); i++) {
//            if(partsOfExpression.get(i).equals("-") &&)
//        }
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
