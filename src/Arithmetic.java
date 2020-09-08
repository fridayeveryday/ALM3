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
//            System.out.println(arithmeticExpression);

        }

        ArrayList<String> expressionComponents = parseExpression(arithmeticExpression);
//        System.out.println(expressionComponents);
        if (!checkIsCorrectExpr(expressionComponents)) {
            System.out.println("Incorrect format of expression. Please recheck it.");
            return;
        }
        ArrayList<String> elemInRPN = makeRPN(expressionComponents);
//        System.out.println(elemInRPN);
        var res = solveExpressionInRPN(elemInRPN);
        if (res != null){
            System.out.print("Result: ");
            System.out.println(res);
        }else {
        }

    }

    public static void printMealy(String state,  String X, String Y){
        System.out.printf("|%-6s| X:%-10s| Y:%-6s|", state, X, Y);
        System.out.println();
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
        System.out.println("States, Xs, Ys for checking expression:");
        System.out.printf("|State | X %-10s| Y %-6s|", " "," ");
        System.out.println("\n______________________________");
        printMealy("S0","","");
        int numOfBraces = 0;
        int i = 0;
        printMealy("S1", "1", "Y1");
        for (i = 0; i < elemsOfExpr.size();) {
            String elem = elemsOfExpr.get(i);
            printMealy("S2","X1","Y2");
            if (elem.equals("(")){
                printMealy("S3","X2","Y3");
                numOfBraces++;
                i++;
                continue;
            }
            else if(elem.equals(")")){
                numOfBraces--;
                printMealy("S3","!X2X3","Y4");
                i++;
                continue;
            }
            printMealy("S3","!X2!X3","-");
            i++;
            printMealy("S1","1","Y5");

        }
        if (numOfBraces != 0){
            System.out.println("The number of opening and closing curly brackets does not match");
            printMealy("S0", "X4", "Y6");
            return false;
        }
        printMealy("S0", "!X4", "Y7");
        System.out.println("The end of checking.");
        System.out.println("\n______________________________");
        return true;
    }

    public static boolean checkIsEmptyLine(String arithmeticExpression) {
        // Does the string exist?
        if (arithmeticExpression != null ) {
            return false;
        } else {
            System.out.println("Empty line. Recheck it.");
            return true;
        }
    }

    public static Number solveExpressionInRPN(ArrayList<String> elemInRPN) {
        System.out.println("States, Xs, Ys for solving expression:");
        System.out.printf("|State | X %-10s| Y %-6s|", " "," ");
        System.out.println("\n______________________________");
        printMealy("S0","","");
        Stack<Double> stack4Solving = new Stack<>();
        printMealy("S0", "1","Y1");
        int i = 0;
        for (i = 0; i < elemInRPN.size();) {
            String elem = elemInRPN.get(i);
            printMealy("S2", "X1","Y2");
            if (elem.matches(anyNumberInRegex)) {
                stack4Solving.push(Double.parseDouble(elem));
                printMealy("S5","X2","Y3");
            }
            else if (fifthPriorLvl.operations.contains(elem)) {
                double lastValInStack = stack4Solving.pop();
                double resOfFun = 0;
                switch (elem) {
                    case "ln":
                        if (lastValInStack > 0)
                            resOfFun = Math.log(lastValInStack);
                        else {
                            System.out.println("The argument of the ln function is not in the range of acceptable values");
                            return null;
                        }
                        break;
                    case "sin":
                        resOfFun = Math.sin(lastValInStack);
                        break;
                }
                stack4Solving.push(resOfFun);
            }
            else {
                double secondNum = stack4Solving.pop();
                double firstNum = stack4Solving.pop();
                double res = 0;
                printMealy("S3","!X2","Y4");
                switch (elem) {
                    case "+":
                        res = firstNum + secondNum;
                        printMealy("S4","X3","Y5");
                        break;
                    case "-":
                        res = firstNum - secondNum;
                        printMealy("S4","!X3X4","Y6");
                        break;
                    case "/":
                        if (secondNum != 0){
                            res = firstNum / secondNum;
                            printMealy("S4","!X3!X4X5X6","Y9");
                        }
                        else {
                            System.out.println("Division by zero.");
                            printMealy("S0","!X3!X4X5!X6","Y7");
                            return null;
                        }
                        break;

                    case "*":
                        res = firstNum * secondNum;
                        printMealy("S4","!X3!X4!X5","Y8");
                        break;
                    case "^":
                        res = Math.pow(firstNum, secondNum);
                        break;
                }
                stack4Solving.push(res);
                printMealy("S5","1","Y10");

            }
            i++;
            printMealy("S1","1","Y11");
        }
        printMealy("S0","!X1","Y12");
        System.out.println("The end of solving.");
        System.out.println("\n______________________________");
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

        char[] expressionByChar = expression.toCharArray();
        ArrayList<String> partsOfExpression = new ArrayList<>();
        StringBuilder operandOrFun = new StringBuilder("");

        // number of braces what is needed to add
        int numOfBracesAfterMinus = 0;
        // show necessity to add braces after an opening brace was added
        char s;
        Stack<String> stackOfBracesAndMinuses = new Stack<String>();

        for (int i = 0; i < expressionByChar.length; i++) {
            s = expressionByChar[i];

            // if s is an operation
            if (operations.contains(Character.toString(s))) {
                if (operandOrFun.length() != 0) {
                    partsOfExpression.add(operandOrFun.toString());
                    operandOrFun.setLength(0);

                    if (numOfBracesAfterMinus > 0 && s != '(') {
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

                            if (numOfBracesAfterMinus > 0 && s != '(') {
                                numOfBracesAfterMinus--;
                                partsOfExpression.add(")");
                            }
                        }
                        partsOfExpression.add(")");

                    }
                }

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
