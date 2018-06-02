public class Error {
    private int linea;
    private String comentario;

    public Error(int l, String c) {
        linea = l;
        comentario = c;
    }

    public int get_num_linea(){
        return linea;
    }

    public String get_comentario(){
        return comentario;
    }

    public void set_comentario(String c){
        comentario = c;
    }
    public void set_num_linea(int l){
        linea = l;
    }

}