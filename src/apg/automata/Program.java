/**
 * Copyright 2011 ABNF Parser Generator Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package apg.automata;

/**
 * The program that an automaton executes.
 */
public class Program {
    private static final Instruction MATCH = Instruction.newMatch();
    private static final Instruction NOP = Instruction.newNop();
    private Instruction[] instructions;

    private Program() {
    }

    public static Program newChar(int charCode) {
        Program p = new Program();
        p.instructions = new Instruction[] {
                Instruction.newChar(charCode), MATCH };
        return p;
    }

    public static Program newCharClass(RangeSet ranges) {
        Program p = new Program();
        p.instructions = new Instruction[] {
                Instruction.newCharClass(ranges), MATCH };
        return p;
    }

    public int size() {
        return this.instructions.length;
    }

    public Instruction get(int index) {
        return this.instructions[index];
    }

    public Program concatenate(Program that) {
        if (Integer.MAX_VALUE - this.instructions.length
                < that.instructions.length - 1) {
            throw new OutOfMemoryError();
        }

        Program prog = new Program();
        prog.instructions = new Instruction[
                that.instructions.length + this.instructions.length - 1];

        for (int i = 0; i < this.instructions.length - 1; i++) {
            prog.instructions[i] = this.instructions[i];
        }

        for (int i = 0; i < that.instructions.length; i++) {
            prog.instructions[this.instructions.length - 1 + i] =
                    that.instructions[i];
        }

        return prog;
    }

    public Program or(Program that) {
        if (Integer.MAX_VALUE - this.instructions.length - 1
                < that.instructions.length) {
            throw new OutOfMemoryError();
        }

        Program prog = new Program();
        prog.instructions = new Instruction[
                this.instructions.length + that.instructions.length + 1];

        // split L1, L2
        prog.instructions[0] = Instruction.newSplit(
                1, this.instructions.length + 1);

        // codes for e1
        for (int i = 0; i < this.instructions.length - 1; i++) {
            prog.instructions[1 + i] = this.instructions[i];
        }

        // jump L3
        prog.instructions[this.instructions.length] =
                Instruction.newJump(that.instructions.length);

        // codes for e2
        for (int i = 0; i < that.instructions.length; i++) {
            prog.instructions[this.instructions.length + 1 + i] =
                    that.instructions[i];
        }

        return prog;
    }

    public Program plus() {
        if (Integer.MAX_VALUE - this.instructions.length < 1) {
            throw new OutOfMemoryError();
        }

        Program prog = new Program();
        prog.instructions = new Instruction[this.instructions.length + 1];

        for (int i = 0; i < this.instructions.length - 1; i++) {
            prog.instructions[i] = this.instructions[i];
        }

        // split L1, L3
        prog.instructions[this.instructions.length - 1] =
                Instruction.newSplit(1 - this.instructions.length, 1);

        prog.instructions[this.instructions.length] = Instruction.newMatch();

        return prog;
    }

    public Program question() {
        if (Integer.MAX_VALUE - this.instructions.length < 1) {
            throw new OutOfMemoryError();
        }

        Program prog = new Program();
        prog.instructions = new Instruction[this.instructions.length + 1];

        // split L1, L2
        prog.instructions[0] = Instruction.newSplit(
                1, this.instructions.length);

        for (int i = 0; i < this.instructions.length; i++) {
            prog.instructions[1 + i] = this.instructions[i];
        }

        return prog;
    }

    public Program repeat(int min, int max) {
        min = Math.max(0, min);

        if (max == -1) {
            if (min == 0) {
                return this.star();
            }

            if (min == 1) {
                return this.plus();
            }

            if ((Integer.MAX_VALUE - 1) / (min - 1)
                    < this.instructions.length - 1) {
                throw new OutOfMemoryError();
            }

            Program prog = new Program();
            prog.instructions = new Instruction[
                    (this.instructions.length - 1) * (min - 1) + 1];

            for (int i = 0; i < min - 1; i++) {
                for (int j = 0; j < this.instructions.length - 1; j++) {
                    prog.instructions[(this.instructions.length - 1) * i + j] =
                            this.instructions[j];
                }
            }

            prog.instructions[(this.instructions.length - 1) * (min - 1)] =
                    Instruction.newMatch();

            return prog.concatenate(this.plus());
        }

        Program prefix = new Program();
        prefix.instructions = new Instruction[] { NOP, MATCH };

        Program suffix = new Program();
        suffix.instructions = new Instruction[] { NOP, MATCH };

        if (min > 0) {
            if ((Integer.MAX_VALUE - 1) / min < this.instructions.length - 1) {
                throw new OutOfMemoryError();
            }

            prefix = new Program();
            prefix.instructions = new Instruction[
                    (this.instructions.length - 1) * min + 1];

            for (int i = 0; i < min; i++) {
                for (int j = 0; j < this.instructions.length - 1; j++) {
                    prefix.instructions[(this.instructions.length - 1) * i + j]
                            = this.instructions[j];
                }
            }

            prefix.instructions[(this.instructions.length - 1) * min] =
                    Instruction.newMatch();
        }

        if (max - min > 0) {
            Program prog = this.question();
            for (int i = 0; i < max - min; i++) {
                suffix = suffix.concatenate(prog);
            }
        }

        return prefix.concatenate(suffix);
    }

    public Program star() {
        if (Integer.MAX_VALUE - this.instructions.length < 2) {
            throw new OutOfMemoryError();
        }

        Program prog = new Program();
        prog.instructions = new Instruction[this.instructions.length + 2];

        // split L2, L3
        prog.instructions[0] = Instruction.newSplit(
                1, this.instructions.length + 1);

        for (int i = 0; i < this.instructions.length - 1; i++) {
            prog.instructions[1 + i] = this.instructions[i];
        }

        // jump L1
        prog.instructions[this.instructions.length] = Instruction.newJump(
                -1 * this.instructions.length);

        prog.instructions[this.instructions.length + 1] =
                Instruction.newMatch();

        return prog;
    }
}