package detector_errores_html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HTMLFormatter {

    public static List<String> formatt(List<Linea> inputFile) {
        // Formatea una lista de sentencias HTML a una valida para analizar.
        List<String> formattedFile = new ArrayList<>();
        String superS = "";
        for (Linea l : inputFile) {
            superS = superS.concat(l.get_linea()).concat("\n");
        }

        superS = superS.replace("\n", "");
        superS = superS.replace(">", ">\n");

        String arr[] = superS.split("\n");
        Collections.addAll(formattedFile, arr);

        return formattedFile;
    }
}
