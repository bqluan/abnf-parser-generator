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
import apg.syntax.Token;

public class ConcatenationRule extends Rule {
    // concatenation = repetition *(1*c-wsp repetition)
    private static final Token[] TOKENS = new Token[] {
            Token.newRuleName("repetition"), Token.newRuleName("c-wsp"),
            Token.newRepetion(1, -1), Token.newRuleName("repetition"),
            Token.newConcatenation(), Token.newRepetion(0, -1),
            Token.newConcatenation(), };

    public ConcatenationRule() {
        super("concatenation", TOKENS);
    }

    public Object reduce(Object[] body) {
        List<Token> tokens = new ArrayList<Token>();

        for (Object t : (Object[]) body[0]) {
            tokens.add((Token) t);
        }

        for (int i = 1; i < body.length; i++) {
            if (body[i] == null) {
                continue;
            }

            for (Token t : (Token[]) body[i]) {
                tokens.add(t);
            }

            tokens.add(Token.newConcatenation());
        }

        return tokens.toArray(new Token[] {});
    }
}
