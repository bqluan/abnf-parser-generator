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

import apg.automata.Range;
import apg.syntax.Rule;
import apg.syntax.Token;

public class ProseValRule extends Rule {
    // prose-val = "<" *(%x20-3D / %x3F-7E) ">"
    private static final Token[] TOKENS = new Token[]{Token.newChar('<'),
            Token.newNum(new Range(0x20, 0x3D)),
            Token.newNum(new Range(0x3F, 0x7E)), Token.newAlternation(),
            Token.newRepetion(0, -1), Token.newConcatenation(),
            Token.newChar('>'), Token.newConcatenation(),};

    public ProseValRule() {
        super("prose-val", TOKENS);
    }
}
