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

public class HexdigRule extends Rule {
    // HEXDIG = DIGIT / "A" / "B" / "C" / "D" / "E" / "F"
    private static final Token[] TOKENS = new Token[] {
            Token.newRuleName("DIGIT"), Token.newChar('A'),
            Token.newAlternation(), Token.newChar('B'), Token.newAlternation(),
            Token.newChar('C'), Token.newAlternation(), Token.newChar('D'),
            Token.newAlternation(), Token.newChar('E'), Token.newAlternation(),
            Token.newChar('F'), Token.newAlternation(), };

    public HexdigRule() {
        super("HEXDIG", TOKENS);
    }

    public Object reduce(Object[] body) {
        int v = (Integer) body[0];
        return (v >= 0 && v <= 9) ? v : 10 + v - (int) 'A';
    }
}
