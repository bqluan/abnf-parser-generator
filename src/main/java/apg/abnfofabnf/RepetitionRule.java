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

import apg.syntax.Repeat;
import apg.syntax.Rule;
import apg.syntax.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepetitionRule extends Rule {
    // repetition = [repeat] element
    private static final Token[] TOKENS = new Token[]{
            Token.newRuleName("repeat"), Token.newRepetion(0, 1),
            Token.newRuleName("element"), Token.newConcatenation(),};

    public RepetitionRule() {
        super("repetition", TOKENS);
    }

    public Object reduce(Object[] body) {
        if (body.length == 1) {
            return body[0];
        }

        Repeat range = (Repeat) body[0];
        List<Token> tokens = new ArrayList<Token>();

        Collections.addAll(tokens, (Token[]) body[1]);

        tokens.add(Token.newRepetion(range.min(), range.max()));

        return tokens.toArray(new Token[tokens.size()]);
    }
}
