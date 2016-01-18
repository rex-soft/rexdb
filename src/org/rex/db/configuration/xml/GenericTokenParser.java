package org.rex.db.configuration.xml;

public class GenericTokenParser {

	private final String openToken;
	private final String closeToken;
	private final TokenHandler handler;

	public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
		this.openToken = openToken;
		this.closeToken = closeToken;
		this.handler = handler;
	}

	public String parse(String text) {
		final StringBuilder builder = new StringBuilder();
		final StringBuilder expression = new StringBuilder();
		if (text != null && text.length() > 0) {
			char[] src = text.toCharArray();
			int offset = 0;
			int start = text.indexOf(openToken, offset);
			while (start > -1) {
				if (start > 0 && src[start - 1] == '\\') {
					builder.append(src, offset, start - offset - 1).append(openToken);
					offset = start + openToken.length();
				} else {
					expression.setLength(0);
					builder.append(src, offset, start - offset);
					offset = start + openToken.length();
					int end = text.indexOf(closeToken, offset);
					while (end > -1) {
						if (end > offset && src[end - 1] == '\\') {
							expression.append(src, offset, end - offset - 1).append(closeToken);
							offset = end + closeToken.length();
							end = text.indexOf(closeToken, offset);
						} else {
							expression.append(src, offset, end - offset);
							offset = end + closeToken.length();
							break;
						}
					}
					if (end == -1) {
						builder.append(src, start, src.length - start);
						offset = src.length;
					} else {
						builder.append(handler.handleToken(expression.toString()));
						offset = end + closeToken.length();
					}
				}
				start = text.indexOf(openToken, offset);
			}
			if (offset < src.length) {
				builder.append(src, offset, src.length - offset);
			}
		}
		return builder.toString();
	}
}
