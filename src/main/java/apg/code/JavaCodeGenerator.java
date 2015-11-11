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
package apg.code;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import util.Matrix;

import apg.automata.Dfa;
import apg.lr.Action;
import apg.lr.ParsingTable;

public class JavaCodeGenerator extends CodeGenerator {
    private Dfa[] reverseDfas;
    private ParsingTable parsingTable;

    public JavaCodeGenerator(Dfa[] reverseDfas, ParsingTable parsingTable) {
        super(getResource());
        this.reverseDfas = reverseDfas;
        this.parsingTable = parsingTable;
    }

    protected String get(String placeholder) {
        if (placeholder.equals("InitGotoTable")) {
            return this.InitGotoTable();
        } else if (placeholder.equals("InitActionTable")) {
            return this.InitActionTable();
        } else if (placeholder.equals("InitReverseDfas")) {
            return this.InitReverseDfas();
        } else {
            throw new IllegalArgumentException(String.format(
                    "Method '%1$s' is not supported.", placeholder));
        }
    }

    private String InitGotoTable() {
        StringBuilder result = new StringBuilder();

        Object[] ret = this.MatrixToCompressedMatrix(this.parsingTable
                .getGotoTable());

        @SuppressWarnings("unchecked")
        Integer[] a = ((List<Integer>) ret[0]).toArray(new Integer[] {});
        int[] ia = (int[]) ret[1];
        int[] ja = (int[]) ret[2];

        result.append("this.gotoTable = new CompressedMatrix<Integer>(");

        // param a
        result.append("new Integer[]");
        result.append(this.getIntegerArray(a));
        result.append(',');

        // param ia
        result.append("new int[]");
        result.append(this.getIntArray(ia));
        result.append(',');

        // param ja
        result.append("new int[]");
        result.append(this.getIntArray(ja));
        result.append(");");

        return result.toString();
    }

    private String InitActionTable() {
        StringBuilder result = new StringBuilder();

        Object[] ret = this.MatrixToCompressedMatrix(this.parsingTable
                .getActionTable());

        @SuppressWarnings("unchecked")
        Action[] a = ((List<Action>) ret[0]).toArray(new Action[] {});
        int[] ia = (int[]) ret[1];
        int[] ja = (int[]) ret[2];

        result.append("this.actionTable = new CompressedMatrix<Action>(");

        // param a
        result.append("new Action[]");
        result.append(this.getActionArray(a));
        result.append(',');

        // param ia
        result.append("new int[]");
        result.append(this.getIntArray(ia));
        result.append(',');

        // param ja
        result.append("new int[]");
        result.append(this.getIntArray(ja));
        result.append(");");

        return result.toString();
    }

    private String InitReverseDfas() {
        StringBuilder result = new StringBuilder();

        result.append("this.reverseDfas = new Dfa[]");
        result.append(this.getDfaArray(this.reverseDfas));
        result.append(';');

        return result.toString();
    }

    private String getDfaArray(Dfa[] array) {
        StringBuilder result = new StringBuilder();
        result.append('{');

        for (Dfa i : array) {
            Object[] ret = this
                    .MatrixToCompressedMatrix(i.getTransitionTable());

            @SuppressWarnings("unchecked")
            Integer[] a = ((List<Integer>) ret[0]).toArray(new Integer[] {});
            int[] ia = (int[]) ret[1];
            int[] ja = (int[]) ret[2];

            // param transitionTable
            result.append("new Dfa(new CompressedMatrix<Integer>(");

            // param a
            result.append("new Integer[]");
            result.append(this.getIntegerArray(a));
            result.append(',');

            // param ia
            result.append("new int[]");
            result.append(this.getIntArray(ia));
            result.append(',');

            // param ja
            result.append("new int[]");
            result.append(this.getIntArray(ja));
            result.append("),");

            // param acceptStates
            Set<Integer> acceptStates = i.getAcceptStates();
            result.append("new int[]");
            result.append(this.getIntegerArray(acceptStates
                    .toArray(new Integer[] {})));
            result.append("),");
        }

        result.append('}');
        return result.toString();
    }

    private String getIntArray(int[] array) {
        StringBuilder result = new StringBuilder();
        result.append('{');

        for (int i : array) {
            result.append(i);
            result.append(',');
        }

        result.append('}');
        return result.toString();
    }

    private String getIntegerArray(Integer[] array) {
        StringBuilder result = new StringBuilder();
        result.append('{');

        for (Integer i : array) {
            result.append(i);
            result.append(',');
        }

        result.append('}');
        return result.toString();
    }

    private String getActionArray(Action[] array) {
        StringBuilder result = new StringBuilder();
        result.append('{');

        for (Action i : array) {
            result.append(String.format("new Action(ActionType.%1$s, %2$d),",
                    i.type(), i.state()));
        }

        result.append('}');
        return result.toString();
    }

    private <T> Object[] MatrixToCompressedMatrix(Matrix<T> matrix) {
        // allocate
        List<T> a = new ArrayList<T>(matrix.size());
        int[] ia = new int[matrix.rowSize() + 1];
        int[] ja = new int[matrix.size()];

        // copy
        int lastRow = -1;
        int i = 0;
        for (int currRow = 0; currRow < matrix.rowSize(); currRow++) {
            for (int currCol = 0; currCol < matrix.colSize(); currCol++) {
                T entry = matrix.get(currRow, currCol);
                if (entry == null) {
                    continue;
                }

                a.add(entry);
                ja[i] = currCol;

                if (currRow != lastRow) {
                    for (int j = lastRow + 1; j <= currRow; j++) {
                        ia[j] = i;
                    }

                    lastRow = currRow;
                }

                i++;
            }
        }

        ia[lastRow + 1] = i;

        return new Object[] { a, ia, ja };
    }

    private static String getResource() {
        StringBuilder content = new StringBuilder();
        InputStream template = JavaCodeGenerator.class
                .getResourceAsStream("JavaCodeGenerator.template");

        try {
            int c = template.read();
            while (c != -1) {
                content.append((char) c);
                c = template.read();
            }
        } catch (IOException e) {
            // nop
        } finally {
            try {
                template.close();
            } catch (IOException e) {
            }
        }

        return content.toString();
    }
}
