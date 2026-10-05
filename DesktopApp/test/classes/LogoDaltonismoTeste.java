package classes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/** Confere a identidade da marca, transparência, contraste e restauração entre modos. */
public final class LogoDaltonismoTeste {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try { verificar(); }
            catch (Exception e) { throw new RuntimeException(e); }
            finally {
                Acessibilidade.setModoDaltonismo(ModoDaltonismo.PADRAO);
                Acessibilidade.setBaixaVisaoAtiva(false);
            }
        });
        System.out.println("OK: cores das logos, transparência, contraste, dimensões, baixa visão e restauração exata do padrão.");
    }

    private static void verificar() throws Exception {
        File pasta = new File("DesktopApp/build/daltonismo/logos");
        pasta.mkdirs();
        for (String recurso : new String[]{"Logo.png", "LogoLogin.png", "LogoMenor.png"}) {
            Icon fonte = new ImageIcon(LogoDaltonismoTeste.class.getResource("/images/" + recurso));
            IconeImagem logo = new IconeImagem("/images/" + recurso, fonte.getIconWidth(), fonte.getIconHeight());
            BufferedImage original = pintar(fonte);
            for (boolean baixaVisao : new boolean[]{false, true}) {
                Acessibilidade.setBaixaVisaoAtiva(baixaVisao);
                for (ModoDaltonismo modo : ModoDaltonismo.values()) {
                    Acessibilidade.setModoDaltonismo(modo);
                    BufferedImage atual = pintar(logo);
                    exigir(logo.getIconWidth() == fonte.getIconWidth() && logo.getIconHeight() == fonte.getIconHeight(),
                            "Modo de cores alterou o tamanho da logo");
                    boolean mudou = false;
                    for (int y = 0; y < original.getHeight(); y++) for (int x = 0; x < original.getWidth(); x++) {
                        int a = original.getRGB(x, y), b = atual.getRGB(x, y);
                        exigir((a >>> 24) == (b >>> 24), "Transparência/desenho alterado");
                        if (modo == ModoDaltonismo.PADRAO) exigir(a == b, "Padrão não preservou pixels originais");
                        if ((a >>> 24) == 255 && a != b) mudou = true;
                    }
                    if (modo != ModoDaltonismo.PADRAO) {
                        exigir(mudou, "Logo não acompanhou o modo");
                        PaletaAcessibilidade p = Acessibilidade.getPaleta();
                        exigir(contraste(p.getLogoBase(), p.getLogoDestaque()) >= 3, "Cores da marca sem separação de luminosidade");
                        exigir(contraste(p.getLogoDestaque(), Color.WHITE) >= 4.5, "Logo sem contraste no painel");
                        exigir(contraste(p.getLogoDestaque(), p.getFundo()) >= 4.5, "Logo sem contraste no fundo");
                    }
                    if (!baixaVisao && recurso.equals("Logo.png")) {
                        BufferedImage previa = new BufferedImage(atual.getWidth(), atual.getHeight(), BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = previa.createGraphics();
                        g.setColor(modo == ModoDaltonismo.PADRAO ? PainelListagem.fundoTela : Acessibilidade.getPaleta().getFundo());
                        g.fillRect(0, 0, previa.getWidth(), previa.getHeight()); g.drawImage(atual, 0, 0, null); g.dispose();
                        ImageIO.write(previa, "png", new File(pasta, modo.name() + ".png"));
                    }
                }
            }
        }
    }
    private static BufferedImage pintar(Icon icone) {
        BufferedImage imagem = new BufferedImage(icone.getIconWidth(), icone.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics(); icone.paintIcon(null, g, 0, 0); g.dispose(); return imagem;
    }
    private static double contraste(Color a, Color b) {
        double la = luminancia(a), lb = luminancia(b);
        return (Math.max(la, lb) + .05) / (Math.min(la, lb) + .05);
    }
    private static double luminancia(Color cor) {
        double[] canais = {cor.getRed() / 255.0, cor.getGreen() / 255.0, cor.getBlue() / 255.0};
        for (int i = 0; i < canais.length; i++) canais[i] = canais[i] <= .04045
                ? canais[i] / 12.92 : Math.pow((canais[i] + .055) / 1.055, 2.4);
        return .2126 * canais[0] + .7152 * canais[1] + .0722 * canais[2];
    }
    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
