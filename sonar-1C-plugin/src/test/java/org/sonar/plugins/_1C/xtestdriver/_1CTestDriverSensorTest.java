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
package org.sonar.plugins._1C.xtestdriver;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.plugins._1C._1CPlugin;
import org.sonar.plugins._1C.core._1C;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class _1CTestDriverSensorTest {

  private _1CTestDriverSensor sensor;
  private SensorContext context;
  private _1C language;
  private Settings settings;
  private ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);

  @Before
  public void init() {
    settings = new Settings();
    language = new _1C(settings);
    sensor = new _1CTestDriverSensor(language);
    context = mock(SensorContext.class);
  }

  @Test
  public void test_shouldExecuteOnProject() {
    Project project = mockProject(new Java());
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    project = mockProject(language);
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    settings.setProperty(_1CPlugin._1CTESTDRIVER_REPORTS_PATH, "xtestdriver");
    assertThat(sensor.shouldExecuteOnProject(project)).isTrue();
  }

  @Test
  public void testAnalyseUnitTests() {
    settings.setProperty(_1CPlugin._1CTESTDRIVER_REPORTS_PATH, "target/xtestdriver");

    when(fileSystem.getSourceCharset()).thenReturn(Charset.defaultCharset());

    File baseDir = new File("src/test/resources/org/sonar/plugins/_1C/xtestdriver/sensortests");
    when(fileSystem.getBasedir()).thenReturn(baseDir);
    when(fileSystem.getTestDirs()).thenReturn(Arrays.asList(new File(baseDir, "test")));

    Project project = mockProject(language);

    sensor.analyse(project, context);

    verify(context).saveMeasure((Resource) anyObject(), eq(CoreMetrics.TESTS), eq(2.0));
    verify(context).saveMeasure((Resource) anyObject(), eq(CoreMetrics.SKIPPED_TESTS), eq(0.0));
    verify(context).saveMeasure((Resource) anyObject(), eq(CoreMetrics.TEST_ERRORS), eq(0.0));
    verify(context).saveMeasure((Resource) anyObject(), eq(CoreMetrics.TEST_FAILURES), eq(0.0));
    verify(context).saveMeasure((Resource) anyObject(), eq(CoreMetrics.TEST_EXECUTION_TIME), eq(700.0));
    verify(context).saveMeasure((Resource) anyObject(), eq(CoreMetrics.TEST_SUCCESS_DENSITY), eq(100.0));

    verify(context).saveSource((Resource) anyObject(), eq("This is content for PersonTest.bsl file used in unit tests."));
  }

  @Test
  public void testGetUnitTestFileName() {
    assertEquals("com/company/PersonTest.bsl", sensor.getUnitTestFileName("Chrome_16091263_Windows.com.company.PersonTest"));
    assertEquals("PersonTest.bsl", sensor.getUnitTestFileName("Chrome_16091263_Windows.PersonTest"));
  }

  @Test
  public void test_toString() {
    assertThat(sensor.toString()).isEqualTo("_1CTestDriverSensor");
  }

  private Project mockProject(final Language language) {
    return new Project("mock") {
      @Override
      public ProjectFileSystem getFileSystem() {
        return fileSystem;
      }

      @Override
      public Language getLanguage() {
        return language;
      }
    };
  }


}
