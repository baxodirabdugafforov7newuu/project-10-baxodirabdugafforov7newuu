import myenum.StringEnum;
import myenum.TokenType;
import utils.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static myenum.StringEnum.*;

public class XMLCompilationEngine implements ICompilationEngine {
    private BufferedWriter bw;
    private JackTokenizer jackTokenizer;

    public XMLCompilationEngine(JackTokenizer jackTokenizer, String test) {
        String outputPath = jackTokenizer.getFilePath();
        try {
            outputPath = outputPath.replace(".jack", "T.xml");
            bw = new BufferedWriter(new FileWriter(outputPath));
            write("<tokens>");
            this.jackTokenizer = jackTokenizer;
            jackTokenizer.advance();
            while (jackTokenizer.hasMoreTokens()) {
                eat(jackTokenizer.tokenType());
            }
            write("</tokens>");
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public XMLCompilationEngine(JackTokenizer jackTokenizer) {
        String outputPath = jackTokenizer.getFilePath();
        try {
            outputPath = outputPath.replace(".jack", ".xml");
            bw = new BufferedWriter(new FileWriter(outputPath));
            this.jackTokenizer = jackTokenizer;
            while (jackTokenizer.hasMoreTokens()) {
                jackTokenizer.advance();
                compileClass();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void compileClass() {
        // TODO: Implement compileClass method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <class> tag.
        // 2. Use eat() to consume the 'class' keyword.
        // 3. Use eat() to consume the class name (identifier).
        // 4. Use eat() to consume the opening '{' symbol.
        // 5. Use a while loop to compile class variable declarations (compileClassVarDec()).
        // 6. Use a while loop to compile subroutine declarations (compileSubroutine()).
        // 7. Use eat() to consume the closing '}' symbol.
        // 8. Write the closing </class> tag.
        write("<class>");
        eat(KEYWORD_CLASS);
        eat(TokenType.IDENTIFIER);
        eat("{");

        while (jackTokenizer.getThisToken().equals("static") || jackTokenizer.getThisToken().equals("field")) {
            compileClassVarDec();
        }

        while (jackTokenizer.getThisToken().equals("constructor") ||
                jackTokenizer.getThisToken().equals("function") ||
                jackTokenizer.getThisToken().equals("method")) {
            compileSubroutine();
        }

        eat("}");
        write("</class>");
    }

    @Override
    public void compileClassVarDec() {
        // TODO: Implement compileClassVarDec method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <classVarDec> tag.
        // 2. Use eat() to consume the keyword (static or field).
        // 3. Call compileType() to handle the type.
        // 4. Use eat() to consume the variable name (identifier).
        // 5. Use a while loop to handle multiple variable names separated by commas.
        // 6. Use eat() to consume the closing ';' symbol.
        // 7. Write the closing </classVarDec> tag.
        write("classVarDec");
        if (jackTokenizer.getThisToken().equals("static") || jackTokenizer.getThisToken().equals("field")){
            eat(jackTokenizer.getThisToken());
        }

        compileType();
        eat(TokenType.IDENTIFIER);
        while (jackTokenizer.getThisToken().equals(",")) {
            eat(",");
            eat(TokenType.IDENTIFIER);
        }
        eat(";");
        write("</classVarDec");
    }

    @Override
    public void compileVarDec() {
        // TODO: Implement compileVarDec method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <varDec> tag.
        // 2. Use eat() to consume the 'var' keyword.
        // 3. Call compileType() to handle the type.
        // 4. Use eat() to consume the variable name (identifier).
        // 5. Use a while loop to handle multiple variable names separated by commas.
        // 6. Use eat() to consume the closing ';' symbol.
        // 7. Write the closing </varDec> tag.
        write("<varDec>");
        eat(KEYWORD_VAR);
        compileType();
        eat(TokenType.IDENTIFIER);
        while (jackTokenizer.getThisToken().equals(",")) {
            eat(",");
            eat(TokenType.IDENTIFIER);
        }
        eat(";");
        write("</varDec>");

    }


    @Override
    public String compileType() {
        // TODO: Implement compileType method
        // Hint: The structure of the implementation should be as follows:
        // 1. Check if the current token is a primitive type (int, char, boolean) or an identifier (class name).
        // 2. Use eat() to consume the type token.
        // 3. Return the type as a string.
        String token = jackTokenizer.getThisToken();

        if (StringEnum.keywords.contains(token)) {
            eat(token);
        } else if (jackTokenizer.tokenType() == TokenType.IDENTIFIER) {
            eat(TokenType.IDENTIFIER);
        } else {
            throw new RuntimeException("Unknown token type: " + token);
        }
        return token;
    }

    @Override
    public void compileSubroutine() {
        // TODO: Implement compileSubroutine method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <subroutineDec> tag.
        // 2. Use eat() to consume the subroutine keyword (constructor, function, method).
        // 3. Call compileType() to handle the return type or use eat() to consume 'void'.
        // 4. Use eat() to consume the subroutine name (identifier).
        // 5. Use eat() to consume the opening '(' symbol.
        // 6. Call compileParameterList() to handle the parameter list.
        // 7. Use eat() to consume the closing ')' symbol.
        // 8. Call compileSubroutineBody() to handle the subroutine body.
        // 9. Write the closing </subroutineDec> tag.
        write("<subroutineDec>");
        if (jackTokenizer.tokenType() == TokenType.KEYWORD && (jackTokenizer.getThisToken().equals(KEYWORD_CONSTRUCTOR) || jackTokenizer.getThisToken().equals(KEYWORD_FUNCTION) || jackTokenizer.getThisToken().equals(KEYWORD_METHOD))) {
            eat(jackTokenizer.getThisToken());
        } else {
            throw new RuntimeException("Unknown token type");
        }

        eat(KEYWORD_VOID);
        eat(TokenType.IDENTIFIER);
        eat("(");
        compileParameterList();
        eat(")");
        compileSubroutineBody();
        write("</subroutineDec>");
    }

    @Override
    public void compileParameterList() {
        // TODO: Implement compileParameterList method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <parameterList> tag.
        // 2. Use a while loop to handle multiple parameters separated by commas.
        // 3. For each parameter, call compileType() and use eat() to consume the parameter name (identifier).
        // 4. Write the closing </parameterList> tag.
        write("<parameterList>");
        while (jackTokenizer.getThisToken().equals(",")) {
            eat(jackTokenizer.tokenType());
            compileType();
            eat(TokenType.IDENTIFIER);
        }
        eat("</parameterList>");
    }


    @Override
    public void compileSubroutineBody() {
        // TODO: Implement compileSubroutineBody method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <subroutineBody> tag.
        // 2. Use eat() to consume the opening '{' symbol.
        // 3. Use a while loop to handle variable declarations (compileVarDec()).
        // 4. Call compileStatements() to handle the statements.
        // 5. Use eat() to consume the closing '}' symbol.
        // 6. Write the closing </subroutineBody> tag.
        write("<subroutineBody>");
        eat("{");
        while (jackTokenizer.getThisToken().equals(KEYWORD_VAR)) {
            compileVarDec();
        }
        compileStatements();
        eat("}");
        eat("</subroutineBody>");
    }


    @Override
    public void compileStatements() {
        // TODO: Implement compileStatements method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <statements> tag.
        // 2. Use a while loop to handle different types of statements (let, if, while, do, return).
        // 3. For each statement type, call the corresponding compile method (compileLet(), compileIf(), compileWhile(), compileDo(), compileReturn()).
        // 4. Write the closing </statements> tag.
        write("<statements>");
        while (jackTokenizer.getThisToken().equals(StringEnum.KEYWORD_LET) ||
                jackTokenizer.getThisToken().equals(StringEnum.KEYWORD_IF) ||
                jackTokenizer.getThisToken().equals(StringEnum.KEYWORD_WHILE) ||
                jackTokenizer.getThisToken().equals(StringEnum.KEYWORD_DO) ||
                jackTokenizer.getThisToken().equals(StringEnum.KEYWORD_RETURN)) {
            switch (jackTokenizer.getThisToken()) {
                case StringEnum.KEYWORD_LET:
                    compileLet();
                    break;
                case StringEnum.KEYWORD_IF:
                    compileIf();
                    break;
                case StringEnum.KEYWORD_WHILE:
                    compileWhile();
                    break;
                case StringEnum.KEYWORD_DO:
                    compileDo();
                    break;
                case StringEnum.KEYWORD_RETURN:
                    compileReturn();
                    break;
                default:
                    throw new IllegalStateException("Unexpected statement type: " + jackTokenizer.getThisToken());
            }
        }
    }


    @Override
    public void compileLet() {
        // TODO: Implement compileLet method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <letStatement> tag.
        // 2. Use eat() to consume the 'let' keyword.
        // 3. Use eat() to consume the variable name (identifier).
        // 4. If the next token is '[', handle array indexing by calling compileExpression().
        // 5. Use eat() to consume the '=' symbol.
        // 6. Call compileExpression() to handle the expression.
        // 7. Use eat() to consume the closing ';' symbol.
        // 8. Write the closing </letStatement> tag.
        write("<letStatement>");
        eat(StringEnum.KEYWORD_LET);
        eat(TokenType.IDENTIFIER);
        if (jackTokenizer.tokenType().equals("[")) {
            eat("[");
            compileExpression();
            eat("]");
        }
        eat("=");
        compileExpression();
        eat(";");
        write("</letStatement>");
    }


    @Override
    public void compileDo() {
        // TODO: Implement compileDo method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <doStatement> tag.
        // 2. Use eat() to consume the 'do' keyword.
        // 3. Use eat() to consume the subroutine call (identifier).
        // 4. If the next token is '(', handle the subroutine call by calling compileExpressionList().
        // 5. If the next token is '.', handle the method call by consuming the class name, '.', and subroutine name, then call compileExpressionList().
        // 6. Use eat() to consume the closing ';' symbol.
        // 7. Write the closing </doStatement> tag.
        write("<doStatement>");
        eat(StringEnum.KEYWORD_DO);
        eat(TokenType.IDENTIFIER);

        if (jackTokenizer.getThisToken().equals("(")) {
            eat("(");
            compileExpressionList();
            eat(")");
        } else if (jackTokenizer.getThisToken().equals(".")) {
            eat(".");
            eat(TokenType.IDENTIFIER);
            eat("(");
            compileExpressionList();
            eat(")");
        }

        eat(";");
        write("</doStatement>");
    }

    @Override
    public int compileExpressionList() {
        // TODO: Implement compileExpressionList method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <expressionList> tag.
        // 2. Use a while loop to handle multiple expressions separated by commas.
        // 3. For each expression, call compileExpression().
        // 4. Write the closing </expressionList> tag.
        // 5. Return the number of expressions in the list.
        write("<expressionList>");
        int expressionCount = 0;
        boolean firstExpression = true;
        while (!jackTokenizer.getThisToken().equals(")")) {
            if (!firstExpression) {
                eat(",");
            }
            compileExpression();
            expressionCount++;
            firstExpression = false;
        }

        write("</expressionList>");
        return expressionCount;
    }

    @Override
    public void compileWhile() {
        // TODO: Implement compileWhile method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <whileStatement> tag.
        // 2. Use eat() to consume the 'while' keyword.
        // 3. Use eat() to consume the opening '(' symbol.
        // 4. Call compileExpression() to handle the condition expression.
        // 5. Use eat() to consume the closing ')' symbol.
        // 6. Use eat() to consume the opening '{' symbol.
        // 7. Call compileStatements() to handle the statements inside the while loop.
        // 8. Use eat() to consume the closing '}' symbol.
        // 9. Write the closing </whileStatement> tag.
        write("<whileStatement>");
        eat(StringEnum.KEYWORD_WHILE);
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
        write("</whileStatement>");
    }


    @Override
    public void compileReturn() {
        // TODO: Implement compileReturn method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <returnStatement> tag.
        // 2. Use eat() to consume the 'return' keyword.
        // 3. If the next token is not ';', call compileExpression() to handle the return expression.
        // 4. Use eat() to consume the closing ';' symbol.
        // 5. Write the closing </returnStatement> tag.

        write("<returnStatement>");
        eat(StringEnum.KEYWORD_RETURN);
        if (!jackTokenizer.getThisToken().equals(";")) {
            compileExpression();
        }
        eat(";");
        write("</returnStatement>");
    }


    @Override
    public void compileIf() {
        // TODO: Implement compileIf method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <ifStatement> tag.
        // 2. Use eat() to consume the 'if' keyword.
        // 3. Use eat() to consume the opening '(' symbol.
        // 4. Call compileExpression() to handle the condition expression.
        // 5. Use eat() to consume the closing ')' symbol.
        // 6. Use eat() to consume the opening '{' symbol.
        // 7. Call compileStatements() to handle the statements inside the if block.
        // 8. Use eat() to consume the closing '}' symbol.
        // 9. If the next token is 'else', handle the else block by consuming 'else', '{', calling compileStatements(), and '}'.
        // 10. Write the closing </ifStatement> tag.
        write("<ifStatement>");
        eat(StringEnum.KEYWORD_IF);
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        if (jackTokenizer.getThisToken().equals(StringEnum.KEYWORD_ELSE)) {
            eat(StringEnum.KEYWORD_ELSE);
            eat("{");
            compileStatements();
            eat("}");
        }
        write("</ifStatement>");
    }

    @Override
    public void compileExpression() {
        // TODO: Implement compileExpression method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <expression> tag.
        // 2. Call compileTerm() to handle the first term.
        // 3. Use a while loop to handle multiple terms separated by operators.
        // 4. For each operator, use eat() to consume the operator and call compileTerm() to handle the next term.
        // 5. Write the closing </expression> tag.
        write("<expression>");
        compileTerm();
        while (jackTokenizer.getThisToken().equals(",")) {
            eat(TokenType.IDENTIFIER);
            compileTerm();
        }
        eat("</expression>");
    }


    @Override
    public void compileTerm() {
        // TODO: Implement compileTerm method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <term> tag.
        // 2. Check the type of the current token and handle accordingly:
        //    - If the token is an integer constant, use eat() to consume it.
        //    - If the token is a string constant, use eat() to consume it.
        //    - If the token is a keyword constant, use eat() to consume it.
        //    - If the token is an identifier, handle variable, array, or subroutine calls.
        //    - If the token is '(', handle the expression inside parentheses by calling compileExpression().
        //    - If the token is a unary operator, use eat() to consume it and call compileTerm() to handle the term.
        // 3. Write the closing </term> tag.
        write("<term>");

        String currentToken = jackTokenizer.getThisToken();


        if (jackTokenizer.tokenType().equals(TokenType.INT_CONSTANT)) {
            eat(TokenType.INT_CONSTANT);
        } else if (jackTokenizer.tokenType().equals(TokenType.STRING_CONSTANT)) {
            eat(TokenType.STRING_CONSTANT);
        } else if (jackTokenizer.tokenType().equals(TokenType.KEYWORD)) {
            if (currentToken.equals("true") || currentToken.equals("false") || currentToken.equals("null") || currentToken.equals("this")) {
                eat(TokenType.KEYWORD);
            }
        } else if (jackTokenizer.tokenType().equals(TokenType.IDENTIFIER)) {
            eat(TokenType.IDENTIFIER);

            if (jackTokenizer.getThisToken().equals("[")) {
                eat("[");
                compileExpression();
                eat("]");
            }

            else if (jackTokenizer.getThisToken().equals("(")) {
                eat("(");
                compileExpressionList();
                eat(")");
            } else if (jackTokenizer.getThisToken().equals(".")) {
                eat(".");
                eat(TokenType.IDENTIFIER);
                eat("(");
                compileExpressionList();
                eat(")");
            }
        } else if (currentToken.equals("(")) {
            eat("(");
            compileExpression();
            eat(")");
        } else if (currentToken.equals("-") || currentToken.equals("~")) {
            eat(currentToken);
            compileTerm();
        }

        write("</term>");

    }

    private void advance() {
        if (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
        }
    }

    private void eat(String str) {
        if (jackTokenizer.getThisToken().equals(str)) {
            // writes <tokenType> str </tokenType>
            write(jackTokenizer.getThisTokenAsTag());
        } else {
            System.out.println("expect " + str + " but get " + jackTokenizer.getThisToken());
          //  throw new RuntimeException("expect " + str + " but get " + jackTokenizer.getThisToken());
        }
        advance();
    }

    private void eat(TokenType tokenType) {
        if (jackTokenizer.tokenType() == tokenType) {
            // writes <tokenType> val </tokenType>
            write(jackTokenizer.getThisTokenAsTag());
        } else {
            System.out.println("expect " + StringUtils.getTokenType(tokenType) + " but get " + StringUtils.getTokenType(jackTokenizer.tokenType()));
           // throw new RuntimeException("expect " + StringUtils.getTokenType(tokenType) + " but get " + StringUtils.getTokenType(jackTokenizer.tokenType()));
        }
        advance();
    }

    private void write(String str) {
        try {
            bw.write(str);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
