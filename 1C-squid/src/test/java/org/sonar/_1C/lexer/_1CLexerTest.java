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
package org.sonar._1C.lexer;

import com.google.common.base.Charsets;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BomCharacterChannel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar._1C.parser._1CConfiguration;
import org.sonar._1C.api._1CKeyword;
import org.sonar._1C.api._1CTokenType;

import static com.sonar.sslr.test.lexer.LexerMatchers.hasComment;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasTokens;
import static org.junit.Assert.assertThat;

public class _1CLexerTest {

  private static Lexer lexer;

  @BeforeClass
  public static void init() {
    lexer = _1CLexer.create(new _1CConfiguration(Charsets.UTF_8));
  }


  @Test
  public void lexInlineComment() {
    assertThat(lexer.lex("// My Comment \n new line"), hasComment("// My Comment "));
    assertThat(lexer.lex("//"), hasComment("//"));
  }


  @Test
  public void decimalLiteral() {
    assertThat(lexer.lex("0"), hasToken("0", _1CTokenType.NUMERIC_LITERAL));
    assertThat(lexer.lex("123"), hasToken("123", _1CTokenType.NUMERIC_LITERAL));

    assertThat(lexer.lex("123.456"), hasToken("123.456", _1CTokenType.NUMERIC_LITERAL));

  }



  @Test
  public void stringLiteral() {
    assertThat("empty", lexer.lex("\"\""), hasToken("\"\"", GenericTokenType.LITERAL));

    assertThat(lexer.lex("\"hello world\""), hasToken("\"hello world\"", GenericTokenType.LITERAL));

    assertThat("escaped double quote", lexer.lex("\"\"\"\""), hasToken("\"\"\"\"", GenericTokenType.LITERAL));

  }

  @Test
  public void nullLiteral() {
    assertThat(lexer.lex("null"), hasToken("NULL", _1CKeyword.NULL));
    assertThat(lexer.lex("NULL"), hasToken("NULL", _1CKeyword.NULL));
    assertThat(lexer.lex("Null"), hasToken("NULL", _1CKeyword.NULL));
  }

  @Test
  public void undefLiteral() {
    assertThat(lexer.lex("Неопределено"), hasToken("НЕОПРЕДЕЛЕНО", _1CKeyword.UNDEF));
  }

  @Test
  public void export() {
    assertThat(lexer.lex("Export"), hasToken("EXPORT", _1CKeyword.EXPORT));
    assertThat(lexer.lex("Экспорт"), hasToken("ЭКСПОРТ", _1CKeyword.EXPORT));
  }

  @Test
  public void booleanLiteral() {
    assertThat(lexer.lex("false"), hasToken("FALSE", _1CKeyword.FALSE));
    assertThat(lexer.lex("true"), hasToken("TRUE", _1CKeyword.TRUE));
  }

  @Test
  public void identifier() {
    assertThat(lexer.lex("_"), hasToken("_", GenericTokenType.IDENTIFIER));
    assertThat(lexer.lex("identifier"), hasToken("IDENTIFIER", GenericTokenType.IDENTIFIER));
    assertThat(lexer.lex("i42"), hasToken("I42", GenericTokenType.IDENTIFIER));
  }

  @Test
  public void bom() {
    assertThat(lexer.lex(Character.toString((char) BomCharacterChannel.BOM_CHAR)), hasTokens("EOF"));
  }

}
