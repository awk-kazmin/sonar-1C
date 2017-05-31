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

import org.sonar._1C.checks.CheckList;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.plugins._1C.core._1C;

import java.util.List;

public class _1CRuleRepository extends RuleRepository {

  private final AnnotationRuleParser annotationRuleParser;

  public _1CRuleRepository(AnnotationRuleParser annotationRuleParser) {
    super(CheckList.REPOSITORY_KEY, _1C.KEY);
    setName(CheckList.REPOSITORY_NAME);
    this.annotationRuleParser = annotationRuleParser;
  }

  @Override
  public List<Rule> createRules() {
    return annotationRuleParser.parse(CheckList.REPOSITORY_KEY, CheckList.getChecks());
  }

}
