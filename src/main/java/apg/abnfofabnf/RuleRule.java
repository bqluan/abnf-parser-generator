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

import apg.syntax.Rule;
import apg.syntax.Token;

public class RuleRule extends Rule {
    // rule = rulename defined-as elements c-nl
    private static final Token[] TOKENS = new Token[] {
            Token.newRuleName("rulename"), Token.newRuleName("defined-as"),
            Token.newConcatenation(), Token.newRuleName("elements"),
            Token.newConcatenation(), Token.newRuleName("c-nl"),
            Token.newConcatenation(), };

    public RuleRule() {
        super("rule", TOKENS);
    }

    public Object reduce(Object[] body) {
        Object[] objs = (Object[]) body[2];
        Token[] tokens = new Token[objs.length];

        for (int i = 0; i < objs.length; i++) {
            tokens[i] = (Token) objs[i];
        }

        return new Rule((String) body[0], tokens);
    }
}
