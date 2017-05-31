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
package org.sonar.plugins._1C.xtest;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.plugins._1C._1CPlugin;
import org.sonar.plugins._1C.core._1C;

import static org.fest.assertions.Assertions.assertThat;

public class _1CTestSensorTest {

  private _1C language;
  private Settings settings;
  private _1CTestSensor sensor;

  @Before
  public void setUp() {
    settings = new Settings();
    language = new _1C(settings);
    sensor = new _1CTestSensor(language);
  }

  @Test
  public void test_shouldExecuteOnProject() {
    Project project = mockProject();
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    project.setLanguage(language);
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    settings.setProperty(_1CPlugin._1CTEST_REPORTS_PATH, "1ctest");
    assertThat(sensor.shouldExecuteOnProject(project)).isTrue();
  }

  @Test
  public void test_toString() {
    assertThat(sensor.toString()).isEqualTo("_1CTestSensor");
  }

  private Project mockProject() {
    return new Project("mock");
  }

}
