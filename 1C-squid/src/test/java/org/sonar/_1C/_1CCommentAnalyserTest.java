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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar._1C.parser._1CCommentAnalyser;

import static org.fest.assertions.Assertions.assertThat;

public class _1CCommentAnalyserTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private _1CCommentAnalyser analyser = new _1CCommentAnalyser();

  @Test
  public void content() {
    assertThat(analyser.getContents("// comment")).isEqualTo(" comment");
//    assertThat(analyser.getContents("<!-- comment")).isEqualTo(" comment");
//    assertThat(analyser.getContents("<!-- comment -->")).isEqualTo(" comment ");
  }

  @Test
  public void blank() {
    assertThat(analyser.isBlank(" ")).isTrue();
    assertThat(analyser.isBlank("comment")).isFalse();
  }

  @Test
  public void unknown_type_of_comment() {
    thrown.expect(IllegalArgumentException.class);
    analyser.getContents("");
  }

}
