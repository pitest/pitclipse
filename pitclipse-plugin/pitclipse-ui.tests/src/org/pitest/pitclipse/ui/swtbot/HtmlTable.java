package org.pitest.pitclipse.ui.swtbot;

import static org.pitest.pitclipse.ui.swtbot.ResultsParser.caseInsensitveIndexOf;

import java.util.List;
import java.util.Map;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList.Builder;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableMap;

public class HtmlTable {

    private static final String TABLE_START = "<table>";
    private static final String TABLE_END = "</table>";

    private ImmutableList<Map<String, String>> results = ImmutableList.of();

    public HtmlTable(String html) {
        int startPos = caseInsensitveIndexOf(html, TABLE_START);
        if (startPos != -1) {
            int endPos = caseInsensitveIndexOf(html, TABLE_END, startPos);
            if (endPos != -1) {
                parseTable(stripOutTag(html, TABLE_START, TABLE_END));
            }
        }
    }

    private void parseTable(String table) {
        List<String> headers = getHeaders(table);
        List<List<String>> rows = getRows(table);
        Builder<Map<String, String>> resultBuilder = ImmutableList.builder();
        for (List<String> row : rows) {
            org.pitest.pitclipse.reloc.guava.collect.ImmutableMap.Builder<String, String> rowBuilder = ImmutableMap
                    .builder();
            for (int i = 0; i < headers.size(); i++) {
                rowBuilder.put(headers.get(i), row.get(i));
            }
            resultBuilder.add(rowBuilder.build());
        }
        results = resultBuilder.build();
    }

    private List<List<String>> getRows(String table) {
        Builder<List<String>> resultBuilder = ImmutableList.builder();
        int position = caseInsensitveIndexOf(table, "<tbody>");
        while (caseInsensitveIndexOf(table, "<tr>", position) != -1) {
            String row = stripOutTag(table.substring(position), "<tr>", "</tr>");
            resultBuilder.add(parseRow(row));
            position = caseInsensitveIndexOf(table, "<tr>", position) + 9;
        }
        return resultBuilder.build();
    }

    private List<String> parseRow(String row) {
        Builder<String> resultBuilder = ImmutableList.builder();
        int position = caseInsensitveIndexOf(row, "<td>");
        while (caseInsensitveIndexOf(row, "<td>", position) != -1) {
            String cell = stripOutTag(row.substring(position), "<td>", "</td>").
                    replaceAll("<div.*>", "").
                    replaceAll("<DIV.*>", "").
                    trim();

            resultBuilder.add(cell);
            position = caseInsensitveIndexOf(row, "<td>", position) + 9;
        }
        return resultBuilder.build();
    }

    private List<String> getHeaders(String table) {
        Builder<String> builder = ImmutableList.builder();
        int position = caseInsensitveIndexOf(table, "<th>");
        while (caseInsensitveIndexOf(table, "<th>", position) != -1) {
            String header = stripOutTag(table.substring(position), "<th>", "</th>");
            builder.add(header);
            position = caseInsensitveIndexOf(table, "<th>", position) + 9;
        }
        return builder.build();
    }

    private String stripOutTag(String html, String startString, String endString) {
        String tagValue = getValue(html, startString, endString);
        return tagValue.
                replaceFirst(startString, "").
                replaceFirst(startString.toLowerCase(), "").
                replaceFirst(startString.toUpperCase(), "").
                replaceFirst(endString, "").
                replaceFirst(endString.toUpperCase(), "").
                replaceFirst(endString.toLowerCase(), "").
                trim();
    }

    private String getValue(String html, String startString, String endString) {
        String returnValue = "";
        int startPos = caseInsensitveIndexOf(html, startString);
        if (startPos != -1) {
            int endPos = caseInsensitveIndexOf(html, endString, startPos);
            if (endPos != -1) {
                returnValue = html.substring(startPos, endPos + endString.length());
            }
        }
        return returnValue;
    }

    public ImmutableList<Map<String, String>> getResults() {
        return results;
    }
}
