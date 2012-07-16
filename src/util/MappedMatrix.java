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
package util;

import java.util.TreeMap;

public class MappedMatrix<T> implements Matrix<T> {
    private TreeMap<Key, T> entries;
    private int rowSize;
    private int colSize;

    public MappedMatrix() {
        this.entries = new TreeMap<Key, T>();
        this.rowSize = 0;
        this.colSize = 0;
    }

    @Override
    public T get(int x, int y) {
        return this.entries.get(new Key(x, y));
    }

    @Override
    public T put(int x, int y, T v) {
        if (x > this.rowSize - 1) {
            this.rowSize = x + 1;
        }

        if (y > this.colSize - 1) {
            this.colSize = y + 1;
        }

        return this.entries.put(new Key(x, y), v);
    }

    @Override
    public int size() {
        return this.entries.size();
    }

    @Override
    public int rowSize() {
        return this.rowSize;
    }

    @Override
    public int colSize() {
        return this.colSize;
    }

    private class Key implements Comparable<Key> {
        private int x;
        private int y;

        public Key(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Key o) {
            if (this.x != o.x) {
                return this.x - o.x;
            }

            return this.y - o.y;
        }
    }
}
