package detector_errores_html;

import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AnalizadorFormularios {

    private static final String DEFAULT_PATTERN_RETURN = ">";

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
                    errores_encontrados.add(new Error(numero_linea + 1, "Caracter ilegal entre comillas o posible atributo sin encerrar."));
                    break;
                case 2:
                    errores_encontrados.add(new Error(numero_linea + 1, "Atributo de longitud 0."));
                    break;
                case 3:
                    errores_encontrados.add(new Error(numero_linea + 1, "Valor de atributo no iniciado con comillas."));
                    break;
                case 4:
                    errores_encontrados.add(new Error(numero_linea + 1, "Comillas sin cerrar."));
                    break;
                default:
                    break;
            }
            // Extraemos la linea de input
            linea_editada = m.group(0);

            if (esNameConocido(linea_editada)) {
                // Si la linea tiene un name conocido reemplazar por el correspondiente
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

    private static String eliminarPattern(String linea) {
        // Si la linea cuenta con un pattern la elimina por completo, si no lo tiene, no hace nada.
        // Retorna la linea modificada
        String editedLine = linea;
        Pattern p = Pattern.compile("(pattern=\\s*\"[^\"]*\")");
        Matcher m = p.matcher(linea);
        while (m.find()) {
            editedLine = editedLine.replace(m.group(1), "");
        }
        return editedLine;
    }

    private static String extraerValorName(String linea) {
        // Recupera el valor del atributo name

        Pattern p = Pattern.compile("name=\\s*\"([^\"]*)");
        Matcher m = p.matcher(linea);
        if (m.find()) {
            return m.group(1).toLowerCase();
        } else return "";
    }

    private static boolean esNameConocido(String valorName){
        return !(expresionesPattern(valorName).equals(DEFAULT_PATTERN_RETURN));
    }

    private static String expresionesPattern(String valorName) {
        // Dado un valor de name retorna una pattern html apropiado con comentarios.
        switch (valorName) {
            case "nombre":
                return " pattern=\"[a-zA-Z]{2-30}\">  <!--Acepta caracteres desde la \"a\" hasta la \"z\" tanto en mayuscula como minusculas, con longitud de 2 a 30 caracteres-->";
            case "apellido":
                return " pattern=\"[a-zA-Z]{2-30}\">  <!--Acepta caracteres desde la \"a\" hasta la \"z\" tanto en mayuscula como minusculas, con longitud de 2 a 30 caracteres-->";
            case "dni":
                return " pattern=\"[0-9]{8}\">  <!--Acepta solo numeros enteros de longitud 8-->";
            case "cuil":
                return " pattern=\"[0-99][0-9]{8}{0-9}\">  <!--Acepta el siguiente formato: un numero entero de 0 a 99, concatenado con entero de 8 digitos, concatenado con un digito de 0 a 9-->";
            case "correo_electronico":
                return " pattern=\"[a-zA-Z0-9]+([.][a-zA-Z0-9_-][+])*@[a-zA-Z]+([a-zA-Z][+])*.[a-zA-Z]+([.][a-zA-Z]){2-5}\">  <!--Acepta el siguiente formato: secuenca de al menos un caracter continuado por el simbolo @, continuado por una cadena de caracteres terminadas con un ., finalizando con una secuencia de 2 a 5 caracteres -->";
            case "telefono":
                return " pattern=\"[0-9]{2-4}[0-9]{6-8}\">  <!--Acepta el siguiente formato: un numero entero de 2 a 4 digitos, concatenado con otro entero de 6 a 8 digitos.-->";
            case "fecha_de_nacimiento":
                return " pattern=\"[0-31]/[0-12]/[1900-2018]\">  <!--Acepta el siguiente formato: un numero entero entre 0 y 31, concatenado con \"/\", concatenado con un numero entero de 0 a 12, concatenado con \"/\", concatenado con un numero entero entre 1900 y 2018-->";
            case "comentarios":
                return " pattern=\"[a-zA-Z0-9]*\"> <!--Acepta cualquier combinacion de carateres del abecedario junto a digitos de 0 al 9-->";
            default:
                return DEFAULT_PATTERN_RETURN;
        }
    }

    // Retorna true si todos los caracteres son validos
    private static int correctoEntreComillas(String linea) {
        Pattern p = Pattern.compile("\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(linea);

        while (m.find()) {
            if (contieneCharsIlegales(m.group(1))) {
                // Si contiene chars ilegales retorna 1.
                return 1;
            } else if (m.group(1).length() == 0) {
                // Si es de longitud vacia retorna 2.
                return 2;
            }
        }

        p = Pattern.compile("=\\s*[^\"\\s]");
        m = p.matcher(linea);
        if (m.find()) {
            // Si existe algun igual no seguido por espacios o comillas retorna 3.
            return 3;
        }

        if (linea.chars().filter(ch -> ch == '"').count() % 2 != 0) {
            // Si la cantidad de comillas no es par significa que alguna no esta cerrada. Retorna 4.
            return 4;
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
