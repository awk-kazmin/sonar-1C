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
package org.sonar.plugins._1C.lcov;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.InputFileUtils;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.plugins._1C._1CPlugin;
import org.sonar.plugins._1C.core._1C;

import java.io.File;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class LCOVSensorTest {

  private final File baseDir = new File("src/test/resources/org/sonar/plugins/_1C/xtestdriver/sensortests/main");

  private LCOVSensor sensor;
  private SensorContext context;
  private Settings settings;
  private ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);

  @Before
  public void init() {
    settings = new Settings();
    settings.setProperty(_1CPlugin.LCOV_REPORT_PATH, "xTestDriver.conf-coverage.dat");
    sensor = new LCOVSensor(new _1C(settings));
    context = mock(SensorContext.class);
  }

  @Test
  public void test_should_execute() {
    Project project = mockProject(new Java());
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    project = mockProject(new _1C(settings));
    assertThat(sensor.shouldExecuteOnProject(project)).isTrue();

    settings.setProperty(_1CPlugin.LCOV_REPORT_PATH, "");
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();
  }

  @Test
  public void report_not_found() throws Exception {
    Project project = mockProject(new _1C(settings));
    when(fileSystem.resolvePath("xTestDriver.conf-coverage.dat"))
        .thenReturn(new File("not-found"));

    sensor.analyse(project, context);

    verifyZeroInteractions(context);
  }

  @Test
  public void testFileInXTestDriverCoverageReport() {
    when(fileSystem.mainFiles(_1C.KEY)).thenReturn(Arrays.asList(InputFileUtils.create(baseDir, "Person.bsl")));
    Project project = mockProject(new _1C(settings));

    when(fileSystem.resolvePath("xTestDriver.conf-coverage.dat"))
        .thenReturn(new File("src/test/resources/org/sonar/plugins/_1C/xtestdriver/xTestDriver.conf-coverage.dat"));

    sensor.analyse(project, context);
    verify(context, times(3)).saveMeasure((Resource) anyObject(), (Measure) anyObject());
  }

  @Test
  public void testFileNotInXTestDriverCoverageReport() {
    InputFile inputFile = InputFileUtils.create(baseDir, "another.bsl");
    when(fileSystem.mainFiles(_1C.KEY)).thenReturn(Arrays.asList(inputFile));
    Project project = mockProject(new _1C(settings));

    when(fileSystem.resolvePath("xTestDriver.conf-coverage.dat"))
        .thenReturn(new File("src/test/resources/org/sonar/plugins/_1C/xtestdriver/xTestDriver.conf-coverage.dat"));

    when(context.getMeasure(org.sonar.api.resources.File.fromIOFile(inputFile.getFile(), project), CoreMetrics.LINES)).thenReturn(
        new Measure(CoreMetrics.LINES, (double) 20));
    when(context.getMeasure(org.sonar.api.resources.File.fromIOFile(inputFile.getFile(), project), CoreMetrics.NCLOC)).thenReturn(
        new Measure(CoreMetrics.LINES, (double) 22));

    sensor.analyse(project, context);

    verify(context).saveMeasure((Resource) anyObject(), eq(CoreMetrics.LINES_TO_COVER), eq(22.0));
    verify(context).saveMeasure((Resource) anyObject(), eq(CoreMetrics.UNCOVERED_LINES), eq(22.0));
  }

  @Test
  public void test_toString() {
    assertThat(sensor.toString()).isEqualTo("LCOVSensor");
  }

  private Project mockProject(final Language language) {
    Project project = new Project("dummy") {

      public ProjectFileSystem getFileSystem() {
        return fileSystem;
      }

      public Language getLanguage() {
        return language;
      }

      @Override
      public String getLanguageKey() {
        return language.getKey();
      }
    };

    return project;
  }

}
