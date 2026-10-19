package classes;

import java.awt.*;
import javax.swing.JPanel;

/** FlowLayout editável no NetBeans, com altura que acompanha a quebra de linha. */
public class PainelFerramentas extends JPanel {
    public PainelFerramentas() { setOpaque(false); setLayout(new FlowLayout(FlowLayout.LEADING, 16, 0)); }
    int larguraDisponivel() {
        Container pai = getParent();
        return pai == null || pai.getWidth() == 0 ? 1200
                : Math.max(1, pai.getWidth() - pai.getInsets().left - pai.getInsets().right);
    }
    @Override public Dimension getMinimumSize() { return new Dimension(0,getPreferredSize().height); }
    @Override public Dimension getPreferredSize() {
        if (!(getLayout() instanceof FlowLayout)) return super.getPreferredSize();
        FlowLayout layout = (FlowLayout)getLayout();
        int largura = larguraDisponivel(), x = layout.getHgap(), altura = 0, linha = 0;
        for (Component campo : getComponents()) if (campo.isVisible()) {
            Dimension tamanho = campo.getPreferredSize();
            if (x > layout.getHgap() && x + tamanho.width + layout.getHgap() > largura) {
                altura += linha + 12; x = layout.getHgap(); linha = 0;
            }
            x += tamanho.width + layout.getHgap(); linha = Math.max(linha, tamanho.height);
        }
        return new Dimension(largura, altura + linha);
    }
}
