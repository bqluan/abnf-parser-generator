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

import apg.syntax.Repeat;
import apg.syntax.Rule;
import apg.syntax.Token;

public class BinValRule extends Rule {
    // bin-val = "b" 1*BIT [ 1*("." 1*BIT) / ("-" 1*BIT) ]
    private static final Token[] TOKENS = new Token[] { Token.newChar('b'),
            Token.newRuleName("BIT"), Token.newRepetion(1, -1),
            Token.newConcatenation(), Token.newChar('.'),
            Token.newRuleName("BIT"), Token.newRepetion(1, -1),
            Token.newConcatenation(), Token.newRepetion(1, -1),
            Token.newChar('-'), Token.newRuleName("BIT"),
            Token.newRepetion(1, -1), Token.newConcatenation(),
            Token.newAlternation(), Token.newRepetion(0, 1),
            Token.newConcatenation(), };

    public BinValRule() {
        super("bin-val", TOKENS);
    }

    public Object reduce(Object[] body) {
        int min = 0;
        int i = 1;
        while (i < body.length) {
            int v = (Integer) body[i];

            if (v == (int) '.' || v == (int) '-') {
                break;
            }

            if (min > (Integer.MAX_VALUE - v) / 2) {
                throw new IllegalStateException("integer overflow");
            }

            min = min << 1 | v;

            i++;
        }

        if (i >= body.length) {
            return new Integer[] { min };
        }

        if ((Integer) body[i] == (int) '-') {
            i++;
            int max = 0;

            while (i < body.length) {
                int v = (Integer) body[i];

                if (max > (Integer.MAX_VALUE - v) / 2) {
                    throw new IllegalStateException("integer overflow");
                }

                max = max << 1 | v;

                i++;
            }

            return new Repeat(min, max);
        }

        List<Integer> charCodes = new ArrayList<Integer>();
        charCodes.add(min);

        while (true) {
            // skip the '.'.
            i++;

            int next = 0;
            while (i < body.length && (Integer) body[i] != (int) '.') {
                int v = (Integer) body[i];

                if (next > (Integer.MAX_VALUE - v) / 2) {
                    throw new IllegalStateException("integer overflow");
                }

                next = next << 1 | v;

                i++;
            }

            charCodes.add(next);

            if (i >= body.length) {
                break;
            }
        }

        return charCodes.toArray(new Integer[] {});
    }
}
