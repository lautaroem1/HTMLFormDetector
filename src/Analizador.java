import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analizador {

    public static void main(String[] args) {

        char invalidChar[] = {'”', '’', '=', '¿', '¡', '‘', ' '};
        String invalidString =  "\"\'’=¿¡‘ ";
        // System.out.println(invalidString);
        String superString = "";

        String input = "<img src><input type=\"__\" name=\"__\" id=\"_\" pattern=\"Expresion_regular\"/> <nani>";

        // System.out.println(input);

        Pattern patternTags = Pattern.compile("[<>]");
        String[] tagsList = patternTags.split(input);
        for (String p : tagsList) {
            if(p.matches("\\s*input\\s+(.)*")){
                // System.out.println(p);
                superString = superString.concat(p);
            }
        }

        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(superString);
        while (m.find()) {
            System.out.println(m.group(1));
        }

    }

    private static void tagSeparator() {
        String stringToSearch = "<p>Yada yada yada <code>foo</code> yada yada ...\n"
                + "more here <code>bar</code> etc etc\n"
                + "and still more <code>baz</code> and now the end</p>\n";

        // the pattern we want to search for
        Pattern p = Pattern.compile(" <code>(\\w+)</code> ", Pattern.MULTILINE);
        Matcher m = p.matcher(stringToSearch);

        // print all the matches that we find
        while (m.find())
        {
            System.out.println(m.group(1));
        }
    }

    private static void groupExtractor() {
        String text = "[Username [rank] -> me] message";

        String patron = "^\\[ ([\\w]+) \\[([\\w]+)] -> \\w+] (.*)$";
        Pattern rx = Pattern.compile(patron);
        Matcher m = rx.matcher(text);
        if (m.find()) {
            System.out.println("Match found:");
            for (int i = 0; i <= m.groupCount(); i++) {
                System.out.println("  Group " + i + ": " + m.group(i));
            }
        }
    }

    private static List<Error> validacion_formularios(List<Linea> archivo_html) { // implementar
        List<Error> errores_encontrados = new ArrayList<>();

        char[] caracteres;
        char[] bloqueA = null;
        char[] invalidos = {'"', '=', '¿', '¡', ' ',};
        char[] validos = {};

        // Deteccion de error
        for (Linea l : archivo_html) {
            if (l.get_linea().contains("<input")) {
                // Convertimos a cadena de caracteres. Verficiar los atributos.
                caracteres = l.get_linea().toCharArray();
                for (int i = 0; i < caracteres.length; i++) {
                    if (caracteres[i] == '=') {
                        int j = i;
                        while (caracteres[j] != '"') {
                            // recorro la linea hasta encontrar el primer igual
                            j++;
                        }
                        while (caracteres[j + 1] != '"') {
                            // recorro los caracteres que hay entre las comillas
                            int indice = 0;
                            // indice para que lo llene desde la posicion 0
                            bloqueA[indice] = caracteres[j];
                            // Asingno ese bloque de caracteres a un nuevo bloque para ser analizado.
                            indice++;
                            j++;
                        }
                        // Analisis del bloqueA
                        for (i = 0; i < bloqueA.length; i++) {
                            for (j = 0; j < invalidos.length; j++) {
                                if (bloqueA[i] == invalidos[j]) { /* se ha detectado un error */
                                    Error e = new Error(archivo_html.indexOf(l), "Error en input, caracteres no validos");
                                    errores_encontrados.add(e);
                                }
                            }
                        }
                    }
                    // aca habria que controlar lo que va despues del igual y antes del igual.
                }
            }
        }
        return errores_encontrados;
    }
}
