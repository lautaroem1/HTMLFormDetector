
package detector_errores_html;


import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fundamentos de computación
 */
class Detector_errores_HTML {

    List<Linea> leer_html() {

        List<Linea> leer_archivo_html = new ArrayList<>();
        try {
            JFileChooser fChooser = new JFileChooser("C:\\");
            fChooser.showOpenDialog(null);
            File archivo = fChooser.getSelectedFile();
            FileInputStream fstream = new FileInputStream(archivo);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String linea;
            while ((linea = br.readLine()) != null) {
                leer_archivo_html.add(new Linea(linea, linea.length()));
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return leer_archivo_html;
    }

}
