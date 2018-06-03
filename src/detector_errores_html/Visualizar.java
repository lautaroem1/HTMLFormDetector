
package detector_errores_html;

import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author Fundamentos de computación
 */
public class Visualizar extends javax.swing.JFrame {
    private static final long serialVersionUID = 1234567891;

    @SuppressWarnings("serial")
    private Visualizar() {
        initComponents();
    }

    @SuppressWarnings("serial")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        // Variables declaration - do not modify//GEN-BEGIN:variables
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        DefaultTableModel dtm = new DefaultTableModel();
        jTable1 = new RowTable(dtm);
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Línea", "Código"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                    false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
        }

        jTable2.setModel(new DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Errores"
                }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addComponent(jScrollPane2))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private static void ejecutar_detector() {

        Detector_errores_HTML deh = new Detector_errores_HTML();

        List<Linea> archivo_html = HTMLFormatter.formatt(deh.leer_html());
        // Usamos el HTML formateado.
        List<Error> errores = AnalizadorFormularios.validacion_formulario(archivo_html);

        // Ejemplo:
        // errores.add(new Error(3,"Comentario del error en la linea 3"));
        // errores.add(new Error(2,"Comentario del error en la linea 2"));
        // errores.add(new Error(2,"Comentario del error en la linea 2 v2"));
        // errores.add(new Error(2,"Comentario del error en la linea 2 v3"));
        // errores.add(new Error(6,"Comentario del error en la linea 6"));
        //

        errores.sort((e1, e2) -> {
            Integer i1 = e1.get_num_linea();
            Integer i2 = e2.get_num_linea();
            return i1.compareTo(i2);
        });

        DefaultTableModel tabla_html = new DefaultTableModel();
        int i = 1;

        tabla_html.addColumn("Línea");
        tabla_html.addColumn("Código");
        Iterator<Error> iterator_error = errores.iterator();
        Error next_error = null;
        if (iterator_error.hasNext())
            next_error = iterator_error.next();
        for (Iterator<Linea> iterator_linea = archivo_html.iterator(); iterator_linea.hasNext(); i++) {
            Linea next = iterator_linea.next();
            Vector<Object> datos = new Vector<>();
            datos.addElement(i);
            datos.addElement(next.getLinea());
            tabla_html.addRow(datos);
            if (next_error != null && next_error.get_num_linea() == i) {
                jTable1.setRowColor(i - 1);
                boolean flag = true;
                while (iterator_error.hasNext() && flag) {
                    next_error = iterator_error.next();
                    if (next_error.get_num_linea() != i) {
                        flag = false;
                    }
                }
            }
        }

        jTable1.getColumnModel().getColumn(0).setPreferredWidth(10);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable1.setModel(tabla_html);

        jTable1.setEnabled(false);
        jTable1.addMouseListener(
                new MouseInputAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        int row = jTable1.rowAtPoint(me.getPoint());
                        row++;

                        DefaultTableModel dtm = new DefaultTableModel();
                        dtm.addColumn("Errores");
                        for (Error next : errores) {
                            if (next.get_num_linea() == row) {
                                Vector<Object> datos = new Vector<>();
                                datos.add(next.get_comentario());
                                dtm.addRow(datos);
                            }
                        }
                        jTable2.setModel(dtm);

                    }
                }
        );
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | javax.swing.UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ex) {
            java.util.logging.Logger.getLogger(Visualizar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Visualizar().setVisible(true);
            ejecutar_detector();
        });


    }

    private static RowTable jTable1;
    private static javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
