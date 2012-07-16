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
package apg.abnfofabnf;

import java.util.ArrayList;
import java.util.List;

import apg.syntax.Rule;
import apg.syntax.RuleList;
import apg.syntax.Token;

public class RuleListRule extends Rule {
    // rulelist = 1*( rule / (*c-wsp c-nl) )
    private static final Token[] TOKENS = new Token[] {
            Token.newRuleName("rule"), Token.newRuleName("c-wsp"),
            Token.newRepetion(0, -1), Token.newRuleName("c-nl"),
            Token.newConcatenation(), Token.newAlternation(),
            Token.newRepetion(1, -1), };
    private static final Rule[] EMPTY_RULE_ARRAY = new Rule[] {};

    public RuleListRule() {
        super("rulelist", TOKENS);
    }

    public Object reduce(Object[] body) {
        List<Rule> rules = new ArrayList<Rule>();

        for (Object rule : body) {
            if (rule == null) {
                continue;
            }

            rules.add((Rule) rule);
        }

        if (rules.size() != 0) {
            rules.add(
                    0,
                    new Rule("__0__", new Token[] {
                            Token.newRuleName(rules.get(0).name()),
                            Token.newChar((char) Token.END_MARKER),
                            Token.newConcatenation(), }));
        }

        return new RuleList(rules.toArray(EMPTY_RULE_ARRAY));
    }
}