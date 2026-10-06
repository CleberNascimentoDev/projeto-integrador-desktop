package classes;

import java.awt.*;
import javax.swing.Icon;

/** Marca de seleção vetorial, legível mesmo quando a fonte não possui o glifo ✓. */
public final class IconeSelecao implements Icon {
    private final int tamanho;
    public IconeSelecao(int tamanho) { this.tamanho = tamanho; }
    @Override public int getIconWidth() { return tamanho; }
    @Override public int getIconHeight() { return tamanho; }
    @Override public void paintIcon(Component componente, Graphics g, int x, int y) {
        Graphics2D desenho = (Graphics2D) g.create();
        desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        desenho.setColor(componente.getForeground());
        desenho.setStroke(new BasicStroke(Math.max(2, tamanho / 7f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        desenho.drawLine(x + tamanho / 6, y + tamanho / 2, x + tamanho * 2 / 5, y + tamanho * 4 / 5);
        desenho.drawLine(x + tamanho * 2 / 5, y + tamanho * 4 / 5, x + tamanho * 5 / 6, y + tamanho / 5);
        desenho.dispose();
    }
}
