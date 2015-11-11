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

public class HexValRule extends Rule {
    // hex-val = "x" 1*HEXDIG [ 1*("." 1*HEXDIG) / ("-" 1*HEXDIG) ]
    private static final Token[] TOKENS = new Token[] { Token.newChar('x'),
            Token.newRuleName("HEXDIG"), Token.newRepetion(1, -1),
            Token.newConcatenation(), Token.newChar('.'),
            Token.newRuleName("HEXDIG"), Token.newRepetion(1, -1),
            Token.newConcatenation(), Token.newRepetion(1, -1),
            Token.newChar('-'), Token.newRuleName("HEXDIG"),
            Token.newRepetion(1, -1), Token.newConcatenation(),
            Token.newAlternation(), Token.newRepetion(0, 1),
            Token.newConcatenation(), };

    public HexValRule() {
        super("hex-val", TOKENS);
    }

    public Object reduce(Object[] body) {
        int min = 0;
        int i = 1;
        while (i < body.length) {
            int v = (Integer) body[i];

            if (v == (int) '.' || v == (int) '-') {
                break;
            }

            if (min > (Integer.MAX_VALUE - v) / 16) {
                throw new IllegalStateException("integer overflow");
            }

            min = (min * 16) + v;

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

                if (max > (Integer.MAX_VALUE - v) / 16) {
                    throw new IllegalStateException("integer overflow");
                }

                max = (max * 16) + v;

                i++;
            }

            return new Repeat(min, max);
        }

        List<Integer> charCodes = new ArrayList<Integer>();
        charCodes.add(min);

        while (true) {
            // Skip the '.'.
            i++;

            int next = 0;
            while (i < body.length && (Integer) body[i] != (int) '.') {
                int v = (Integer) body[i];

                if (next > (Integer.MAX_VALUE - v) / 16) {
                    throw new IllegalStateException("integer overflow");
                }

                next = (next * 16) + v;

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
