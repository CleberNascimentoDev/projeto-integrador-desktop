package classes;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import views.*;

/** Exercita o duplo clique e a separação visual das subtelas sem acessar o banco real. */
public final class SubtelasTeste {
    private static final Map<String, Object> cargo = Map.of(
            "id_cargo_pk", 7, "nome_cargo", "ANALISTA DE SISTEMAS",
            "salario_base_cargo", new BigDecimal("4500.00"), "nivel_cargo", "Pleno",
            "setor_cargo", "Tecnologia", "requisitos_cargo", "Conhecimentos de Java",
            "atividades_cargo", "Desenvolver sistemas");

    public static void main(String[] argumentos) throws Exception {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        Field conexao = database.ConexaoBanco.class.getDeclaredField("conexao");
        conexao.setAccessible(true);
        conexao.set(null, simularJdbc(Connection.class));
        File imagens = new File("DesktopApp/build/subtelas/imagens");
        imagens.mkdirs();
        SwingUtilities.invokeAndWait(() -> {
            try { verificar(imagens); }
            catch (Exception e) { throw new RuntimeException(e); }
            finally {
                Acessibilidade.setBaixaVisaoAtiva(false);
                Acessibilidade.setDaltonismoAtivo(false);
                for (Window janela : Window.getWindows()) janela.dispose();
            }
        });
        System.out.println("OK: duplo clique abre os detalhes visíveis, campos preservados, voltar fecha apenas os detalhes e subtelas diferenciadas nos modos de acessibilidade.");
    }

    private static void verificar(File imagens) throws Exception {
        JDesktopPane desktop = new JDesktopPane();
        desktop.setSize(1600, 960);
        TelaCargo tela = new TelaCargo();
        desktop.add(tela);
        tela.addNotify();
        tela.setVisible(true);
        tela.setMaximum(true);
        organizar(desktop);
        JTable tabela = (JTable) campo(tela, "tbCargo");
        JDesktopPane detalhes = (JDesktopPane) campo(tela, "painelDesktopPane");
        exigir(SwingUtilities.isDescendingFrom(detalhes, tela), "Desktop dos detalhes está fora da tela visível");
        exigir(tabela.getRowCount() == 1, "Cargo simulado não carregado");
        tabela.setRowSelectionInterval(0, 0);
        clicar(tabela, 1);
        exigir(detalhes.getAllFrames().length == 0, "Clique simples abriu detalhes");
        for (boolean baixaVisao : new boolean[]{false, true}) {
            Acessibilidade.setBaixaVisaoAtiva(baixaVisao);
            atualizarModo(BaixaVisao.class, tela);
            organizar(desktop);
            clicar(tabela, 2);
            organizar(desktop);
            exigir(detalhes.getAllFrames().length == 1, "Duplo clique não abriu detalhes");
            TelaDetalhesCargo detalhe = (TelaDetalhesCargo) detalhes.getAllFrames()[0];
            atualizarModo(BaixaVisao.class, detalhe);
            organizar(desktop);
            exigir(tela.isVisible() && detalhes.getWidth() > 0 && detalhes.getHeight() > 0,
                    "Tela de cargos deixou de aparecer");
            exigir(detalhe.isVisible() && detalhe.getWidth() > 0 && detalhe.getHeight() > 0,
                    "Detalhes estão invisíveis");
            exigir(new Rectangle(0, 0, detalhes.getWidth(), detalhes.getHeight()).contains(detalhe.getBounds()),
                    "Detalhes ultrapassaram a área da tela de cargos");
            exigir("ANALISTA DE SISTEMAS".equals(((JTextField) campo(detalhe, "tfNomeCargo")).getText()),
                    "Detalhes não correspondem ao cargo selecionado");
            exigir("Pleno".equals(((JTextField) campo(detalhe, "tfNivel")).getText()), "Nível não preenchido");
            exigir("Tecnologia".equals(((JTextField) campo(detalhe, "tfSetor")).getText()), "Setor não preenchido");
            exigir("Conhecimentos de Java".equals(((JTextArea) campo(detalhe, "taRequisitos")).getText()),
                    "Requisitos não preenchidos");
            exigir("Desenvolver sistemas".equals(((JTextArea) campo(detalhe, "taAtividades")).getText()),
                    "Atividades não preenchidas");
            exigir(!((JTextField) campo(detalhe, "tfNomeCargo")).isEditable(), "Detalhes passaram a permitir edição");
            verificarFundo((JComponent) detalhe.getContentPane());
            salvar(desktop, new File(imagens, baixaVisao ? "cargo-baixa-visao.png" : "cargo-normal.png"));
            ((JButton) campo(detalhe, "btVoltar")).doClick();
            exigir(detalhe.isClosed() && !tela.isClosed() && detalhes.getAllFrames().length == 0,
                    "Voltar não fechou apenas a janela de detalhes");
        }
        tela.dispose();
        Acessibilidade.setBaixaVisaoAtiva(false);
        TelaCadastrarProcessoJd processo = new TelaCadastrarProcessoJd(null, false);
        TelaAtribuirRecJd atribuicao = new TelaAtribuirRecJd(null, false);
        for (boolean baixaVisao : new boolean[]{false, true}) {
            for (boolean daltonismo : new boolean[]{false, true}) {
                Acessibilidade.setBaixaVisaoAtiva(baixaVisao);
                Acessibilidade.setDaltonismoAtivo(daltonismo);
                for (JDialog janela : new JDialog[]{processo, atribuicao}) {
                    atualizarModo(BaixaVisao.class, janela);
                    atualizarModo(Daltonismo.class, janela);
                    janela.pack();
                    organizar(janela);
                    verificarFundo((JComponent) janela.getContentPane());
                }
                salvar(processo.getContentPane(), new File(imagens,
                        "processo-" + baixaVisao + "-" + daltonismo + ".png"));
            }
        }
    }

    private static void verificarFundo(JComponent painel) {
        exigir(PainelListagem.fundoSubtela.equals(painel.getBackground()), "Subtela perdeu o fundo neutro");
        exigir(painel.getBorder() instanceof javax.swing.border.LineBorder
                && ((javax.swing.border.LineBorder) painel.getBorder()).getThickness() >= 2,
                "Subtela sem borda de separação");
    }

    private static void atualizarModo(Class<?> modo, RootPaneContainer janela) throws Exception {
        Method atualizar = modo.getDeclaredMethod("atualizarTela", JRootPane.class);
        atualizar.setAccessible(true);
        atualizar.invoke(null, janela.getRootPane());
    }

    private static void clicar(JTable tabela, int cliques) {
        MouseEvent evento = new MouseEvent(tabela, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                0, 20, tabela.getRowHeight() / 2, cliques, false, MouseEvent.BUTTON1);
        tabela.dispatchEvent(evento);
    }

    private static Object campo(Object tela, String nome) throws Exception {
        Field campo = tela.getClass().getDeclaredField(nome);
        campo.setAccessible(true);
        return campo.get(tela);
    }
    private static void organizar(Container painel) {
        painel.doLayout();
        for (Component filho : painel.getComponents()) if (filho instanceof Container) organizar((Container) filho);
    }
    private static void salvar(Container painel, File arquivo) throws Exception {
        BufferedImage imagem = new BufferedImage(painel.getWidth(), painel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D grafico = imagem.createGraphics();
        painel.printAll(grafico);
        grafico.dispose();
        ImageIO.write(imagem, "png", arquivo);
    }
    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
    private static Object simularJdbc(Class<?> tipo) {
        int[] linha = {0};
        return Proxy.newProxyInstance(SubtelasTeste.class.getClassLoader(), new Class<?>[]{tipo}, (p, m, a) -> {
            if (m.getName().equals("executeUpdate")) throw new AssertionError("Teste tentou gravar dados");
            if (m.getReturnType() == PreparedStatement.class) return simularJdbc(PreparedStatement.class);
            if (m.getReturnType() == ResultSet.class) return simularJdbc(ResultSet.class);
            if (m.getName().equals("next")) return linha[0]++ == 0;
            if (m.getName().equals("getInt")) return cargo.getOrDefault(a[0], 0);
            if (m.getName().equals("getString") || m.getName().equals("getBigDecimal"))
                return cargo.get(a[0]);
            if (m.getReturnType() == boolean.class) return false;
            if (m.getReturnType() == int.class) return 0;
            return null;
        });
    }
}
