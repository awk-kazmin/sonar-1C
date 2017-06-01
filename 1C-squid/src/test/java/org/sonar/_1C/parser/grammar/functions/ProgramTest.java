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
package org.sonar._1C.parser.grammar.functions;

import com.google.common.base.Joiner;
import org.junit.Test;
import org.sonar._1C.parser._1CGrammar;
import org.sonar.sslr.parser.LexerlessGrammar;

import static org.sonar.sslr.tests.Assertions.assertThat;

public class ProgramTest {

  LexerlessGrammar g = _1CGrammar.createGrammar();

  @Test
  public void realLife() {
    assertThat(g.rule(_1CGrammar.METHOD_DEFINATION))
    .matches("Function A() EndFunction")
    .matches("function A() a=5 EndFunction")
    .notMatches("function1 A() EndFunction")
    ;

    assertThat(g.rule(_1CGrammar.PROGRAM))
        .matches("")
        .matches("VAR a;")
        .matches("IF (TRUE) THEN ENDIF;")
        .matches("document.write(\"Hello world\");")
        .matches(code("FUNCTION func()",
                "doSomething();",
                "ENDFUNCTION")
        );

    assertThat(g.rule(_1CGrammar.PROGRAM)).matches(code(
        //"//#!/usr/bin/env node",
        "FUNCTION func() ENDFUNCTION"));


    // http://www.w3schools.com/js/tryit.asp?filename=tryjs_ifthenelse
    assertThat(g.rule(_1CGrammar.PROGRAM)).matches(code(
        "VAR d;",
        "VAR time;",
        "IF (time < 10) THEN",
        "  document.write(\"Good morning\");",
        "ELSE ",
        "  document.write(\"Good day\");",
        "ENDIF"));
  }

  private static String code(String... lines) {
    return Joiner.on("\n").join(lines);
  }

}
