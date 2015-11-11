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
package apg.lr;

public class Item implements Comparable<Item> {
    private int ruleId;
    private int state;
    private int terminal;

    public Item(int ruleId, int state, int terminal) {
        this.ruleId = ruleId;
        this.state = state;
        this.terminal = terminal;
    }

    public int ruleId() {
        return this.ruleId;
    }

    public int state() {
        return this.state;
    }

    public int terminal() {
        return this.terminal;
    }

    public String toString() {
        return String.format("(%1$d,%2$d,%3$d)", this.ruleId, this.state,
                this.terminal);
    }

    @Override
    public int compareTo(Item o) {
        if (this.ruleId != o.ruleId) {
            return this.ruleId - o.ruleId;
        }

        if (this.state != o.state) {
            return this.state - o.state;
        }

        return this.terminal - o.terminal;
    }
}
