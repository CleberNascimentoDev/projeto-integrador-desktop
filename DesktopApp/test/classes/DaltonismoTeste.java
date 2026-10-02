package classes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import views.*;

/** Verifica a paleta e sua restauração sem abrir janelas ou acessar o banco. */
public class DaltonismoTeste {
    public static void main(String[] args) throws Exception {
        Field conexao = database.ConexaoBanco.class.getDeclaredField("conexao");
        conexao.setAccessible(true);
        conexao.set(null, simularJdbc(Connection.class));
        SwingUtilities.invokeAndWait(() -> {
            java.util.List<RootPaneContainer> telas = new ArrayList<>();
            try {
                telas.add(new TelaLogin());
                telas.add(new TelaMenu());
                telas.add(new TelaCargo());
                telas.add(new TelaGestaoRec());
                telas.add(new TelaProcessoSeletivo());
                telas.add(new TelaDetalhesCargo());
                telas.add(new TelaCadastroJd(null, false));
                telas.add(new TelaEditarJd(null, false));
                telas.add(new TelaCadastroUsuarioJd(null, false));
                telas.add(new TelaCadastrarProcessoJd(null, false));
                telas.add(new TelaEditarProcessoJd(null, false));
                telas.add(new TelaAtribuirRecJd(null, false));
                telas.add(new TelaAcessibilidade(null));
                JDialog mensagem = new JOptionPane("Dados inválidos. Verifique os campos.",
                        JOptionPane.WARNING_MESSAGE).createDialog("Validação");
                Acessibilidade.configurarTela(mensagem);
                telas.add(mensagem);
                Method atualizar = Daltonismo.class.getDeclaredMethod("atualizarTela", JRootPane.class);
                atualizar.setAccessible(true);
                Acessibilidade.setTecladoAtivo(false);
                for (RootPaneContainer tela : telas) verificar(tela, atualizar, args);
                verificarAlteracaoDinamica();
                verificarBotaoNoLogin();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                Acessibilidade.setDaltonismoAtivo(false);
                Acessibilidade.setTecladoAtivo(true);
                for (RootPaneContainer tela : telas) {
                    if (tela instanceof Window) ((Window) tela).dispose();
                    else ((JInternalFrame) tela).dispose();
                }
            }
        });
        System.out.println("OK: 13 telas e mensagem; contraste, restauração e teclado independente.");
    }

    private static void verificar(RootPaneContainer tela, Method atualizar, String[] args) throws Exception {
        if (tela instanceof Window) ((Window) tela).pack();
        else { ((JInternalFrame) tela).addNotify(); ((JInternalFrame) tela).pack(); }
        organizar((Container) tela);
        Map<JComponent, Object[]> originais = new IdentityHashMap<>();
        guardar(tela.getContentPane(), originais);
        Dimension tamanho = ((Component) tela).getSize();
        for (int repeticao = 0; repeticao < 3; repeticao++) {
            Acessibilidade.setDaltonismoAtivo(true);
            atualizar.invoke(null, tela.getRootPane());
            for (Map.Entry<JComponent, Object[]> entrada : originais.entrySet()) {
                JComponent campo = entrada.getKey();
                Object[] original = entrada.getValue();
                exigir(Objects.equals(campo.getFont(), original[2]), "Modo daltonismo alterou a fonte");
                if (campo instanceof JButton && ((JButton) campo).isContentAreaFilled()) {
                    Color fundo = (Color) original[1];
                    if (fundo.equals(new Color(192, 1, 1))) {
                        exigir(campo.getBackground().equals(new Color(255, 210, 128)), "Excluir não recebeu laranja");
                        exigir(contraste(campo.getForeground(), campo.getBackground()) >= 4.5, "Excluir sem contraste");
                    } else if (fundo.equals(new Color(11, 176, 142))) {
                        exigir(campo.getBackground().equals(new Color(0, 85, 140)), "Editar não recebeu azul");
                        exigir(contraste(campo.getForeground(), campo.getBackground()) >= 4.5, "Editar sem contraste");
                    }
                }
                if (campo instanceof JLabel && "*".equals(((JLabel) campo).getText())) {
                    exigir(contraste(campo.getForeground(), Color.WHITE) >= 4.5, "Asterisco sem contraste");
                }
            }
            exigir(((Component) tela).getSize().equals(tamanho), "Modo daltonismo alterou dimensões");
            if (repeticao == 0 && args.length > 0) {
                File diretorio = new File(args[0]);
                diretorio.mkdirs();
                Container conteudo = tela.getContentPane();
                BufferedImage imagem = new BufferedImage(Math.max(1, conteudo.getWidth()),
                        Math.max(1, conteudo.getHeight()), BufferedImage.TYPE_INT_RGB);
                Graphics2D g = imagem.createGraphics();
                conteudo.printAll(g);
                g.dispose();
                ImageIO.write(imagem, "png", new File(diretorio, tela.getClass().getSimpleName() + ".png"));
            }
            Acessibilidade.setDaltonismoAtivo(false);
            atualizar.invoke(null, tela.getRootPane());
            for (Map.Entry<JComponent, Object[]> entrada : originais.entrySet()) {
                JComponent campo = entrada.getKey();
                Object[] original = entrada.getValue();
                exigir(Objects.equals(campo.getForeground(), original[0])
                        && Objects.equals(campo.getBackground(), original[1])
                        && Objects.equals(campo.getBorder(), original[3]), "Aparência original não restaurada");
            }
            exigir(!Acessibilidade.isTecladoAtivo(), "Modo daltonismo alterou navegação por teclado");
        }
    }

    private static void verificarAlteracaoDinamica() throws Exception {
        JButton botao = new JButton("Editar");
        Color original = new Color(11, 176, 142);
        botao.setBackground(original);
        Method aplicar = Daltonismo.class.getDeclaredMethod("aplicarComponentes", Component.class);
        aplicar.setAccessible(true);
        Acessibilidade.setDaltonismoAtivo(true);
        aplicar.invoke(null, botao);
        botao.setBackground(new Color(192, 1, 1));
        exigir(botao.getBackground().equals(new Color(255, 210, 128)), "Cor dinâmica não adaptada");
        Acessibilidade.setDaltonismoAtivo(false);
        aplicar.invoke(null, botao);
        exigir(botao.getBackground().equals(new Color(192, 1, 1)), "Última cor original não restaurada");
    }

    private static void verificarBotaoNoLogin() {
        // Raiz visível simulada exercita o mesmo caminho público usado pelo botão,
        // sem depender de uma janela real no computador.
        JFrame login = new JFrame() {
            @Override protected JRootPane createRootPane() {
                return new JRootPane() {
                    @Override public boolean isShowing() { return true; }
                };
            }
        };
        JPanel painel = new JPanel();
        Color corOriginal = new Color(233, 243, 255);
        painel.setBackground(corOriginal);
        JButton entrar = new JButton("Entrar");
        Color botaoOriginal = new Color(31, 53, 80);
        entrar.setBackground(botaoOriginal);
        entrar.setForeground(Color.WHITE);
        painel.add(entrar);
        login.setContentPane(painel);
        Acessibilidade.configurarTela(login);
        TelaAcessibilidade opcoes = new TelaAcessibilidade(login);
        try {
            JButton botao = null;
            for (Component campo : opcoes.getContentPane().getComponents()) {
                if (campo instanceof JButton && ((JButton) campo).getText().equals("Ativar modo daltonismo")) {
                    botao = (JButton) campo;
                }
            }
            exigir(botao != null, "Botão daltonismo não encontrado");
            botao.doClick();
            exigir(painel.getBackground().equals(new Color(248, 245, 238)), "Fundo do login não mudou");
            exigir(entrar.getBackground().equals(new Color(0, 85, 140)), "Botão Entrar não mudou");
            exigir(contraste(entrar.getForeground(), entrar.getBackground()) >= 4.5, "Entrar sem contraste");
            exigir(botao.getText().equals("Desativar modo daltonismo"), "Botão não confirmou ativação");
            botao.doClick();
            exigir(painel.getBackground().equals(corOriginal), "Fundo do login não restaurado");
            exigir(entrar.getBackground().equals(botaoOriginal), "Botão Entrar não restaurado");
        } finally {
            opcoes.dispose();
            login.dispose();
        }
    }

    private static void guardar(Component componente, Map<JComponent, Object[]> originais) {
        if (componente instanceof JInternalFrame) return;
        if (componente instanceof JComponent) {
            JComponent campo = (JComponent) componente;
            originais.put(campo, new Object[]{campo.getForeground(), campo.getBackground(), campo.getFont(), campo.getBorder()});
        }
        if (componente instanceof JPanel || componente instanceof JDesktopPane || componente instanceof JScrollPane
                || componente instanceof JViewport || componente instanceof JOptionPane) {
            for (Component filho : ((Container) componente).getComponents()) guardar(filho, originais);
        }
    }

    private static void organizar(Container painel) {
        painel.doLayout();
        for (Component filho : painel.getComponents()) if (filho instanceof Container) organizar((Container) filho);
    }

    private static double contraste(Color texto, Color fundo) {
        double a = luminancia(texto), b = luminancia(fundo);
        return (Math.max(a, b) + 0.05) / (Math.min(a, b) + 0.05);
    }

    private static double luminancia(Color cor) {
        double[] canais = {cor.getRed() / 255.0, cor.getGreen() / 255.0, cor.getBlue() / 255.0};
        for (int i = 0; i < canais.length; i++) canais[i] = canais[i] <= 0.04045
                ? canais[i] / 12.92 : Math.pow((canais[i] + 0.055) / 1.055, 2.4);
        return 0.2126 * canais[0] + 0.7152 * canais[1] + 0.0722 * canais[2];
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static Object simularJdbc(Class<?> tipo) {
        return Proxy.newProxyInstance(DaltonismoTeste.class.getClassLoader(), new Class<?>[]{tipo}, (proxy, metodo, args) -> {
            Class<?> retorno = metodo.getReturnType();
            if (retorno == PreparedStatement.class) return simularJdbc(PreparedStatement.class);
            if (retorno == ResultSet.class) return simularJdbc(ResultSet.class);
            if (retorno == boolean.class) return false;
            if (retorno == int.class) return 0;
            if (retorno == long.class) return 0L;
            if (retorno == double.class) return 0.0;
            return null;
        });
    }
}
