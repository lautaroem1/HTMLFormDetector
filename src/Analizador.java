import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analizador {

    public static void main(String[] args) {

        List<String> inputFile = new ArrayList<>();
        inputFile.add("<img src><input type=\"__\" name=\"__\" id=\"_\" pattern=\"Expresion");
        inputFile.add("_no_");
        inputFile.add("regular\"> <input >");
        inputFile.add("<head></head>");

        inputSplitter(fileToHTML(inputFile));

    }


    private static boolean inputCheker(String inputLine) {
        char[] inputArray = inputLine.toCharArray();
        if (quoteAnalysis(inputLine)) {
            boolean inQuotes = false;
            for (char c : inputArray) {
                if (!inQuotes) {
                }
            }
        }
        return true;
    }

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

    private static String fileToHTML(List<String> inputFile) {
        String superS = "";
        for (String s : inputFile) {
            superS = superS.concat(s).concat("\n");
        }
        return superS;
    }

    private static void inputSplitter(String s) {
        String preparedS;

        preparedS = s.replace("\n", "");
        preparedS = preparedS.replace(">", ">\n");

        Pattern p = Pattern.compile("<(\\s*input\\s+.*\\s*)>", Pattern.MULTILINE);
        Matcher m = p.matcher(preparedS);

        // print all the matches that we find
        while (m.find())
        {
            System.out.println(m.group(1));
            if ( quoteAnalysis(m.group(1)) ){
                System.out.println("Valid input format!");
            }
            else {
                System.out.println("Invalid char found!");
            }
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

    public static void extractorAfanado(){
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

    private static void primeraVersion(List<String> inputFile){
        String superString = "";

        // Lista de strings de salida del HTML.
        List<String> outputfile = new ArrayList<>();

        int lineSkipper = 0;
        Pattern pTagInit = Pattern.compile("[<]");

        for (int i = 0; i < inputFile.size(); i++) {
            System.out.println("Line value: " + i);
            System.out.println("Lineskipper value: " + lineSkipper);

            // Iteramos sobre el documento entero.
            if (lineSkipper == 0) {

                // Recuperamos la linea que estamos viendo
                String input = inputFile.get(i);
                System.out.println("Reading string number " + i + ": " + input);

                // Tomamos la linea analizada y la separamos por tags.
                String[] tagsList = pTagInit.split(input);

                for (String tagSection : tagsList) {

                    // Procedemos a analizar por tags
                    System.out.println("    Actual tag: " + tagSection);

                    // Si el tag extraido es del formato de input entonces resulta de interes para analizar.
                    if (tagSection.matches("\\s*input\\s+(.)*")) {

                        // Lo concatenamos al superstring del formato input
                        System.out.println("    Tag \"" + tagSection + "\" is of input type!");
                        superString = superString.concat(tagSection);

                        if (tagSection.matches("(.)*>(\\s)*$")) {
                            // Si el string en cuestion tiene el simbolo de cierre al final, terminar.
                            System.out.println("    Input on single line!");
                        } else {
                            // Si no lo tiene, hay que analizar la siguiente linea hasta encontrarlo
                            System.out.println("    Multi line input, cheking next lines:");
                            int j = i + 1;
                            while (j < inputFile.size()) {
                                superString = superString.concat(inputFile.get(j));

                                System.out.println("        Looking for closing symbol in next line: " + inputFile.get(j));
                                if (inputFile.get(j).contains(">")) {
                                    System.out.println("        Found closing symbol!");
                                    j = inputFile.size();
                                } else {
                                    System.out.println("        Closing symbol not found, cheking next line");
                                    lineSkipper++;
                                }
                                j++;
                            }
                        }
                        System.out.println("    SuperString input: " + superString);
                        System.out.println("    Cleaning SuperString input...");
                        superString = superString.substring(0, superString.indexOf(">"));
                        System.out.println("    SuperString input cleaned: " + superString);
                        System.out.println("    Validating input ...");
                        inputCheker(superString);
                        System.out.println("    --------");
                        superString = "";

                        // Concatenaria todos los inputs en uno solo si es que hay multiples input por linea.
                        // Con un break solo seria el primero en la linea
                        break;
                    } else {
                        System.out.println("    Tag \"" + tagSection + "\" is not an input!");
                    }
                    System.out.println("    --------");
                }
            } else {
                // Si tuvimos que skipear una linea, la ignoramos
                lineSkipper--;
            }
        }
        System.out.println("Reached end of file!");
    }
}
