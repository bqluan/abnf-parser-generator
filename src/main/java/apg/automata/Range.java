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
package apg.automata;

/**
 * Range of character codes.
 */
public class Range implements Comparable<Range> {
    private int min;
    private int max;

    public Range(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(
                    "Min must be less than or equal to max.");
        }

        this.min = min;
        this.max = max;
    }

    public int min() {
        return this.min;
    }

    public int max() {
        return this.max;
    }

    public int compareTo(Range o) {
        if (this.max < o.min) {
            return -1;
        } else if (this.min > o.max) {
            return 1;
        } else {
            return 0;
        }
    }
}
