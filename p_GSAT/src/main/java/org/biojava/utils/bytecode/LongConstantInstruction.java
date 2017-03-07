/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the terms of the GNU Lesser General Public
 * Licence. This should be distributed with the code. If you do not have a copy, see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual authors. These should be listed
 * in @author doc comments.
 *
 * For more information on the BioJava project and its aims, or to join the biojava-l mailing list,
 * visit the home page at:
 *
 * http://www.biojava.org/
 *
 */
package org.biojava.utils.bytecode;

/**
 * Instructions which retrieve a long from the constant pool.
 *
 * @author Thomas Down
 * @author Matthew Pocock
 */

class LongConstantInstruction implements Instruction {
  private final long val;

  LongConstantInstruction(long val) {
    this.val = val;
  }

  public void writeCode(CodeContext ctx) throws CodeException {
    ctx.writeByte(ByteCode.op_ldc2_w);
    ctx.writeShort(ctx.getConstants().resolveLong(val));
  }

  public int stackDepth() {
    return stackDelta();
  }

  public int stackDelta() {
    return 1;
  }
}
