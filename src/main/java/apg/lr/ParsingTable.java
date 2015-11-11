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
package apg.lr;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import util.MappedMatrix;
import util.Matrix;

import apg.automata.Dfa;
import apg.automata.Program;
import apg.automata.Range;
import apg.automata.RangeSet;
import apg.syntax.Rule;
import apg.syntax.RuleList;
import apg.syntax.SyntaxError;
import apg.syntax.Token;

public class ParsingTable {
    private RuleList rules;
    private TreeMap<Integer, Dfa> ruleIdToDfa;
    private List<Set<Integer>> firstTable;
    private List<Set<Item>> itemSets;
    private Matrix<Integer> gotoTable;
    private Matrix<Action> actionTable;

    public ParsingTable(RuleList rules) throws SyntaxError {
        this.rules = rules;
        this.ruleIdToDfa = new TreeMap<Integer, Dfa>();
        this.itemSets = new ArrayList<Set<Item>>();
        this.gotoTable = new MappedMatrix<Integer>();
        this.actionTable = new MappedMatrix<Action>();
        int maxRuleId = RuleList.MIN_RULE_ID + this.rules.size() - 1;

        Range alphabet = new Range(0, maxRuleId);
        for (int i = RuleList.MIN_RULE_ID; i <= maxRuleId; i++) {
            Rule r = this.rules.get(i);
            this.ruleIdToDfa.put(r.getId(),
                    new Dfa(this.RpnToProgram(r.tokens()), alphabet));
        }

        int[] firstGraphColor = new int[maxRuleId + 1];
        this.firstTable = new ArrayList<Set<Integer>>();
        for (int i = 0; i <= maxRuleId; i++) {
            this.firstTable.add(null);
        }

        for (int i = 0; i <= Token.END_MARKER; i++) {
            Set<Integer> s = new TreeSet<Integer>();
            s.add(i);
            this.firstTable.set(i, s);
            firstGraphColor[i] = 2;
        }

        for (int i = RuleList.MIN_RULE_ID; i <= maxRuleId; i++) {
            this.first(i, firstGraphColor);
        }

        int unmarkedItemSet = 0;
        Set<Item> itemSet0 = new TreeSet<Item>();
        itemSet0.add(new Item(RuleList.MIN_RULE_ID, 0, Token.END_MARKER));
        this.itemSets.add(this.closure(itemSet0));

        while (unmarkedItemSet < this.itemSets.size()) {
            for (int i = 0; i <= maxRuleId; i++) {
                Set<Item> to = new TreeSet<Item>();
                Set<Item> acceptedItems = new TreeSet<Item>();

                for (Item item : this.itemSets.get(unmarkedItemSet)) {
                    Dfa fa = this.ruleIdToDfa.get(item.ruleId());
                    fa.setState(item.state());

                    if (fa.isMatch()) {
                        acceptedItems.add(item);
                    }

                    if (fa.step(i)) {
                        to.add(new Item(item.ruleId(), fa.getState(), item
                                .terminal()));
                    }
                }

                to = this.closure(to);
                if (to.size() != 0) {
                    int nextState = this.itemSets.indexOf(to);

                    if (nextState == -1) {
                        this.itemSets.add(to);
                        nextState = this.itemSets.size() - 1;
                    }

                    if (i < Token.END_MARKER) {
                        this.actionTable.put(unmarkedItemSet, i, new Action(
                                ActionType.Shift, nextState));
                    } else if (i == Token.END_MARKER) {
                        this.actionTable.put(unmarkedItemSet, i, Action.ACCEPT);
                    } else {
                        this.gotoTable.put(unmarkedItemSet, i, nextState);
                    }
                }

                for (Item item : acceptedItems) {
                    Action action = new Action(ActionType.Reduce, item.ruleId());

                    Action oldAction = this.actionTable.get(unmarkedItemSet,
                            item.terminal());
                    if (oldAction != null && !oldAction.equals(action)) {
                        throw new SyntaxError(
                                "A conflict error occurs, the grammer is not LR(1).");
                    }

                    this.actionTable.put(unmarkedItemSet, item.terminal(),
                            action);
                }
            }

            unmarkedItemSet++;
        }
    }

    public Matrix<Action> getActionTable() {
        return this.actionTable;
    }

    public Matrix<Integer> getGotoTable() {
        return this.gotoTable;
    }

    public Action action(int state, int terminal) {
        if (terminal < 0 || terminal > Token.END_MARKER) {
            throw new IllegalArgumentException(
                    "The second parameter is not a terminal");
        }

        Action a = this.actionTable.get(state, terminal);
        return a == null ? Action.ERROR : a;
    }

    public int go(int state, int nonterminal) {
        int maxRuleId = RuleList.MIN_RULE_ID + this.rules.size() - 1;

        if (nonterminal < RuleList.MIN_RULE_ID || nonterminal > maxRuleId) {
            throw new IllegalArgumentException(
                    "The second parameter is not a nonterminal.");
        }

        Integer nextState = this.gotoTable.get(state, nonterminal);
        return nextState == null ? -1 : nextState;
    }

    private Set<Item> closure(Set<Item> items) {
        Set<Item> closure = new TreeSet<Item>();
        Set<Item> unmarkedItemSet = new TreeSet<Item>(items);
        Queue<Item> unmarkedItems = new LinkedList<Item>(items);

        while (unmarkedItems.size() != 0) {
            Item i = unmarkedItems.poll();
            unmarkedItemSet.remove(i);
            closure.add(i);

            Dfa fa = this.ruleIdToDfa.get(i.ruleId());
            fa.setState(i.state());

            for (int outChar : fa.out()) {
                if (outChar < RuleList.MIN_RULE_ID) {
                    // Don't count nonterminals in.
                    continue;
                }

                Set<Integer> firstSet = new TreeSet<Integer>();

                fa.step(outChar);

                if (fa.isMatch()) {
                    firstSet.add(i.terminal());
                }

                for (int outChar2 : fa.out()) {
                    firstSet.addAll(this.firstTable.get(outChar2));
                }

                for (int first : firstSet) {
                    Item newItem = new Item(outChar, 0, first);

                    if (!closure.contains(newItem)
                            && unmarkedItemSet.add(newItem)) {
                        unmarkedItems.add(newItem);
                    }
                }

                fa.setState(i.state());
            }
        }

        return closure;
    }

    private Set<Integer> first(int nonterminal, int[] graphColor) throws SyntaxError {
        if (graphColor[nonterminal] == 2) {
            return this.firstTable.get(nonterminal);
        }

        if (graphColor[nonterminal] == 1) {
            throw new SyntaxError(String.format(
                    "Cyclic dependency error: '%1$s' depends on itself.",
                    this.rules.get(nonterminal).name()));
        }

        graphColor[nonterminal] = 1;
        Set<Integer> s = new TreeSet<Integer>();

        Dfa fa = this.ruleIdToDfa.get(nonterminal);
        fa.setState(0);

        for (int sub : fa.out()) {
            if (sub == nonterminal) {
                continue;
            }

            if (sub <= Token.END_MARKER) {
                s.add(sub);
            } else {
                s.addAll(this.first(sub, graphColor));
            }
        }

        this.firstTable.set(nonterminal, s);
        graphColor[nonterminal] = 2;
        return s;
    }

    private Program RpnToProgram(Token[] tokens) throws SyntaxError {
        Stack<Program> stack = new Stack<Program>();

        for (Token token : tokens) {
            switch (token.tag()) {
            case Alternation: {
                stack.push(stack.pop().or(stack.pop()));
                break;
            }

            case Char: {
                int charCode = (int) token.character();

                if (charCode > Token.END_MARKER || charCode < 0) {
                    throw new SyntaxError(
                            String.format(
                                    "ABNF doesn't support Unicode, character codes must be in range [0, %1$d].",
                                    Token.END_MARKER));
                }

                stack.push(Program.newChar(charCode));
                break;
            }

            case Concatenation: {
                Program right = stack.pop();
                stack.push(stack.pop().concatenate(right));
                break;
            }

            case Num: {
                RangeSet ranges = new RangeSet();
                ranges.add(token.num());
                stack.push(Program.newCharClass(ranges));
                break;
            }

            case Repetion: {
                stack.push(stack.pop().repeat(token.min(), token.max()));
                break;
            }

            case RuleName: {
                stack.push(Program.newChar(this.rules.get(token.ruleName())
                        .getId()));
                break;
            }

            default:
                throw new IllegalStateException(String.format(
                        "Tag '%1$s' is not supported.", token.tag()));
            }
        }

        if (stack.size() != 1) {
            throw new IllegalStateException(
                    "The tokens parameter is not in Reverse Polish Notation.");
        }

        return stack.pop();
    }
}
