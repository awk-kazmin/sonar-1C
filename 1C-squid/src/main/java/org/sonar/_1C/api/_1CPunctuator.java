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
package org.sonar._1C.api;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;
import org.sonar.sslr.grammar.GrammarRuleKey;

public enum _1CPunctuator implements TokenType, GrammarRuleKey {

  LPARENTHESIS("("),
  RPARENTHESIS(")"),
  LBRACKET("["),
  RBRACKET("]"),
  DOT("."),
  TILDA("~"),
  SEMI(";"),
  DDOT(":"),
  COMMA(","),
  LT("<"),
  GT(">"),
  LE("<="),
  GE(">="),
  NOTEQUAL("<>"),
  PLUS("+"),
  MINUS("-"),
  STAR("*"),
  MOD("%"),
  DIV("/"),

  QUERY("?"),
  EQU("=");

  private final String value;

  private _1CPunctuator(String word) {
    this.value = word;
  }

  public String getName() {
    return name();
  }

  public String getValue() {
    return value;
  }

  public boolean hasToBeSkippedFromAst(AstNode node) {
    return false;
  }

}
