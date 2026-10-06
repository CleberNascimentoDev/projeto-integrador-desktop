package classes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;

/** Paletas, transições, controles reais, novas janelas e persistência em JVMs separadas. */
public final class ModosDaltonismoTeste {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--gravar")) {
            SwingUtilities.invokeAndWait(() -> Acessibilidade.setModoDaltonismo(ModoDaltonismo.TRITANOPIA));
            System.out.println("OK: preferência Tritanopia salva");
            return;
        }
        if (args.length > 0 && args[0].equals("--ler")) {
            exigir(Acessibilidade.getModoDaltonismo() == ModoDaltonismo.TRITANOPIA, "Preferência não carregada em nova JVM");
            JFrame[] nova = new JFrame[1];
            SwingUtilities.invokeAndWait(() -> {
                nova[0] = janela(); nova[0].setContentPane(new JPanel()); nova[0].pack();
                Acessibilidade.configurarTela(nova[0]);
            });
            SwingUtilities.invokeAndWait(() -> {
                exigir(nova[0].getContentPane().getBackground().equals(Acessibilidade.getPaleta().getFundo()),
                        "Nova janela não aplicou o modo salvo");
                Acessibilidade.setModoDaltonismo(ModoDaltonismo.PADRAO); nova[0].dispose();
            });
            System.out.println("OK: preferência carregada em nova JVM");
            return;
        }
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeAndWait(() -> {
            try { verificar(); }
            catch (Exception e) { throw new RuntimeException(e); }
            finally {
                Acessibilidade.setModoDaltonismo(ModoDaltonismo.PADRAO);
                Acessibilidade.setBaixaVisaoAtiva(false);
                for (Window janela : Window.getWindows()) janela.dispose();
            }
        });
        System.out.println("OK: três paletas, todas as transições, múltiplas janelas, seleções, campos desabilitados, combo, menus, alertas, desenhos e baixa visão.");
    }

    private static void verificar() throws Exception {
        Acessibilidade.setModoDaltonismo(ModoDaltonismo.PADRAO);
        JFrame janela = janela();
        JPanel painel = new JPanel();
        painel.setBackground(PainelListagem.fundoTela);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        JButton editar = new JButton("Editar");
        JButton excluir = new JButton("Excluir");
        JButton desabilitado = new JButton("Indisponível");
        PainelListagem.configurarBotao(editar, "Editar", 18);
        PainelListagem.configurarBotao(excluir, "Excluir", 18);
        PainelListagem.configurarBotao(desabilitado, "Indisponível", 18);
        desabilitado.setEnabled(false);
        JTextField campo = new JTextField("Texto selecionável", 30);
        JTextField inativo = new JTextField("Campo indisponível", 30);
        inativo.setEnabled(false);
        JComboBox<String> combo = new JComboBox<>(new String[]{"Primeiro", "Segundo"});
        JList<String> lista = new JList<>(new String[]{"Primeiro", "Segundo"});
        lista.setSelectedIndex(1);
        JCheckBox caixa = new JCheckBox("Opção marcada", true);
        JRadioButton radio = new JRadioButton("Opção exclusiva", true);
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Dados", new JPanel()); abas.addTab("Outros", new JPanel()); abas.setSelectedIndex(1);
        JTable tabela = new JTable(new DefaultTableModel(new Object[][]{{"DEV SENIOR", "Não atribuído"}, {"JUNIOR", "Cleber"}},
                new String[]{"Processo", "Recrutador"}));
        JScrollPane rolagem = new JScrollPane(tabela);
        PainelListagem.configurarTabela(tabela, rolagem, true);
        tabela.setRowSelectionInterval(0, 0);
        JLabel obrigatorio = new JLabel("*");
        obrigatorio.setForeground(Color.RED);
        BotaoAjuda ajuda = new BotaoAjuda();
        BotaoAcessibilidade acessibilidade = new BotaoAcessibilidade();
        JOptionPane alerta = new JOptionPane("Erro: dados inválidos", JOptionPane.ERROR_MESSAGE);
        for (JComponent componente : new JComponent[]{editar, excluir, desabilitado, campo, inativo, combo,
                lista, caixa, radio, abas, obrigatorio, ajuda, acessibilidade, rolagem, alerta}) painel.add(componente);
        janela.setContentPane(painel);
        JMenuBar barra = new JMenuBar(); JMenu menu = new JMenu("Gerenciamento"); menu.add(new JMenuItem("Cadastrar")); barra.add(menu);
        janela.setJMenuBar(barra);
        janela.pack();
        Acessibilidade.configurarTela(janela);
        Map<JComponent, Object[]> originais = new IdentityHashMap<>();
        guardar(painel, originais); guardar(barra, originais);
        Color selecaoOriginal = tabela.getSelectionBackground();
        Object modelo = tabela.getModel();
        Object renderizadorCombo = combo.getRenderer();
        JFrame outra = janela(); outra.setContentPane(new JPanel()); outra.pack(); Acessibilidade.configurarTela(outra);
        int eventos = editar.getActionListeners().length;
        File imagens = new File("DesktopApp/build/daltonismo/imagens"); imagens.mkdirs();
        for (boolean baixaVisao : new boolean[]{false, true}) {
            Acessibilidade.setBaixaVisaoAtiva(baixaVisao);
            for (ModoDaltonismo origem : ModoDaltonismo.values()) {
                for (ModoDaltonismo destino : ModoDaltonismo.values()) {
                    Acessibilidade.setModoDaltonismo(origem);
                    Acessibilidade.setModoDaltonismo(destino);
                    exigir(tabela.getModel() == modelo && tabela.getSelectedRow() == 0, "Troca de modo alterou modelo/seleção");
                    exigir(lista.getSelectedIndex() == 1 && combo.getSelectedIndex() == 0 && abas.getSelectedIndex() == 1,
                            "Troca de modo alterou valores dos controles");
                    exigir(caixa.isSelected() && radio.isSelected() && !inativo.isEnabled() && !desabilitado.isEnabled(),
                            "Troca de modo alterou estados funcionais");
                    exigir(editar.getActionListeners().length == eventos && Acessibilidade.isTecladoAtivo(), "Eventos ou navegação alterados");
                    if (destino == ModoDaltonismo.PADRAO) continue;
                    PaletaAcessibilidade p = Acessibilidade.getPaleta();
                    verificarContraste(p);
                    exigir(editar.getBackground().equals(p.getPrimaria()), "Botão não acompanhou a paleta");
                    exigir(excluir.getBackground().equals(p.getExclusao()), "Exclusão não acompanhou a paleta");
                    exigir(((JComponent) outra.getContentPane()).getBackground().equals(baixaVisao ? p.getSuperficie() : p.getFundo()), "Segunda janela não atualizada: " + destino + "/" + baixaVisao);
                    exigir(tabela.getSelectionBackground().equals(p.getSelecao()), "Seleção sem paleta");
                    Component celula = tabela.prepareRenderer(tabela.getCellRenderer(0, 0), 0, 0);
                    exigir(((JLabel) celula).getIcon() instanceof IconeSelecao, "Seleção depende somente de cor");
                    exigir(contraste(celula.getForeground(), celula.getBackground()) >= 4.5, "Célula selecionada sem contraste");
                    Component cabecalho = tabela.getColumnModel().getColumn(0).getHeaderRenderer()
                            .getTableCellRendererComponent(tabela, "Processo", false, false, -1, 0);
                    exigir(cabecalho.getBackground().equals(p.getPrimaria()), "Cabeçalho não acompanha o modo");
                    exigir(contraste(inativo.getDisabledTextColor(), inativo.getBackground()) >= 4.5, "Campo desabilitado sem contraste");
                    Component item = lista.getCellRenderer().getListCellRendererComponent(lista, "Segundo", 1, true, false);
                    exigir(contraste(item.getForeground(), item.getBackground()) >= 4.5, "Item selecionado sem contraste");
                    Component opcao = combo.getRenderer().getListCellRendererComponent(new JList<>(), "Primeiro", 0, true, false);
                    exigir(contraste(opcao.getForeground(), opcao.getBackground()) >= 4.5, "Combo selecionado sem contraste");
                    desabilitado.setEnabled(true);
                    exigir(desabilitado.getBackground().equals(p.getPrimaria()), "Reabilitar botão não restaurou a ação primária");
                    desabilitado.setEnabled(false);
                    editar.getModel().setRollover(true);
                    exigir(contraste(editar.getForeground(), editar.getBackground()) >= 4.5, "Hover sem contraste");
                    editar.getModel().setRollover(false);
                    salvar(acessibilidade, new File(imagens, destino.name() + "-botao.png"));
                }
            }
        }
        Acessibilidade.setModoDaltonismo(ModoDaltonismo.PADRAO);
        Acessibilidade.setBaixaVisaoAtiva(false);
        for (Map.Entry<JComponent, Object[]> entrada : originais.entrySet()) {
            JComponent componente = entrada.getKey(); Object[] original = entrada.getValue();
            exigir(Objects.equals(componente.getBackground(), original[0]) && Objects.equals(componente.getForeground(), original[1])
                    && bordasEquivalentes(componente, (javax.swing.border.Border) original[2]), "Padrão não restaurado: " + componente.getClass().getSimpleName()
                            + " fg=" + componente.getForeground() + "/" + original[1] + " bg=" + componente.getBackground() + "/" + original[0]
                            + " border=" + componente.getBorder() + "/" + original[2]);
        }
        exigir(tabela.getSelectionBackground().equals(selecaoOriginal) && combo.getRenderer() == renderizadorCombo,
                "Seleção/renderizador original não restaurado");
        for (ModoDaltonismo modo : new ModoDaltonismo[]{ModoDaltonismo.PROTANOPIA, ModoDaltonismo.DEUTERANOPIA, ModoDaltonismo.TRITANOPIA}) {
            Acessibilidade.setModoDaltonismo(modo);
            TelaAcessibilidade opcoes = new TelaAcessibilidade(janela);
            atualizar(opcoes.getRootPane());
            organizar(opcoes);
            salvar(opcoes.getContentPane(), new File(imagens, modo.name() + "-opcoes.png"));
            BotaoAjuda botao = new BotaoAjuda();
            Method criarAjuda = BotaoAjuda.class.getDeclaredMethod("criarJanelaAjuda");
            criarAjuda.setAccessible(true);
            JDialog dialogo = (JDialog) criarAjuda.invoke(botao);
            atualizar(dialogo.getRootPane()); organizar(dialogo);
            salvar(dialogo.getContentPane(), new File(imagens, modo.name() + "-ajuda.png"));
            exigir(((JComponent) opcoes.getContentPane()).getBackground().equals(Acessibilidade.getPaleta().getSuperficie()),
                    "Nova janela não recebeu a paleta");
            dialogo.dispose(); opcoes.dispose();
        }
        janela.dispose(); outra.dispose();
    }

    private static JFrame janela() {
        return new JFrame() {
            @Override protected JRootPane createRootPane() {
                return new JRootPane() { @Override public boolean isShowing() { return true; } };
            }
        };
    }
    private static void verificarContraste(PaletaAcessibilidade p) {
        for (Color fundo : new Color[]{p.getPrimaria(), p.getSucesso(), p.getErro(), p.getInformacao(), p.getHover()})
            exigir(contraste(Color.WHITE, fundo) >= 4.5, "Texto de estado sem contraste");
        exigir(contraste(p.getTextoAviso(), p.getAviso()) >= 4.5, "Aviso sem contraste");
        exigir(contraste(p.getTextoExclusao(), p.getExclusao()) >= 4.5, "Exclusão sem contraste");
        exigir(contraste(p.getTextoBadge(), p.getBadge()) >= 4.5, "Badge sem contraste");
        exigir(contraste(p.getTextoSecundario(), p.getFundo()) >= 4.5, "Placeholder sem contraste");
        exigir(contraste(p.getBorda(), p.getSuperficie()) >= 3, "Borda sem contraste");
    }
    private static double contraste(Color a, Color b) {
        double la = luminancia(a), lb = luminancia(b); return (Math.max(la, lb) + .05) / (Math.min(la, lb) + .05);
    }
    private static double luminancia(Color cor) {
        double[] c = {cor.getRed() / 255.0, cor.getGreen() / 255.0, cor.getBlue() / 255.0};
        for (int i = 0; i < c.length; i++) c[i] = c[i] <= .04045 ? c[i] / 12.92 : Math.pow((c[i] + .055) / 1.055, 2.4);
        return .2126 * c[0] + .7152 * c[1] + .0722 * c[2];
    }
    private static void guardar(Component componente, Map<JComponent, Object[]> originais) {
        if (componente instanceof JComponent) {
            JComponent c = (JComponent) componente;
            originais.put(c, new Object[]{c.getBackground(), c.getForeground(), c.getBorder()});
        }
        if (componente instanceof JPanel || componente instanceof JMenuBar || componente instanceof JPopupMenu
                || componente instanceof JOptionPane || componente instanceof JScrollPane || componente instanceof JViewport
                || componente instanceof JTabbedPane)
            for (Component filho : ((Container) componente).getComponents()) guardar(filho, originais);
        if (componente instanceof JMenu) guardar(((JMenu) componente).getPopupMenu(), originais);
    }
    private static boolean bordasEquivalentes(JComponent componente, javax.swing.border.Border original) {
        javax.swing.border.Border atual = componente.getBorder();
        if (Objects.equals(atual, original)) return true;
        return atual instanceof javax.swing.plaf.UIResource && original instanceof javax.swing.plaf.UIResource
                && atual.getClass() == original.getClass()
                && atual.getBorderInsets(componente).equals(original.getBorderInsets(componente));
    }
    private static void atualizar(JRootPane raiz) throws Exception {
        Method atualizar = Daltonismo.class.getDeclaredMethod("atualizarTela", JRootPane.class);
        atualizar.setAccessible(true); atualizar.invoke(null, raiz);
    }
    private static void organizar(Container painel) {
        painel.doLayout(); for (Component filho : painel.getComponents()) if (filho instanceof Container) organizar((Container) filho);
    }
    private static void salvar(Component painel, File arquivo) throws Exception {
        int largura = Math.max(1, painel.getWidth()), altura = Math.max(1, painel.getHeight());
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics(); painel.printAll(g); g.dispose(); ImageIO.write(imagem, "png", arquivo);
    }
    private static void exigir(boolean condicao, String mensagem) { if (!condicao) throw new AssertionError(mensagem); }
}
