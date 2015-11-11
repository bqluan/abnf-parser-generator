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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import util.MappedMatrix;
import util.Matrix;

public class Dfa {
    private Matrix<Integer> transition;
    private Matrix<Integer> reverseTransition;
    private int state;
    private Set<Integer> acceptStates;

    public Dfa(Program prog, Range alphabet) {
        this.transition = new MappedMatrix<Integer>();
        this.reverseTransition = new MappedMatrix<Integer>();
        this.state = 0;
        this.acceptStates = new TreeSet<Integer>();

        List<Set<Integer>> states = new ArrayList<Set<Integer>>();
        states.add(new Nfa(prog).state());
        if (states.get(0).contains(-1)) {
            this.acceptStates.add(0);
        }

        int unmarkedState = 0;
        while (unmarkedState < states.size()) {
            for (int c = alphabet.min(); c <= alphabet.max(); c++) {
                Nfa fa = new Nfa(prog, states.get(unmarkedState));
                fa.step(c);

                Set<Integer> to = fa.state();
                if (to.size() != 0) {
                    int nextState = states.indexOf(to);
                    if (nextState == -1) {
                        states.add(to);
                        nextState = states.size() - 1;

                        if (to.contains(-1)) {
                            this.acceptStates.add(nextState);
                        }
                    }

                    this.transition.put(unmarkedState, c, nextState);
                    this.reverseTransition.put(nextState, c, unmarkedState);
                }
            }

            unmarkedState++;
        }
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Matrix<Integer> getTransitionTable() {
        return this.transition;
    }
    
    public Set<Integer> getAcceptStates() {
        return this.acceptStates;
    }

    public boolean isMatch() {
        return this.acceptStates.contains(this.state);
    }

    public boolean step(int character) {
        Integer nextState = this.transition.get(this.state, character);
        if (nextState == null) {
            return false;
        }

        this.state = nextState;
        return true;
    }

    public List<Integer> out() {
        List<Integer> values = new ArrayList<Integer>();

        for (int i = 0; i < this.transition.colSize(); i++) {
            Integer nextState = this.transition.get(this.state, i);
            if (nextState != null) {
                values.add(i);
            }
        }

        return values;
    }

    public List<Integer> in() {
        List<Integer> values = new ArrayList<Integer>();

        for (int i = 0; i < this.reverseTransition.colSize(); i++) {
            Integer nextState = this.reverseTransition.get(this.state, i);
            if (nextState != null) {
                values.add(i);
            }
        }

        return values;
    }
}
