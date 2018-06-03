package detector_errores_html;

class Linea {
    private String linea;
    private int largo;

    Linea(String linea, int largo) {
        this.linea = linea;
        this.largo = largo;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public int getLargo() {
        return largo;
    }

    public void setLargo(int largo) {
        this.largo = largo;
    }
}
