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
import apg.syntax.Rule;
import apg.syntax.Token;

public class CharValRule extends Rule {
    // char-val = DQUOTE *(%x20-21 / %x23-7E) DQUOTE
    private static final Token[] TOKENS = new Token[] {
            Token.newRuleName("DQUOTE"), Token.newNum(new Range(0x20, 0x21)),
            Token.newNum(new Range(0x23, 0x7E)), Token.newAlternation(),
            Token.newRepetion(0, -1), Token.newConcatenation(),
            Token.newRuleName("DQUOTE"), Token.newConcatenation(), };

    public CharValRule() {
        super("char-val", TOKENS);
    }

    public Object reduce(Object[] body) {
        List<Integer> tokens = new ArrayList<Integer>();

        for (int i = 1; i < body.length - 1; i++) {
            tokens.add((Integer) body[i]);
        }

        return tokens.toArray(new Integer[] {});
    }
}
