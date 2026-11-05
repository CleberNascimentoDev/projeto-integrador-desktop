package classes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import views.TelaMapaAtalhoJd;
import views.TelaSobre;

/** Regressão da leitura em alto contraste, ampliação e restauração. */
public final class LeituraAcessivelTeste {
    public static void main(String[] args) throws Exception {
        System.setProperty("mainrh.preferencias", "/mainrh/testes/leitura-" + System.nanoTime());
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeAndWait(() -> {
            try {
                verificar(new TelaSobre(null, false), "sobre");
                verificar(new TelaMapaAtalhoJd(null, false), "atalhos");
            } catch (Exception erro) { throw new RuntimeException(erro); }
            finally {
                Acessibilidade.restaurarPadrao();
                for (Window janela : Window.getWindows()) janela.dispose();
            }
        });
        System.out.println("OK: leitura, contraste, quebra de linhas, rolagem, fechamento e restauracao nas duas janelas.");
    }

    private static void verificar(JDialog janela, String nome) throws Exception {
        janela.setVisible(true);
        RotuloHtml texto = localizar(janela, RotuloHtml.class);
        JScrollPane rolagem = localizar(janela, JScrollPane.class);
        java.lang.reflect.Field campoFechar = janela.getClass().getDeclaredField(
                nome.equals("sobre") ? "btEntendi4" : "btEntendi");
        campoFechar.setAccessible(true);
        JButton fechar = (JButton) campoFechar.get(janela);
        if (texto == null || rolagem == null || fechar == null) throw new AssertionError("Leitura incompleta");
        float fonteOriginal = texto.getFont().getSize2D();
        for (int modo : new int[]{0, 1, 2, 3, 0, 2, 1, 0}) {
            Acessibilidade.setAltoContrasteAtivo(modo == 1 || modo == 2);
            Acessibilidade.setBaixaVisaoAtiva(modo == 2 || modo == 3);
            organizar(janela);
            Dimension tamanho = texto.getPreferredSize();
            rolagem.getViewport().doLayout();
            if (tamanho.width > rolagem.getViewport().getWidth() + 1)
                throw new AssertionError(nome + ": texto ultrapassa a largura");
            if (texto.getText().matches("(?s).*(?<![-\\w])color\\s*:.*"))
                throw new AssertionError(nome + ": cor fixa no HTML");
            if ((modo == 1 || modo == 2) && texto.getForeground().getRed() < 230)
                throw new AssertionError(nome + ": texto escuro em alto contraste");
            if (modo == 2 && texto.getFont().getSize2D() <= fonteOriginal)
                throw new AssertionError(nome + ": fonte nao ampliada");
            Rectangle botao = SwingUtilities.convertRectangle(fechar.getParent(), fechar.getBounds(), janela);
            if (!new Rectangle(0, 0, janela.getWidth(), janela.getHeight()).contains(botao))
                throw new AssertionError(nome + ": botao de fechar fora da janela");
            if (modo == 0 && texto.getFont().getSize2D() != fonteOriginal)
                throw new AssertionError(nome + ": fonte original nao restaurada");
            if (modo == 2) {
                rolagem.getVerticalScrollBar().setValue(rolagem.getVerticalScrollBar().getMaximum());
                organizar(janela);
                rolagem.getVerticalScrollBar().setValue(0);
            }
            File arquivo = new File("DesktopApp/build/leitura/" + nome + "-" + modo + ".png");
            arquivo.getParentFile().mkdirs();
            BufferedImage imagem = new BufferedImage(janela.getWidth(), janela.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = imagem.createGraphics(); janela.paint(g); g.dispose();
            ImageIO.write(imagem, "png", arquivo);
        }
        fechar.doClick();
        if (janela.isDisplayable()) throw new AssertionError(nome + ": fechamento nao funcionou");
    }

    private static void organizar(Container painel) {
        painel.doLayout();
        for (Component filho : painel.getComponents()) if (filho instanceof Container) organizar((Container) filho);
    }

    private static <T> T localizar(Component componente, Class<T> tipo) {
        if (tipo.isInstance(componente)) return tipo.cast(componente);
        if (componente instanceof Container) for (Component filho : ((Container) componente).getComponents()) {
            T encontrado = localizar(filho, tipo);
            if (encontrado != null) return encontrado;
        }
        return null;
    }
}
