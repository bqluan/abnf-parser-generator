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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

public class Nfa {
    private Program prog;
    private Queue<Integer> state;

    public Nfa(Program prog) {
        this.prog = prog;
        this.state = new LinkedList<Integer>();
        this.state.add(0);
        this.closure();
    }

    public Nfa(Program prog, Set<Integer> state) {
        this.prog = prog;
        this.state = new LinkedList<Integer>(state);
        this.closure();
    }

    public void step(int character) {
        Queue<Integer> pending = new LinkedList<Integer>();

        while (this.state.size() != 0) {
            int ip = this.state.poll();

            if (ip == -1) {
                continue;
            }

            Instruction i = this.prog.get(ip);

            switch (i.getOpcode()) {
                case Char:
                    if (i.getChar() == character) {
                        pending.add(ip + 1);
                    }

                    break;

                case CharClass:
                    if (i.getCharClass().contains(new Range(character, character))) {
                        pending.add(ip + 1);
                    }

                    break;

                default:
                    throw new IllegalStateException();
            }
        }

        this.state = pending;
        this.closure();
    }

    public Set<Integer> state() {
        return new TreeSet<Integer>(this.state);
    }

    public boolean isMatch() {
        return this.state.contains(-1);
    }

    public List<Integer> out() {
        List<Integer> values = new ArrayList<Integer>();

        for (int ip : this.state) {
            if (ip == -1) {
                continue;
            }

            Instruction i = this.prog.get(ip);

            switch (i.getOpcode()) {
                case Jump:
                case Match:
                case Nop:
                case Split:
                    break;

                case Char:
                    values.add(i.getChar());
                    break;

                case CharClass:
                    for (Range r : i.getCharClass()) {
                        for (int j = r.min(); j <= r.max(); j++) {
                            values.add(j);
                        }
                    }

                    break;

                default:
                    throw new IllegalStateException();
            }
        }

        return values;
    }

    private void closure() {
        Queue<Integer> pending = new LinkedList<Integer>();
        boolean isMatch = false;

        while (this.state.size() != 0) {
            int ip = this.state.poll();

            while (ip >= 0 && ip < this.prog.size()) {
                Instruction i = this.prog.get(ip);
                boolean stopThread = false;

                switch (i.getOpcode()) {
                    case Char:
                    case CharClass:
                        pending.add(ip);
                        stopThread = true;
                        break;

                    case Jump:
                        ip += i.getOffset();
                        break;

                    case Match:
                        if (!isMatch) {
                            pending.add(-1);
                            isMatch = true;
                        }

                        stopThread = true;
                        break;

                    case Nop:
                        ip++;
                        break;

                    case Split:
                        this.state.add(ip + i.getOffsetY());
                        ip += i.getOffsetX();
                        break;

                    default:
                        throw new IllegalStateException();
                }

                if (stopThread) {
                    break;
                }
            }
        }

        this.state = pending;
    }
}
