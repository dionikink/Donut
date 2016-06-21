package donut.spril.instructions;

import donut.spril.LocalInstruction;
import donut.spril.Reg;

/**
 * Created by Gijs on 21-Jun-16.
 */

/**
 * [Jump Reg Target] instruction from sprockell splitted into Target - Abs/Rel Int
 *  and Target - Ind Reg (see JumpAI.java).
 */

public class JumpI extends LocalInstruction {

    private int targetAbsInt;
    private int targetRelInt;
    private boolean isAbsolute;

    /** Jump instruction for sprockell type: Target -> Abs Int and Target -> Rel Int.
     *  Indicated by boolean isAbsolute.
     */
    public JumpI(int targetInt, boolean isAbsoluteTarget) {
        if (isAbsoluteTarget) {
            this.targetAbsInt = targetInt;
            this.isAbsolute = true;
        } else {
            this.targetRelInt = targetInt;
            this.isAbsolute = false;
        }
    }

    public int getTarget() {
        if (isAbsolute) {
            return targetAbsInt;
        }
        return targetRelInt;
    }

}
