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

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class _1CTokenTypeTest {

  @Test
  public void test() {
    assertThat(_1CTokenType.values().length).isEqualTo(4);

    for (_1CTokenType type : _1CTokenType.values()) {
      assertThat(type.getName()).isEqualTo(type.name());
      assertThat(type.getValue()).isEqualTo(type.name());
      assertThat(type.hasToBeSkippedFromAst(null)).isFalse();
    }
  }

}
