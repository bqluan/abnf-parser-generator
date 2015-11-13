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
package apg.abnfofabnf;

import apg.syntax.Rule;
import apg.syntax.Token;

public class RuleNameRule extends Rule {
    // rulename = ALPHA *(ALPHA / DIGIT / "-")
    private static final Token[] TOKENS = new Token[]{
            Token.newRuleName("ALPHA"), Token.newRuleName("ALPHA"),
            Token.newRuleName("DIGIT"), Token.newAlternation(),
            Token.newChar('-'), Token.newAlternation(),
            Token.newRepetion(0, -1), Token.newConcatenation(),};

    public RuleNameRule() {
        super("rulename", TOKENS);
    }

    public Object reduce(Object[] body) {
        StringBuilder name = new StringBuilder();

        for (Object i : body) {
            name.append((char) (int) (Integer) i);
        }

        return name.toString();
    }
}
