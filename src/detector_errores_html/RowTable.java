package detector_errores_html;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fundamentos de computación
 */
public class RowTable extends JTable {
    private Map<Integer, Color> rowColor = new HashMap<Integer, Color>();
    private static final long serialVersionUID = 1234567891;

    @SuppressWarnings("serial")
    RowTable(TableModel model) {
        super(model);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);

        if (!isRowSelected(row)) {
            Color color = rowColor.get(row);
            c.setBackground(color == null ? getBackground() : color);
        }

        return c;
    }

    void setRowColor(int row) {
        rowColor.put(row, Color.red);
    }
}