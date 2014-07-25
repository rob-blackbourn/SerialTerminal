package net.jetblack.serialterminal.ui.utils;

import java.text.ParseException;
import java.text.StringCharacterIterator;

public class StringUtils {
	public static String deleteWhitespace(String str) {
		if (isEmpty(str)) {
			return str;
		}
		int sz = str.length();
		char[] chs = new char[sz];
		int count = 0;
		for (int i = 0; i < sz; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				chs[count++] = str.charAt(i);
			}
		}
		if (count == sz) {
			return str;
		}
		return new String(chs, 0, count);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static String replaceEscapeSequencies(String text) throws ParseException {
		StringBuilder s = new StringBuilder();
		
		StringCharacterIterator iter = new StringCharacterIterator(text);
		
		while (iter.current() != StringCharacterIterator.DONE) {
			if (iter.current() == '\\') {
				s.append(processBackslashEscapeSequence(iter));
			} else if (iter.current() == '^') {
				s.append(processCaretEscapeSequence(iter));
			} else {
				s.append(iter.current());
			}
			
			iter.next();
		}
		
		return s.toString();
	}
	
	private static char processBackslashEscapeSequence(StringCharacterIterator iter) throws ParseException {

		final int offset = iter.getIndex();
		
		switch (iter.next()) {
		case StringCharacterIterator.DONE:
			throw new ParseException("Missing character in escape sequence", offset);
		case '\\':
			return '\\';
		case '^':
			return '^';
		case 'n':
			return '\n';
		case 't':
			return '\t';
		case 'b':
			return '\b';
		case 'r':
			return '\r';
		case 'f':
			return '\f';
		case 'x':
			iter.next();
			String hexText = toString(iter, 2);
			try {
				return (char)Integer.parseInt(hexText, 16);
			} catch (NumberFormatException e) {
				throw new ParseException("Invalid hex escape sequence \\x" + hexText + " - " + e.getMessage(), offset);
			}
		case 'u':
			iter.next();
			String unicodeText = toString(iter, 4);
			try {
				return (char)Integer.parseInt(unicodeText, 16);
			} catch (NumberFormatException e) {
				throw new ParseException("Invalod unicode escape sequence \\u" + unicodeText + " - " + e.getMessage(), offset);
			}
		default:
			String octalText = toString(iter, 3);
			try {
				return (char)Integer.parseInt(octalText, 8);
			} catch (NumberFormatException e) {
				throw new ParseException("Invalod octal escape sequence \\" + octalText + " - " + e.getMessage(), offset);
			}
		}
	}
	
	private static char processCaretEscapeSequence(StringCharacterIterator iter) throws ParseException {
		final int offset = iter.getIndex();
		final char c = iter.next();
		if (c == StringCharacterIterator.DONE) {
			throw new ParseException("Missing character in escape sequence", offset);
		}
		
		if (c == '?') {
			return 0x7f; // DEL
		} else if (c >= '@' && c <= '_') {
			return (char)(c - '@');
		} else if (c >= 'a' && c <= 'z') {
			return (char)(c - 'a');
		} else {
			throw new ParseException("Unknown control code ^" + c, offset);
		}
	}
	
	private static String toString(StringCharacterIterator iter, int count) throws ParseException {
		int offset = iter.getIndex();
		char[] chars = new char[count];
		for (int i = 0; i < count; ++i) {
			if ((chars[i] = iter.current()) == StringCharacterIterator.DONE) {
				throw new ParseException("Not enought characters - expected " + count + " found " + i, offset);
			}
			iter.next();
		}
		return new String(chars);
	}
}
