package edu.wpi.first.tableviewer;

import java.util.Locale;

public class StringUtil {
    public static String hex(char ch) {
        return Integer.toHexString(ch).toLowerCase(Locale.ENGLISH);
    }

    public static void escapeString(StringBuilder out, String str, boolean inArray) {
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            // handle unicode
            /*if (ch > 0xfff) {
                out.append("\\u" + hex(ch));
            } else if (ch > 0xff) {
                out.append("\\u0" + hex(ch));
            } else if (ch > 0x7f) {
                out.append("\\u00" + hex(ch));
            } else*/ if (ch < 32) {
                switch (ch) {
                    case '\b':
                        out.append("\\b");
                        break;
                    case '\n':
                        out.append("\\n");
                        break;
                    case '\t':
                        out.append("\\t");
                        break;
                    case '\f':
                        out.append("\\f");
                        break;
                    case '\r':
                        out.append("\\r");
                        break;
                    default:
                        if (ch > 0xf) {
                            out.append("\\u00" + hex(ch));
                        } else {
                            out.append("\\u000" + hex(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case ',': case ']':
                        if (inArray) {
                            out.append('\\');
                        }
                        out.append(ch);
                        break;
                    case '\\' :
                        out.append("\\\\");
                        break;
                    default:
                        out.append(ch);
                        break;
                }
            }
        }
    }

    public static String escapeString(String str, boolean inArray) {
        StringBuilder out = new StringBuilder();
        escapeString(out, str, inArray);
        return out.toString();
    }

    public static void unescapeString(StringBuilder out, String str) {
        int sz = str.length();
        StringBuilder unicode = new StringBuilder(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == 4) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        out.append((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new NumberFormatException("Unable to parse unicode value: " + unicode);
                    }
                }
                continue;
            }
            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;
                switch (ch) {
                    case 'r':
                        out.append('\r');
                        break;
                    case 'f':
                        out.append('\f');
                        break;
                    case 't':
                        out.append('\t');
                        break;
                    case 'n':
                        out.append('\n');
                        break;
                    case 'b':
                        out.append('\b');
                        break;
                    case 'u':
                        {
                            // uh-oh, we're in unicode country....
                            inUnicode = true;
                            break;
                        }
                    default:
                        out.append(ch);
                        break;
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.append(ch);
        }
        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.append('\\');
        }
    }

    public static String unescapeString(String str) {
        StringBuilder out = new StringBuilder();
        unescapeString(out, str);
        return out.toString();
    }
}
