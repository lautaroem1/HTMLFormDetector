package detector_errores_html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

class HTMLPrinter {

    static void imprimirArchivo(List<Linea> outFile) {
        FileWriter fw = null;
        PrintWriter pw;

        File f = new File("Archivo_modificado.html");
        try {
            fw = new FileWriter(f);
            pw = new PrintWriter(fw);

            for (Linea l : outFile) {
                pw.print(l.getLinea() + "\n");
            }
            System.out.println("Se ha creado el nuevo archivo HTML.");

        } catch (IOException e) {
            System.out.println("Error con File/FileWriter/PrintWriter.");
            e.printStackTrace();
        } finally {
            try {
                // Tratamos de cerrar el fichero.
                if (fw != null) {
                    fw.close();
                }
            } catch (Exception e) {
                System.out.println("Error en fw.close");
                e.printStackTrace();
            }
        }
    }
}
