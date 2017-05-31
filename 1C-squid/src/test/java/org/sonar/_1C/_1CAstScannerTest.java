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
package org.sonar._1C;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.sonar.sslr.squid.AstScanner;
import org.junit.Test;
import org.sonar._1C.parser._1CAstScanner;
import org.sonar._1C.parser._1CConfiguration;
import org.sonar._1C.api._1CMetric;
import org.sonar.squid.api.SourceFile;
import org.sonar.squid.api.SourceProject;
import org.sonar.squid.indexer.QueryByType;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class _1CAstScannerTest {

  @Test
  public void files() {
    AstScanner<LexerlessGrammar> scanner = _1CAstScanner.create(new _1CConfiguration(Charsets.UTF_8));
    scanner.scanFiles(ImmutableList.of(new File("src/test/resources/metrics/lines.bsl"), new File("src/test/resources/metrics/lines_of_code.bsl")));
    SourceProject project = (SourceProject) scanner.getIndex().search(new QueryByType(SourceProject.class)).iterator().next();
    assertThat(project.getInt(_1CMetric.FILES)).isEqualTo(2);
  }

  @Test
  public void comments() {
    SourceFile file = _1CAstScanner.scanSingleFile(new File("src/test/resources/metrics/comments.bsl"));
    assertThat(file.getInt(_1CMetric.COMMENT_LINES)).isEqualTo(3);
    assertThat(file.getInt(_1CMetric.COMMENT_BLANK_LINES)).isEqualTo(4);
    assertThat(file.getNoSonarTagLines()).contains(10);
    assertThat(file.getNoSonarTagLines().size()).isEqualTo(1);
  }

  @Test
  public void lines() {
    SourceFile file = _1CAstScanner.scanSingleFile(new File("src/test/resources/metrics/lines.bsl"));
    assertThat(file.getInt(_1CMetric.LINES)).isEqualTo(7);
  }

  @Test
  public void lines_of_code() {
    SourceFile file = _1CAstScanner.scanSingleFile(
            new File("src/test/resources/metrics/lines_of_code.bsl"));
    assertThat(file.getInt(_1CMetric.LINES_OF_CODE)).isEqualTo(3);
  }

  @Test
  public void statements() {
    //SourceFile file = _1CAstScanner.scanSingleFile(new File("src/test/resources/metrics/statements.bsl"));
    //assertThat(file.getInt(_1CMetric.STATEMENTS)).isEqualTo(11);
  }

  @Test
  public void functions() {
    SourceFile file = _1CAstScanner.scanSingleFile(
            new File("src/test/resources/metrics/functions.bsl"));
    assertThat(file.getInt(_1CMetric.METHODS)).isEqualTo(3);
    //assertThat(file.getInt(_1CMetric.STATEMENTS)).isEqualTo(7);
  }

  @Test
  public void complexity() {
    SourceFile file = _1CAstScanner.scanSingleFile(new File("src/test/resources/metrics/complexity.bsl"));
    assertThat(file.getInt(_1CMetric.COMPLEXITY)).isEqualTo(6);
  }

}
