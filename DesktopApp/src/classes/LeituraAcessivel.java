package classes;

import java.awt.*;
import javax.swing.*;

/** Mantém o texto com quebra de linhas e o botão de fechar sempre disponível. */
public final class LeituraAcessivel {
    private LeituraAcessivel() { }

    public static void configurar(JDialog janela, JPanel painel, JLabel texto, JButton fechar,
            int largura, int altura) {
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JScrollPane rolagem = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, texto);
        rolagem.getViewport().setBackground(painel.getBackground());
        Rectangle area = janela.getGraphicsConfiguration().getBounds();
        Insets margens = Toolkit.getDefaultToolkit().getScreenInsets(janela.getGraphicsConfiguration());
        janela.setSize(Math.min(largura, area.width - margens.left - margens.right - 32),
                Math.min(altura, area.height - margens.top - margens.bottom - 32));
        janela.setLocationRelativeTo(janela.getOwner());
        janela.getRootPane().registerKeyboardAction(e -> janela.dispose(), KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        Acessibilidade.configurarTela(janela);
    }
}
