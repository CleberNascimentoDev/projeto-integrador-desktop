package classes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import views.TelaMenu;

/** Verifica a interrogação e a quebra de linhas da ajuda sem abrir janelas. */
public class AjudaTeste {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeAndWait(() -> {
            try { verificar(args.length > 0 ? new File(args[0]) : null); }
            catch (Exception erro) { throw new RuntimeException(erro); }
        });
        System.out.println("OK: interrogação visível e ajuda ajustada, com baixa visão e modos combinados.");
    }

    private static void verificar(File imagens) throws Exception {
        TelaMenu menu = new TelaMenu();
        menu.pack();
        menu.setSize(1280, 900);
        var campo = TelaMenu.class.getDeclaredField("botaoAjuda");
        campo.setAccessible(true);
        BotaoAjuda botao = (BotaoAjuda) campo.get(menu);
        Method criar = BotaoAjuda.class.getDeclaredMethod("criarJanelaAjuda");
        criar.setAccessible(true);
        Method atualizar = BaixaVisao.class.getDeclaredMethod("atualizarTela", JRootPane.class);
        atualizar.setAccessible(true);
        try {
            for (int modo = 0; modo < 3; modo++) {
                Acessibilidade.setBaixaVisaoAtiva(modo > 0);
                Acessibilidade.setDaltonismoAtivo(modo == 2);
                atualizar.invoke(null, menu.getRootPane());
                Insets bordas = botao.getInsets();
                FontMetrics fonte = botao.getFontMetrics(botao.getFont());
                exigir(botao.getWidth() >= fonte.stringWidth("?") + bordas.left + bordas.right,
                        "Interrogação não cabe na largura do botão");
                exigir(botao.getHeight() >= fonte.getHeight() + bordas.top + bordas.bottom,
                        "Interrogação não cabe na altura do botão");
                BufferedImage imagemBotao = imagem(botao);
                int pontosPretos = 0;
                for (int y = bordas.top; y < botao.getHeight() - bordas.bottom; y++)
                    for (int x = bordas.left; x < botao.getWidth() - bordas.right; x++)
                        if ((imagemBotao.getRGB(x, y) & 0xffffff) == 0) pontosPretos++;
                exigir(pontosPretos > 10, "Interrogação não foi desenhada");
                JDialog ajuda = (JDialog) criar.invoke(botao);
                try {
                    atualizar.invoke(null, ajuda.getRootPane());
                    organizar(ajuda);
                    JLabel mensagem = mensagem(ajuda.getContentPane());
                    View texto = (View) mensagem.getClientProperty(BasicHTML.propertyKey);
                    exigir(mensagem.getWidth() > ajuda.getWidth() * 0.75, "Texto não aproveita a largura da ajuda");
                    exigir(texto.getPreferredSpan(View.Y_AXIS) <= mensagem.getHeight() + 2, "Texto da ajuda foi cortado");
                    exigir(!(ajuda.getContentPane() instanceof JScrollPane), "Ajuda recebeu rolagem na janela");
                    if (imagens != null) {
                        imagens.mkdirs();
                        ImageIO.write(imagemBotao, "png", new File(imagens, "Interrogacao-" + modo + ".png"));
                        ImageIO.write(imagem(ajuda.getContentPane()), "png", new File(imagens, "Ajuda-" + modo + ".png"));
                    }
                } finally { ajuda.dispose(); }
            }
        } finally {
            Acessibilidade.setDaltonismoAtivo(false);
            Acessibilidade.setBaixaVisaoAtiva(false);
            menu.dispose();
        }
    }

    private static JLabel mensagem(Container painel) {
        for (Component campo : painel.getComponents()) {
            if (campo instanceof JLabel && ((JLabel) campo).getText() != null
                    && ((JLabel) campo).getText().startsWith("<html>")) return (JLabel) campo;
            if (campo instanceof JPanel) {
                JLabel encontrado = mensagem((Container) campo);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    private static void organizar(Container painel) {
        painel.doLayout();
        for (Component campo : painel.getComponents())
            if (campo instanceof Container) organizar((Container) campo);
    }

    private static BufferedImage imagem(Component campo) {
        BufferedImage imagem = new BufferedImage(campo.getWidth(), campo.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D grafico = imagem.createGraphics();
        grafico.setColor(Color.WHITE);
        grafico.fillRect(0, 0, imagem.getWidth(), imagem.getHeight());
        campo.printAll(grafico);
        grafico.dispose();
        return imagem;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
