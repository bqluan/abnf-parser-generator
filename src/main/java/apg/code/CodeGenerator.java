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
package apg.code;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.PrintStream;

public class CodeGenerator {
    private String template;

    protected CodeGenerator(String template) {
        this.template = template;
    }

    public void generate(PrintStream out) {
        int length = this.template.length();
        int i = 0;
        while (i < length) {
            if (this.template.charAt(i) == '@' && i + 1 < length
                    && this.template.charAt(i + 1) == '@') {
                // Skip the second '@'.
                i += 2;
                if (i >= length) {
                    break;
                }

                StringBuilder placeholder = new StringBuilder();
                while (i < length
                        && Character.isLetterOrDigit(this.template.charAt(i))) {
                    placeholder.append(this.template.charAt(i));
                    i++;
                }

                out.print(this.get(placeholder.toString()));
            } else {
                out.print(this.template.charAt(i));
                i++;
            }
        }
    }

    protected String get(String placeholder) {
        throw new NotImplementedException();
    }
}
