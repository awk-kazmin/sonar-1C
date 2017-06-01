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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;
import org.sonar.sslr.grammar.GrammarRuleKey;

import java.util.HashSet;
import java.util.Set;

public enum _1СKeyword implements TokenType, GrammarRuleKey {

  // Reserved words

  NULL("null"),
  UNDEF("Undefined","неопределено"),
  TRUE("true", "Истина"),
  FALSE("false", "Ложь"),

  // BOOL

  AND("AND","и"),
  OR("OR","или"),
  NOT("NOT","не"),

  // Keywords


  BREAK("break", "Прервать"),
  CATCH("Except","Исключение"),
  END_TRY("EndTry","КонецПопытки"),
  CONTINUE("continue","Продолжить"),
  DO("do","Цикл"),
  END_DO("EndDo","КонецЦикла"),
  ELSE("else","Иначе"),
  FOR("for","Для"),
  EACH("each","Каждого"),
  IN("in","Из"),
  TO("to","По"),
  FUNCTION("Function","Функция"),
  END_FUNCTION("EndFunction","КонецФункции"),
  PROCEDURE("Procedure","Процедура"),
  END_PROCEDURE("EndProcedure","КонецПроцедуры"),
  IF("if","Если"),
  ELSEIF("ElseIf", "ИначеЕсли"),
  THEN("THEN", "Тогда"),
  END_IF("EndIf", "КонецЕсли"),
  NEW("new", "Новый"),
  RETURN("return", "Возврат"),
  THROW("Raise", "ВызватьИсключение"),
  GOTO("goto", "Перейти"),
  TRY("try", "Попытка"),
  VAR("var", "Перем"),
  WHILE("while", "Пока"),
  REMOVE_HANDLER("RemoveHandler", "УдалитьОбработчик"),
  ADD_HANDLER("AddHandler", "ДобавитьОбработчик"),
  EXECUTE("Execute","Выполнить"),
  ATSERVER("&AtServer","&НаСервере"),
  ATSERVERNOCONTEXT("&AtServerNoContext","&НаСервереБезКонтекста"),
  ATCLIENTATSERVERNOCONTEXT("&AtClientAtServerNoContext","&НаКлиентеНаСервереБезКонтекста"),
  ATCLIENTATSERVER("&AtClientAtServer","&НаКлиентеНаСервере"),
  ATCLIENT("&AtClient","&НаКлиенте"),
  BEFORE("&Before","&Перед"),
  AFTER("&After","&После"),
  AROUND("&Around","&Вместо"),

  // Future reserved words

  VAL("val", "Знач"),
  EXPORT("export","Экспорт");

  private final String en_value;
  private final String ru_value;
  private final String value;

  public Boolean isOne() {
    return is_one;
  }

  private final Boolean is_one;

  private _1СKeyword(String value) {
    this(value, value);
  }

  private _1СKeyword(String en_value, String ru_value) {
    this.en_value = en_value.toUpperCase();
    this.ru_value = ru_value.toUpperCase();
    value = en_value;
    is_one = en_value.equalsIgnoreCase(ru_value);
  }

  public String getName() {
    return name();
  }

  public String getRuValue() {
    return ru_value.toUpperCase();
  }

  public String getValue() {
    return value;
  }

  public boolean hasToBeSkippedFromAst(AstNode node) {
    return false;
  }


  public static String[] keywordValues() {
    _1СKeyword[] keywordsEnum = _1СKeyword.values();
    Set<String> keywords = new HashSet<String>();
    for (int i = 0; i < keywordsEnum.length; i++) {
      keywords.add(keywordsEnum[i].getEnValue());
      if(!keywords.contains(keywordsEnum[i].getRuValue())) {
        keywords.add(keywordsEnum[i].getRuValue());
      }
    }
    return keywords.toArray(new String[keywords.size()]);
  }


  public String getEnValue() {
    return en_value.toUpperCase();
  }
}
