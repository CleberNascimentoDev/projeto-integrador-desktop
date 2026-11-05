package classes;

import java.awt.Component;
import javax.swing.*;

/** JMenuBar JavaBean: o editor gerencia menus e a classe mantém a ação de acessibilidade. */
public class BarraMenu extends JMenuBar {
    private Component espaco;
    private BotaoAcessibilidade acessibilidade;

    public BarraMenu() {
        espaco = Box.createHorizontalGlue();
        acessibilidade = new BotaoAcessibilidade();
        acessibilidade.addActionListener(e -> new TelaAcessibilidade(SwingUtilities.getWindowAncestor(this)).setVisible(true));
        adicionarAcessibilidade();
    }
    private void adicionarAcessibilidade() {
        super.addImpl(espaco,null,-1);
        super.addImpl(acessibilidade,null,-1);
    }
    @Override protected void addImpl(Component campo,Object restricoes,int indice) {
        if (campo instanceof JMenu) {
            // Mesmo espaço dos dois lados em todos os menus superiores, inclusive no GUI Builder.
            ((JMenu) campo).setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        }
        int antes = espaco == null ? -1 : getComponentZOrder(espaco);
        super.addImpl(campo,restricoes,campo instanceof JMenu && antes >= 0 ? antes : indice);
    }
    @Override public void removeAll() {
        super.removeAll();
        if (acessibilidade != null) adicionarAcessibilidade();
    }
    public String getTextoAcessibilidade() { return acessibilidade.getText(); }
    public void setTextoAcessibilidade(String texto) { acessibilidade.setText(texto); }
    @Override public java.awt.Dimension getPreferredSize() {
        java.awt.Dimension tamanho = super.getPreferredSize();
        int altura = 0;
        for (Component campo : getComponents()) if (campo.isVisible())
            altura = Math.max(altura, campo.getPreferredSize().height);
        java.awt.Insets margens = getInsets();
        return new java.awt.Dimension(tamanho.width,
                Math.max(tamanho.height, altura + margens.top + margens.bottom));
    }
}
