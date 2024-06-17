package org.example.demoboard.util;

import org.owasp.encoder.Encode;

public class XssFilter {
    public static String filter(String input) {
        return Encode.forHtmlContent(input);
    }
}