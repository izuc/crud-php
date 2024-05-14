package software.crud.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Helper {

    private static final List<String> RESERVED_WORDS = List.of("as", "on", "t");
    private static final Pattern SPECIAL_CHARACTERS = Pattern.compile("[^0-9a-zA-Z _]");
    private static final Pattern UNDERSCORES = Pattern.compile("_");

    // New capitalize method
    public static String capitalize(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
    }

    public static void copy(String sourceDirectory, String targetDirectory) {
        copyAll(new File(sourceDirectory), new File(targetDirectory));
    }

    public static String getTableCharacter(List<String> characterUsed, String tableName) {
        return getTableCharacter(characterUsed, tableName, 2);
    }

    public static String getTableCharacter(List<String> characterUsed, String tableName, int size) {
        Random random = new Random();
        StringBuilder text = new StringBuilder(String.valueOf(tableName.charAt(0)));

        while (text.length() < size) {
            text.append(tableName.charAt(random.nextInt(tableName.length())));
        }

        String result = text.toString();
        if (!characterUsed.contains(result) && !RESERVED_WORDS.contains(result)) {
            return result;
        }
        return getTableCharacter(characterUsed, tableName, size + 1);
    }

    public static void copyAll(File source, File target) {
        if (!target.exists()) {
            target.mkdirs();
        }

        for (File file : source.listFiles()) {
            if (file.isFile()) {
                copyFile(file, new File(target, file.getName()));
            } else if (file.isDirectory()) {
                copyAll(file, new File(target, file.getName()));
            }
        }
    }

    private static void copyFile(File source, File target) {
        try {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String removeSpecialCharacters(String str) {
        return SPECIAL_CHARACTERS.matcher(str).replaceAll("");
    }

    public static String getDataTypePHP(String input) {
        String type = input.toLowerCase();
        if (type.startsWith("unsigned")) {
            type = type.substring(8).trim();
        }
        if (type.contains("(")) {
            type = type.split("\\(", 2)[0];
        }

        return switch (type) {
            case "binary", "bit", "boolean" -> "boolean";
            case "datetime2", "datetime", "date", "timestamp" -> "datetime";
            case "time" -> "string";
            case "int", "int32", "int16", "tinyint", "mediumint", "smallint", "bigint", "int64" -> "integer";
            case "decimal", "double", "float", "real" -> "float";
            default -> "string";
        };
    }

    public static String getDataTypeCSharpSQL(String input) {
        String type = input.toLowerCase();
        boolean unsigned = type.startsWith("unsigned");
        if (unsigned) {
            type = type.substring(8).trim();
        }
        if (type.contains("(")) {
            type = type.split("\\(")[0];
        }

        return switch (type) {
            case "binary", "bit", "boolean" -> "Boolean";
            case "datetime", "date" -> "Date";
            case "timestamp" -> "DateTime";
            case "time" -> "TimeSpan";
            case "tinyint" -> "Int16";
            case "int", "mediumint", "smallint" -> unsigned ? "UInt32" : "Int32";
            case "bigint" -> unsigned ? "UInt64" : "Int64";
            case "double", "decimal", "float", "real" -> "Double";
            default -> "String";
        };
    }

    public static String getDataTypeDart(String input) {
        String type = input.toLowerCase();
        if (type.startsWith("unsigned")) {
            type = type.substring(8).trim();
        }
        if (type.contains("(")) {
            type = type.split("\\(", 2)[0];
        }

        return switch (type) {
            case "binary", "bit", "boolean", "tinyint" -> "bool";
            case "datetime2", "datetime", "date", "timestamp" -> "DateTime";
            case "time" -> "String";
            case "int", "int32", "int16", "mediumint", "smallint", "bigint", "int64" -> "int";
            case "decimal", "numeric", "double", "float", "real" -> "double";
            default -> "String";
        };
    }

    public static String getDataTypeJava(String dataType) {
        return switch (dataType.toLowerCase()) {
            case "int" -> "int";
            case "varchar" -> "String";
            case "datetime" -> "Date";
            default -> "String";
        };
    }

    public static String toPascalCase(String original) {
        return UNDERSCORES.splitAsStream(SPECIAL_CHARACTERS.matcher(original).replaceAll("_"))
                .filter(w -> !w.isEmpty())
                .map(Helper::capitalize)
                .collect(Collectors.joining());
    }

    public static String toCamelCase(String original) {
        String pascalCase = toPascalCase(original);
        if (pascalCase.isEmpty()) {
            return "";
        }
        return Character.toLowerCase(pascalCase.charAt(0)) + pascalCase.substring(1);
    }

    public static String toPascalCaseWithSpaces(String original) {
        return UNDERSCORES.splitAsStream(original)
                .filter(w -> !w.isEmpty())
                .map(Helper::toPascalCase)
                .collect(Collectors.joining(" "));
    }
}
