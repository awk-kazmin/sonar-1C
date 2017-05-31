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

import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.PunctuatorChannel;
import com.sonar.sslr.impl.channel.UnknownCharacterChannel;
import org.sonar._1C.parser._1CConfiguration;
import org.sonar._1C.api._1СKeyword;
import org.sonar._1C.api._1CPunctuator;
import org.sonar._1C.api._1CTokenType;

import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.commentRegexp;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;

public final class _1СLexer {

  private _1СLexer() {
  }



  public static final String NUMERIC_LITERAL = "(?:"
      // Decimal
      + "[0-9]++\\.([0-9]++)?+"
      // Decimal
      // Integer Literals
      // Decimal and Octal
      + "|[0-9]++"
      + ")";

  public static final String STRING_LITERAL = "(?:"
      + "\"([^\"]|[\"]{2})*\""  // simple text literal
      + ")";

  public static final String DATE_LITERAL = "'\\d{4}\\d{2}(\\d{2}|\\d{4}|\\d{6}|\\d{8})'";

  public static final String SINGLE_LINE_COMMENT = "//[^\\n\\r]*+";
  public static final String PREPOCESSOR = "#[^\\n\\r]*+";

  public static final String COMMENT = "(?:" + SINGLE_LINE_COMMENT + ")";

  private static final String UNICODE_LETTER = "[A-Za-zА-Яа-яЁё]";
  private static final String UNICODE_DIGIT = "\\p{Nd}";
  private static final String IDENTIFIER_START = "(?:[_&" + UNICODE_LETTER + "])";
  public static final String IDENTIFIER_PART = "(?:" + IDENTIFIER_START + "|[" + UNICODE_DIGIT + "])";

  public static final String IDENTIFIER = IDENTIFIER_START + IDENTIFIER_PART + "*+";

  /**
   * LF, CR, LS, PS
   */
  public static final String LINE_TERMINATOR = "\\n\\r\\u2028\\u2029";

  /**
   * Tab, Vertical Tab, Form Feed, Space, No-break space, Byte Order Mark, Any other Unicode "space separator"
   */
  public static final String WHITESPACE = "\\t\\u000B\\f\\u0020\\u00A0\\uFEFF\\p{Zs}";

  public static Lexer create(_1CConfiguration conf) {
    return Lexer.builder()
        .withCharset(conf.getCharset())

        .withFailIfNoChannelToConsumeOneCharacter(true)

        // Channels, which consumes more frequently should come first.
        // Whitespace character occurs more frequently than any other, and thus come first:
        .withChannel(new BlackHoleChannel("[" + LINE_TERMINATOR + WHITESPACE + "]++"))

        // Comments
        .withChannel(commentRegexp(COMMENT))
        .withChannel(commentRegexp(PREPOCESSOR))

        // String Literals
        .withChannel(regexp(GenericTokenType.LITERAL, STRING_LITERAL))

        .withChannel(regexp(_1CTokenType.NUMERIC_LITERAL, NUMERIC_LITERAL))

        .withChannel(new _1CIdentifierAndKeywordChannel(IDENTIFIER, _1СKeyword.values()))
        .withChannel(new PunctuatorChannel(_1CPunctuator.values()))

        .withChannel(new UnknownCharacterChannel(true))

        .build();
  }
}
