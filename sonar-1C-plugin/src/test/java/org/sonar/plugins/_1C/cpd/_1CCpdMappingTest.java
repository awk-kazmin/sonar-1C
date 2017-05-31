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
package org.sonar.plugins._1C.cpd;

import org.junit.Test;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins._1C.core._1C;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class _1CCpdMappingTest {

  @Test
  public void test() {
    _1C language = mock(_1C.class);
    ProjectFileSystem fs = mock(ProjectFileSystem.class);
    _1CCpdMapping mapping = new _1CCpdMapping(language, fs);
    assertThat(mapping.getLanguage()).isSameAs(language);
    assertThat(mapping.getTokenizer()).isInstanceOf(_1CTokenizer.class);
  }

}
