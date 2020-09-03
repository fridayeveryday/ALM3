
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
        System.out.println(-9.0 / -(2 * 4)
        );

        //an initialization of operations
        for (OperationPriority op : priorities) {
            operations.addAll(op.operations);
        }
        operations.addAll(Arrays.asList(")"));

        String path = "C:\\Temp\\ALM3.txt";
        String arithmeticExpression = fetchLineFromFile(path);
        if (checkIsEmptyLine(arithmeticExpression)) {
            return;
        } else {
            arithmeticExpression = arithmeticExpression.replaceAll("\\s+", "");
            System.out.println(arithmeticExpression);
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
            } else {
                double secondNum = stack4Solving.pop();
                double firstNum = stack4Solving.pop();
                double res = 0;
                switch (elem) {
                    case "+":
                        res = firstNum + secondNum;
                        break;
                    case "-":
                        res = firstNum - secondNum;
                        break;
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
//        System.out.println(stack4Solving);
//        double last = stack4Solving.pop();
//        double second2last = stack4Solving.pop();
//        return last + second2last;
        return stack4Solving.pop();

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
        String positiveNumInRegex = "[+]?\\d+[.,]?\\d*";
        ArrayList<String> signsBeforeNegSign = new ArrayList<>(Arrays.asList("+", "(", "/", "*", "^"));

        char[] expressionByChar = expression.toCharArray();
        ArrayList<String> partsOfExpression = new ArrayList<>();
        StringBuilder operandOrFun = new StringBuilder("");
        ArrayList<String> positNegatSign = new ArrayList<>(secondPriorLvl.operations);
//        int i = 0;
//        while (i < expressionByChar.length) {
//            operandOrFun.append(expressionByChar[i]);
//            if (operations.contains(operandOrFun.toString())) {
//
//                if (operandOrFun.length() != 0) {
//                    partsOfExpression.add(operandOrFun.toString());
//                    operandOrFun.setLength(0);
//                }
//                i++;
//            } else {
//
//            }
//        }
//        for (int i = 0; i < expressionByChar.length; ) {
//            char oneChar = expressionByChar[i];
//            // if s is an operation
//            if (operations.contains(Character.toString(oneChar))) {
//                operandOrFun.append(expressionByChar[i]);
//                i++;
//                oneChar = expressionByChar[i];
//                do {
//                    operandOrFun.append(expressionByChar[i]);
//                    i++;
////                    oneChar = expressionByChar[i];
//                }
//                while ((i < expressionByChar.length) && !operations.contains(Character.toString(expressionByChar[i])));
//                if (operandOrFun.length() != 0) {
//                    if (operandOrFun.toString().matches(positiveNumInRegex)) {
//                        operandOrFun.deleteCharAt(0);
//                    }
//                    partsOfExpression.add(operandOrFun.toString());
//                    if (i < expressionByChar.length) partsOfExpression.add("+");
//                    operandOrFun.setLength(0);
//                }
////                partsOfExpression.add(Character.toString(s));
//            } else {
//                operandOrFun.append(oneChar);
//            }
//        }


//        int i = 0;
//        char oneChar = expressionByChar[i];
//        do {
//            oneChar = expressionByChar[i];
//            operandOrFun.append(oneChar);
//
//            i++;
//        }while (i<expressionByChar.length);
//        for (char s : expressionByChar) {
//            if (s == '+' || s == '-'){
//                operandOrFun.append(s);
//                continue;
//            }
//            // if s is an operation
//            if (operations.contains(Character.toString(s))) {
//                partsOfExpression.add(operandOrFun.toString());
//                operandOrFun.setLength(0);
//            } else {
//                operandOrFun.append(s);
//            }
//        }
//        if (operandOrFun.length() != 0) {
//            partsOfExpression.add(operandOrFun.toString());
//            operandOrFun.setLength(0);
//        } else {
//            System.out.println("Incorrect line.");
//        }
//        System.out.println(partsOfExpression);
//        ArrayList<String> preparedExpression = new ArrayList<>();
//        StringBuilder concatTempStr = new StringBuilder();
        // number of braces what is needed to add
        int numOfBracesAfterMinus = 0;
        // show necessity to add braces after an opening brace was added
        boolean necessity2AddBraces = false;
        char s;
        Stack<String> stackOfBracesAndMinuses = new Stack<String>();

        for (int i = 0; i < expressionByChar.length; i++) {
            s = expressionByChar[i];

            // if s is an operation
            if (operations.contains(Character.toString(s))) {
                if (operandOrFun.length() != 0) {
                    partsOfExpression.add(operandOrFun.toString());
                    operandOrFun.setLength(0);

                    if (numOfBracesAfterMinus > 0 && s!='(') {
                        numOfBracesAfterMinus--;
                        partsOfExpression.add(")");
                    }
                }

                if (s == '-' && (expressionByChar[i + 1] == '('
                        || expressionByChar[i + 1] == 'l'
                        || expressionByChar[i + 1] == 's'
                        || expressionByChar[i + 1] == 'c')) {
//                    numOfBracesAfterMinus++;
                    stackOfBracesAndMinuses.push(Character.toString(s));
                    stackOfBracesAndMinuses.push(Character.toString(expressionByChar[i + 1]));
                    String lastELemInPOfExp = "";
                    if (!partsOfExpression.isEmpty())
                        lastELemInPOfExp = partsOfExpression.get(partsOfExpression.size() - 1);
                    if (lastELemInPOfExp.matches(anyNumberInRegex) || lastELemInPOfExp.equals(")")) {
                        partsOfExpression.add("+");
                    }
                    partsOfExpression.add("(");
                    partsOfExpression.add("-1");
                    partsOfExpression.add("*");
                    continue;
                } else if (s == ')') {
                    if (!stackOfBracesAndMinuses.empty())
                        stackOfBracesAndMinuses.pop();
                    String lastElem = "";
                    if (!stackOfBracesAndMinuses.empty())
                        lastElem = stackOfBracesAndMinuses.pop();
                    if (lastElem.equals("-")) {
                        // if operandOrFun is a string like "ln", "sin" or not one digit number
                        if (operandOrFun.length() != 0) {
                            partsOfExpression.add(operandOrFun.toString());
                            operandOrFun.setLength(0);

                            if (numOfBracesAfterMinus > 0 && s!='(') {
                                numOfBracesAfterMinus--;
                                partsOfExpression.add(")");
                            }
                        }
//                        if (numOfBracesAfterMinus > 0) {
//                            numOfBracesAfterMinus--;
                            partsOfExpression.add(")");
//                        }
                    }
                }
                // if operandOrFun is a like "log", "sin"


                if (s == '-') {
                    String lastELemInPOfExp = "";
                    if (!partsOfExpression.isEmpty())
                        lastELemInPOfExp = partsOfExpression.get(partsOfExpression.size() - 1);
                    if (lastELemInPOfExp.matches(anyNumberInRegex) || lastELemInPOfExp.equals(")")) {
                        partsOfExpression.add("+");
                    }
                    numOfBracesAfterMinus++;
                    partsOfExpression.add("(");
                    partsOfExpression.add("-1");
                    partsOfExpression.add("*");
                } else {

                    partsOfExpression.add(Character.toString(s));
                }
            } else {

                operandOrFun.append(s);
            }
        }
        if (operandOrFun.length() != 0) {
            partsOfExpression.add(operandOrFun.toString());
            operandOrFun.setLength(0);
        }

        while (numOfBracesAfterMinus > 0) {
            partsOfExpression.add(")");
            numOfBracesAfterMinus--;
        }
        System.out.println(partsOfExpression);
        ArrayList<String> preparedExpression = new ArrayList<>();
        StringBuilder concatTempStr = new StringBuilder();
//        for (int i = 0; i < partsOfExpression.size(); i++) {
//            if (
//                    (
//                            (
//                                    // if negative num is at begin???
//                                    signsBeforeNegSign.contains(partsOfExpression.get(i))
//                                            || i == 0
//                            )
//                                    && partsOfExpression.get(i + 1).equals("-")
//                                    && partsOfExpression.get(i + 2).matches(anyNumberInRegex)
//                    )
//                            ||
//
//            ) {
//                preparedExpression.add(partsOfExpression.get(i));
//                concatTempStr.append(partsOfExpression.get(i + 1));
//                concatTempStr.append(partsOfExpression.get(i + 2));
//                preparedExpression.add(concatTempStr.toString());
//                i += 2;
//                concatTempStr.setLength(0);
//            } else {
//                preparedExpression.add(partsOfExpression.get(i));
//            }
//        }
//        System.out.println(preparedExpression);
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

