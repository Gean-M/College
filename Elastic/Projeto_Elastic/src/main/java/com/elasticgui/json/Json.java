package com.elasticgui.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilitário mínimo de JSON (parse e serialização), escrito sem dependências
 * externas para que o projeto compile apenas com o JDK padrão (sem precisar
 * de Maven Central, internet ou Docker).
 *
 * Suporta os tipos: Map<String,Object>, List<Object>, String, Long, Double,
 * Boolean e null - suficiente para conversar com a API REST do Elasticsearch.
 */
public final class Json {

    private Json() {
    }

    // ---------------------------------------------------------------
    // Construtores de conveniência
    // ---------------------------------------------------------------

    /** Cria um Map ordenado (LinkedHashMap) a partir de pares chave/valor. */
    public static Map<String, Object> obj(Object... kv) {
        if (kv.length % 2 != 0) {
            throw new IllegalArgumentException("Número ímpar de argumentos para Json.obj");
        }
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }

    /** Cria uma lista a partir de valores soltos. */
    public static List<Object> arr(Object... values) {
        List<Object> list = new ArrayList<>();
        for (Object v : values) {
            list.add(v);
        }
        return list;
    }

    // ---------------------------------------------------------------
    // Stringify
    // ---------------------------------------------------------------

    public static String stringify(Object value) {
        StringBuilder sb = new StringBuilder();
        write(value, sb);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void write(Object value, StringBuilder sb) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            writeString((String) value, sb);
        } else if (value instanceof Boolean) {
            sb.append(value.toString());
        } else if (value instanceof Number) {
            sb.append(value.toString());
        } else if (value instanceof Map) {
            writeMap((Map<String, Object>) value, sb);
        } else if (value instanceof List) {
            writeList((List<Object>) value, sb);
        } else {
            // fallback: trata como string
            writeString(value.toString(), sb);
        }
    }

    private static void writeMap(Map<String, Object> map, StringBuilder sb) {
        sb.append('{');
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            writeString(e.getKey(), sb);
            sb.append(':');
            write(e.getValue(), sb);
        }
        sb.append('}');
    }

    private static void writeList(List<Object> list, StringBuilder sb) {
        sb.append('[');
        boolean first = true;
        for (Object o : list) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            write(o, sb);
        }
        sb.append(']');
    }

    private static void writeString(String s, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    // ---------------------------------------------------------------
    // Pretty print (usado na tela "Ver JSON bruto")
    // ---------------------------------------------------------------

    public static String stringifyPretty(Object value) {
        StringBuilder sb = new StringBuilder();
        writePretty(value, sb, 0);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void writePretty(Object value, StringBuilder sb, int indent) {
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            if (map.isEmpty()) {
                sb.append("{}");
                return;
            }
            sb.append("{\n");
            int i = 0;
            for (Map.Entry<String, Object> e : map.entrySet()) {
                indent(sb, indent + 1);
                writeString(e.getKey(), sb);
                sb.append(": ");
                writePretty(e.getValue(), sb, indent + 1);
                if (++i < map.size()) {
                    sb.append(',');
                }
                sb.append('\n');
            }
            indent(sb, indent);
            sb.append('}');
        } else if (value instanceof List) {
            List<Object> list = (List<Object>) value;
            if (list.isEmpty()) {
                sb.append("[]");
                return;
            }
            sb.append("[\n");
            for (int i = 0; i < list.size(); i++) {
                indent(sb, indent + 1);
                writePretty(list.get(i), sb, indent + 1);
                if (i < list.size() - 1) {
                    sb.append(',');
                }
                sb.append('\n');
            }
            indent(sb, indent);
            sb.append(']');
        } else {
            write(value, sb);
        }
    }

    private static void indent(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
    }

    // ---------------------------------------------------------------
    // Parse
    // ---------------------------------------------------------------

    public static Object parse(String json) {
        Parser p = new Parser(json);
        p.skipWhitespace();
        Object value = p.parseValue();
        p.skipWhitespace();
        return value;
    }

    private static final class Parser {
        private final String s;
        private int pos;

        Parser(String s) {
            this.s = s;
        }

        void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        char peek() {
            if (pos >= s.length()) {
                throw new IllegalArgumentException("JSON inválido: fim inesperado da entrada");
            }
            return s.charAt(pos);
        }

        Object parseValue() {
            skipWhitespace();
            char c = peek();
            switch (c) {
                case '{':
                    return parseObject();
                case '[':
                    return parseArray();
                case '"':
                    return parseString();
                case 't':
                    expect("true");
                    return Boolean.TRUE;
                case 'f':
                    expect("false");
                    return Boolean.FALSE;
                case 'n':
                    expect("null");
                    return null;
                default:
                    return parseNumber();
            }
        }

        void expect(String literal) {
            if (!s.regionMatches(pos, literal, 0, literal.length())) {
                throw new IllegalArgumentException("JSON inválido perto da posição " + pos);
            }
            pos += literal.length();
        }

        Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            pos++; // {
            skipWhitespace();
            if (peek() == '}') {
                pos++;
                return map;
            }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                if (peek() != ':') {
                    throw new IllegalArgumentException("Esperado ':' na posição " + pos);
                }
                pos++;
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                char c = peek();
                if (c == ',') {
                    pos++;
                } else if (c == '}') {
                    pos++;
                    break;
                } else {
                    throw new IllegalArgumentException("Esperado ',' ou '}' na posição " + pos);
                }
            }
            return map;
        }

        List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            pos++; // [
            skipWhitespace();
            if (peek() == ']') {
                pos++;
                return list;
            }
            while (true) {
                Object value = parseValue();
                list.add(value);
                skipWhitespace();
                char c = peek();
                if (c == ',') {
                    pos++;
                } else if (c == ']') {
                    pos++;
                    break;
                } else {
                    throw new IllegalArgumentException("Esperado ',' ou ']' na posição " + pos);
                }
            }
            return list;
        }

        String parseString() {
            if (peek() != '"') {
                throw new IllegalArgumentException("Esperada string na posição " + pos);
            }
            pos++;
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = s.charAt(pos++);
                if (c == '"') {
                    break;
                }
                if (c == '\\') {
                    char esc = s.charAt(pos++);
                    switch (esc) {
                        case '"':
                            sb.append('"');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '/':
                            sb.append('/');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'u':
                            String hex = s.substring(pos, pos + 4);
                            sb.append((char) Integer.parseInt(hex, 16));
                            pos += 4;
                            break;
                        default:
                            throw new IllegalArgumentException("Escape inválido: \\" + esc);
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        Object parseNumber() {
            int start = pos;
            if (peek() == '-') {
                pos++;
            }
            while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
                pos++;
            }
            boolean isDouble = false;
            if (pos < s.length() && s.charAt(pos) == '.') {
                isDouble = true;
                pos++;
                while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
                    pos++;
                }
            }
            if (pos < s.length() && (s.charAt(pos) == 'e' || s.charAt(pos) == 'E')) {
                isDouble = true;
                pos++;
                if (pos < s.length() && (s.charAt(pos) == '+' || s.charAt(pos) == '-')) {
                    pos++;
                }
                while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
                    pos++;
                }
            }
            String numStr = s.substring(start, pos);
            if (numStr.isEmpty() || numStr.equals("-")) {
                throw new IllegalArgumentException("Número inválido na posição " + start);
            }
            if (isDouble) {
                return Double.parseDouble(numStr);
            }
            try {
                return Long.parseLong(numStr);
            } catch (NumberFormatException ex) {
                return Double.parseDouble(numStr);
            }
        }
    }

    // ---------------------------------------------------------------
    // Helpers de navegação segura em estruturas parseadas
    // ---------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object o) {
        return o instanceof Map ? (Map<String, Object>) o : null;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> asList(Object o) {
        return o instanceof List ? (List<Object>) o : null;
    }

    public static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    public static Integer asInteger(Object o) {
        return o instanceof Number ? ((Number) o).intValue() : null;
    }

    public static Long asLong(Object o) {
        return o instanceof Number ? ((Number) o).longValue() : null;
    }

    public static Double asDouble(Object o) {
        return o instanceof Number ? ((Number) o).doubleValue() : null;
    }

    /** Navega em um Map usando um "caminho" tipo "hits.total.value". */
    @SuppressWarnings("unchecked")
    public static Object path(Map<String, Object> root, String dottedPath) {
        Object current = root;
        for (String part : dottedPath.split("\\.")) {
            if (!(current instanceof Map)) {
                return null;
            }
            current = ((Map<String, Object>) current).get(part);
        }
        return current;
    }
}
