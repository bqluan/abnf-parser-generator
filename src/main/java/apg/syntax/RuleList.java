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
package apg.syntax;

import java.util.TreeMap;

public class RuleList {
    public static final int MIN_RULE_ID = Token.END_MARKER + 1;
    private Rule[] rules;
    private TreeMap<String, Rule> indexByName;

    public RuleList(Rule[] rules) {
        this.rules = rules;
        this.indexByName = new TreeMap<String, Rule>();

        // Build index
        for (int i = 0; i < this.rules.length; i++) {
            Rule r = this.rules[i];
            r.setId(MIN_RULE_ID + i);
            this.indexByName.put(r.name(), r);
        }
    }

    public int size() {
        return this.rules.length;
    }

    public Rule get(String name) {
        return this.indexByName.get(name);
    }

    public Rule get(int index) {
        return this.rules[index - MIN_RULE_ID];
    }
}
