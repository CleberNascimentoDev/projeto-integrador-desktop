package classes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import views.TelaGestaoRec;
import views.TelaMenu;

/** Confere composição, dados preservados e busca com JDBC simulado, sem escrever no banco. */
public final class GestaoRecrutadoresTeste {
    private static final Object[][] dados = {{2, "DEV SENIOR", null}, {1, "DESENVOLVEDOR JUNIOR", "CLEBER"}};
    private static String ultimoFiltro;

    public static void main(String[] argumentos) throws Exception {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        Field conexao = database.ConexaoBanco.class.getDeclaredField("conexao");
        conexao.setAccessible(true);
        conexao.set(null, simularConexao());
        File imagens = new File(argumentos.length > 0 ? argumentos[0] : "DesktopApp/build/gestao/imagens");
        imagens.mkdirs();
        SwingUtilities.invokeAndWait(() -> {
            try { verificar(imagens); }
            catch (Exception e) { throw new RuntimeException(e); }
        });
        System.out.println("OK: gestão em quatro resoluções desktop e uma menor, baixa visão, busca, contagem, seleção e dados originais.");
        if (argumentos.length > 1 && "--mostrar".equals(argumentos[1])) {
            SwingUtilities.invokeAndWait(() -> {
                TelaMenu menu = new TelaMenu();
                menu.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                menu.setExtendedState(JFrame.NORMAL);
                menu.setSize(1366, 768);
                menu.setLocationRelativeTo(null);
                TelaGestaoRec tela = new TelaGestaoRec();
                JDesktopPane desktop = (JDesktopPane) campo(menu, "painelPrincipal");
                desktop.add(tela);
                menu.setVisible(true);
                tela.setVisible(true);
                try { tela.setMaximum(true); tela.setSelected(true); }
                catch (java.beans.PropertyVetoException e) { throw new RuntimeException(e); }
                Timer fechar = new Timer(20000, e -> { tela.dispose(); menu.dispose(); });
                fechar.setRepeats(false);
                fechar.start();
            });
        }
    }

    private static void verificar(File imagens) throws Exception {
        Method atualizar = BaixaVisao.class.getDeclaredMethod("atualizarTela", JRootPane.class);
        atualizar.setAccessible(true);
        for (Dimension tamanho : new Dimension[]{new Dimension(1366, 768), new Dimension(1440, 900),
                new Dimension(1600, 900), new Dimension(1920, 1080), new Dimension(800, 800)}) {
            for (boolean ampliado : new boolean[]{false, true}) {
                TelaMenu menu = new TelaMenu();
                menu.setExtendedState(JFrame.NORMAL);
                menu.setSize(tamanho);
                organizar(menu);
                JDesktopPane desktop = (JDesktopPane) campo(menu, "painelPrincipal");
                TelaGestaoRec tela = new TelaGestaoRec();
                desktop.add(tela);
                tela.setVisible(true);
                tela.setMaximum(true);
                organizar(menu);
                JTable tabela = (JTable) campo(tela, "tbProcesso");
                JTextField busca = (JTextField) campo(tela, "tfPesquisa");
                exigir(tabela.getRowCount() == 2, "Dados não carregados");
                exigir("DEV SENIOR".equals(tabela.getValueAt(0, 0)), "Nome original modificado");
                busca.setText("JUNIOR");
                exigir("%JUNIOR%".equals(ultimoFiltro) && tabela.getRowCount() == 1, "Busca original não executada");
                exigir(encontrarTexto(tela.getContentPane(), "1 processo"), "Contador singular incorreto");
                busca.setText("inexistente");
                exigir(encontrarTexto(tela.getContentPane(), "0 processos"), "Contador vazio incorreto");
                busca.setText("");
                exigir(tabela.getRowCount() == 2 && encontrarTexto(tela.getContentPane(), "2 processos"), "Contador não atualizado");
                Acessibilidade.setBaixaVisaoAtiva(ampliado);
                atualizar.invoke(null, tela.getRootPane());
                organizar(menu);
                verificarLimites(tela.getContentPane());
                verificarFoco(tela, menu);
                JLabel nome = (JLabel) tabela.prepareRenderer(tabela.getCellRenderer(0, 0), 0, 0);
                exigir("Desenvolvedor Sênior".equals(nome.getText()), "Apresentação do nome incorreta");
                exigir("DEV SENIOR".equals(tabela.getValueAt(0, 0)), "Renderizador alterou o modelo");
                tabela.setRowSelectionInterval(1, 1);
                exigir(tabela.getSelectedRow() == 1, "Seleção original indisponível");
                tabela.clearSelection();
                exigir(((JButton) campo(tela, "btAtribuir")).getActionListeners().length > 0, "Ação de atribuir removida");
                exigir(((JButton) campo(tela, "btExcluir")).getActionListeners().length > 0, "Ação de excluir removida");
                exigir(tela.getRootPane().getActionMap().get("cadastrar") != null, "Atalho de atribuir removido");
                BufferedImage imagem = new BufferedImage(tamanho.width, tamanho.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D grafico = imagem.createGraphics();
                menu.getRootPane().printAll(grafico);
                grafico.dispose();
                ImageIO.write(imagem, "png", new File(imagens, "Gestao-" + tamanho.width + "-" + (ampliado ? "baixa-visao" : "normal") + ".png"));
                Acessibilidade.setBaixaVisaoAtiva(false);
                ((JButton) campo(tela, "btVoltar")).doClick();
                exigir(tela.isClosed(), "Voltar não fecha a tela");
                menu.dispose();
            }
        }
    }

    private static Object campo(Object tela, String nome) {
        try { Field campo = tela.getClass().getDeclaredField(nome); campo.setAccessible(true); return campo.get(tela); }
        catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
    }
    private static void verificarFoco(TelaGestaoRec tela, TelaMenu menu) throws Exception {
        Method acessibilidade = TelaGestaoRec.class.getDeclaredMethod("acessibilidade");
        acessibilidade.setAccessible(true);
        acessibilidade.invoke(tela);
        JButton voltar = (JButton) campo(tela, "btVoltar");
        Dimension preferencia = voltar.getPreferredSize();
        Rectangle limites = voltar.getBounds();
        for (java.awt.event.FocusListener listener : voltar.getFocusListeners())
            listener.focusGained(new java.awt.event.FocusEvent(voltar, java.awt.event.FocusEvent.FOCUS_GAINED));
        organizar(menu);
        exigir(voltar.getPreferredSize().equals(preferencia) && voltar.getBounds().equals(limites),
                "Voltar mudou de tamanho ao receber foco");
        for (java.awt.event.FocusListener listener : voltar.getFocusListeners())
            listener.focusLost(new java.awt.event.FocusEvent(voltar, java.awt.event.FocusEvent.FOCUS_LOST));
        organizar(menu);
        exigir(voltar.getPreferredSize().equals(preferencia) && voltar.getBounds().equals(limites),
                "Voltar mudou de tamanho ao perder foco");
    }
    private static void organizar(Container painel) {
        painel.doLayout();
        for (Component filho : painel.getComponents()) if (filho instanceof Container) organizar((Container) filho);
    }
    private static boolean encontrarTexto(Container painel, String texto) {
        for (Component campo : painel.getComponents()) {
            if (campo instanceof JLabel && texto.equals(((JLabel) campo).getText())) return true;
            if (campo instanceof Container && encontrarTexto((Container) campo, texto)) return true;
        }
        return false;
    }
    private static void verificarLimites(Container painel) {
        for (Component filho : painel.getComponents()) {
            if (filho instanceof JButton || filho instanceof JTextField || filho instanceof JPanel || filho instanceof JScrollPane) {
                exigir(filho.getX() >= 0 && filho.getY() >= 0 && filho.getX() + filho.getWidth() <= painel.getWidth()
                        && filho.getY() + filho.getHeight() <= painel.getHeight(), "Controle fora da área: " + filho.getClass().getSimpleName());
                if (filho instanceof JButton) {
                    JButton botao = (JButton) filho;
                    exigir(botao.getWidth() >= botao.getPreferredSize().width, "Botão com texto cortado: " + botao.getText());
                }
            }
            if (filho instanceof JPanel) verificarLimites((Container) filho);
        }
    }
    private static void exigir(boolean condicao, String mensagem) { if (!condicao) throw new AssertionError(mensagem); }

    private static Connection simularConexao() {
        return (Connection) Proxy.newProxyInstance(GestaoRecrutadoresTeste.class.getClassLoader(), new Class<?>[]{Connection.class}, (p, m, a) -> {
            if (m.getName().equals("prepareStatement")) return simularConsulta();
            return retornoPadrao(m.getReturnType());
        });
    }
    private static PreparedStatement simularConsulta() {
        String[] filtro = {""};
        return (PreparedStatement) Proxy.newProxyInstance(GestaoRecrutadoresTeste.class.getClassLoader(), new Class<?>[]{PreparedStatement.class}, (p, m, a) -> {
            if (m.getName().equals("setString")) { filtro[0] = (String) a[1]; ultimoFiltro = filtro[0]; }
            if (m.getName().equals("executeUpdate")) throw new AssertionError("Teste tentou gravar dados");
            if (m.getName().equals("executeQuery")) {
                List<Object[]> linhas = new ArrayList<>();
                for (Object[] linha : dados) if (((String) linha[1]).toLowerCase().contains(filtro[0].replace("%", "").toLowerCase())) linhas.add(linha);
                int[] indice = {-1};
                return Proxy.newProxyInstance(GestaoRecrutadoresTeste.class.getClassLoader(), new Class<?>[]{ResultSet.class}, (rp, rm, ra) -> {
                    if (rm.getName().equals("next")) return ++indice[0] < linhas.size();
                    if (rm.getName().equals("getInt")) return linhas.get(indice[0])[0];
                    if (rm.getName().equals("getString")) return linhas.get(indice[0])["nome_proce".equals(ra[0]) ? 1 : 2];
                    return retornoPadrao(rm.getReturnType());
                });
            }
            return retornoPadrao(m.getReturnType());
        });
    }
    private static Object retornoPadrao(Class<?> tipo) {
        if (tipo == boolean.class) return false;
        if (tipo == int.class) return 0;
        return null;
    }
}
