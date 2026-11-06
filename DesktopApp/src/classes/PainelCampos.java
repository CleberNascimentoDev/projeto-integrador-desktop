package classes;

import java.awt.*;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.swing.*;

/** Grade do formulário: duas colunas no Design e uma quando faltar largura. */
public class PainelCampos extends JPanel implements Scrollable {
    private final Map<Component,GridBagConstraints> posicoes = new IdentityHashMap<>();
    public PainelCampos() { setOpaque(false); setLayout(new GridBagLayout()); }
    private void atualizarColunas() {
        // No GUI Builder as restrições são controladas pelo editor, inclusive durante o arraste.
        if (!java.beans.Beans.isDesignTime() && getLayout() instanceof GridBagLayout) {
            posicoes.keySet().removeIf(campo -> campo.getParent() != this);
            GridBagLayout grade = (GridBagLayout)getLayout();
            for (Component campo : getComponents()) {
                // O texto digitado não deve impor uma largura mínima à coluna.
                // A altura preferida preserva rótulos e entradas quando a grade encolhe.
                campo.setMinimumSize(new Dimension(0, campo.getPreferredSize().height));
                GridBagConstraints original = posicoes.computeIfAbsent(campo, grade::getConstraints);
                GridBagConstraints atual = (GridBagConstraints)original.clone();
                if (getWidth() > 0 && getWidth() < (Acessibilidade.isBaixaVisaoAtiva() ? 820 : 650)) {
                    atual.gridx = 0; atual.gridy = original.gridy * 2 + original.gridx;
                    atual.insets = new Insets(original.insets.top,0,original.insets.bottom,0);
                }
                grade.setConstraints(campo,atual);
            }
        }
    }
    @Override public void doLayout() {
        atualizarColunas();
        super.doLayout();
        // GridBagLayout centraliza grades sem pesos verticais; formulários começam no topo.
        int topo = Integer.MAX_VALUE;
        for (Component campo : getComponents()) if (campo.isVisible()) topo = Math.min(topo, campo.getY());
        int margem = getInsets().top;
        if (topo > margem && topo != Integer.MAX_VALUE)
            for (Component campo : getComponents()) campo.setLocation(campo.getX(), campo.getY() - topo + margem);
    }
    @Override public Dimension getPreferredSize() {
        atualizarColunas();
        return super.getPreferredSize();
    }
    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }
    @Override public int getScrollableUnitIncrement(Rectangle area, int eixo, int direcao) { return 24; }
    @Override public int getScrollableBlockIncrement(Rectangle area, int eixo, int direcao) {
        return Math.max(24, area.height - 24);
    }
}
