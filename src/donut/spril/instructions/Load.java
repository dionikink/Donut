package donut.spril.instructions;

import donut.spril.LocalInstruction;
import donut.spril.Reg;

/**
 * Created by Gijs on 21-Jun-16.
 */

/**
 * [Load MemAddr Reg] instruction from sprockell splitted into MemAddr - DirAddr Int,
 *  MemAddr - IndAddr Reg and MemAddr - ImmValue Int.
 */

public class Load extends LocalInstruction {

    private Reg registerAddress;
    private Reg inRegister;

    /** Load instruction for sprockell type: MemAddr -> IndAddr Reg */
    public Load(Reg registerAddress, Reg inRegister) {
        this.registerAddress = registerAddress;
        this.inRegister = inRegister;
    }

    public Reg getRegisterAddress() {
        return registerAddress;
    }

    public Reg getInRegister() {
        return inRegister;
    }
}
