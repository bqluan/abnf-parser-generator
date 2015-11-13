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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlternationRule extends Rule {
    // alternation = concatenation *("/" *c-wsp concatenation)
    private static final Token[] TOKENS = new Token[]{
            Token.newRuleName("concatenation"), Token.newChar('/'),
            Token.newRuleName("c-wsp"), Token.newRepetion(0, -1),
            Token.newConcatenation(), Token.newRuleName("concatenation"),
            Token.newConcatenation(), Token.newRepetion(0, -1),
            Token.newConcatenation(),};

    public AlternationRule() {
        super("alternation", TOKENS);
    }

    public Object reduce(Object[] body) {
        List<Token> tokens = new ArrayList<Token>();

        for (Object t : (Object[]) body[0]) {
            tokens.add((Token) t);
        }

        for (int i = 1; i < body.length; i++) {
            if (body[i] == null || body[i] instanceof Integer) {
                continue;
            }

            Collections.addAll(tokens, (Token[]) body[i]);

            tokens.add(Token.newAlternation());
        }

        return tokens.toArray(new Token[tokens.size()]);
    }
}
