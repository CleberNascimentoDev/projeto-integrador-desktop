package classes;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/** Mantém a fonte original em alta resolução até a pintura, inclusive em telas com escala. */
public final class IconeImagem implements Icon {
    private final Image imagem;
    private final int largura;
    private final int altura;
    private final boolean logo;
    private final Map<ModoDaltonismo, BufferedImage> imagensAdaptadas = new EnumMap<>(ModoDaltonismo.class);

    public IconeImagem(String recurso, int largura, int altura) {
        imagem = new ImageIcon(java.util.Objects.requireNonNull(
                IconeImagem.class.getResource(recurso), "Imagem não encontrada: " + recurso)).getImage();
        this.largura = largura;
        this.altura = altura;
        logo = recurso.equals("/images/Logo.png") || recurso.equals("/images/LogoMenor.png")
                || recurso.equals("/images/LogoLogin.png");
    }

    @Override public int getIconWidth() { return largura; }
    @Override public int getIconHeight() { return altura; }

    @Override public void paintIcon(Component componente, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Image atual = imagem;
        if (logo && Acessibilidade.isDaltonismoAtivo()) {
            atual = imagensAdaptadas.computeIfAbsent(Acessibilidade.getModoDaltonismo(), this::adaptarLogo);
        }
        g.drawImage(atual, x, y, largura, altura, componente);
        g.dispose();
    }

    /** Reatribui as duas cores da marca; preserva geometria, resolução e transparência. */
    private BufferedImage adaptarLogo(ModoDaltonismo modo) {
        BufferedImage original = new BufferedImage(imagem.getWidth(null), imagem.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D desenho = original.createGraphics();
        desenho.drawImage(imagem, 0, 0, null);
        desenho.dispose();
        PaletaAcessibilidade paleta = PaletaAcessibilidade.paraModo(modo);
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int pixel = original.getRGB(x, y);
                if ((pixel >>> 24) == 0) continue;
                Color cor = new Color(pixel, true);
                boolean destaque = cor.getGreen() > cor.getRed() && cor.getGreen() > cor.getBlue();
                Color adaptada = destaque ? paleta.getLogoDestaque() : paleta.getLogoBase();
                original.setRGB(x, y, (pixel & 0xff000000) | (adaptada.getRGB() & 0x00ffffff));
            }
        }
        return original;
    }
}
