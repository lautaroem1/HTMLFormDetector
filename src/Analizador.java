import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analizador {

    public static void main(String[] args) {

        // groupExtractor();
        List<String> inputFile = new ArrayList<>();
        inputFile.add("<img src><input type=\"__\" name=\"nombre\" id=\"_\" pattern=\"Expresi on");
        inputFile.add("_no_");
        inputFile.add("regular\"> <input >");
        inputFile.add("<body> hola mundo </body>");
        inputFile.add("<head></head>");
        inputFile.add("<img> <input % name=\"cuil\" pattern=\"algo que");
        inputFile.add("no importa \">");

        List<String> formattedHTML = htmlFormatter(inputFile);

        List<String> outputFile = new ArrayList<>();

        for (int i = 0; i < formattedHTML.size(); i++) {
            String s = formattedHTML.get(i);
            System.out.println("Reading line: " + s);

            String editedLine = lineAnalysis(s, i);
            System.out.println("After analysis: " + editedLine);
            outputFile.add(editedLine);
        }
    }

    private static List<String> htmlFormatter(List<String> inputFile) {
        List<String> formattedFile = new ArrayList<>();
        String preparedSuperS = "";
        for (String s : inputFile) {
            preparedSuperS = preparedSuperS.concat(s).concat("\n");
        }

        preparedSuperS = preparedSuperS.replace("\n", "");
        preparedSuperS = preparedSuperS.replace(">", ">\n");

        String arr[] = preparedSuperS.split("\n");
        Collections.addAll(formattedFile, arr);

        return formattedFile;
    }

    private static String lineAnalysis(String line, int lineNumber) {
        String editedLine;

        Pattern inputPattern = Pattern.compile("<(\\s*input\\s+.*\\s*)>");
        Matcher inputMatcher = inputPattern.matcher(line);


        // Si encontro linea de tipo input analizar
        if (inputMatcher.find()) {
            // Analizar que entre comillas sea correcto.
            if (quoteAnalysis(inputMatcher.group(1))) {
            } else {
                System.out.println("Error inside quotes in line: " + lineNumber);
            }
            // Extraemos la linea de input
            editedLine = inputMatcher.group(0);

            if (editedLine.contains("name=")) {
                // Si la linea de input contiene name, debemos eliminar el pattern que tenia e insertar el correcto.
                System.out.println("Found name attribute.");
                System.out.println("Removing pattern...");
                editedLine = patternRemover(editedLine);
                System.out.println("Removed pattern result line: " + editedLine);
                System.out.println("Adding pattern...");
                editedLine = patternAdder(editedLine);
                System.out.println("Added pattern result line: " + editedLine);
            }
        } else {
            editedLine = line;
        }
        return editedLine;
    }

    private static String patternAdder(String line) {
        return line.replace(">", patternExpression(nameValueExtractor(line)));

    }

    private static String patternRemover(String line) {
        // Si la linea cuenta con un pattern la elimina por completo.
        String editedLine = line;
        Pattern patPattern = Pattern.compile(".*(pattern=\".*\").*");
        Matcher patMatcher = patPattern.matcher(line);
        while (patMatcher.find()) {
            editedLine = editedLine.replace(patMatcher.group(1), "");
        }
        return editedLine;
    }

    private static String nameValueExtractor(String line) {
        // Recupera el valor del atributo name
        StringBuilder stringBuilder = new StringBuilder();

        int startOfName = line.indexOf("name=") + 6;
        int endOfName = 0;
        for (int i = startOfName; i < line.length(); i++) {
            if (line.charAt(i) == '"') {
                endOfName = i;
                break;
            }
        }
        for (int i = startOfName; i < endOfName; i++) {
            stringBuilder.append(line.charAt(i));
        }
        return stringBuilder.toString().toLowerCase();
    }

    private static String patternExpression(String nameValue) {
        System.out.println("Comparing with: " + nameValue);
        switch (nameValue) {
            case "nombre":
                return "pattern=\"<[a-zA-Z]{2-30}/>\">  <!--This is a comment. Comments are not displayed in the browser-->";
            case "apellido":
                return "pattern=\"<[a-zA-Z]{2-30}/>\">  <!--This is a comment. Comments are not displayed in the browser-->";
            case "dni":
                return "pattern=\"<[0-9]{8}/>\">  <!--This is a comment. Comments are not displayed in the browser-->";
            case "cuil":
                return "pattern=\"<[0-99][0-9]{8}{0-9}/>\">  <!--This is a comment. Comments are not displayed in the browser-->";
            case "correo_electronico":
                return "pattern=\"<[a-zA-Z0-9]+([.][a-zA-Z0-9_-][+])*@[a-zA-Z]+([a-zA-Z][+])*.[a-zA-Z]+([.][a-zA-Z]){2-5}/>\">  <!--This is a comment. Comments are not displayed in the browser-->";
            case "telefono":
                return "pattern=\"<[0-9]{2-4}[0-9]{6-8}/>\">  <!--This is a comment. Comments are not displayed in the browser-->";
            case "fecha_de_nacimiento":
                return "pattern=\"< [0-31]/[0-12]/[1900-2018]/>\">  <!--This is a comment. Comments are not displayed in the browser-->";
            case "comentarios":
                return "pattern=\"<[a-zA-Z0-9]*/>\"> <!--This is a comment. Comments are not displayed in the browser-->";
            default:
                return ">";
        }
    }

    // Retorna true si todos los caracteres son validos
    private static boolean quoteAnalysis(String superString) {
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(superString);

        while (m.find()) {
            if (containsIllegals(m.group(1))) {
                return false;
            }
        }
        return true;
    }

    // Verifica que el string pasado por parametro no contenga caracteres invalidos.
    private static boolean containsIllegals(String toExamine) {
        Pattern pattern = Pattern.compile("[\\s\"\'’=¿¡‘]");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }
}
