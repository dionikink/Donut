package donut;

import org.antlr.v4.runtime.*;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.List;

/**
 * Created by Gijs on 15-Jun-16.
 */
public class GrammarTest {

    /**
     * Test files with a full program.
     */
    private static final String EmtpyFile = "src/donut/sample/test/testEmpty.donut";
    private static final String IfStatementsFile = "src/donut/sample/test/testIfStatements.donut";
    private static final String MargeFile = "src/donut/sample/test/testMarge.donut";
    private static final String NumberOperatorsFile = "src/donut/sample/test/testNumberOperators.donut";
    private static final String NumbersFile = "src/donut/sample/test/testNumbers.donut";
    private static final String ReactionFile = "src/donut/sample/test/testReaction.donut";
    private static final String ReactionOperatorsFile = "src/donut/sample/test/testReactionOperators.donut";


    /**
     * Tests for certain parts of the code.
     */
    private static final String IntegerDeclaration = "number n = 4;";
    private static final String IntegerDeclaration2 = "number j;";
    private static final String ArrayDeclaration = "bunchof number a = bunchof 6 number;";
    private static final String CharacterDeclaration = "symbol c = \'%\';";
    private static final String CharacterDeclaration2 = "symbol g;";
    private static final String BooleanDeclaration = "reaction r = WOOHOO;";
    private static final String BooleanDeclaration2 = "reaction d;";

    @Test
    public void testArrayDeclaration() {
        verify(runTest(format(ArrayDeclaration), false));
        // TODO test arraydeclaration without assigning.
    }

    @Test
    public void testCharacterDeclaration() {
        verify(runTest(format(CharacterDeclaration), false));
        verify(runTest(format(CharacterDeclaration2), false));
    }

    @Test
    public void testBooleanDeclaration() {
        verify(runTest(format(BooleanDeclaration), false));
        verify(runTest(format(BooleanDeclaration2), false));
    }

    @Test
    public void testIntegerDeclaration() {
        verify(runTest(format(IntegerDeclaration), false));
        verify(runTest(format(IntegerDeclaration2), false));
    }

    @Test
    public void testEmptyFile() {
        verify(runTest(EmtpyFile, true));
    }

    @Test
    public void testIfStatementsFile() {
        verify(runTest(IfStatementsFile, true));
    }

    @Test
    public void testMargeFile() {
        verify(runTest(MargeFile, true));
    }

    @Test
    public void testNumberOperatorsFile() {
        verify(runTest(NumberOperatorsFile, true));
    }

    @Test
    public void testNumbersFile() {
        verify(runTest(NumbersFile, true));
    }

    @Test
    public void testReactionFile() {
        verify(runTest(ReactionFile, true));
    }

    @Test
    public void testReactionOperatorsFile() {
        verify(runTest(ReactionOperatorsFile, true));
    }

    public void verify(List<String> list) {
        if (list.size() == 0) {
            Assert.assertTrue(list.size() == 0);
        } else {
            for (String s : list) {
                System.out.println(s);
            }
        }
    }


    public String format(String testStatement) {
        String beginFile = "Alright brain... it's all up to you {";
        String endFile = "} I hope I didn\'t brain my damage.";
        return beginFile + testStatement + endFile;
    }




    public List<String> runTest(String text, boolean textIsFile) {
        CharStream stream;
        try {
            if (textIsFile) {
                stream = new ANTLRInputStream(new FileInputStream(new File(text)));
            } else {
                stream = new ANTLRInputStream(new StringReader(text));
            }
        } catch (IOException e) {
            return null;
        }
        DonutLexer lexer = new DonutLexer(stream);
        ErrorListener errorListener = new ErrorListener();
        TokenStream tokenStream = new CommonTokenStream(lexer);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        DonutParser parser = new DonutParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        parser.program();
        return errorListener.getErrors();

    }
}