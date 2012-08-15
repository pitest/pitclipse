package org.pitest.pitclipse.ui.swtbot;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class HtmlTable {

	private static final String TABLE_START = "<table>";
	private static final String TABLE_END = "</table>";

	private final List<Map<String, String>> results = ImmutableList.of();

	public HtmlTable(String html) {
		int startPos = html.indexOf(TABLE_START);
		if (startPos != -1) {
			int endPos = html.indexOf(TABLE_END, startPos);
			if (endPos != -1) {
				parseTable(stripOutTag(html, TABLE_START, TABLE_END));
			}
		}
	}

	private void parseTable(String table) {
		List<String> headers = getHeaders(table);
	}

	private List<String> getHeaders(String table) {
		Builder<String> builder = ImmutableList.builder();
		int position = table.indexOf("<th>");
		while (table.indexOf("<th>", position) != -1) {
			String header = stripOutTag(table.substring(position), "<th>",
					"</th>");
			builder.add(header);
			position = table.indexOf("<th>", position) + 9;
		}
		return builder.build();
	}

	private String stripOutTag(String html, String startString, String endString) {
		String tagValue = getValue(html, startString, endString);
		return tagValue.replaceFirst(startString, "").replaceFirst(endString,
				"");
	}

	private String getValue(String html, String startString, String endString) {
		String returnValue = "";
		int startPos = html.indexOf(startString);
		if (startPos != -1) {
			int endPos = html.indexOf(endString, startPos);
			if (endPos != -1) {
				returnValue = html.substring(startPos,
						endPos + endString.length());
			}
		}
		return returnValue;
	}
}
