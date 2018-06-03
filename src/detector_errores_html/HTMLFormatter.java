package detector_errores_html;

import java.util.ArrayList;
import java.util.List;

class HTMLFormatter {

    static List<Linea> formatt(List<Linea> inputFile) {
        // Formatea una lista de sentencias HTML a una valida para analizar.

        List<Linea> fFile = new ArrayList<>();

        String superS = "";
        for (Linea l : inputFile) {
            superS = superS.concat(l.getLinea()).concat("\n");
        }

        superS = superS.replace("\n", "");
        superS = superS.replace(">", ">\n");

        String arr[] = superS.split("\n");

        for (String s : arr) {
            fFile.add(new Linea(s, s.length()));
        }

        return fFile;
    }
}
