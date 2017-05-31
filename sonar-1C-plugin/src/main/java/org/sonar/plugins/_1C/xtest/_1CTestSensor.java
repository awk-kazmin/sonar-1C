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

import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.plugins._1C._1CPlugin;
import org.sonar.plugins._1C.core._1C;
import org.sonar.plugins._1C.xtestdriver._1CTestDriverSensor;

import java.io.File;

public class _1CTestSensor extends _1CTestDriverSensor {

  public _1CTestSensor(_1C _1c) {
    super(_1c);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return _1c.equals(project.getLanguage())
      && StringUtils.isNotBlank(_1c.getSettings().getString(_1CPlugin._1CTEST_REPORTS_PATH));
  }

  public void analyse(Project project, SensorContext context) {
    String _1CTestDriverFolder = _1c.getSettings().getString(_1CPlugin._1CTEST_REPORTS_PATH);
    collect(project, context, new File(project.getFileSystem().getBasedir(), _1CTestDriverFolder));
  }

  protected org.sonar.api.resources.File getUnitTestFileResource(String classKey) {
    // For JsTest assume notation com/company/MyJsTest.js that maps directly to file name
    return new org.sonar.api.resources.File(classKey);
  }

  protected String getUnitTestFileName(String className) {
    return className;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
