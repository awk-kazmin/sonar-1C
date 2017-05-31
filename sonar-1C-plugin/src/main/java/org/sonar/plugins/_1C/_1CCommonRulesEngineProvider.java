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

import org.sonar.api.resources.Project;
import org.sonar.commonrules.api.CommonRulesEngine;
import org.sonar.commonrules.api.CommonRulesEngineProvider;
import org.sonar.plugins._1C.core._1C;

public class _1CCommonRulesEngineProvider extends CommonRulesEngineProvider {

  public _1CCommonRulesEngineProvider() {
    super();
  }

  public _1CCommonRulesEngineProvider(Project project) {
    super(project);
  }

  @Override
  protected void doActivation(CommonRulesEngine engine) {
    engine.activateRule("DuplicatedBlocks");
    engine.activateRule("InsufficientCommentDensity");
    engine.activateRule("InsufficientLineCoverage");
    engine.activateRule("InsufficientBranchCoverage");
  }

  @Override
  protected String getLanguageKey() {
    return _1C.KEY;
  }

}
