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
package apg.lr;

public class Action {
    public static final Action ERROR = new Action(ActionType.Error);
    public static final Action ACCEPT = new Action(ActionType.Accept);

    private ActionType type;
    private int data;

    public Action(ActionType type) {
        this(type, 0);
    }

    public Action(ActionType type, int data) {
        this.type = type;
        this.data = data;
    }

    public ActionType type() {
        return this.type;
    }

    public int ruleId() {
        return this.data;
    }

    public int state() {
        return this.data;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Action) {
            Action a = (Action) o;
            return this.type == a.type && this.data == a.data;
        }

        return false;
    }
}
