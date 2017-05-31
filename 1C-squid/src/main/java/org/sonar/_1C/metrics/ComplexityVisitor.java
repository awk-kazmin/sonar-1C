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
package org.sonar._1C.metrics;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.squid.SquidAstVisitor;
import org.sonar._1C.api._1CMetric;
import org.sonar._1C.api._1CPunctuator;
import org.sonar._1C.parser._1СGrammar;
import org.sonar.sslr.parser.LexerlessGrammar;

public class ComplexityVisitor extends SquidAstVisitor<LexerlessGrammar> {

  @Override
  public void init() {
    subscribeTo(
        // Branching nodes
        _1СGrammar.IF_STATEMENT,
        _1СGrammar.ITERATION_STATEMENT,
        _1СGrammar.CATCH_,
        _1СGrammar.RETURN_STATEMENT,
        _1СGrammar.THROW_STATEMENT,
        // Expressions
        _1CPunctuator.QUERY
        //_1CPunctuator.ANDAND,
        //_1CPunctuator.OROR
    );
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.is(_1СGrammar.RETURN_STATEMENT) && isLastReturnStatement(astNode)) {
      return;
    }
    getContext().peekSourceCode().add(_1CMetric.COMPLEXITY, 1);
  }

  private boolean isLastReturnStatement(AstNode astNode) {
    AstNode parent = astNode.getParent().getParent();
    return parent.is(_1СGrammar.SOURCE_ELEMENT);
  }

}
