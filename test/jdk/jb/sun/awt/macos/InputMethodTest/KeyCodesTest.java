/*
 * Copyright (c) 2000-2023 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @test
 * @summary Regression test for JBR-5173 macOS keyboard support rewrite
 * @run shell Runner.sh KeyCodesTest
 * @requires (jdk.version.major >= 8 & os.family == "mac")
 */

import static java.awt.event.KeyEvent.*;

public class KeyCodesTest implements Runnable {
    static private final int VK_BACK_QUOTE_ISO = 0x01000000+0x0060;
    static private final int VK_SECTION = 0x01000000+0x00A7;
    @Override
    public void run() {
        verify('!', VK_EXCLAMATION_MARK, "com.apple.keylayout.French-PC", VK_SLASH);
        verify('"', VK_QUOTEDBL, "com.apple.keylayout.French-PC", VK_3);
        verify('#', VK_NUMBER_SIGN, "com.apple.keylayout.British-PC", VK_BACK_SLASH);
        verify('$', VK_DOLLAR, "com.apple.keylayout.French-PC", VK_CLOSE_BRACKET);
        verify('&', VK_AMPERSAND, "com.apple.keylayout.French-PC", VK_1);
        verify('\'', VK_QUOTE, "com.apple.keylayout.French-PC", VK_4);
        verify('(', VK_LEFT_PARENTHESIS, "com.apple.keylayout.French-PC", VK_5);
        verify(')', VK_RIGHT_PARENTHESIS, "com.apple.keylayout.French-PC", VK_MINUS);
        verify('*', VK_ASTERISK, "com.apple.keylayout.French-PC", VK_BACK_SLASH);
        verify('+', VK_PLUS, "com.apple.keylayout.German", VK_CLOSE_BRACKET);
        verify(',', VK_COMMA, "com.apple.keylayout.ABC", VK_COMMA);
        verify('-', VK_MINUS, "com.apple.keylayout.ABC", VK_MINUS);
        verify('.', VK_PERIOD, "com.apple.keylayout.ABC", VK_PERIOD);
        verify('/', VK_SLASH, "com.apple.keylayout.ABC", VK_SLASH);
        verify(':', VK_COLON, "com.apple.keylayout.French-PC", VK_PERIOD);
        verify(';', VK_SEMICOLON, "com.apple.keylayout.ABC", VK_SEMICOLON);
        verify('<', VK_LESS, "com.apple.keylayout.French-PC", VK_BACK_QUOTE);
        verify('=', VK_EQUALS, "com.apple.keylayout.ABC", VK_EQUALS);
        // TODO: figure out which keyboard layout has VK_GREATER as a key on the primary layer
        verify('@', VK_AT, "com.apple.keylayout.Norwegian", VK_BACK_SLASH);
        verify('[', VK_OPEN_BRACKET, "com.apple.keylayout.ABC", VK_OPEN_BRACKET);
        verify('\\', VK_BACK_SLASH, "com.apple.keylayout.ABC", VK_BACK_SLASH);
        verify(']', VK_CLOSE_BRACKET, "com.apple.keylayout.ABC", VK_CLOSE_BRACKET);
        // TODO: figure out which keyboard layout has VK_CIRCUMFLEX as a key on the primary layer
        verify('_', VK_UNDERSCORE, "com.apple.keylayout.French-PC", VK_8);
        verify('`', VK_BACK_QUOTE, "com.apple.keylayout.ABC", VK_BACK_QUOTE);
        verify('{', VK_BRACELEFT, "com.apple.keylayout.LatinAmerican", VK_QUOTE);
        verify('}', VK_BRACERIGHT, "com.apple.keylayout.LatinAmerican", VK_BACK_SLASH);
        verify('\u00a1', VK_INVERTED_EXCLAMATION_MARK, "com.apple.keylayout.Spanish-ISO", VK_EQUALS);
        // TODO: figure out which keyboard layout has VK_EURO_SIGN as a key on the primary layer
        verify('/', VK_DIVIDE, "com.apple.keylayout.ABC", VK_DIVIDE, VK_SLASH);
        verify('*', VK_MULTIPLY, "com.apple.keylayout.ABC", VK_MULTIPLY, VK_ASTERISK);
        verify('+', VK_ADD, "com.apple.keylayout.ABC", VK_ADD, VK_PLUS);
        verify('-', VK_SUBTRACT, "com.apple.keylayout.ABC", VK_SUBTRACT, VK_MINUS);
        verify('\t', VK_TAB, "com.apple.keylayout.ABC", VK_TAB);
        verify(' ', VK_SPACE, "com.apple.keylayout.ABC", VK_SPACE);

        // Test numpad numbers
        for (int i = 0; i < 10; ++i) {
            verify((char)('0' + i), VK_NUMPAD0 + i, "com.apple.keylayout.ABC", VK_NUMPAD0 + i, VK_0 + i);
        }
        verify('\0', VK_F1, "com.apple.keylayout.ABC", VK_F1);
        verify('\0', VK_F19, "com.apple.keylayout.ABC", VK_F19);

        // Test ANSI/ISO keyboard weirdness
        verify('\u00a7', 0x01000000+0x00A7, "com.apple.keylayout.ABC", VK_SECTION);
        verify('\u00b2', 0x01000000+0x00B2, "com.apple.keylayout.French-PC", VK_SECTION);
        verify('#', VK_NUMBER_SIGN, "com.apple.keylayout.CanadianFrench-PC", VK_SECTION);
        verify('\u00ab', 0x01000000+0x00AB, "com.apple.keylayout.CanadianFrench-PC", VK_BACK_QUOTE_ISO);
        verify('#', VK_NUMBER_SIGN, "com.apple.keylayout.CanadianFrench-PC", VK_BACK_QUOTE);

        // Test extended key codes that don't match the unicode char
        verify('\u00e4', 0x01000000+0x00C4, "com.apple.keylayout.German", VK_QUOTE);
        verify('\u00e5', 0x01000000+0x00C5, "com.apple.keylayout.Norwegian", VK_OPEN_BRACKET);
        verify('\u00e6', 0x01000000+0x00C6, "com.apple.keylayout.Norwegian", VK_QUOTE);
        verify('\u00e7', 0x01000000+0x00C7, "com.apple.keylayout.French-PC", VK_9);
        verify('\u00f1', 0x01000000+0x00D1, "com.apple.keylayout.Spanish-ISO", VK_SEMICOLON);
        verify('\u00f6', 0x01000000+0x00D6, "com.apple.keylayout.German", VK_SEMICOLON);
        verify('\u00f8', 0x01000000+0x00D8, "com.apple.keylayout.Norwegian", VK_SEMICOLON);
    }

    private void verify(char ch, int vk, String layout, int key, int correctKeyCode) {
        InputMethodTest.section("Key code test: " + vk + ", char: " + ch);
        InputMethodTest.layout(layout);
        InputMethodTest.type(key, 0);
        InputMethodTest.expectKeyCode(vk);
        if (ch != 0) {
            InputMethodTest.expect(String.valueOf(ch));
            InputMethodTest.expectTrue(getExtendedKeyCodeForChar(ch) == correctKeyCode, "getExtendedKeyCodeForChar");
        }
    }

    private void verify(char ch, int vk, String layout, int key) {
        verify(ch, vk, layout, key, vk);
    }
}
