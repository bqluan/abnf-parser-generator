/**
 * Copyright 2011 ABNF Parser Generator Authors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package apg.automata;

/**
 * The instruction that an automaton executes.
 */
public class Instruction {
    private Opcode opcode;
    private Object data;

    private Instruction() {
    }

    public static Instruction newChar(int charCode) {
        Instruction i = new Instruction();
        i.opcode = Opcode.Char;
        i.data = new int[]{charCode};
        return i;
    }

    public static Instruction newCharClass(RangeSet rangeSet) {
        Instruction i = new Instruction();
        i.opcode = Opcode.CharClass;
        i.data = rangeSet;
        return i;
    }

    public static Instruction newJump(int offset) {
        Instruction i = new Instruction();
        i.opcode = Opcode.Jump;
        i.data = new int[]{offset};
        return i;
    }

    public static Instruction newMatch() {
        Instruction i = new Instruction();
        i.opcode = Opcode.Match;
        return i;
    }

    public static Instruction newNop() {
        Instruction i = new Instruction();
        i.opcode = Opcode.Nop;
        return i;
    }

    public static Instruction newSplit(int x, int y) {
        Instruction i = new Instruction();
        i.opcode = Opcode.Split;
        i.data = new int[]{x, y};
        return i;
    }

    public Opcode getOpcode() {
        return this.opcode;
    }

    public int getChar() {
        if (this.opcode != Opcode.Char) {
            throw new IllegalStateException("Opcode must be Char.");
        }

        return ((int[]) this.data)[0];
    }

    public RangeSet getCharClass() {
        if (this.opcode != Opcode.CharClass) {
            throw new IllegalStateException("Opcode must be CharClass.");
        }

        return (RangeSet) this.data;
    }

    public int getOffset() {
        if (this.opcode != Opcode.Jump) {
            throw new IllegalStateException("Opcode must be Jump.");
        }

        return ((int[]) this.data)[0];
    }

    public int getOffsetX() {
        if (this.opcode != Opcode.Split) {
            throw new IllegalStateException("Opcode must be Split.");
        }

        return ((int[]) this.data)[0];
    }

    public int getOffsetY() {
        if (this.opcode != Opcode.Split) {
            throw new IllegalStateException("Opcode must be Split.");
        }

        return ((int[]) this.data)[1];
    }
}
