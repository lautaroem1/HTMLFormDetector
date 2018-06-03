package detector_errores_html;

public class Error {
    private int linea;
    private String comentario;

    public Error(int numLinea, String comentario) {
        this.linea = numLinea;
        this.comentario = comentario;
    }

    public int get_num_linea() {
        return linea;
    }

    public String get_comentario() {
        return comentario;
    }

    public void set_comentario(String c) {
        comentario = c;
    }

    public void set_num_linea(int l) {
        linea = l;
    }

}