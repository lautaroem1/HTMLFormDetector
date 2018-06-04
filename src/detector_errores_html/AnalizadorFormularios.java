package detector_errores_html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AnalizadorFormularios {

    static List<Error> validacion_formulario(List<Linea> formattedFile) {

        List<Error> errores_encontrados = new ArrayList<>();
        List<Linea> outFile = new ArrayList<>();

        for (int i = 0; i < formattedFile.size(); i++) {
            String editedLine = analisisLinea(formattedFile.get(i).getLinea(), i, errores_encontrados);
            outFile.add(new Linea(editedLine, editedLine.length()));
        }

        HTMLPrinter.imprimirArchivo(outFile);

        return errores_encontrados;
    }

    private static String analisisLinea(String linea, int numero_linea, List<Error> errores_encontrados) {
        // Busca que la linea sea de tag input y verifica que cumpla los requisitos.
        // Retorna la linea modificada en caso de que haya que cambiar el pattern, o la misma si no es una sentencia input.

        String linea_editada;
        Pattern p = Pattern.compile("<(\\s*input\\s+.*\\s*)>");
        Matcher m = p.matcher(linea);

        // Si encontro linea de tipo input validacion_formulario
        if (m.find()) {
            // Analizar que entre comillas sea correcto.
            switch (correctoEntreComillas(m.group(1))) {
                // Es necesario el + 1 por como trabaja la tabla a diferencia de la lista.
                case 1:
                    // Error de tipo 1
                    errores_encontrados.add(new Error(numero_linea + 1, "Caracter ilegal entre comillas."));
                    break;
                case 2:
                    errores_encontrados.add(new Error(numero_linea + 1, "Posible valor de atributo no encerrado en comillas."));
                    break;
                default:
                    break;
            }
            // Extraemos la linea de input
            linea_editada = m.group(0);
            if (linea_editada.contains("name=")) {
                linea_editada = eliminarPattern(linea_editada);
                linea_editada = agregarPattern(linea_editada);
            }
        }

        // De lo contrario dejar la linea como estaba.
        else {
            linea_editada = linea;
        }
        return linea_editada;
    }

    private static String agregarPattern(String line) {
        // Dada una linea remplaza el pattern que tenga por el correspondiente, o la deja igual en caso de que no se encuentre el pattern correcto.
        return line.replace(">", expresionesPattern(extraerValorName(line)));
    }

    private static String eliminarPattern(String line) {
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

    private static String extraerValorName(String linea) {
        // Recupera el valor del atributo name
        StringBuilder sb = new StringBuilder();

        int indiceInicio = linea.indexOf("name=") + 6;
        int indiceFin = 0;
        for (int i = indiceInicio; i < linea.length(); i++) {
            if (linea.charAt(i) == '"') {
                indiceFin = i;
                break;
            }
        }
        for (int i = indiceInicio; i < indiceFin; i++) {
            sb.append(linea.charAt(i));
        }
        return sb.toString().toLowerCase();
    }

    private static String expresionesPattern(String valorName) {
        // Dado un valor de name retorna una pattern html apropiado con comentarios.
        switch (valorName) {
            case "nombre":
                return "pattern=\"<[a-zA-Z]{2-30}/>\">  <!--Acepta caracteres desde la \"a\" hasta la \"z\" tanto en mayuscula como minusculas, con longitud de 2 a 30 caracteres-->";
            case "apellido":
                return "pattern=\"<[a-zA-Z]{2-30}/>\">  <!--Acepta caracteres desde la \"a\" hasta la \"z\" tanto en mayuscula como minusculas, con longitud de 2 a 30 caracteres-->";
            case "dni":
                return "pattern=\"<[0-9]{8}/>\">  <!--Acepta solo numeros enteros de longitud 8-->";
            case "cuil":
                return "pattern=\"<[0-99][0-9]{8}{0-9}/>\">  <!--Acepta el siguiente formato: un numero entero de 0 a 99, concatenado con entero de 8 digitos, concatenado con un digito de 0 a 9-->";
            case "correo_electronico":
                return "pattern=\"<[a-zA-Z0-9]+([.][a-zA-Z0-9_-][+])*@[a-zA-Z]+([a-zA-Z][+])*.[a-zA-Z]+([.][a-zA-Z]){2-5}/>\">  <!--Acepta el siguiente formato: secuenca de al menos un caracter continuado por el simbolo @, continuado por una cadena de caracteres terminadas con un ., finalizando con una secuencia de 2 a 5 caracteres -->";
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
    private static int correctoEntreComillas(String linea) {
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(linea);

        List<Integer> equalPositions = new ArrayList<>();
        char[] charArray = linea.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == '=') {
                equalPositions.add(i);
            }
        }
        for (Integer i : equalPositions) {
            if (linea.charAt(i + 1) != '\"') {
                return 2;
            }
        }

        while (m.find()) {
            if (contieneCharsIlegales(m.group(1)) || m.group(1).length() == 0) {
                // Si contiene caracteres ilegales o es vacio, debe retornar falso.
                return 1;
            }
        }
        return 0;
        // Si cumple las reglas indicadas, retornara 0, el cual es codigo de valido.
    }

    // Verifica que el string pasado por parametro no contenga caracteres invalidos.
    private static boolean contieneCharsIlegales(String linea) {
        Pattern pattern = Pattern.compile("[\\s\"\'’=¿¡‘]");
        Matcher matcher = pattern.matcher(linea);
        return matcher.find();
    }

    // Inicializa un archivo de prueba HTML.
    /* private static List<Linea> iniciarArchivo() {
        List<Linea> lineas = new ArrayList<>();

        List<String> stringList = new ArrayList<>();
        stringList.add("<img src><input type=\"__\" name=\"nombre\" id=\"yourname\" pattern=\"Expresion");
        stringList.add("_no_");
        stringList.add("regular\"> <input >");
        stringList.add("<body> hola mundo </body>");
        stringList.add("<head></head>");
        stringList.add("<img> <input % name=\"cuil\" pattern=\"some_");
        stringList.add("pattern\">");

        for (String s : stringList) {
            lineas.add(new Linea(s, s.length()));
        }

        return lineas;
    }
    */
}
