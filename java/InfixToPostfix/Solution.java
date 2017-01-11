import java.util.Stack;

public class Solution {
    public static void main(String[] args) {
        Solution s = new Solution();
        System.out.println(s.infixpostfix("4 * ( 1 + 2 * ( 9 / 3 ) - 5 )"));
    }

    public String infixpostfix(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        Stack<Token> stack = new Stack<>();
        stack.push(new Token('(', -1));
        StringBuffer result = new StringBuffer();
        for (Token t = null; (t = readToken(str, t == null ? 0 : t.eIndex + 1)) != null;) {
            switch (t.type) {
                case LEFTPAREN:
                    stack.push(t);
                    break;
                case OPERAND:
                    result.append(t);
                    break;
                case OPERATOR:
                    while (!stack.isEmpty() && stack.peek().type == TokenType.OPERATOR
                        && dominate(stack.peek(), t)) {
                        result.append(stack.pop());
                    }
                    stack.push(t);
                    break;
                case RIGHTPAREN:
                    while (!stack.isEmpty() && stack.peek().type != TokenType.LEFTPAREN) {
                        result.append(stack.pop());
                    }
                    if (!stack.isEmpty()) {
                        stack.pop();
                    }
                    break;
                default: throw new RuntimeException("invalid token type");
            }
        }
        return result.toString();
    }

    private boolean dominate(Token t1, Token t2) {
        assert t1.type == TokenType.OPERATOR;
        assert t2.type == TokenType.OPERATOR;
        if (t1.operator == '/' || t1.operator == '*') {
            return true;
        }
        if (t2.operator == '-' || t2.operator == '+') {
            return true;
        }
        return false;
    }

    private Token readToken(String str, int index) {
        while (index < str.length() && str.charAt(index) == ' ') {
            index++;
        }
        if (index == str.length()) {
            return new Token(')', index);
        }
        if (index > str.length()) {
            return null;
        }
        char ch = str.charAt(index);
        if (Character.isDigit(ch)) {
            int num = 0;
            while (index < str.length() && Character.isDigit(ch = str.charAt(index))) {
                num = num * 10 + (ch - '0');
                index++;
            }
            return new Token(num, index - 1);
        } else {
            return new Token(ch, index);
        }
    }

    enum TokenType {
        LEFTPAREN, RIGHTPAREN, OPERAND, OPERATOR
    }

    class Token {
        TokenType type;
        int operand;
        char operator;
        int eIndex;
        public Token(int operand, int eIndex) {
            this.type = TokenType.OPERAND;
            this.operand = operand;
            this.eIndex = eIndex;
        }
        public Token(char ch, int eIndex) {
            switch (ch) {
                case '(':
                    this.type = TokenType.LEFTPAREN;
                    break;
                case ')':
                    this.type = TokenType.RIGHTPAREN;
                    break;
                default:
                    this.type = TokenType.OPERATOR;
                    this.operator = ch;
                    break;
            }
            this.eIndex = eIndex;
        }
        public String toString() {
            switch (type) {
                case LEFTPAREN:
                    return "( ";
                case RIGHTPAREN:
                    return ") ";
                case OPERAND:
                    return operand + " ";
                case OPERATOR:
                    return operator + " ";
                default:
                    return "";
            }
        }
    }
}