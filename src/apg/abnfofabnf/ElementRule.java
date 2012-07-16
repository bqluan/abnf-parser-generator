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

import apg.automata.Range;
import apg.syntax.Repeat;
import apg.syntax.Rule;
import apg.syntax.Token;

public class ElementRule extends Rule {
    // element = rulename / group / option / char-val / num-val / prose-val
    private static final Token[] TOKENS = new Token[] {
            Token.newRuleName("rulename"), Token.newRuleName("group"),
            Token.newAlternation(), Token.newRuleName("option"),
            Token.newAlternation(), Token.newRuleName("char-val"),
            Token.newAlternation(), Token.newRuleName("num-val"),
            Token.newAlternation(), Token.newRuleName("prose-val"),
            Token.newAlternation(), };

    public ElementRule() {
        super("element", TOKENS);
    }

    public Object reduce(Object[] body) {
        // prose-val
        if (body[0] == null) {
            throw new IllegalStateException();
        }

        Object v = body[0];

        if (v instanceof String) {
            // rulename
            return new Token[] { Token.newRuleName((String) v), };
        } else if (v instanceof Token[]) {
            // group, option
            return v;
        } else if (v instanceof Integer[]) {
            // char-val
            List<Token> tokens = new ArrayList<Token>();

            Integer[] charCodes = (Integer[]) v;

            if (charCodes.length == 0) {
                return tokens.toArray(new Token[] {});
            }

            tokens.add(Token.newChar((char) (int) charCodes[0]));

            for (int i = 1; i < charCodes.length; i++) {
                tokens.add(Token.newChar((char) (int) charCodes[i]));
                tokens.add(Token.newConcatenation());
            }

            return tokens.toArray(new Token[] {});
        } else if (v instanceof Repeat) {
            // num-val
            Repeat r = (Repeat) v;
            Range range = new Range(r.min(), r.max());
            return new Token[] { Token.newNum(range), };
        } else {
            throw new IllegalStateException();
        }
    }
}
