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
package apg.syntax;

import apg.automata.Range;

public class Token {
    public static final int END_MARKER = 256;
    private Tag tag;
    private Object data;

    private Token() {
    }

    public static Token newRuleName(String ruleName) {
        Token t = new Token();
        t.tag = Tag.RuleName;
        t.data = ruleName;
        return t;
    }

    public static Token newChar(char c) {
        Token t = new Token();
        t.tag = Tag.Char;
        t.data = c;
        return t;
    }

    public static Token newNum(Range r) {
        Token t = new Token();
        t.tag = Tag.Num;
        t.data = r;
        return t;
    }

    public static Token newAlternation() {
        Token t = new Token();
        t.tag = Tag.Alternation;
        return t;
    }

    public static Token newConcatenation() {
        Token t = new Token();
        t.tag = Tag.Concatenation;
        return t;
    }

    public static Token newProse() {
        Token t = new Token();
        t.tag = Tag.Prose;
        return t;
    }

    public static Token newRepetion(int min, int max) {
        Token t = new Token();
        t.tag = Tag.Repetion;
        t.data = new int[] { min, max };
        return t;
    }

    public Tag tag() {
        return this.tag;
    }

    public String ruleName() {
        if (this.tag != Tag.RuleName) {
            throw new IllegalStateException();
        }

        return (String) this.data;
    }

    public char character() {
        if (this.tag != Tag.Char) {
            throw new IllegalStateException();
        }

        return (Character) this.data;
    }

    public Range num() {
        if (this.tag != Tag.Num) {
            throw new IllegalStateException();
        }

        return (Range) this.data;
    }

    public int min() {
        if (this.tag != Tag.Repetion) {
            throw new IllegalStateException();
        }

        return ((int[]) this.data)[0];
    }

    public int max() {
        if (this.tag != Tag.Repetion) {
            throw new IllegalStateException();
        }

        return ((int[]) this.data)[1];
    }
}
