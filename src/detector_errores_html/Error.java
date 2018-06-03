package detector_errores_html;

class Error {
    private int linea;
    private String comentario;

    Error(int numLinea, String comentario) {
        this.linea = numLinea;
        this.comentario = comentario;
    }

    int get_num_linea() {
        return linea;
    }

    String get_comentario() {
        return comentario;
    }

}