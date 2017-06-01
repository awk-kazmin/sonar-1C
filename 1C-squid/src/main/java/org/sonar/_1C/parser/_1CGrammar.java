/*
 * Sonar 1С Plugin
 * Copyright (C) 2017 Vasiliy Kazmin and SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar._1C.parser;

import com.sonar.sslr.api.GenericTokenType;
import org.sonar._1C.api._1CKeyword;
import org.sonar._1C.api._1CPunctuator;
import org.sonar._1C.api._1CTokenType;
import org.sonar._1C.lexer._1CLexer;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;
import org.sonar.sslr.parser.LexerlessGrammar;

import static org.sonar._1C.api._1CPunctuator.*;
import static org.sonar._1C.api._1CTokenType.IDENTIFIER;
import static org.sonar._1C.api._1CTokenType.NUMERIC_LITERAL;
import static org.sonar._1C.api._1CKeyword.*;

/**
 * Grammar for ECMAScript.
 * Based on <a href="http://www.ecma-international.org/publications/standards/Ecma-262.htm">ECMA-262</a>
 * edition 5.1 (June 2011).
 */
public enum _1CGrammar implements GrammarRuleKey {

  /**
   * End of file.
   */
  EOF,

  /**
   * End of statement.
   */
  EOS,
  EOS_NO_LB,

  IDENTIFIER_NAME,


  // A.1 Lexical

  LITERAL,
  NULL_LITERAL,
  UNDEF_LITERAL,
  BOOLEAN_LITERAL,
  STRING_LITERAL,
  DATETIME_LITERAL,
  KEYWORD,
  LETTER_OR_DIGIT,

  /**
   * Spacing.
   */
  SPACING,

  /**
   * Spacing without line break.
   */
  SPACING_NO_LB,
  NEXT_NOT_LB,
  LINE_TERMINATOR_SEQUENCE,

  // A.3 Expressions

  ARRAY_LITERAL,
  CALL_EXPRESSION,
  ARGUMENTS,
  LEFT_HAND_SIDE_EXPRESSION,
  EXPRESSION,

  // A.4 Statements

  STATEMENT,
  //STATEMENT_LIST,
  EXPRESSION_STATEMENT,
  IF_STATEMENT,
  ELSE_CLAUSE,
  ITERATION_STATEMENT,
  WHILE_STATEMENT,
  FOR_STATEMENT,
  CONTINUE_STATEMENT,
  BREAK_STATEMENT,
  RETURN_STATEMENT,
  LABELLED_STATEMENT,
  THROW_STATEMENT,
  TRY_STATEMENT,
  CATCH_,

  // A.5 Functions and Programs

  FUNCTION_DECLARATION,
  FUNCTION_DEFINATION,
  PROCEDURE_DECLARATION,
  PROCEDURE_DEFINATION,
  FORMAL_PARAMETER_LIST,
  PROGRAM,
  SOURCE_ELEMENT,

  FORMAL_PARAMETER,
  METHOD_DECLARATION,
  VARS_DECLARATION,
  METHOD_DEFINATION,
  VARS_DECLARATION_LIST,
  VAR_DECLARATION,
  EACH_STATEMENT,
  TO_STATEMENT,
  ELSEIF_CLAUSE,
  ITERATION_BODY_STATEMENT,
  OPERATION,
  GOTO_STATEMENT,
  CALL_STATEMENT,
  ENDS,
  EMPTY_STATEMENT,
  DIRECTIVE,
  ADD_HANDLER_STATEMENT,
  REMOVE_HANDLER_STATEMENT,
  ANNOTATION;

  public static LexerlessGrammar createGrammar() {
    return createGrammarBuilder().build();
  }

  public static LexerlessGrammarBuilder createGrammarBuilder() {
    LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

    b.rule(IDENTIFIER_NAME).is(
        SPACING,
        b.regexp(_1CLexer.IDENTIFIER)
    );

    b.rule(LITERAL).is(b.firstOf(
        NULL_LITERAL,
        UNDEF_LITERAL,
        DATETIME_LITERAL,
        BOOLEAN_LITERAL,
        NUMERIC_LITERAL,
        STRING_LITERAL)
    );

    b.rule(NULL_LITERAL).is(NULL);

    b.rule(UNDEF_LITERAL).is(UNDEF);

    b.rule(BOOLEAN_LITERAL).is(b.firstOf(
        TRUE,
        FALSE)
    );

    lexical(b);
    expressions(b);
    statements(b);
    functionsAndPrograms(b);

    b.setRootRule(PROGRAM);

    return b;
  }

  /**
   * A.1 Lexical
   */
  private static void lexical(LexerlessGrammarBuilder b) {
    b.rule(SPACING).is(
        b.skippedTrivia(b.regexp("[" + _1CLexer.LINE_TERMINATOR + _1CLexer.WHITESPACE + "]*+")),
        b.zeroOrMore(
            b.firstOf(b.commentTrivia(b.regexp(_1CLexer.COMMENT)), b.commentTrivia(_1CLexer.PREPOCESSOR)),
            b.skippedTrivia(b.regexp("[" + _1CLexer.LINE_TERMINATOR + _1CLexer.WHITESPACE + "]*+")))).skip();

    b.rule(SPACING_NO_LB).is(
        b.zeroOrMore(
            b.firstOf(
                b.skippedTrivia(b.regexp("[" + _1CLexer.WHITESPACE + "]++")),
                b.commentTrivia(b.regexp("(?:" + _1CLexer.SINGLE_LINE_COMMENT +  ")"))))
    ).skip();

    b.rule(NEXT_NOT_LB).is(b.nextNot(b.regexp("(?:" + "|[" + _1CLexer.LINE_TERMINATOR + "])"))
    ).skip();

    b.rule(LINE_TERMINATOR_SEQUENCE).is(b.skippedTrivia(b.regexp("(?:\\n|\\r\\n|\\r|\\u2028|\\u2029)"))).skip();

    // Empty token is mandatory for the next two rules, because Toolkit is unable to work with AstNode without tokens
    Object emptyToken = b.token(_1CTokenType.EMPTY, "");

    b.rule(EOS).is(b.firstOf(
        b.sequence(SPACING, SEMI),
        b.sequence(SPACING_NO_LB, emptyToken, LINE_TERMINATOR_SEQUENCE),
        b.sequence(SPACING, emptyToken, b.endOfInput())));

    b.rule(EOS_NO_LB).is(b.firstOf(
        b.sequence(SPACING_NO_LB, NEXT_NOT_LB, SEMI),
        b.sequence(SPACING_NO_LB, emptyToken, LINE_TERMINATOR_SEQUENCE),
        b.sequence(SPACING_NO_LB, emptyToken, b.endOfInput())));

    b.rule(EOF).is(b.token(GenericTokenType.EOF, b.endOfInput())).skip();

    b.rule(IDENTIFIER).is(
        SPACING,
        b.nextNot(KEYWORD),
        b.regexp(_1CLexer.IDENTIFIER)
    );

    b.rule(NUMERIC_LITERAL).is(
        SPACING,
        b.regexp(_1CLexer.NUMERIC_LITERAL)
    );

    b.rule(STRING_LITERAL).is(
            SPACING,
            b.token(GenericTokenType.LITERAL, b.regexp(_1CLexer.STRING_LITERAL))
    );

    b.rule(DATETIME_LITERAL).is(
            SPACING,
            b.token(GenericTokenType.LITERAL, b.regexp(_1CLexer.DATE_LITERAL))
    );

    punctuators(b);
    keywords(b);
  }

  private static void punctuators(LexerlessGrammarBuilder b) {
    punctuator(b, LPARENTHESIS, "(");
    punctuator(b, RPARENTHESIS, ")");
    punctuator(b, LBRACKET, "[");
    punctuator(b, RBRACKET, "]");
    punctuator(b, DOT, ".");
    punctuator(b, SEMI, ";");
    punctuator(b, COMMA, ",");
    punctuator(b, LT, "<", b.nextNot("=", ">"));
    punctuator(b, GT, ">", b.nextNot("="));
    punctuator(b, LE, "<=");
    punctuator(b, GE, ">=");
    punctuator(b, NOTEQUAL, "<>");
    punctuator(b, PLUS, "+", b.nextNot(b.firstOf("+", "=")));
    punctuator(b, MINUS, "-", b.nextNot(b.firstOf("-", "=")));
    punctuator(b, STAR, "*", b.nextNot("="));
    punctuator(b, MOD, "%", b.nextNot("="));
    punctuator(b, DIV, "/", b.nextNot("="));
    punctuator(b, QUERY, "?");
    punctuator(b, TILDA, "~");
    punctuator(b, DDOT, ":");
    punctuator(b, EQU, "=");
  }

  private static void keywords(LexerlessGrammarBuilder b) {
    b.rule(LETTER_OR_DIGIT).is(b.regexp(_1CLexer.IDENTIFIER_PART));
    Object[] rest = new Object[_1CKeyword.values().length - 2];
    for (int i = 0; i < _1CKeyword.values().length; i++) {
      _1CKeyword tokenType = _1CKeyword.values()[i];

      if(tokenType.isOne()) {
        b.rule(tokenType).is(SPACING, b.regexp("(?iu)(?:"+tokenType.getEnValue()+")"), b.nextNot(LETTER_OR_DIGIT));
      } else {
        b.rule(tokenType).is(SPACING, b.regexp("(?iu)(?:"+tokenType.getEnValue()+"|"+tokenType.getRuValue()+")"), b.nextNot(LETTER_OR_DIGIT));
      }
      if (i > 1) {
        if(tokenType.isOne()) {
          rest[i - 2] = "(?iu)(?:"+tokenType.getEnValue()+")";
        } else {
          rest[i - 2] ="(?iu)(?:"+tokenType.getEnValue()+"|"+tokenType.getRuValue()+")";
        }
      }
    }
    b.rule(KEYWORD).is(b.firstOf(
        _1CKeyword.values()[0].isOne()? b.regexp("(?iu)(?:"+ _1CKeyword.values()[0].getEnValue()+")"):b.regexp("(?iu)(?:"+ _1CKeyword.values()[0].getEnValue()+"|"+ _1CKeyword.values()[0].getRuValue()+")"),
        _1CKeyword.values()[1].isOne()? b.regexp("(?iu)(?:"+ _1CKeyword.values()[1].getEnValue()+")"):b.regexp("(?iu)(?:"+ _1CKeyword.values()[1].getEnValue()+"|"+ _1CKeyword.values()[1].getRuValue()+")"),
        rest), b.nextNot(LETTER_OR_DIGIT));
  }

  private static void punctuator(LexerlessGrammarBuilder b, GrammarRuleKey ruleKey, String value) {
    for (_1CPunctuator tokenType : _1CPunctuator.values()) {
      if (value.equals(tokenType.getValue())) {
        b.rule(tokenType).is(SPACING, value);
        return;
      }
    }
    throw new IllegalStateException(value);
  }

  private static Object word(LexerlessGrammarBuilder b, String value) {
    return b.sequence(SPACING, b.token(GenericTokenType.IDENTIFIER, value));
  }

  private static void punctuator(LexerlessGrammarBuilder b, GrammarRuleKey ruleKey, String value, Object element) {
    for (_1CPunctuator tokenType : _1CPunctuator.values()) {
      if (value.equals(tokenType.getValue())) {
        b.rule(tokenType).is(SPACING, value, element);
        return;
      }
    }
    throw new IllegalStateException(value);
  }

  /**
   * A.3 Expressions
   */
  private static void expressions(LexerlessGrammarBuilder b) {
      b.rule(EXPRESSION).is(
              b.optional(NOT),
              b.firstOf(
                      LITERAL,
                      LEFT_HAND_SIDE_EXPRESSION,
                      b.sequence(LPARENTHESIS, EXPRESSION, RPARENTHESIS),
                      b.sequence(QUERY, LPARENTHESIS, EXPRESSION, COMMA, EXPRESSION, COMMA, EXPRESSION, RPARENTHESIS)
              ), b.zeroOrMore(OPERATION, EXPRESSION)
      );

      b.rule(OPERATION).is(b.firstOf(
              PLUS,
              MOD,
              MINUS,
              STAR,
              AND,
              OR,
              EQU,
              NOTEQUAL,
              NEW,
              GE,
              LE,
              b.sequence(GT, b.nextNot(EQU)),
              b.sequence(LT, b.nextNot(EQU))
      ));

      b.rule(LEFT_HAND_SIDE_EXPRESSION).is(
              IDENTIFIER,
              b.zeroOrMore(b.firstOf(
                      b.sequence(ARRAY_LITERAL, b.nextNot(LPARENTHESIS)),
                      CALL_EXPRESSION,
                      b.sequence(DOT, IDENTIFIER)
              ))
      );

      b.rule(CALL_EXPRESSION).is(LPARENTHESIS, ARGUMENTS, RPARENTHESIS);
      b.rule(ARGUMENTS).is(b.optional(EXPRESSION, b.zeroOrMore(COMMA, EXPRESSION)));
      b.rule(ARRAY_LITERAL).is(LBRACKET, EXPRESSION, RBRACKET);
  }

  /**
   * A.4 Statement
   */
  private static void statements(LexerlessGrammarBuilder b) {

    b.rule(ENDS).is(SPACING,b.firstOf(
            SEMI,
            EOF,
            END_DO,
            END_FUNCTION,
            END_PROCEDURE,
            CATCH,
            END_IF,
            ELSE,
            ELSEIF,
            END_TRY
    ));

    b.rule(STATEMENT).is(b.firstOf(
        EMPTY_STATEMENT,
        IF_STATEMENT,
        EXPRESSION_STATEMENT,
        ITERATION_STATEMENT,
        CONTINUE_STATEMENT,
        LABELLED_STATEMENT,
        GOTO_STATEMENT,
        BREAK_STATEMENT,
        RETURN_STATEMENT,
        CALL_STATEMENT,
        THROW_STATEMENT,
        ADD_HANDLER_STATEMENT,
        REMOVE_HANDLER_STATEMENT,
        TRY_STATEMENT), b.optional(SEMI), b.optional(SPACING),b.nextNot(b.firstOf(FUNCTION, PROCEDURE, VAR)));


    b.rule(ADD_HANDLER_STATEMENT).is(ADD_HANDLER, IDENTIFIER, DOT , IDENTIFIER, COMMA, IDENTIFIER, DOT, IDENTIFIER);
    b.rule(REMOVE_HANDLER_STATEMENT).is(REMOVE_HANDLER, IDENTIFIER, DOT , IDENTIFIER, COMMA, IDENTIFIER, DOT, IDENTIFIER);

    b.rule(EMPTY_STATEMENT).is(SEMI);

    b.rule(CALL_STATEMENT).is(IDENTIFIER, b.zeroOrMore(b.firstOf(
            b.sequence(ARRAY_LITERAL, DOT, IDENTIFIER),
            b.sequence(CALL_EXPRESSION, DOT, IDENTIFIER),
            b.sequence(DOT, IDENTIFIER)
    )),CALL_EXPRESSION);

    b.rule(LABELLED_STATEMENT).is(TILDA, IDENTIFIER, DDOT);

    b.rule(GOTO_STATEMENT).is(GOTO, TILDA, IDENTIFIER);

    b.rule(EXPRESSION_STATEMENT).is(LEFT_HAND_SIDE_EXPRESSION , EQU, EXPRESSION);


    b.rule(IF_STATEMENT).is(IF, EXPRESSION, THEN,
            b.zeroOrMore(STATEMENT),
            b.optional(ELSEIF_CLAUSE),
            b.optional(ELSE_CLAUSE),
            END_IF
    );
    b.rule(ELSEIF_CLAUSE).is(ELSEIF, EXPRESSION, THEN, b.zeroOrMore(STATEMENT));
    b.rule(ELSE_CLAUSE).is(ELSE, b.zeroOrMore(STATEMENT));
    b.rule(ITERATION_STATEMENT).is(b.firstOf(
        WHILE_STATEMENT,
        FOR_STATEMENT));
    b.rule(WHILE_STATEMENT).is(WHILE, EXPRESSION,  ITERATION_BODY_STATEMENT);
    b.rule(FOR_STATEMENT).is(
                FOR, b.firstOf(EACH_STATEMENT, TO_STATEMENT), ITERATION_BODY_STATEMENT
    );
    b.rule(ITERATION_BODY_STATEMENT).is(DO,
            b.zeroOrMore(STATEMENT),
            END_DO);
    b.rule(TO_STATEMENT).is(IDENTIFIER, EQU, EXPRESSION, TO, EXPRESSION );

    b.rule(EACH_STATEMENT).is(EACH, IDENTIFIER, IN, EXPRESSION);

    b.rule(CONTINUE_STATEMENT).is(CONTINUE);
    b.rule(BREAK_STATEMENT).is(BREAK);
    b.rule(RETURN_STATEMENT).is(RETURN, b.optional(EXPRESSION));
    b.rule(THROW_STATEMENT).is(THROW, b.optional(EXPRESSION));
    b.rule(TRY_STATEMENT).is(
            TRY,
              b.zeroOrMore(STATEMENT),
            CATCH,
              b.zeroOrMore(STATEMENT),
            END_TRY
    );

  }

  /**
   * A.5 Functions and Programs
   */
  private static void functionsAndPrograms(LexerlessGrammarBuilder b) {
    b.rule(PROGRAM).is(b.sequence(
            b.zeroOrMore(VARS_DECLARATION),
            b.zeroOrMore(METHOD_DECLARATION),
            b.zeroOrMore(METHOD_DEFINATION),
            b.zeroOrMore(STATEMENT),
            b.optional(SPACING),
            EOF
    ));
    b.rule(VARS_DECLARATION).is(b.sequence(VARS_DECLARATION_LIST, SEMI));
    b.rule(VARS_DECLARATION_LIST).is(b.sequence(b.optional(DIRECTIVE), VAR, VAR_DECLARATION, b.zeroOrMore(COMMA, VAR_DECLARATION)));
    b.rule(VAR_DECLARATION).is(b.sequence(IDENTIFIER, b.optional(EXPORT)));

    b.rule(METHOD_DECLARATION).is(b.firstOf(PROCEDURE_DECLARATION, FUNCTION_DECLARATION));

    b.rule(PROCEDURE_DECLARATION).is(b.sequence(PROCEDURE, IDENTIFIER, LPARENTHESIS, b.optional(FORMAL_PARAMETER_LIST),RPARENTHESIS, b.optional(EXPORT), SEMI));
    b.rule(FUNCTION_DECLARATION).is(
            b.sequence(FUNCTION, IDENTIFIER, LPARENTHESIS, b.optional(FORMAL_PARAMETER_LIST), RPARENTHESIS, b.optional(EXPORT), SEMI));

    b.rule(METHOD_DEFINATION).is(
            b.optional(ANNOTATION), b.optional(DIRECTIVE), b.firstOf(FUNCTION_DEFINATION, PROCEDURE_DEFINATION));
    b.rule(ANNOTATION).is(b.firstOf(BEFORE, AFTER, AROUND, b.sequence(BEFORE, AFTER)));

    b.rule(DIRECTIVE).is(b.firstOf(ATCLIENT, ATCLIENTATSERVER, ATCLIENTATSERVERNOCONTEXT, ATSERVER, ATSERVERNOCONTEXT));
    b.rule(FUNCTION_DEFINATION).is(
            b.sequence(
                    FUNCTION, IDENTIFIER, LPARENTHESIS, b.optional(FORMAL_PARAMETER_LIST), RPARENTHESIS, b.optional(EXPORT),
                    b.zeroOrMore(VARS_DECLARATION),
                    b.zeroOrMore(STATEMENT),
                    END_FUNCTION

            ));
    b.rule(PROCEDURE_DEFINATION).is(
            b.sequence(PROCEDURE, IDENTIFIER, LPARENTHESIS,
                    b.optional(FORMAL_PARAMETER_LIST), RPARENTHESIS,
                    b.optional(EXPORT),
                    b.zeroOrMore(VARS_DECLARATION),
                    b.zeroOrMore(STATEMENT), END_PROCEDURE

            ));


    b.rule(FORMAL_PARAMETER_LIST).is(FORMAL_PARAMETER, b.zeroOrMore(COMMA, FORMAL_PARAMETER));
    b.rule(FORMAL_PARAMETER).is(
            b.optional(VAL),
            IDENTIFIER,
            b.optional(b.sequence(
                    EQU,
                    EXPRESSION)
            )
    );



  }

  /**
   * Declares some constructs, which ES5 grammar does not support, but script engines support.
   * For example prototype.js version 1.7 has a function declaration in a block, which is invalid under both ES3 and ES5.
   */
  private static Object permissive(Object object) {
    return object;
  }

  private final String internalName;

  private _1CGrammar() {
    String name = name();
    StringBuilder sb = new StringBuilder();
    int i = 0;
    while (i < name.length()) {
      if (name.charAt(i) == '_' && i + 1 < name.length()) {
        i++;
        sb.append(name.charAt(i));
      } else {
        sb.append(Character.toUpperCase(name.charAt(i)));
      }
      i++;
    }
    this.internalName = sb.toString();
  }

  @Override
  public String toString() {
    // This allows to keep compatibility with old XPath expressions
    return internalName;
  }

}
