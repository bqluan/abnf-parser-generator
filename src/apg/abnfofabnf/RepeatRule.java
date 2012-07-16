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

import apg.syntax.Repeat;
import apg.syntax.Rule;
import apg.syntax.Token;

public class RepeatRule extends Rule {
    // repeat = 1*DIGIT / (*DIGIT "*" *DIGIT)
    private static final Token[] TOKENS = new Token[] {
            Token.newRuleName("DIGIT"), Token.newRepetion(1, -1),
            Token.newRuleName("DIGIT"), Token.newRepetion(0, -1),
            Token.newChar('*'), Token.newConcatenation(),
            Token.newRuleName("DIGIT"), Token.newRepetion(0, -1),
            Token.newConcatenation(), Token.newAlternation(), };

    public RepeatRule() {
        super("repeat", TOKENS);
    }

    public Object reduce(Object[] body) {
        int min = 0;
        int max = -1;

        int i = 0;
        while (i < body.length && (Integer) body[i] != (int) '*') {
            int v = (Integer) body[i];

            if (min > (Integer.MAX_VALUE - v) / 10) {
                throw new IllegalStateException("integer overflow.");
            }

            min = (10 * min) + v;
            i++;
        }

        if (i >= body.length) {
            return new Repeat(min, min);
        }

        // Skip the '*'.
        i++;

        if (i < body.length) {
            max = 0;
            while (i < body.length) {
                int v = (Integer) body[i];

                if (max > (Integer.MAX_VALUE - v) / 10) {
                    throw new IllegalStateException("integer overflow.");
                }

                max = (10 * max) + v;
                i++;
            }
        }

        return new Repeat(min, max);
    }
}
