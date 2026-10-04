package classes;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import views.*;

/** Verifica seleção persistente ao sair e retornar à tabela, sem acessar o banco real. */
public final class SelecaoTabelaTeste {
    private static RootPaneContainer[] telas;
    private static final String[] nomesTabelas = {"tbCargo", "tbProcessos", "tbProcesso", "tbRecrutador"};
    private static final String[] nomesVoltar = {"btVoltar", "btVoltar", "btVoltar", "btCancelar"};

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        Field conexao = database.ConexaoBanco.class.getDeclaredField("conexao");
        conexao.setAccessible(true);
        conexao.set(null, simularJdbc(Connection.class));
        try {
            SwingUtilities.invokeAndWait(() -> {
                telas = new RootPaneContainer[]{new TelaCargo(), new TelaProcessoSeletivo(),
                        new TelaGestaoRec(), new TelaAtribuirRecJd(null, false)};
                for (int i = 0; i < telas.length; i++) {
                    JTable tabela = (JTable) campo(telas[i], nomesTabelas[i]);
                    tabela.setModel(new DefaultTableModel(new Object[][]{{"Primeiro", "Ana"}, {"Segundo", "Bia"}},
                            new String[]{"Nome", "Responsável"}));
                }
            });
            // Deixa a configuração de navegação agendada pelos construtores terminar.
            SwingUtilities.invokeAndWait(() -> {});
            for (boolean baixaVisao : new boolean[]{false, true}) {
                SwingUtilities.invokeAndWait(() -> {
                    Acessibilidade.setBaixaVisaoAtiva(baixaVisao);
                    for (int i = 0; i < telas.length; i++) {
                        atualizar(telas[i]);
                        JTable tabela = (JTable) campo(telas[i], nomesTabelas[i]);
                        exigir("proximoCampo".equals(tabela.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                                .get(KeyStroke.getKeyStroke("TAB"))), "Navegação com Tab alterada");
                        tabela.setRowSelectionInterval(1, 1);
                        foco(tabela, false);
                        foco((Component) campo(telas[i], nomesVoltar[i]), true);
                        JTextField pesquisa = (JTextField) campo(telas[i], "tfPesquisa");
                        javax.swing.table.TableModel modelo = tabela.getModel();
                        foco(pesquisa, true);
                        foco(pesquisa, false);
                        foco(pesquisa, true);
                        exigir(pesquisa.getText().isEmpty(), "Foco da pesquisa inseriu texto no filtro");
                        exigir(tabela.getModel() == modelo, "Foco da pesquisa recarregou a tabela");
                    }
                });
                // Detecta também qualquer limpeza de seleção agendada com invokeLater.
                SwingUtilities.invokeAndWait(() -> {
                    for (int i = 0; i < telas.length; i++) {
                        JTable tabela = (JTable) campo(telas[i], nomesTabelas[i]);
                        exigir(tabela.getSelectedRow() == 1, "Seleção apagada ao sair de " + telas[i].getClass().getSimpleName());
                        Component celula = tabela.prepareRenderer(tabela.getCellRenderer(1, 0), 1, 0);
                        exigir(tabela.getSelectionBackground().equals(celula.getBackground()),
                                "Linha perdeu o destaque visual");
                        foco((Component) campo(telas[i], nomesVoltar[i]), false);
                        foco(tabela, true);
                    }
                });
                SwingUtilities.invokeAndWait(() -> {
                    for (int i = 0; i < telas.length; i++)
                        exigir(((JTable) campo(telas[i], nomesTabelas[i])).getSelectedRow() == 1,
                                "Retornar à tabela alterou a seleção");
                    for (int i = 0; i < 2; i++) {
                        JTable tabela = (JTable) campo(telas[i], nomesTabelas[i]);
                        foco(tabela, false);
                        for (String nome : new String[]{"btVoltar", "tfPesquisa", "btExcluir", "btEditar"}) {
                            Component botao = (Component) campo(telas[i], nome);
                            foco(botao, true);
                            exigir(tabela.getSelectedRow() == 1, "Seleção apagada antes de Cadastrar: " + nome);
                            foco(botao, false);
                        }
                        Component cadastrar = (Component) campo(telas[i], "btCadastrar");
                        foco(cadastrar, true);
                        exigir(tabela.getSelectedRow() == -1, "Cadastrar não limpou a seleção");
                        foco(cadastrar, false);
                        foco((Component) campo(telas[i], nomesVoltar[i]), true);
                        exigir(tabela.getSelectedRow() == -1, "Seleção reapareceu fora da tabela");
                        foco(tabela, true);
                        exigir(tabela.getSelectedRow() == 0, "Retornar à tabela não selecionou a primeira linha");
                    }
                });
            }
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                Acessibilidade.setBaixaVisaoAtiva(false);
                if (telas != null) for (RootPaneContainer tela : telas) {
                    foco((Component) campo(tela, "tfPesquisa"), false);
                    if (tela instanceof JInternalFrame) ((JInternalFrame) tela).dispose();
                    else ((Window) tela).dispose();
                }
            });
        }
        System.out.println("OK: seleção preservada durante a navegação, limpa somente ao focar Cadastrar em cargos e processos, incluindo baixa visão.");
    }

    private static void foco(Component componente, boolean ganhou) {
        FocusEvent evento = new FocusEvent(componente, ganhou ? FocusEvent.FOCUS_GAINED : FocusEvent.FOCUS_LOST);
        for (FocusListener listener : componente.getFocusListeners()) {
            if (ganhou) listener.focusGained(evento); else listener.focusLost(evento);
        }
    }
    private static void atualizar(RootPaneContainer tela) {
        try {
            Method atualizar = BaixaVisao.class.getDeclaredMethod("atualizarTela", JRootPane.class);
            atualizar.setAccessible(true);
            atualizar.invoke(null, tela.getRootPane());
        } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
    }
    private static Object campo(Object tela, String nome) {
        try {
            Field campo = tela.getClass().getDeclaredField(nome);
            campo.setAccessible(true);
            return campo.get(tela);
        } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
    }
    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
    private static Object simularJdbc(Class<?> tipo) {
        return Proxy.newProxyInstance(SelecaoTabelaTeste.class.getClassLoader(), new Class<?>[]{tipo}, (p, m, a) -> {
            if (m.getName().equals("executeUpdate")) throw new AssertionError("Teste tentou gravar dados");
            if (m.getReturnType() == PreparedStatement.class) return simularJdbc(PreparedStatement.class);
            if (m.getReturnType() == ResultSet.class) return simularJdbc(ResultSet.class);
            if (m.getReturnType() == boolean.class) return false;
            if (m.getReturnType() == int.class) return 0;
            return null;
        });
    }
}
