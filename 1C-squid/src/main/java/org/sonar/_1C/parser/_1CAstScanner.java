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

import com.google.common.base.Charsets;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.Parser;
import com.sonar.sslr.squid.AstScanner;
import com.sonar.sslr.squid.SourceCodeBuilderCallback;
import com.sonar.sslr.squid.SourceCodeBuilderVisitor;
import com.sonar.sslr.squid.SquidAstVisitor;
import com.sonar.sslr.squid.SquidAstVisitorContextImpl;
import com.sonar.sslr.squid.metrics.CommentsVisitor;
import com.sonar.sslr.squid.metrics.CounterVisitor;
import com.sonar.sslr.squid.metrics.LinesOfCodeVisitor;
import com.sonar.sslr.squid.metrics.LinesVisitor;
import org.sonar._1C.api._1CMetric;
import org.sonar._1C.api._1CTokenType;
import org.sonar._1C.metrics.ComplexityVisitor;
import org.sonar.squid.api.*;
import org.sonar.squid.indexer.QueryByType;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.io.File;
import java.util.Collection;

public final class _1CAstScanner {

  private _1CAstScanner() {
  }

  /**
   * Helper method for testing checks without having to deploy them on a Sonar instance.
   */
  public static SourceFile scanSingleFile(File file, SquidAstVisitor<LexerlessGrammar>... visitors) {
    if (!file.isFile()) {
      throw new IllegalArgumentException("File '" + file + "' not found.");
    }
    AstScanner<LexerlessGrammar> scanner = create(new _1CConfiguration(Charsets.UTF_8), visitors);
    scanner.scanFile(file);
    Collection<SourceCode> sources = scanner.getIndex().search(new QueryByType(SourceFile.class));
    if (sources.size() != 1) {
      throw new IllegalStateException("Only one SourceFile was expected whereas " + sources.size() + " has been returned.");
    }
    return (SourceFile) sources.iterator().next();
  }

  public static AstScanner<LexerlessGrammar> create(
          _1CConfiguration conf, SquidAstVisitor<LexerlessGrammar>... visitors) {
    final SquidAstVisitorContextImpl<LexerlessGrammar> context = new SquidAstVisitorContextImpl<LexerlessGrammar>(new SourceProject("1С Project"));
    final Parser<LexerlessGrammar> parser = _1CParser.create(conf);

    AstScanner.Builder<LexerlessGrammar> builder = AstScanner.<LexerlessGrammar> builder(context).setBaseParser(parser);

    /* Metrics */
    builder.withMetrics(_1CMetric.values());

    /* Comments */
    builder.setCommentAnalyser(new _1CCommentAnalyser());

    /* Files */
    builder.setFilesMetric(_1CMetric.FILES);

    /* Functions */
    builder.withSquidAstVisitor(CounterVisitor.<LexerlessGrammar> builder()
            .setMetricDef(_1CMetric.METHODS)
            .subscribeTo(
                    _1CGrammar.METHOD_DEFINATION
            ).build());


    builder.withSquidAstVisitor(CounterVisitor.<LexerlessGrammar> builder()
            .setMetricDef(_1CMetric.FUNCTIONS)
            .subscribeTo(
                    _1CGrammar.FUNCTION_DEFINATION
            ).build());

    builder.withSquidAstVisitor(CounterVisitor.<LexerlessGrammar> builder()
            .setMetricDef(_1CMetric.PROCEDURES)
            .subscribeTo(
                    _1CGrammar.PROCEDURE_DEFINATION
            ).build());

    builder.withSquidAstVisitor(
            new SourceCodeBuilderVisitor<LexerlessGrammar>(
                    new SourceCodeBuilderCallback() {
                      public SourceCode createSourceCode(SourceCode parentSourceCode, AstNode astNode) {
                        AstNode identifier = astNode.getFirstChild(_1CTokenType.IDENTIFIER);
                        final String functionName = identifier == null ? "anonymous" : identifier.getTokenValue();
                        final String fileKey = parentSourceCode.isType(SourceFile.class) ? parentSourceCode.getKey() : parentSourceCode.getParent(SourceFile.class).getKey();
                        SourceMethod function = new SourceMethod(fileKey + ":" + functionName + ":" + astNode.getToken().getLine() + ":" + astNode.getToken().getColumn());
                        function.setStartAtLine(astNode.getTokenLine());
                        return function;
                      }
                    },
                    _1CGrammar.METHOD_DEFINATION));


    /* Metrics */
    builder.withSquidAstVisitor(new LinesVisitor<LexerlessGrammar>(_1CMetric.LINES));
    builder.withSquidAstVisitor(new LinesOfCodeVisitor<LexerlessGrammar>(_1CMetric.LINES_OF_CODE));
    builder.withSquidAstVisitor(CommentsVisitor.<LexerlessGrammar> builder().withCommentMetric(_1CMetric.COMMENT_LINES)
        .withBlankCommentMetric(_1CMetric.COMMENT_BLANK_LINES)
        .withNoSonar(true)
        .withIgnoreHeaderComment(conf.getIgnoreHeaderComments())
        .build());
//    builder.withSquidAstVisitor(CounterVisitor.<LexerlessGrammar> builder()
//        .setMetricDef(_1CMetric.STATEMENTS)
//        .subscribeTo(
//                _1СGrammar.IF_STATEMENT,
//                _1СGrammar.EXPRESSION_STATEMENT,
//                _1СGrammar.ITERATION_STATEMENT,
//                _1СGrammar.CONTINUE_STATEMENT,
//                _1СGrammar.LABELLED_STATEMENT,
//                _1СGrammar.GOTO_STATEMENT,
//                _1СGrammar.BREAK_STATEMENT,
//                _1СGrammar.RETURN_STATEMENT,
//                _1СGrammar.CALL_STATEMENT,
//                _1СGrammar.THROW_STATEMENT,
//                _1СGrammar.TRY_STATEMENT)
//        .build());

    builder.withSquidAstVisitor(new ComplexityVisitor());

    /* External visitors (typically Check ones) */
    for (SquidAstVisitor<LexerlessGrammar> visitor : visitors) {
      if (visitor instanceof CharsetAwareVisitor) {
        ((CharsetAwareVisitor) visitor).setCharset(conf.getCharset());
      }
      builder.withSquidAstVisitor(visitor);
    }

    return builder.build();
  }

}
