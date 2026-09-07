package hexlet.code.support;

/**
 * The app is a react-admin SPA: its class names are generated and unstable across builds, so the
 * only durable way to find anything is by the text a person would actually read. This builds the
 * XPath for "an element of this tag whose normalized text equals exactly this string".
 */
public final class Locators {
    private Locators() {}

    public static String byExactText(String tag, String text) {
        return "//" + tagOrWildcard(tag) + "[normalize-space()=" + xpathLiteral(text) + "]";
    }

    /** Same match, but relative to whatever node the XPath is evaluated against. */
    public static String relativeByExactText(String tag, String text) {
        return ".//" + tagOrWildcard(tag) + "[normalize-space()=" + xpathLiteral(text) + "]";
    }

    private static String tagOrWildcard(String tag) {
        return (tag == null || tag.isBlank()) ? "*" : tag;
    }

    /**
     * XPath 1.0 has no escape character for quotes inside a string literal. A value with only one
     * kind of quote can be wrapped in the other; a value with both has to be rebuilt with {@code
     * concat(...)}, splitting on the apostrophe and re-inserting it as its own double-quoted piece.
     */
    private static String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        var pieces = value.split("'", -1);
        var expression = new StringBuilder("concat(");
        for (int i = 0; i < pieces.length; i++) {
            if (i > 0) {
                expression.append(", \"'\", ");
            }
            expression.append("'").append(pieces[i]).append("'");
        }
        return expression.append(")").toString();
    }
}
