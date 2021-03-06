package donut;

import donut.spril.Instruction;
import donut.spril.Operator;
import donut.spril.Program;
import donut.spril.Reg;
import donut.spril.instructions.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

/**
 * Created by Ron on 22-6-2016.
 */
public class GeneratorII extends DonutBaseVisitor<Integer> {

    private static final int TRUE = 1;
    private static final int FALSE = 0; // TODO -- Declare globally
    private static final Reg ZEROREG = new Reg("(reg0)");

    private CheckerResult result;

    private int regCount;

    private int lineCount;

    private ParseTreeProperty<Reg> registers;

    private Program program;

    public Program generate(ParseTree tree, CheckerResult result)   {
        this.program = new Program();
        this.result = result;
        this.registers = new ParseTreeProperty<>();
        this.regCount = 1;
        this.lineCount = 0;
        tree.accept(this);
        return program;
    }

    @Override
    public Integer visitProgram(DonutParser.ProgramContext ctx) {
        int begin = lineCount;
        visitChildren(ctx);
        emit(new EndProg());
        return begin;
    }

    @Override
    public Integer visitBlock(DonutParser.BlockContext ctx) {
        int begin = lineCount;
        this.visitChildren(ctx);
        return begin;
    }

    /*
        Stat
     */

    @Override
    public Integer visitAssStat(DonutParser.AssStatContext ctx) {
        int begin = visit(ctx.expr());
        Reg exprReg = reg(ctx.expr());
        int offset = offset(ctx.ID());
        emit(new StoreAI(exprReg, offset));
        return begin;
    }

    @Override
    public Integer visitIfStat(DonutParser.IfStatContext ctx) {
        int begin = visit(ctx.expr());
        Reg r_cmp = registers.get(ctx.expr());
        if (ctx.ELSE() == null)   {
            Instruction branch = emit(new BranchI(r_cmp, -1, true)); // Branch to then
            Instruction jump = emit(new JumpI( -1, true));           // Jump to end
            int thenLine = visit(ctx.block(0));
            this.program.replace(branch, new BranchI(r_cmp, thenLine, true));
            this.program.replace(jump, new JumpI(lineCount, true));
            emit(new Nop());
        } else {
            Instruction branch = emit(new BranchI(r_cmp, -1, true)); // Branch to then
            Instruction jump = emit(new JumpI(-1, true)); // Jump to else
            Instruction endJump = this.emit(new JumpI(-1, true)); // Jump to end
            int thenLine = visit(ctx.block(0));
            int elseLine = visit(ctx.block(1));
            this.program.replace(branch, new BranchI(r_cmp, thenLine, true));
            this.program.replace(jump, new JumpI(elseLine, true));
            this.program.replace(endJump, new JumpI(lineCount, true));
            emit(new Nop());
        }
        return begin;
    }

    @Override
    public Integer visitWhileStat(DonutParser.WhileStatContext ctx) {
        int begin = visit(ctx.expr());
        Reg r_cmp = reg(ctx.expr());
        Instruction branch = emit(new BranchI(r_cmp, -1, true)); // Branch to block
        Instruction jump = emit(new JumpI(-1, false));           // Jump to end
        visit(ctx.block());
        emit(new JumpI(begin, true));  // Jump to compare
        program.replace(branch, new BranchI(r_cmp, 2, false));
        program.replace(jump, new JumpI(lineCount, true));
        emit(new Nop());
        return begin;
    }

    @Override
    public Integer visitDeclStat(DonutParser.DeclStatContext ctx) {
        int begin;
        if (ctx.getChildCount() > 3) {
            begin = visit(ctx.expr());
            Reg resultExpr = reg(ctx.expr());
            emit(new StoreAI(resultExpr, offset(ctx.ID())));
        } else {
            begin = lineCount;
            emit(new StoreAI(ZEROREG, offset(ctx.ID())));
        }
        return begin;
    }

    /*
        Expr
     */

    @Override
    public Integer visitParExpr(DonutParser.ParExprContext ctx) {
        int begin = visitChildren(ctx);
        this.registers.put(ctx, this.registers.get(ctx.expr()));
        return begin;
    }

    @Override
    public Integer visitNumExpr(DonutParser.NumExprContext ctx) {
        emit(new LoadI(Integer.parseInt(ctx.NUM().getText()), reg(ctx)));
        return lineCount - 1;
    }

    @Override
    public Integer visitCharExpr(DonutParser.CharExprContext ctx) {
        return super.visitCharExpr(ctx);
    }

    @Override
    public Integer visitArrayExpr(DonutParser.ArrayExprContext ctx) {
        return super.visitArrayExpr(ctx);
    }

    @Override
    public Integer visitTrueExpr(DonutParser.TrueExprContext ctx) {
        emit(new LoadI(TRUE, reg(ctx)));
        return lineCount - 1;
    }

    @Override
    public Integer visitFalseExpr(DonutParser.FalseExprContext ctx) {
        emit(new LoadI(FALSE, reg(ctx)));
        return lineCount - 1;
    }

    @Override
    public Integer visitIdExpr(DonutParser.IdExprContext ctx) {
        emit(new LoadAI(offset(ctx.ID()), reg(ctx)));
        return lineCount - 1;
    }

    /*
        -- Operations
     */

    @Override
    public Integer visitMultExpr(DonutParser.MultExprContext ctx) {
        int begin = visit(ctx.expr(0));
        visit(ctx.expr(1));
        Reg r0 = reg(ctx.expr(0));
        Reg r1 = reg(ctx.expr(1));
        emit(new Compute(Operator.MUL, r0, r1, reg(ctx)));
        return begin;
    }

    @Override
    public Integer visitMinusExpr(DonutParser.MinusExprContext ctx) {
        int begin = visit(ctx.expr(0));
        visit(ctx.expr(1));
        Reg r0 = reg(ctx.expr(0));
        Reg r1 = reg(ctx.expr(1));
        emit(new Compute(Operator.SUB, r0, r1, reg(ctx)));
        return begin;
    }

    @Override
    public Integer visitPlusExpr(DonutParser.PlusExprContext ctx) {
        int begin = visit(ctx.expr(0));
        visit(ctx.expr(1));
        Reg r0 = reg(ctx.expr(0));
        Reg r1 = reg(ctx.expr(1));
        emit(new Compute(Operator.ADD, r0, r1, reg(ctx)));
        return begin;
    }

    @Override
    public Integer visitCompExpr(DonutParser.CompExprContext ctx) {
        int begin = visit(ctx.expr(0));
        visit(ctx.expr(1));

        Reg r0 = reg(ctx.expr(0));
        Reg r1 = reg(ctx.expr(1));

        if (ctx.compOperator().EQUALS() != null)   {
            emit(new Compute(Operator.EQUAL, r0, r1, reg(ctx)));
        } else if (ctx.compOperator().NOTEQUALS() != null)    {
            emit(new Compute(Operator.NEQ, r0, r1, reg(ctx)));
        } else if (ctx.compOperator().GT() != null)    {
            emit(new Compute(Operator.GT, r0, r1, reg(ctx)));
        } else if (ctx.compOperator().GE() != null)    {
            emit(new Compute(Operator.GTE, r0, r1, reg(ctx)));
        } else if (ctx.compOperator().LT() != null)    {
            emit(new Compute(Operator.LT, r0, r1, reg(ctx)));
        } else if (ctx.compOperator().LE() != null)    {
            emit(new Compute(Operator.LTE, r0, r1, reg(ctx)));
        }
        return begin;
    }

    @Override
    public Integer visitPrfExpr(DonutParser.PrfExprContext ctx) {
        int begin = visit(ctx.expr());
        visit(ctx.prfOperator());

        if (ctx.prfOperator().MINUS() != null)   {
            Reg reg = registers.get(ctx.expr());
            Reg temp = reg(ctx);
            setReg(ctx, reg);
            emit(new LoadI(-1, temp));
            emit(new Compute(Operator.MUL, reg, temp, reg));
        } else {
            Reg temp = reg(ctx);
            emit(new LoadI(1, temp));
            Reg reg = registers.get(ctx.expr());
            Instruction instr = emit(new Compute(Operator.SUB, temp, reg, reg));
            setReg(ctx, reg);
        }
        return  begin;
    }

    @Override
    public Integer visitDivExpr(DonutParser.DivExprContext ctx) {
        int begin = visit(ctx.expr(0));
        visit(ctx.expr(1));
        Reg r0 = reg(ctx.expr(0));
        Reg r1 = reg(ctx.expr(1));
        emit(new Compute(Operator.DIV, r0, r1, reg(ctx)));
        return begin;
    }

    @Override
    public Integer visitPowExpr(DonutParser.PowExprContext ctx) {
        return super.visitPowExpr(ctx);
    }

    @Override
    public Integer visitBoolExpr(DonutParser.BoolExprContext ctx) {
        int begin = visit(ctx.expr(0));
        visit(ctx.expr(1));

        Reg r0 = reg(ctx.expr(0));
        Reg r1 = reg(ctx.expr(1));

        if (ctx.boolOperator().AND() != null)   {
            emit(new Compute(Operator.AND, r0, r1, reg(ctx)));
        } else if (ctx.boolOperator().OR() != null) {
            emit(new Compute(Operator.OR, r0, r1, reg(ctx)));
        } else if (ctx.boolOperator().XOR() != null) {
            emit(new Compute(Operator.XOR, r0, r1, reg(ctx)));
        } else {
            System.out.println("NO SUCH OPERATION IN GENERATOR.VISITBOOLEXPR");
        }
        return begin;
    }

    /*
        Help functions
     */

    private Instruction emit(Instruction instr) {
        program.add(instr);
        lineCount++;
        return instr;
    }

    private int offset(ParseTree node)  {
        return this.result.getOffset(node);
    }

    /** Returns a register for a given parse tree node,
     * creating a fresh register if there is none for that node. */
    private Reg reg(ParseTree node) {
        Reg result = this.registers.get(node);
        if (result == null) {
            result = new Reg("(reg" + this.regCount + ")");
            this.registers.put(node, result);
            this.regCount++;
        }
        return result;
    }

    private void setReg(ParseTree node, Reg reg) {
        this.registers.put(node, reg);
    }


}
