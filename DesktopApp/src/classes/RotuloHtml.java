package classes;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/** Texto de leitura que acompanha a fonte, a paleta e a largura disponível. */
public class RotuloHtml extends JLabel implements Scrollable {
    public RotuloHtml() {
        setVerticalAlignment(TOP);
        setFocusable(true);
    }

    @Override public void setText(String texto) {
        // O HTML deve herdar as cores e a fonte do componente Swing.
        if (texto != null) texto = texto.replaceAll(
                "(?i)(?<![-\\w])(?:color|font-size)\\s*:\\s*[^;\"']+;?", "");
        super.setText(texto);
    }

    @Override public Dimension getPreferredSize() {
        View html = (View) getClientProperty(BasicHTML.propertyKey);
        if (html == null) return super.getPreferredSize();
        Insets margem = getInsets();
        int largura = getParent() instanceof JViewport ? getParent().getWidth() : getWidth();
        if (largura <= 0) largura = 540;
        html.setSize(Math.max(1, largura - margem.left - margem.right), 0);
        return new Dimension(largura, (int) Math.ceil(html.getPreferredSpan(View.Y_AXIS))
                + margem.top + margem.bottom);
    }

    @Override public Dimension getPreferredScrollableViewportSize() { return new Dimension(540, 600); }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }
    @Override public int getScrollableUnitIncrement(Rectangle area, int eixo, int direcao) {
        return getFontMetrics(getFont()).getHeight();
    }
    @Override public int getScrollableBlockIncrement(Rectangle area, int eixo, int direcao) {
        return Math.max(1, area.height - getScrollableUnitIncrement(area, eixo, direcao));
    }
}
