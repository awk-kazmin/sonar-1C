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
package org.sonar.plugins._1C;

import com.google.common.collect.ImmutableList;
import org.sonar.api.Extension;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;
import org.sonar.plugins._1C.colorizer._1CColorizerFormat;
import org.sonar.plugins._1C.core._1C;
import org.sonar.plugins._1C.core._1CSourceImporter;
import org.sonar.plugins._1C.cpd._1CCpdMapping;
import org.sonar.plugins._1C.xtest._1CTestSensor;
import org.sonar.plugins._1C.xtestdriver._1CTestDriverSensor;
import org.sonar.plugins._1C.lcov.LCOVSensor;

import java.util.List;

@Properties({
  // Global 1C settings
  @Property(
    key = _1CPlugin.FILE_SUFFIXES_KEY,
    defaultValue = _1CPlugin.FILE_SUFFIXES_DEFVALUE,
    name = "File suffixes",
    description = "Comma-separated list of suffixes for files to analyze.",
    global = true,
    project = true),
  @Property(
    key = _1CPlugin.LCOV_REPORT_PATH,
    defaultValue = _1CPlugin.LCOV_REPORT_PATH_DEFAULT_VALUE,
    name = "LCOV file",
    description = "Path (absolute or relative) to the file with LCOV data.",
    global = true,
    project = true),
  @Property(
    key = _1CPlugin._1CTESTDRIVER_REPORTS_PATH,
    defaultValue = _1CPlugin._1CTESTDRIVER_REPORTS_PATH_DEFAULT_VALUE,
    name = "XTestDriver output folder",
    description = "Folder where XTestDriver unit test reports are located.",
    global = true,
    project = true),
  @Property(
    key = _1CPlugin._1CTEST_REPORTS_PATH,
    defaultValue = _1CPlugin._1CTEST_REPORTS_PATH_DEFAULT_VALUE,
    name = "1СTest output folder",
    description = "Folder where XUnit unit test reports are located.",
    global = true,
    project = true)
})
public class _1CPlugin extends SonarPlugin {

  public List<Class<? extends Extension>> getExtensions() {
    return ImmutableList.of(
        _1C.class,
        _1CSourceImporter.class,
        _1CColorizerFormat.class,
        _1CCpdMapping.class,

        _1CSquidSensor.class,
        _1CRuleRepository.class,
        _1CProfile.class,

        _1CCommonRulesEngineProvider.class,

        LCOVSensor.class,
        _1CTestDriverSensor.class,
        _1CTestSensor.class);
  }

  // Global JavaScript constants
  public static final String FALSE = "false";

  public static final String FILE_SUFFIXES_KEY = "sonar.1c.file.suffixes";
  public static final String FILE_SUFFIXES_DEFVALUE = ".bsl";

  public static final String PROPERTY_PREFIX = "sonar.1c";

  public static final String LCOV_REPORT_PATH = PROPERTY_PREFIX + ".lcov.reportPath";
  public static final String LCOV_REPORT_PATH_DEFAULT_VALUE = "";

  public static final String _1CTESTDRIVER_REPORTS_PATH = PROPERTY_PREFIX + ".1ctestdriver.reportsPath";
  public static final String _1CTESTDRIVER_REPORTS_PATH_DEFAULT_VALUE = "";

  public static final String _1CTEST_REPORTS_PATH = PROPERTY_PREFIX + ".1ctest.reportsPath";
  public static final String _1CTEST_REPORTS_PATH_DEFAULT_VALUE = "";

}
