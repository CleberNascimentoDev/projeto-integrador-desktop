package classes;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/** Mantém a fonte original em alta resolução até a pintura, inclusive em telas com escala. */
public final class IconeImagem implements Icon {
    private final Image imagem;
    private final int largura;
    private final int altura;

    public IconeImagem(String recurso, int largura, int altura) {
        imagem = new ImageIcon(java.util.Objects.requireNonNull(
                IconeImagem.class.getResource(recurso), "Imagem não encontrada: " + recurso)).getImage();
        this.largura = largura;
        this.altura = altura;
    }

    @Override public int getIconWidth() { return largura; }
    @Override public int getIconHeight() { return altura; }

    @Override public void paintIcon(Component componente, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(imagem, x, y, largura, altura, componente);
        g.dispose();
    }
}
