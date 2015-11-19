abnf-parser-generator
=====================

The ABNF Parser Generator is a tool that generates a parser from a context-free
grammar written in ABNF. The generated parser is a LR(1) parser. The LR(1)
parser is a parser that reads input from **L**eft to right and produces a **R**ightmost
derivation, where the **1** refers to the number of "look ahead" input symbols that
are used in making parsing decisions.

DEMO

  You can follow these steps to get started:

    - Compile the source code and export class files to a runnable jar file.
      Copy the jar file into 'demo' directory.
    - cd demo/
    - cat calc.abnf | java -jar apg.jar > Parser.java
    - Create a new java project and include the generated file 'Parser.java' in
      it.
    - Modify the main method so that it looks like this one.

        public static void main(String[] args) throws IOException {
            String text = "(1+2)*3";
            byte[] buffer = text.getBytes("US-ASCII");
    
            Parser parser = new Parser();
            ByteInputStream input = new ByteInputStream(buffer, buffer.length);
    
            try {
                boolean isMatch = parser.parse(input);
    
                System.out.println(String.format(
                        "Is '%1$s' a valid arithmetic expression?", text));
                System.out.println(isMatch ? "Yes" : "No");
            } finally {
                input.close();
            }
        }

    - Run the program.
    - Try "(1+2*3".
