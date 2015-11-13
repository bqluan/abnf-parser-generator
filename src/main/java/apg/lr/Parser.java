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
package apg.lr;

import apg.automata.Dfa;
import apg.syntax.RuleList;
import apg.syntax.SyntaxError;
import apg.syntax.Token;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

public class Parser {
    private RuleList rules;
    private Dfa[] reverseDfas;
    private ParsingTable parsingTable;

    public Parser(RuleList rules, Dfa[] reverseDfas, ParsingTable parsingTable) {
        this.rules = rules;
        this.reverseDfas = reverseDfas;
        this.parsingTable = parsingTable;
    }

    public RuleList parse(InputStream input) throws IOException, SyntaxError {
        Stack<Integer> stateStack = new Stack<Integer>();
        Stack<Integer> inputStack = new Stack<Integer>();
        Stack<Object> objectStack = new Stack<Object>();
        stateStack.push(0);

        int c = input.read();
        if (c == -1) {
            c = Token.END_MARKER;
        }

        while (true) {
            Action action = this.parsingTable.action(stateStack.peek(), c);

            if (action.type() == ActionType.Shift) {
                stateStack.push(action.state());
                inputStack.push(c);
                objectStack.push(c);

                c = input.read();
                if (c == -1) {
                    c = Token.END_MARKER;
                }
            } else if (action.type() == ActionType.Reduce) {
                Dfa fa = this.reverseDfas[action.ruleId()
                        - RuleList.MIN_RULE_ID];
                fa.setState(0);

                Object[] in = inputStack.toArray();
                int lastMatchIndex = in.length;

                for (int i = in.length - 1; i >= 0 && fa.step((Integer) in[i]); i--) {
                    if (fa.isMatch()) {
                        lastMatchIndex = i;
                    }
                }

                int bodyLength = in.length - lastMatchIndex;
                Object[] body = new Object[bodyLength];
                for (int i = bodyLength - 1; i >= 0; i--) {
                    inputStack.pop();
                    stateStack.pop();
                    body[i] = objectStack.pop();
                }

                stateStack.push(this.parsingTable.go(stateStack.peek(),
                        action.ruleId()));
                inputStack.push(action.ruleId());
                objectStack.push(this.rules.get(action.ruleId()).reduce(body));
            } else if (action.type() == ActionType.Accept) {
                break;
            } else {
                throw new SyntaxError(
                        "The input ABNF file is not a well-formed ABNF file. Please read [RFC5234], modify your ABNF file and try again.");
            }
        }

        if (objectStack.size() != 1) {
            throw new IllegalStateException(
                    "There must be a bug if the object stack contains more than one object.");
        }

        return (RuleList) objectStack.pop();
    }
}
