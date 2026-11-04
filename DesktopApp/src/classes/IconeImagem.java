package classes;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/** Mantém a fonte original em alta resolução até a pintura, inclusive em telas com escala. */
public final class IconeImagem implements Icon {
    private final Image imagem;
    private final int largura;
    private final int altura;
    private final boolean logo;
    private final String recurso;
    private final Map<String, Image> imagensAdaptadas = new HashMap<>();
    private BufferedImage imagemAltoContraste;

    public IconeImagem(String recurso, int largura, int altura) {
        this.recurso = recurso;
        imagem = new ImageIcon(java.util.Objects.requireNonNull(
                IconeImagem.class.getResource(recurso), "Imagem não encontrada: " + recurso)).getImage();
        this.largura = largura;
        this.altura = altura;
        logo = recurso.equals("/images/Logo.png") || recurso.equals("/images/LogoMenor.png")
                || recurso.equals("/images/LogoLogin.png");
    }

    /** Adapta ícones legados sem alterar seus arquivos ou dimensões. */
    public IconeImagem(ImageIcon original) {
        recurso = null;
        imagem = original.getImage();
        largura = original.getIconWidth();
        altura = original.getIconHeight();
        logo = false;
    }

    @Override public int getIconWidth() { return largura; }
    @Override public int getIconHeight() { return altura; }

    @Override public void paintIcon(Component componente, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Image atual = imagem;
        if (logo && Acessibilidade.isPaletaAdaptada()) {
            String variante = Acessibilidade.isAltoContrasteAtivo() ? "alto-contraste"
                    : Acessibilidade.getModoDaltonismo().name().toLowerCase(java.util.Locale.ROOT);
            atual = imagensAdaptadas.computeIfAbsent(variante, nome -> new ImageIcon(
                    java.util.Objects.requireNonNull(IconeImagem.class.getResource(
                            recurso.replace(".png", "-" + nome + ".png")), "Logo adaptada não encontrada")).getImage());
        } else if (!logo && Acessibilidade.isAltoContrasteAtivo()) {
            if (imagemAltoContraste == null) imagemAltoContraste = adaptarImagem(PaletaAcessibilidade.altoContraste());
            atual = imagemAltoContraste;
        }
        g.drawImage(atual, x, y, largura, altura, componente);
        g.dispose();
    }

    /** Somente ícones legados são adaptados em memória; logos vêm dos PNGs em images. */
    private BufferedImage adaptarImagem(PaletaAcessibilidade paleta) {
        BufferedImage original = new BufferedImage(imagem.getWidth(null), imagem.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D desenho = original.createGraphics();
        desenho.drawImage(imagem, 0, 0, null);
        desenho.dispose();
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int pixel = original.getRGB(x, y);
                if ((pixel >>> 24) == 0) continue;
                Color cor = new Color(pixel, true);
                Color adaptada = cor.getRed() + cor.getGreen() + cor.getBlue() > 650
                        ? paleta.getSuperficie() : paleta.getTexto();
                original.setRGB(x, y, (pixel & 0xff000000) | (adaptada.getRGB() & 0x00ffffff));
            }
        }
        return original;
    }
}
