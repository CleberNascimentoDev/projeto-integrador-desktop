package classes;

import java.awt.*;
import javax.swing.*;

/** Botão reutilizável e disponível no editor de formulários do NetBeans. */
public class BotaoAcessibilidade extends JButton {
    public BotaoAcessibilidade() {
        super("Acessibilidade");
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setForeground(new Color(17, 48, 82));
        setIcon(new IconeImagem("/images/acessib.png", 24, 24));
        setIconTextGap(8);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setPreferredSize(new Dimension(172, 38));
        setRolloverEnabled(true);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        getAccessibleContext().setAccessibleDescription("Abrir opções de acessibilidade");
    }

    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(186, 207, 226, 85));
        g.fillRoundRect(1, 3, getWidth() - 2, getHeight() - 4, 12, 12);
        Color base = getModel().isPressed() ? new Color(221, 235, 248)
                : getModel().isRollover() ? new Color(238, 246, 253) : new Color(248, 251, 255);
        g.setPaint(Acessibilidade.isDaltonismoAtivo() ? getModel().isRollover()
                ? Acessibilidade.getPaleta().getBadge() : Acessibilidade.getPaleta().getSuperficie()
                : Acessibilidade.isBaixaVisaoAtiva() ? Color.WHITE
                : new GradientPaint(0, 2, Color.WHITE, 0, getHeight(), base));
        g.fillRoundRect(2, 2, getWidth() - 5, getHeight() - 6, 11, 11);
        g.setColor(Acessibilidade.isDaltonismoAtivo() ? Acessibilidade.getPaleta().getBorda()
                : Acessibilidade.isBaixaVisaoAtiva() ? Color.BLACK
                : hasFocus() ? new Color(0, 92, 156) : new Color(163, 188, 212));
        g.setStroke(new BasicStroke(hasFocus() ? 2.5f : 1.5f));
        g.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 6, 11, 11);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int iconY = (getHeight() - getIcon().getIconHeight()) / 2 - 1;
        getIcon().paintIcon(this, g, 10, iconY);
        g.setFont(getFont());
        g.setColor(getForeground());
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(getText(), 10 + getIcon().getIconWidth() + getIconTextGap(),
                (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent() - 1);
        g.dispose();
    }

}
