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
package apg;

import apg.abnfofabnf.AlphaRule;
import apg.abnfofabnf.AlternationRule;
import apg.abnfofabnf.BinValRule;
import apg.abnfofabnf.BitRule;
import apg.abnfofabnf.CNlRule;
import apg.abnfofabnf.CWspRule;
import apg.abnfofabnf.CharRule;
import apg.abnfofabnf.CharValRule;
import apg.abnfofabnf.CommentRule;
import apg.abnfofabnf.ConcatenationRule;
import apg.abnfofabnf.CrRule;
import apg.abnfofabnf.CrlfRule;
import apg.abnfofabnf.CtlRule;
import apg.abnfofabnf.DecValRule;
import apg.abnfofabnf.DefinedAsRule;
import apg.abnfofabnf.DigitRule;
import apg.abnfofabnf.DquoteRule;
import apg.abnfofabnf.ElementRule;
import apg.abnfofabnf.ElementsRule;
import apg.abnfofabnf.GroupRule;
import apg.abnfofabnf.HexValRule;
import apg.abnfofabnf.HexdigRule;
import apg.abnfofabnf.HtabRule;
import apg.abnfofabnf.LfRule;
import apg.abnfofabnf.LwspRule;
import apg.abnfofabnf.NumValRule;
import apg.abnfofabnf.OctetRule;
import apg.abnfofabnf.OptionRule;
import apg.abnfofabnf.ProseValRule;
import apg.abnfofabnf.RepeatRule;
import apg.abnfofabnf.RepetitionRule;
import apg.abnfofabnf.RuleListRule;
import apg.abnfofabnf.RuleNameRule;
import apg.abnfofabnf.RuleRule;
import apg.abnfofabnf.SpRule;
import apg.abnfofabnf.StartRule;
import apg.abnfofabnf.VcharRule;
import apg.abnfofabnf.WspRule;
import apg.automata.Dfa;
import apg.automata.Program;
import apg.automata.Range;
import apg.automata.RangeSet;
import apg.code.CodeGenerator;
import apg.code.JavaCodeGenerator;
import apg.io.AbnfInputStream;
import apg.io.WeakReferenceInputStream;
import apg.lr.Parser;
import apg.lr.ParsingTable;
import apg.syntax.Rule;
import apg.syntax.RuleList;
import apg.syntax.SyntaxError;
import apg.syntax.Token;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

class Main {
    public static void main(String[] args) throws IOException {
        // Construct a rule list which contains all rules in the ABNF DEFINITION
        // OF ABNF in [RFC5234].
        RuleList rules = new RuleList(new Rule[]{
                new StartRule(),
                new RuleListRule(),
                new RuleRule(),
                new RuleNameRule(),
                new DefinedAsRule(),
                new ElementsRule(),
                new CWspRule(),
                new CNlRule(),
                new CommentRule(),
                new AlternationRule(),
                new ConcatenationRule(),
                new RepetitionRule(),
                new RepeatRule(),
                new ElementRule(),
                new GroupRule(),
                new OptionRule(),
                new CharValRule(),
                new NumValRule(),
                new BinValRule(),
                new DecValRule(),
                new HexValRule(),
                new ProseValRule(),
                new AlphaRule(),
                new BitRule(),
                new CharRule(),
                new CrRule(),
                new CrlfRule(),
                new CtlRule(),
                new DigitRule(),
                new DquoteRule(),
                new HexdigRule(),
                new HtabRule(),
                new LfRule(),
                new LwspRule(),
                new OctetRule(),
                new SpRule(),
                new VcharRule(),
                new WspRule()
        });

        // Build one reverse DFA for each rule. The reverse DFA is used to help
        // the parser to parse ABNF files.
        Dfa[] reverseDfas = new Dfa[rules.size()];
        Range alphabet = new Range(0, RuleList.MIN_RULE_ID + rules.size() - 1);
        for (int i = 0; i < rules.size(); i++) {
            try {
                reverseDfas[i] = new Dfa(rpnToProgramReverse(rules, rules.get(i + RuleList.MIN_RULE_ID).tokens()), alphabet);
            } catch (SyntaxError e) {
                System.err.println(e.getMessage());
                return;
            }
        }

        // Generate a parsing table from the rules. This parsing table is used
        // by the parser.
        ParsingTable parsingTable;
        try {
            parsingTable = new ParsingTable(rules);
        } catch (SyntaxError e) {
            System.err.println(e.getMessage());
            return;
        }

        // Construct a parser which is able to parse ABNF files.
        Parser parser = new Parser(rules, reverseDfas, parsingTable);

        // Use the parser to parse the ABNF file to a RuleList. the RuleList in
        // this step contains all rules defined in the ABNF file, which is read
        // from the given file or standard input stream.
        InputStream in;
        if (args.length == 1) {
            in = new AbnfInputStream(new FileInputStream(args[0]));
        } else {
            in = new AbnfInputStream(new WeakReferenceInputStream(System.in));
        }

        try {
            rules = parser.parse(in);
        } catch (SyntaxError e) {
            System.err.println(e.getMessage());
            return;
        } finally {
            in.close();
        }

        if (rules.size() == 0) {
            System.err.println("The input ABNF file doesn't contain any rule, no parser is generated.");
            return;
        }

        // Build one reverse DFA for each rule. The reverse DFA is used to help
        // the parser to parse ABNF files.
        reverseDfas = new Dfa[rules.size()];
        alphabet = new Range(0, RuleList.MIN_RULE_ID + rules.size() - 1);
        for (int i = 0; i < rules.size(); i++) {
            try {
                reverseDfas[i] = new Dfa(rpnToProgramReverse(rules, rules.get(i + RuleList.MIN_RULE_ID).tokens()), alphabet);
            } catch (SyntaxError e) {
                System.err.println(e.getMessage());
                return;
            }
        }

        // Generate a parsing table from the rules.
        try {
            parsingTable = new ParsingTable(rules);
        } catch (SyntaxError e) {
            System.err.println(e.getMessage());
            return;
        }

        // Generate code according to the reverse DFA list and the parsing
        // table, write it to standard output. 
        CodeGenerator gen = new JavaCodeGenerator(reverseDfas, parsingTable);
        gen.generate(System.out);
    }

    /**
     * Build a Program for the given rule. The program can be used to match
     * pattern in reverse sequence.
     *
     * @param rules  The rulelist for which to build a program.
     * @param tokens Rule body in Reverse Polish Notation.
     * @return Program for this rule.
     * @throws SyntaxError
     */
    private static Program rpnToProgramReverse(RuleList rules, Token[] tokens) throws SyntaxError {
        Stack<Program> stack = new Stack<Program>();

        for (Token token : tokens) {
            switch (token.tag()) {
                case Alternation: {
                    stack.push(stack.pop().or(stack.pop()));
                    break;
                }

                case Char: {
                    int charCode = (int) token.character();

                    if (charCode > Token.END_MARKER || charCode < 0) {
                        throw new SyntaxError(
                                String.format(
                                        "ABNF doesn't support Unicode, character codes must be in range [0, %1$d].",
                                        Token.END_MARKER));
                    }

                    stack.push(Program.newChar(charCode));
                    break;
                }

                case Concatenation: {
                    stack.push(stack.pop().concatenate(stack.pop()));
                    break;
                }

                case Num: {
                    RangeSet ranges = new RangeSet();
                    ranges.add(token.num());
                    stack.push(Program.newCharClass(ranges));
                    break;
                }

                case Repetion: {
                    stack.push(stack.pop().repeat(token.min(), token.max()));
                    break;
                }

                case RuleName: {
                    stack.push(Program.newChar(rules.get(token.ruleName()).getId()));
                    break;
                }

                default:
                    throw new IllegalArgumentException(String.format("Tag '%1$s' is not supported.", token.tag()));
            }
        }

        if (stack.size() != 1) {
            throw new IllegalStateException("The tokens parameter is not in Reverse Polish Notation.");
        }

        return stack.pop();
    }
}