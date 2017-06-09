package oui.util.dbf;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class RowHeaderRenderer extends JLabel implements ListCellRenderer {
	public RowHeaderRenderer(JTable table) {
	   JTableHeader header = table.getTableHeader();
		ButtonHeaderRenderer renderer = new ButtonHeaderRenderer();

		setOpaque(true);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setHorizontalAlignment(CENTER);
		setForeground(header.getForeground());
		setBackground(header.getBackground());
		setFont(header.getFont());
	}

	public Component getListCellRendererComponent( JList list,Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setText((value == null) ? "" : value.toString());
		return this;
	}
}


class HeaderListener extends MouseAdapter {
	JTableHeader header;
	ButtonHeaderRenderer renderer;
	HeaderListener(JTableHeader header,ButtonHeaderRenderer renderer) {
		this.header = header;
		this.renderer = renderer;
	}

	public void mousePressed(MouseEvent e) {
		int col = header.columnAtPoint(e.getPoint());
		renderer.setPressedColumn(col);
		header.repaint();
		//System.out.println("Ouch! " + col);
	}

	public void mouseReleased(MouseEvent e) {
		int col = header.columnAtPoint(e.getPoint());
		renderer.setPressedColumn(-1);
		// clear
		header.repaint();
	}
}

class ButtonHeaderRenderer extends JButton implements TableCellRenderer {
	int pushedColumn;

	public ButtonHeaderRenderer() {
		pushedColumn = -1;
		setMargin(new Insets(0,0,0,0));
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
		boolean isSelected, boolean hasFocus, int row, int column) {
		setText((value ==null) ? "" : value.toString());
		boolean isPressed = (column == pushedColumn);
		getModel().setPressed(isPressed);
		getModel().setArmed(isPressed);
		return this;
	}


	public void setPressedColumn(int col) {
		pushedColumn = col;
	}

}
