package detector_errores_html;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HTMLPrinter {

    /* List<Error> validacion_formularios(List<Linea> archivo_html) {

        List<Error> errores_encontrados = new ArrayList<Error>();

        FileWriter fw = null;
        PrintWriter pw = null;

        List<String> html = new ArrayList();

        List<String> formattedHTML;

        List<String> outputFile = new ArrayList<>();

        String nuevaLinea;
        File f = new File("Correcciones.html");
        // Deteccion de error
        try {
            fw = new FileWriter(f);
            pw = new PrintWriter(fw);

            for (Linea l : archivo_html) {
                html.add(l.get_linea());
            }
            formattedHTML = Analizador.htmlFormatter(html);
            for (int i = 0; i < formattedHTML.size(); i++) {
                String s = formattedHTML.get(i);
                nuevaLinea = Analizador.lineAnalysis(s, i, errores_encontrados);
                outputFile.add(nuevaLinea);
            }
            for (String s : outputFile) {
                pw.println(s);
            }
            System.out.println("Se imprimio el archivo 'Correcciones.html donde se indican las lineas de error y  se agregaron los respectivos pattern");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                // Nuevamente aprovechamos el finally para
                // asegurarnos que se cierra el fichero.
                if (null != html) {
                    fw.close();
                }
            } catch (Exception e2) {
                System.out.println("no se pudo abrir");
                e2.printStackTrace();
            }
        }
        return errores_encontrados;
    }*/
}
