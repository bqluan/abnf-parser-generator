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

import apg.automata.Range;
import apg.syntax.Rule;
import apg.syntax.Token;

public class VcharRule extends Rule {
    // VCHAR = %x21-7E
    private static final Token[] TOKENS = new Token[] { Token.newNum(new Range(
            0x21, 0x7E)), };

    public VcharRule() {
        super("VCHAR", TOKENS);
    }

    public Object reduce(Object[] body) {
        return body[0];
    }
}
