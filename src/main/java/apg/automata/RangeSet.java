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

import java.util.Iterator;
import java.util.TreeMap;

/**
 * Set of character ranges.
 */
public class RangeSet implements Iterable<Range> {
    private TreeMap<Range, Range> ranges;

    public RangeSet() {
        this.ranges = new TreeMap<Range, Range>();
    }

    public boolean add(Range e) {
        Range r = this.ranges.remove(e);
        if (r != null) {
            e = new Range(Math.min(r.min(), e.min()), Math.max(
                    r.max(), e.max()));
        }

        if (e.max() < Integer.MAX_VALUE) {
            r = this.ranges.remove(new Range(e.min(), e.max() + 1));
            if (r != null) {
                e = new Range(e.min(), r.max());
            }
        }

        if (e.min() > Integer.MIN_VALUE) {
            r = this.ranges.remove(new Range(e.min() - 1, e.max()));
            if (r != null) {
                e = new Range(r.min(), e.max());
            }
        }

        return this.ranges.put(e, e) == null;
    }

    public boolean contains(Range r) {
        return this.ranges.containsKey(r);
    }

    public Iterator<Range> iterator() {
        return this.ranges.keySet().iterator();
    }
}
