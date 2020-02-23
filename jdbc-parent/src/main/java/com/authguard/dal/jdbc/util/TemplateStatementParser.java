package com.authguard.dal.jdbc.util;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateStatementParser {
    private final String variablesRegex;
    private final Pattern variablesPattern;

    public TemplateStatementParser() {
        this.variablesRegex = "\\$\\{[\\w|\\\\.]+\\}";
        this.variablesPattern = Pattern.compile(variablesRegex);
    }

    public TemplateStatement parse(final String template) {
        final Map<String, List<Integer>> variableIndices = new HashMap<>();

        final Matcher matcher = variablesPattern.matcher(template);

        for (int i = 0; matcher.find(); i++) {
            final String name = variableName(template, matcher.toMatchResult());
            addToIndices(variableIndices, name, i);
        }

        final String statement = template.replaceAll(variablesRegex, "?");

        return new TemplateStatement(statement, variableIndices);
    }

    private String variableName(final String string, final MatchResult matchResult) {
        return string.substring(matchResult.start() + 2, matchResult.end() - 1);
    }

    private void addToIndices(final Map<String, List<Integer>> variableIndices, final String name, final int index) {
        final List<Integer> indices = variableIndices.get(name);

        if (indices != null) {
            indices.add(index);
        } else {
            variableIndices.put(name, new ArrayList<>(Collections.singletonList(index)));
        }
    }
}
