package donut.spril.instructions;

import donut.spril.SystemInstruction;

/**
 * Created by Gijs on 21-Jun-16.
 */

/**
 * [TestAndSet MemAddr] instruction from sprockell splitted into MemAddr - DirAddr Int,
 *  MemAddr - IndAddr Reg and MemAddr - ImmValue Int.
 */

public class TestAndSetI extends SystemInstruction {

    private int immediateValue;
    private String instruction = "TestAndSet";

    /** TestAndSetI instruction for sprockell for type: MemAddr - ImmValue Int */
    @Deprecated
    public TestAndSetI(int immediateValue) {
        this.immediateValue = immediateValue;
    }

    public int getImmediateValue() {
        return immediateValue;
    }

    @Override @Deprecated
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(instruction)
                .append(" ")
                .append(immediateValue)
        ;
        return builder.toString();
    }
}
