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


import com.google.common.collect.ImmutableMap;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;
import org.sonar.channel.Channel;
import org.sonar.channel.CodeReader;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar._1C.api._1CKeyword;

import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;

public class _1CIdentifierAndKeywordChannel extends Channel<Lexer> {

    private final Map<String, TokenType> keywordsMap;
    private final StringBuilder tmpBuilder = new StringBuilder();
    private final Matcher matcher;
    private final Token.Builder tokenBuilder = Token.builder();

    public _1CIdentifierAndKeywordChannel(String regexp, _1CKeyword[]... keywordSets) {
        ImmutableMap.Builder<String, TokenType> keywordsMapBuilder = ImmutableMap.builder();
        for (_1CKeyword[] keywords : keywordSets) {
            for (_1CKeyword keyword : keywords) {
                String en_keywordValue = keyword.getEnValue();
                String ru_keywordValue = keyword.getRuValue();
                keywordsMapBuilder.put(en_keywordValue, keyword);
                if(!keyword.isOne())
                    keywordsMapBuilder.put(ru_keywordValue, keyword);
            }
        }
        this.keywordsMap = keywordsMapBuilder.build();
        matcher = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE| Pattern.UNICODE_CASE).matcher("");
    }

    @Override
    public boolean consume(CodeReader code, Lexer lexer) {
        if (code.popTo(matcher, tmpBuilder) > 0) {
            String word = tmpBuilder.toString();
            String wordOriginal = word;
            word = word.toUpperCase();

            TokenType keywordType = keywordsMap.get(word);
            Token token = tokenBuilder
                    .setType(keywordType == null ? IDENTIFIER : keywordType)
                    .setValueAndOriginalValue(word, wordOriginal)
                    .setURI(lexer.getURI())
                    .setLine(code.getPreviousCursor().getLine())
                    .setColumn(code.getPreviousCursor().getColumn())
                    .build();

            lexer.addToken(token);

            tmpBuilder.delete(0, tmpBuilder.length());
            return true;
        }
        return false;
    }

}
