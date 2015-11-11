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
package apg.automata;

/**
 * The Opcode of an instruction.
 */
public enum Opcode {
    /**
     * After reading a Char instruction, the automaton matches a character.
     */
    Char,

    /**
     * After reading a CharClass instruction, the automaton matches a range of
     * characters.
     */
    CharClass,

    /**
     * After reading a Jump instruction, the automaton jumps to the given
     * location.
     */
    Jump,

    /**
     * After reading a Match instruction, the automaton matches a pattern.
     */
    Match,

    /**
     * No operation.
     */
    Nop,

    /**
     * After reading a Split instruction, the automaton splits the execution
     * into two threads.
     */
    Split
}
