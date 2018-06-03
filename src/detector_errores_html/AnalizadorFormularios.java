package detector_errores_html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorFormularios {

    public static void main(String[] args) {

        List<Linea> inFile = initFile();
        List<String> formattedFile = HTMLFormatter.formatt(inFile);

        List<Error> errores_encontrados = validacion_formulario(formattedFile);

    }

    private static List<Error> validacion_formulario(List<String> formattedFile) {

        List<Error> errores_encontrados = new ArrayList<>();
        List<Linea> outFile = new ArrayList<>();

        for (int i = 0; i < formattedFile.size(); i++) {
            String editedLine = analisisLinea(formattedFile.get(i), i, errores_encontrados);
            outFile.add(new Linea(editedLine, editedLine.length()));
        }

        printFile(outFile);

        return errores_encontrados;
    }

    private static String analisisLinea(String line, int lineNumber, List<Error> errorList) {
        // Busca que la linea sea de tag input y verifica que cumpla los requisitos.
        // Retorna la linea modificada en caso de que haya que cambiar el pattern, o la misma si no es una sentencia input.

        String editedLine;
        Pattern p = Pattern.compile("<(\\s*input\\s+.*\\s*)>");
        Matcher m = p.matcher(line);

        // Si encontro linea de tipo input validacion_formulario
        if (m.find()) {
            // Analizar que entre comillas sea correcto.
            if (!correctQuotes(m.group(1))) {
                // Si hay error entre las comillas, generar error.
                errorList.add(new Error(lineNumber, "Caracter ilegal entre comillas!"));
            }
            // Extraemos la linea de input
            editedLine = m.group(0);
            if (editedLine.contains("name=")) {
                // Si la linea de input contiene name, debemos eliminar el pattern que tenia e insertar el correcto.
                // System.out.println("Found name attribute.");
                // System.out.println("Removing pattern...");
                editedLine = patternRemover(editedLine);
                // System.out.println("Removed pattern result line: " + editedLine);
                // System.out.println("Adding pattern...");
                editedLine = patternAdder(editedLine);
                // System.out.println("Added pattern result line: " + editedLine);
            }
        }

        // De lo contrario dejar la linea como estaba.
        else {
            editedLine = line;
        }
        return editedLine;
    }

    private static String patternAdder(String line) {
        // Dada una linea remplaza el pattern que tenga por el correspondiente, o la deja igual en caso de que no se encuentre el pattern correcto.
        return line.replace(">", patternExpression(nameValueExtractor(line)));
    }

    private static String patternRemover(String line) {
        // Si la linea cuenta con un pattern la elimina por completo, si no lo tiene, no hace nada.
        // Retorna la linea modificada
        String editedLine = line;
        Pattern p = Pattern.compile(".*(pattern=\".*\").*");
        Matcher m = p.matcher(line);
        while (m.find()) {
            editedLine = editedLine.replace(m.group(1), "");
        }
        return editedLine;
    }

    private static String nameValueExtractor(String line) {
        // Recupera el valor del atributo name
        StringBuilder sb = new StringBuilder();

        int startOfName = line.indexOf("name=") + 6;
        int endOfName = 0;
        for (int i = startOfName; i < line.length(); i++) {
            if (line.charAt(i) == '"') {
                endOfName = i;
                break;
            }
        }
        for (int i = startOfName; i < endOfName; i++) {
            sb.append(line.charAt(i));
        }
        return sb.toString().toLowerCase();
    }

    private static String patternExpression(String nameValue) {
        // Dado un valor de name retorna una pattern html apropiado con comentarios.
        // System.out.println("Searching pattern for: " + nameValue);
        switch (nameValue) {
            case "nombre":
                return "pattern=\"<[a-zA-Z]{2-30}/>\">  <!--Acepta caracteres desde la \"a\" hasta la \"z\" tanto en mayuscula como minusculas, con longitud de 2 a 30 caracteres-->";
            case "apellido":
                return "pattern=\"<[a-zA-Z]{2-30}/>\">  <!--Acepta caracteres desde la \"a\" hasta la \"z\" tanto en mayuscula como minusculas, con longitud de 2 a 30 caracteres-->";
            case "dni":
                return "pattern=\"<[0-9]{8}/>\">  <!--Acepta solo numeros enteros de longitud 8-->";
            case "cuil":
                return "pattern=\"<[0-99][0-9]{8}{0-9}/>\">  <!--Acepta el siguiente formato: un numero entero de 0 a 99, concatenado con entero de 8 digitos, concatenado con un digito de 0 a 9-->";
            case "correo_electronico":
                return "pattern=\"<[a-zA-Z0-9]+([.][a-zA-Z0-9_-][+])*@[a-zA-Z]+([a-zA-Z][+])*.[a-zA-Z]+([.][a-zA-Z]){2-5}/>\">  <!--Formato de correo electronico-->";
            case "telefono":
                return "pattern=\"<[0-9]{2-4}[0-9]{6-8}/>\">  <!--Acepta el siguiente formato: un numero entero de 2 a 4 digitos, concatenado con otro entero de 6 a 8 digitos.-->";
            case "fecha_de_nacimiento":
                return "pattern=\"< [0-31]/[0-12]/[1900-2018]/>\">  <!--Acepta el siguiente formato: un numero entero entre 0 y 31, concatenado con \"/\", concatenado con un numero entero de 0 a 12, concatenado con \"/\", concatenado con un numero entero entre 1900 y 2018-->";
            case "comentarios":
                return "pattern=\"<[a-zA-Z0-9]*/>\"> <!--Acepta cualquier combinacion de carateres del abecedario junto a digitos de 0 al 9-->";
            default:
                return ">";
        }
    }

    // Retorna true si todos los caracteres son validos
    private static boolean correctQuotes(String superString) {
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(superString);

        while (m.find()) {
            if (containsIllegals(m.group(1)) || m.group(1).length() == 0) {
                // Si contiene caracteres ilegales o es vacio, debe retornar falso.
                return false;
            }
        }
        return true;
        // Si cumple las reglas indicadas, retornara verdadero.
    }

    // Verifica que el string pasado por parametro no contenga caracteres invalidos.
    private static boolean containsIllegals(String toExamine) {
        Pattern pattern = Pattern.compile("[\\s\"\'’=¿¡‘]");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }

    // Inicializa un archivo de prueba HTML.
    private static List<Linea> initFile() {
        List<Linea> lines = new ArrayList<>();

        List<String> stringList = new ArrayList<>();
        stringList.add("<img src><input type=\"__\" name=\"nombre\" id=\"yourname\" pattern=\"Expresion");
        stringList.add("_no_");
        stringList.add("regular\"> <input >");
        stringList.add("<body> hola mundo </body>");
        stringList.add("<head></head>");
        stringList.add("<img> <input % name=\"cuil\" pattern=\"some_");
        stringList.add("pattern\">");

        for (String s : stringList) {
            lines.add(new Linea(s, s.length()));
        }

        return lines;
    }

    // Crea un archivo HTML con las modificaciones ya hechas.
    private static void printFile(List<Linea> outFile) {
        System.out.println("Printing file...");
        for (Linea l : outFile) {
            System.out.println(l.get_linea());
        }
    }
}
