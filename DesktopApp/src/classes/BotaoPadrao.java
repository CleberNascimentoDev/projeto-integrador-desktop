package classes;

import javax.swing.JButton;

/** Botão compartilhado com construtor sem argumentos, editável no GUI Builder. */
public class BotaoPadrao extends JButton {
    public BotaoPadrao() {
        PainelListagem.configurarBotao(this, "", 18);
    }
    @Override public java.awt.Dimension getPreferredSize() {
        java.awt.Dimension tamanho = super.getPreferredSize();
        // O formulário e a baixa visão já calculam tamanhos explícitos. O foco não deve recalculá-los.
        if (isPreferredSizeSet()) return tamanho;
        String texto = getText();
        int larguraTexto = texto == null ? 0 : getFontMetrics(getFont()).stringWidth(texto);
        int larguraIcone = getIcon() == null ? 0 : getIcon().getIconWidth() + getIconTextGap();
        java.awt.Insets margens = getInsets();
        return new java.awt.Dimension(Math.max(tamanho.width,larguraTexto + larguraIcone + margens.left + margens.right + 8),
                Math.max(56,tamanho.height));
    }
    @Override public java.awt.Dimension getMinimumSize() { return getPreferredSize(); }
}
