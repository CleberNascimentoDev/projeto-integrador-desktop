package classes;

import java.awt.*;
import java.awt.event.*;
import java.lang.ref.WeakReference;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

/** Adapta as telas em execução sem alterar os formulários do NetBeans. */
public final class BaixaVisao {
    private static final double ampliacao = 1.5;
    private static final Color fundo = Color.WHITE;
    private static final Color texto = Color.BLACK;
    private static final Color destaque = new Color(17, 48, 82);
    private static final String chaveEstado = "baixaVisao.estado";
    private static final List<WeakReference<JRootPane>> telas = new ArrayList<>();
    private static boolean observandoJanelas;

    private BaixaVisao() { }

    public static void configurarTela(RootPaneContainer tela) {
        JRootPane raiz = tela.getRootPane();
        if (raiz.getClientProperty(chaveEstado) != null) return;
        raiz.putClientProperty(chaveEstado, new EstadoTela(tela));
        telas.add(new WeakReference<>(raiz));
        raiz.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && raiz.isShowing()) {
                SwingUtilities.invokeLater(() -> atualizarTela(raiz));
            }
        });
        // Inclui as mensagens do JOptionPane e as janelas de ajuda.
        if (!observandoJanelas) {
            observandoJanelas = true;
            Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
                if (e instanceof WindowEvent && e.getID() == WindowEvent.WINDOW_OPENED
                        && e.getSource() instanceof RootPaneContainer) {
                    RootPaneContainer janela = (RootPaneContainer) e.getSource();
                    configurarTela(janela);
                    atualizarTela(janela.getRootPane());
                }
            }, AWTEvent.WINDOW_EVENT_MASK);
        }
    }

    public static void atualizarTelas() {
        telas.removeIf(referencia -> referencia.get() == null);
        for (WeakReference<JRootPane> referencia : new ArrayList<>(telas)) {
            JRootPane raiz = referencia.get();
            if (raiz != null && raiz.isShowing()) atualizarTela(raiz);
        }
    }

    private static void atualizarTela(JRootPane raiz) {
        atualizarTela(raiz, Acessibilidade.isBaixaVisaoAtiva());
    }

    public static void restaurarTelas() {
        for (WeakReference<JRootPane> referencia : new ArrayList<>(telas)) {
            JRootPane raiz = referencia.get();
            if (raiz != null) atualizarTela(raiz, false);
        }
    }

    private static void atualizarTela(JRootPane raiz, boolean ativo) {
        EstadoTela estado = (EstadoTela) raiz.getClientProperty(chaveEstado);
        if (estado == null || estado.ativo == ativo) return;
        estado.tela.getContentPane().validate();
        if (ativo) {
            estado.conteudo = estado.tela.getContentPane();
            estado.tamanho = ((Component) estado.tela).getSize();
            estado.preferencia = estado.conteudo.isPreferredSizeSet()
                    ? estado.conteudo.getPreferredSize() : null;
            Dimension tamanhoConteudo = estado.conteudo.getSize();
            guardarComponentes(estado.conteudo);
            if (raiz.getJMenuBar() != null) guardarComponentes(raiz.getJMenuBar());
            aplicarComponentes(estado.conteudo, true);
            if (raiz.getJMenuBar() != null) aplicarComponentes(raiz.getJMenuBar(), true);
            // O menu mantém seu desktop flexível; cada janela interna possui sua própria rolagem.
            if (!possuiDesktop(estado.conteudo)) {
                Dimension necessario = estado.conteudo.getPreferredSize();
                Dimension minimo = ampliar(tamanhoConteudo);
                estado.conteudo.setPreferredSize(new Dimension(Math.max(necessario.width, minimo.width),
                        Math.max(necessario.height, minimo.height)));
                estado.rolagem = new JScrollPane(estado.conteudo);
                estado.rolagem.getVerticalScrollBar().setUnitIncrement(24);
                estado.rolagem.getHorizontalScrollBar().setUnitIncrement(24);
                estado.tela.setContentPane(estado.rolagem);
                ajustarJanela((Component) estado.tela, ampliar(estado.tamanho));
            }
        } else {
            if (estado.rolagem != null) {
                estado.rolagem.setViewportView(null);
                estado.tela.setContentPane(estado.conteudo);
                estado.conteudo.setPreferredSize(estado.preferencia);
                ((Component) estado.tela).setSize(estado.tamanho);
                estado.rolagem = null;
            }
            aplicarComponentes(estado.conteudo, false);
            if (raiz.getJMenuBar() != null) aplicarComponentes(raiz.getJMenuBar(), false);
        }
        estado.ativo = ativo;
        raiz.revalidate();
        raiz.repaint();
    }

    private static boolean possuiDesktop(Container painel) {
        for (Component componente : painel.getComponents()) {
            if (componente instanceof JDesktopPane) return true;
            if (componente instanceof Container && possuiDesktop((Container) componente)) return true;
        }
        return false;
    }

    private static void ajustarJanela(Component janela, Dimension tamanho) {
        Dimension limite;
        if (janela instanceof JInternalFrame && janela.getParent() != null) {
            limite = janela.getParent().getSize();
        } else {
            GraphicsConfiguration configuracao = janela.getGraphicsConfiguration();
            if (configuracao == null) configuracao = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration();
            Rectangle area = configuracao.getBounds();
            Insets margens = Toolkit.getDefaultToolkit().getScreenInsets(configuracao);
            limite = new Dimension(area.width - margens.left - margens.right,
                    area.height - margens.top - margens.bottom);
        }
        janela.setSize(Math.min(tamanho.width, limite.width), Math.min(tamanho.height, limite.height));
        if (janela instanceof Window) ((Window) janela).setLocationRelativeTo(((Window) janela).getOwner());
    }

    private static Dimension ampliar(Dimension tamanho) {
        return new Dimension((int) Math.ceil(tamanho.width * ampliacao),
                (int) Math.ceil(tamanho.height * ampliacao));
    }

    private static void guardarComponentes(Component componente) {
        if (componente instanceof JInternalFrame
                || !(componente instanceof JComponent)) return;
        JComponent campo = (JComponent) componente;
        if (campo.getClientProperty(chaveEstado) == null) {
            campo.putClientProperty(chaveEstado, new EstadoComponente(campo));
        }
        if (campo instanceof JTable) guardarComponentes(((JTable) campo).getTableHeader());
        if (campo instanceof JMenu) guardarComponentes(((JMenu) campo).getPopupMenu());
        if (campo instanceof JPanel || campo instanceof JScrollPane || campo instanceof JViewport
                || campo instanceof JMenuBar || campo instanceof JPopupMenu || campo instanceof JOptionPane
                || campo instanceof JDesktopPane) {
            for (Component filho : campo.getComponents()) guardarComponentes(filho);
        }
    }

    private static void aplicarComponentes(Component componente, boolean ativo) {
        // Não percorre o interior das janelas internas nem os filhos criados pelo Look and Feel.
        if (componente instanceof JInternalFrame) return;
        if (!(componente instanceof JComponent)) return;
        JComponent campo = (JComponent) componente;
        EstadoComponente estado = (EstadoComponente) campo.getClientProperty(chaveEstado);
        if (ativo && estado == null) {
            estado = new EstadoComponente(campo);
            campo.putClientProperty(chaveEstado, estado);
        }
        if (estado == null) return;
        estado.ativo = ativo;
        if (!ativo && campo instanceof JDesktopPane) {
            for (JScrollPane rolagem : estado.paineisRolagem) {
                Component painel = rolagem.getViewport().getView();
                rolagem.setViewportView(null);
                campo.remove(rolagem);
                campo.add(painel, BorderLayout.CENTER);
            }
            estado.paineisRolagem.clear();
        }
        if (ativo) {
            if (estado.fonte != null) campo.setFont(estado.fonte.deriveFont(
                    Math.max(18f, (float) (estado.fonte.getSize2D() * ampliacao))));
            aplicarCores(campo);
            if (campo instanceof AbstractButton) {
                ((AbstractButton) campo).setFocusPainted(true);
                ((AbstractButton) campo).setBorderPainted(true);
            }
            if (estado.preferencia != null) campo.setPreferredSize(ampliar(estado.preferencia));
            if (campo instanceof JTable) {
                JTable tabela = (JTable) campo;
                tabela.setRowHeight((int) Math.ceil(estado.alturaLinha * ampliacao));
                aplicarComponentes(tabela.getTableHeader(), true);
                for (EstadoColuna coluna : estado.colunas) coluna.aplicar(tabela, true);
            }
            if (estado.html != null) {
                ((JLabel) campo).setText(estado.html
                        .replaceAll("font-size:\\s*[0-9.]+pt", "font-size: " + campo.getFont().getSize() + "pt")
                        .replaceAll("color:\\s*#[0-9a-fA-F]+", "color: #000000"));
            }
        } else {
            campo.setFont(estado.fonte);
            campo.setForeground(estado.frente);
            campo.setBackground(estado.fundo);
            campo.setBorder(estado.borda);
            campo.setPreferredSize(estado.preferencia);
            if (campo instanceof AbstractButton) {
                ((AbstractButton) campo).setFocusPainted(estado.focoPintado);
                ((AbstractButton) campo).setBorderPainted(estado.bordaPintada);
            }
            if (campo instanceof JTable) {
                JTable tabela = (JTable) campo;
                tabela.setRowHeight(estado.alturaLinha);
                tabela.setSelectionBackground(estado.selecaoFundo);
                tabela.setSelectionForeground(estado.selecaoTexto);
                aplicarComponentes(tabela.getTableHeader(), false);
                for (EstadoColuna coluna : estado.colunas) coluna.aplicar(tabela, false);
            }
            if (campo instanceof JTextComponent) {
                JTextComponent entrada = (JTextComponent) campo;
                entrada.setCaretColor(estado.cursor);
                entrada.setSelectionColor(estado.selecaoFundo);
                entrada.setSelectedTextColor(estado.selecaoTexto);
                entrada.setDisabledTextColor(estado.textoDesativado);
            }
            if (estado.html != null) ((JLabel) campo).setText(estado.html);
        }
        if (campo instanceof JPanel) {
            JPanel painel = (JPanel) campo;
            if (ativo && estado.layoutFixo) {
                painel.setLayout(null);
                painel.setPreferredSize(ampliar(estado.tamanho));
            } else if (!ativo && estado.layoutFixo) {
                painel.setLayout(estado.layout);
            }
        }
        if (campo instanceof JPanel || campo instanceof JScrollPane || campo instanceof JViewport
                || campo instanceof JMenuBar || campo instanceof JPopupMenu || campo instanceof JOptionPane
                || campo instanceof JDesktopPane) {
            for (Component filho : campo.getComponents()) {
                aplicarComponentes(filho, ativo);
                if (estado.layoutFixo && filho instanceof JComponent) {
                    EstadoComponente original = (EstadoComponente) ((JComponent) filho).getClientProperty(chaveEstado);
                    if (original != null) {
                        Rectangle limites = original.limites;
                        Rectangle ampliados = ativo ? new Rectangle((int) (limites.x * ampliacao),
                                (int) (limites.y * ampliacao), (int) Math.ceil(limites.width * ampliacao),
                                (int) Math.ceil(limites.height * ampliacao)) : limites;
                        if (ativo && filho instanceof JLabel) {
                            Dimension necessario = filho.getPreferredSize();
                            ampliados.width = Math.max(ampliados.width, necessario.width);
                            ampliados.height = Math.max(ampliados.height, necessario.height);
                        }
                        filho.setBounds(ampliados);
                    }
                }
            }
            if (ativo && estado.layoutFixo) {
                Dimension necessario = ampliar(estado.tamanho);
                for (Component filho : campo.getComponents()) {
                    necessario.width = Math.max(necessario.width, filho.getX() + filho.getWidth() + 12);
                    necessario.height = Math.max(necessario.height, filho.getY() + filho.getHeight() + 12);
                }
                campo.setPreferredSize(necessario);
            }
        }
        if (ativo && campo instanceof JDesktopPane && campo.getLayout() instanceof BorderLayout) {
            for (Component filho : campo.getComponents()) {
                if (filho instanceof JPanel) {
                    JScrollPane rolagem = new JScrollPane(filho);
                    rolagem.getVerticalScrollBar().setUnitIncrement(24);
                    rolagem.getHorizontalScrollBar().setUnitIncrement(24);
                    campo.add(rolagem, BorderLayout.CENTER);
                    estado.paineisRolagem.add(rolagem);
                }
            }
        }
        if (campo instanceof JMenu) aplicarComponentes(((JMenu) campo).getPopupMenu(), ativo);
        if (!ativo) {
            campo.removePropertyChangeListener(estado.preferenciaCores);
            campo.removeFocusListener(estado.controladorDeFoco);
            campo.putClientProperty(chaveEstado, null);
        }
    }

    private static void aplicarCores(JComponent campo) {
        boolean botao = campo instanceof AbstractButton || campo instanceof JTableHeader;
        campo.setBackground(botao ? destaque : fundo);
        campo.setForeground(botao ? fundo : texto);
        if (campo instanceof JTable) {
            ((JTable) campo).setSelectionBackground(destaque);
            ((JTable) campo).setSelectionForeground(fundo);
        }
        if (campo instanceof JTextComponent) {
            JTextComponent entrada = (JTextComponent) campo;
            entrada.setCaretColor(texto);
            entrada.setSelectionColor(destaque);
            entrada.setSelectedTextColor(fundo);
            entrada.setDisabledTextColor(new Color(64, 64, 64));
        }
        // Botões transparentes, como a ajuda, são desenhados sobre o painel branco.
        if (campo instanceof AbstractButton && (!((AbstractButton) campo).isContentAreaFilled()
                || campo instanceof JCheckBox || campo instanceof JRadioButton)) {
            campo.setForeground(texto);
        }
        if (Acessibilidade.isDaltonismoAtivo()) {
            Color corFundo = Daltonismo.adaptarFundo(campo, campo.getBackground());
            Color corTexto = Daltonismo.adaptarTexto(campo, campo.getForeground(), campo.getBackground());
            campo.setBackground(corFundo);
            campo.setForeground(corTexto);
        }
    }

    private static final class EstadoTela {
        final RootPaneContainer tela;
        Container conteudo;
        Dimension tamanho;
        Dimension preferencia;
        JScrollPane rolagem;
        boolean ativo;

        EstadoTela(RootPaneContainer tela) { this.tela = tela; }
    }

    private static final class EstadoComponente {
        final Font fonte;
        final Color frente, fundo, cursor, selecaoFundo, selecaoTexto, textoDesativado;
        final Border borda;
        final Dimension preferencia, tamanho;
        final Rectangle limites;
        final LayoutManager layout;
        final boolean layoutFixo, focoPintado, bordaPintada;
        final int alturaLinha;
        final String html;
        final List<EstadoColuna> colunas = new ArrayList<>();
        final List<JScrollPane> paineisRolagem = new ArrayList<>();
        final PropertyChangeListener preferenciaCores;
        final FocusAdapter controladorDeFoco;
        boolean ativo;
        boolean ajustandoCores;

        EstadoComponente(JComponent campo) {
            fonte = campo.getFont();
            frente = campo.getForeground();
            fundo = campo.getBackground();
            borda = campo.getBorder();
            preferencia = campo.isPreferredSizeSet() ? campo.getPreferredSize() : null;
            tamanho = campo.getSize();
            limites = campo.getBounds();
            layout = campo.getLayout();
            layoutFixo = campo instanceof JPanel && (layout instanceof GroupLayout
                    || (layout != null && layout.getClass().getName().equals("org.netbeans.lib.awtextra.AbsoluteLayout")))
                    && !possuiDesktop(campo);
            focoPintado = campo instanceof AbstractButton && ((AbstractButton) campo).isFocusPainted();
            bordaPintada = campo instanceof AbstractButton && ((AbstractButton) campo).isBorderPainted();
            alturaLinha = campo instanceof JTable ? ((JTable) campo).getRowHeight() : 0;
            JTextComponent entrada = campo instanceof JTextComponent ? (JTextComponent) campo : null;
            cursor = entrada != null ? entrada.getCaretColor() : null;
            selecaoFundo = entrada != null ? entrada.getSelectionColor()
                    : campo instanceof JTable ? ((JTable) campo).getSelectionBackground() : null;
            selecaoTexto = entrada != null ? entrada.getSelectedTextColor()
                    : campo instanceof JTable ? ((JTable) campo).getSelectionForeground() : null;
            textoDesativado = entrada != null ? entrada.getDisabledTextColor() : null;
            String mensagem = campo instanceof JLabel ? ((JLabel) campo).getText() : null;
            html = mensagem != null && mensagem.startsWith("<html>") ? mensagem : null;
            if (campo instanceof JTable) {
                JTable tabela = (JTable) campo;
                for (int i = 0; i < tabela.getColumnModel().getColumnCount(); i++) {
                    colunas.add(new EstadoColuna(tabela, i));
                }
            }
            // Placeholders e efeitos de mouse não devem reduzir o contraste no modo ampliado.
            preferenciaCores = e -> {
                EstadoComponente atual = (EstadoComponente) campo.getClientProperty(chaveEstado);
                if (atual == this && ativo && !ajustandoCores
                        && ("foreground".equals(e.getPropertyName()) || "background".equals(e.getPropertyName()))) {
                    ajustandoCores = true;
                    aplicarCores(campo);
                    ajustandoCores = false;
                }
            };
            campo.addPropertyChangeListener(preferenciaCores);
            controladorDeFoco = new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (ativo) {
                        campo.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(destaque, 3), borda));
                        Rectangle area = new Rectangle(0, 0, campo.getWidth(), campo.getHeight());
                        if (campo instanceof JTable && ((JTable) campo).getRowCount() > 0) {
                            JTable tabela = (JTable) campo;
                            area = tabela.getCellRect(Math.max(0, tabela.getSelectedRow()), 0, true);
                        }
                        campo.scrollRectToVisible(area);
                    }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (ativo) campo.setBorder(borda);
                }
            };
            campo.addFocusListener(controladorDeFoco);
        }
    }

    private static final class EstadoColuna {
        final TableColumn coluna;
        final int largura, preferencia;
        final TableCellRenderer renderizador;
        final Font fonte;

        EstadoColuna(JTable tabela, int indice) {
            coluna = tabela.getColumnModel().getColumn(indice);
            largura = coluna.getWidth();
            preferencia = coluna.getPreferredWidth();
            renderizador = coluna.getHeaderRenderer();
            TableCellRenderer original = renderizador != null ? renderizador
                    : tabela.getTableHeader().getDefaultRenderer();
            fonte = original.getTableCellRendererComponent(tabela, coluna.getHeaderValue(),
                    false, false, -1, indice).getFont();
        }

        void aplicar(JTable tabela, boolean ativo) {
            coluna.setPreferredWidth(ativo ? (int) (preferencia * ampliacao) : preferencia);
            coluna.setWidth(ativo ? (int) (largura * ampliacao) : largura);
            if (!ativo) {
                coluna.setHeaderRenderer(renderizador);
                return;
            }
            TableCellRenderer original = renderizador != null ? renderizador
                    : tabela.getTableHeader().getDefaultRenderer();
            coluna.setHeaderRenderer((t, valor, selecionado, foco, linha, indice) -> {
                Component cabecalho = original.getTableCellRendererComponent(t, valor, selecionado, foco, linha, indice);
                cabecalho.setFont(fonte.deriveFont(Math.max(18f, (float) (fonte.getSize2D() * ampliacao))));
                cabecalho.setBackground(destaque);
                cabecalho.setForeground(fundo);
                return cabecalho;
            });
        }
    }
}
