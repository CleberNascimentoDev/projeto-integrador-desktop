package classes;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import classes.PaletaAcessibilidade.Papel;

/** Adapta componentes reais, preservando modelos, eventos, fontes e layouts. */
public final class Daltonismo {
    private static final String chaveEstado = "daltonismo.estado";
    private static final java.util.List<WeakReference<JRootPane>> telas = new ArrayList<>();
    private static final Map<String, Object> iconesOriginais = new HashMap<>();
    private static boolean observandoJanelas;

    private Daltonismo() { }

    public static void marcar(JComponent campo, Papel papel) { campo.putClientProperty("acessibilidade.papel", papel); }
    public static Color cor(Color original, Papel papel) {
        return Acessibilidade.isDaltonismoAtivo() ? Acessibilidade.getPaleta().getCor(papel) : original;
    }
    public static Color corBorda(Color original) {
        return Acessibilidade.isDaltonismoAtivo() ? Acessibilidade.getPaleta().getBorda() : original;
    }
    public static Color corSelecao(Color original) {
        return Acessibilidade.isDaltonismoAtivo() ? Acessibilidade.getPaleta().getSelecao() : original;
    }

    public static void configurarTela(RootPaneContainer tela) {
        atualizarPadroesSwing();
        JRootPane raiz = tela.getRootPane();
        for (WeakReference<JRootPane> referencia : telas) if (referencia.get() == raiz) return;
        telas.add(new WeakReference<>(raiz));
        raiz.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && raiz.isShowing())
                SwingUtilities.invokeLater(() -> atualizarTela(raiz));
        });
        SwingUtilities.invokeLater(() -> { if (raiz.isShowing()) atualizarTela(raiz); });
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

    /** Apenas ícones de mensagens: não troca o Look and Feel nem recria os controles. */
    public static void atualizarPadroesSwing() {
        for (String tipo : new String[]{"error", "warning", "information", "question"}) {
            String chave = "OptionPane." + tipo + "Icon";
            if (Acessibilidade.isDaltonismoAtivo()) {
                if (!iconesOriginais.containsKey(chave)) iconesOriginais.put(chave, UIManager.get(chave));
                UIManager.put(chave, new IconeMensagem(tipo, (Icon) iconesOriginais.get(chave)));
            } else if (iconesOriginais.containsKey(chave)) {
                UIManager.put(chave, iconesOriginais.remove(chave));
            }
        }
    }

    public static void atualizarTelas() {
        // Inclui janelas de terceiros abertas via JOptionPane, além das telas registradas.
        for (Window janela : Window.getWindows())
            if (janela.isShowing() && janela instanceof RootPaneContainer) configurarTela((RootPaneContainer) janela);
        telas.removeIf(referencia -> referencia.get() == null);
        for (WeakReference<JRootPane> referencia : new ArrayList<>(telas)) {
            JRootPane raiz = referencia.get();
            if (raiz != null && raiz.isShowing()) atualizarTela(raiz);
        }
    }
    private static void atualizarTela(JRootPane raiz) {
        aplicarComponentes(raiz.getContentPane());
        if (raiz.getJMenuBar() != null) aplicarComponentes(raiz.getJMenuBar());
        raiz.revalidate();
        raiz.repaint();
    }
    public static void restaurarTelas() {
        for (WeakReference<JRootPane> referencia : new ArrayList<>(telas)) {
            JRootPane raiz = referencia.get();
            if (raiz != null) {
                aplicarComponentes(raiz.getContentPane(), false);
                if (raiz.getJMenuBar() != null) aplicarComponentes(raiz.getJMenuBar(), false);
            }
        }
    }
    private static void aplicarComponentes(Component componente) {
        if (Acessibilidade.isDaltonismoAtivo()) guardarComponentes(componente);
        aplicarComponentes(componente, Acessibilidade.isDaltonismoAtivo());
    }
    private static void guardarComponentes(Component componente) {
        if (!(componente instanceof JComponent) || componente instanceof JInternalFrame) return;
        JComponent campo = (JComponent) componente;
        if (campo.getClientProperty(chaveEstado) == null)
            campo.putClientProperty(chaveEstado, new EstadoComponente(campo));
        if (campo instanceof JTable) guardarComponentes(((JTable) campo).getTableHeader());
        if (campo instanceof JPanel || campo instanceof JDesktopPane || campo instanceof JScrollPane
                || campo instanceof JViewport || campo instanceof JMenuBar || campo instanceof JPopupMenu
                || campo instanceof JOptionPane || campo instanceof JTabbedPane)
            for (Component filho : campo.getComponents()) guardarComponentes(filho);
        if (campo instanceof JMenu) guardarComponentes(((JMenu) campo).getPopupMenu());
    }
    private static void aplicarComponentes(Component componente, boolean ativo) {
        if (!(componente instanceof JComponent) || componente instanceof JInternalFrame) return;
        JComponent campo = (JComponent) componente;
        EstadoComponente estado = (EstadoComponente) campo.getClientProperty(chaveEstado);
        if (ativo) {
            if (estado == null) {
                estado = new EstadoComponente(campo);
                campo.putClientProperty(chaveEstado, estado);
            }
            estado.aplicar(campo);
        } else if (estado != null) {
            estado.restaurar(campo);
            campo.putClientProperty(chaveEstado, null);
        }
        if (campo instanceof JTable) aplicarComponentes(((JTable) campo).getTableHeader(), ativo);
        if (campo instanceof JPanel || campo instanceof JDesktopPane || campo instanceof JScrollPane
                || campo instanceof JViewport || campo instanceof JMenuBar || campo instanceof JPopupMenu
                || campo instanceof JOptionPane || campo instanceof JTabbedPane) {
            for (Component filho : campo.getComponents()) aplicarComponentes(filho, ativo);
        }
        if (campo instanceof JMenu) aplicarComponentes(((JMenu) campo).getPopupMenu(), ativo);
    }

    private static boolean vermelho(Color cor) {
        return cor != null && cor.getRed() > cor.getGreen() * 1.4 && cor.getRed() > cor.getBlue() * 1.4;
    }
    private static boolean verde(Color cor) {
        return cor != null && cor.getGreen() > cor.getRed() * 1.3 && cor.getGreen() > cor.getBlue() * 1.1;
    }
    private static boolean amarelo(Color cor) {
        return cor != null && cor.getRed() > 180 && cor.getGreen() > 120 && cor.getBlue() < 80;
    }
    private static Papel papel(JComponent campo, Color fundo) {
        Object papel = campo.getClientProperty("acessibilidade.papel");
        if (papel instanceof Papel) return (Papel) papel;
        if (campo instanceof AbstractButton) {
            String nome = ((AbstractButton) campo).getText();
            if ((nome != null && nome.startsWith("Excluir")) || vermelho(fundo)) return Papel.EXCLUSAO;
            if (amarelo(fundo)) return Papel.AVISO;
        }
        return Papel.PRIMARIO;
    }
    static Color adaptarFundo(JComponent campo, Color fundo) {
        PaletaAcessibilidade paleta = Acessibilidade.getPaleta();
        if (Boolean.TRUE.equals(campo.getClientProperty("acessibilidade.subtela"))) return paleta.getSubtela();
        if (!campo.isEnabled()) return paleta.getDesabilitado();
        if (campo instanceof JMenuItem || campo instanceof JPopupMenu || campo instanceof JMenuBar) return paleta.getSuperficie();
        if (campo instanceof JTableHeader) return paleta.getPrimaria();
        if (campo instanceof AbstractButton && ((AbstractButton) campo).isContentAreaFilled()
                && !(campo instanceof JCheckBox) && !(campo instanceof JRadioButton)) return paleta.getCor(papel(campo, fundo));
        if (campo instanceof JTextComponent || campo instanceof JTable || campo instanceof JList
                || campo instanceof JComboBox || campo instanceof JViewport || campo instanceof JOptionPane) return paleta.getSuperficie();
        if (campo instanceof JPanel || campo instanceof JDesktopPane) {
            return Color.WHITE.equals(fundo) ? paleta.getSuperficie() : paleta.getFundo();
        }
        return fundo;
    }
    static Color adaptarTexto(JComponent campo, Color frente, Color fundo) {
        PaletaAcessibilidade paleta = Acessibilidade.getPaleta();
        if (!campo.isEnabled()) return paleta.getTextoDesabilitado();
        if (campo instanceof JTableHeader) return paleta.getTextoSelecao();
        if (campo instanceof JMenuItem) return paleta.getTexto();
        if (campo instanceof AbstractButton && ((AbstractButton) campo).isContentAreaFilled()
                && !(campo instanceof JCheckBox) && !(campo instanceof JRadioButton)) return paleta.getTexto(papel(campo, fundo));
        if (campo.getClientProperty("acessibilidade.papel") == Papel.AVISO) return paleta.getTextoAviso();
        if (campo instanceof JLabel && "*".equals(((JLabel) campo).getText())) return paleta.getErro();
        if (vermelho(frente)) return paleta.getErro();
        if (amarelo(frente)) return paleta.getTextoAviso();
        return verde(frente) ? paleta.getSucesso() : paleta.getTexto();
    }
    private static Border adaptarBorda(Border borda) {
        if (borda instanceof LineBorder) {
            LineBorder linha = (LineBorder) borda;
            return new LineBorder(corBorda(linha.getLineColor()), linha.getThickness(), linha.getRoundedCorners());
        }
        if (borda instanceof CompoundBorder) {
            CompoundBorder composta = (CompoundBorder) borda;
            return new CompoundBorder(adaptarBorda(composta.getOutsideBorder()), adaptarBorda(composta.getInsideBorder()));
        }
        return borda;
    }

    private static void configurarNimbus(JComponent campo, Object original) {
        if (!(UIManager.getLookAndFeel() instanceof javax.swing.plaf.nimbus.NimbusLookAndFeel)) return;
        PaletaAcessibilidade paleta = Acessibilidade.getPaleta();
        UIDefaults tema = new UIDefaults();
        if (original instanceof UIDefaults) tema.putAll((UIDefaults) original);
        String nome = campo.getClass().getSimpleName();
        String[] prefixos = campo instanceof JMenuItem ? new String[]{"Menu", "MenuBar:Menu", "MenuItem"}
                : campo instanceof AbstractButton ? new String[]{"Button", "ToggleButton", "CheckBox", "RadioButton"}
                : new String[]{nome.startsWith("J") ? nome.substring(1) : nome};
        for (String prefixo : prefixos) {
            tema.put(prefixo + "[Enabled].textForeground", new ColorUIResource(campo.getForeground()));
            tema.put(prefixo + "[Disabled].textForeground", new ColorUIResource(paleta.getTextoDesabilitado()));
            if (campo instanceof AbstractButton && !(campo instanceof JCheckBox) && !(campo instanceof JRadioButton)) {
                for (String estado : new String[]{"Enabled", "Focused", "MouseOver", "Focused+MouseOver", "Pressed", "Focused+Pressed", "Disabled", "Selected", "Enabled+Selected"}) {
                    boolean destaque = campo instanceof JMenuItem && !estado.equals("Enabled") && !estado.equals("Disabled");
                    Color fundo = estado.equals("Disabled") ? paleta.getDesabilitado()
                            : destaque ? paleta.getSelecao() : estado.contains("MouseOver") || estado.contains("Pressed")
                            ? campo.getBackground().darker() : campo.getBackground();
                    Color texto = estado.equals("Disabled") ? paleta.getTextoDesabilitado()
                            : destaque ? paleta.getTextoSelecao() : campo.getForeground();
                    tema.put(prefixo + "[" + estado + "].textForeground", new ColorUIResource(texto));
                    tema.put(prefixo + "[" + estado + "].backgroundPainter", (Painter<JComponent>) (g, c, w, h) -> {
                        g.setColor(fundo);
                        if (c instanceof JMenuItem) g.fillRect(0, 0, w, h); else g.fillRoundRect(0, 0, w, h, 8, 8);
                    });
                }
            }
        }
        campo.putClientProperty("Nimbus.Overrides", tema);
        campo.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
    }

    private static final class EstadoComponente {
        Color frente, fundo;
        Border borda;
        final Color selecao, textoSelecao, desabilitado, cursor;
        final Object tema, herdarTema;
        final Icon icone;
        final ListCellRenderer<?> renderizadorCombo;
        final Map<TableColumn, TableCellRenderer> cabecalhos = new IdentityHashMap<>();
        boolean ajustandoCores;
        final PropertyChangeListener preferenciaCores;

        EstadoComponente(JComponent campo) {
            frente = campo.getForeground(); fundo = campo.getBackground(); borda = campo.getBorder();
            tema = campo.getClientProperty("Nimbus.Overrides");
            icone = campo instanceof JLabel ? ((JLabel) campo).getIcon() : null;
            herdarTema = campo.getClientProperty("Nimbus.Overrides.InheritDefaults");
            JTextComponent texto = campo instanceof JTextComponent ? (JTextComponent) campo : null;
            selecao = texto != null ? texto.getSelectionColor() : campo instanceof JTable ? ((JTable) campo).getSelectionBackground()
                    : campo instanceof JList ? ((JList<?>) campo).getSelectionBackground() : null;
            textoSelecao = texto != null ? texto.getSelectedTextColor() : campo instanceof JTable ? ((JTable) campo).getSelectionForeground()
                    : campo instanceof JList ? ((JList<?>) campo).getSelectionForeground() : null;
            desabilitado = texto != null ? texto.getDisabledTextColor() : null;
            cursor = texto != null ? texto.getCaretColor() : null;
            renderizadorCombo = campo instanceof JComboBox ? ((JComboBox<?>) campo).getRenderer() : null;
            preferenciaCores = e -> {
                if (ajustandoCores) return;
                if ("foreground".equals(e.getPropertyName())) frente = (Color) e.getNewValue();
                else if ("background".equals(e.getPropertyName())) fundo = (Color) e.getNewValue();
                else if ("border".equals(e.getPropertyName())) borda = (Border) e.getNewValue();
                else if ("model".equals(e.getPropertyName()) && campo instanceof JTable) {
                    SwingUtilities.invokeLater(() -> { if (campo.getClientProperty(chaveEstado) == this) aplicar(campo); });
                    return;
                } else if (!"enabled".equals(e.getPropertyName())) return;
                aplicar(campo);
            };
            campo.addPropertyChangeListener(preferenciaCores);
        }
        @SuppressWarnings({"unchecked", "rawtypes"})
        void aplicar(JComponent campo) {
            ajustandoCores = true;
            try {
                PaletaAcessibilidade p = Acessibilidade.getPaleta();
                campo.setBackground(adaptarFundo(campo, fundo));
                campo.setForeground(new ColorUIResource(adaptarTexto(campo, frente, fundo)));
                campo.setBorder(adaptarBorda(borda));
                configurarNimbus(campo, tema);
                if (campo instanceof JLabel && icone != null) {
                    JOptionPane mensagem = (JOptionPane) SwingUtilities.getAncestorOfClass(JOptionPane.class, campo);
                    if (mensagem != null && mensagem.getIcon() == null) {
                        String tipo = switch (mensagem.getMessageType()) {
                            case JOptionPane.ERROR_MESSAGE -> "error";
                            case JOptionPane.WARNING_MESSAGE -> "warning";
                            case JOptionPane.QUESTION_MESSAGE -> "question";
                            default -> "information";
                        };
                        ((JLabel) campo).setIcon(new IconeMensagem(tipo, icone));
                    }
                }
                if (campo instanceof JTextComponent) {
                    JTextComponent texto = (JTextComponent) campo;
                    texto.setSelectionColor(p.getSelecao()); texto.setSelectedTextColor(p.getTextoSelecao());
                    texto.setDisabledTextColor(p.getTextoDesabilitado()); texto.setCaretColor(p.getTexto());
                }
                if (campo instanceof JTable) {
                    JTable tabela = (JTable) campo;
                    tabela.setSelectionBackground(p.getSelecao()); tabela.setSelectionForeground(p.getTextoSelecao());
                    for (int i = 0; i < tabela.getColumnCount(); i++) {
                        TableColumn coluna = tabela.getColumnModel().getColumn(i);
                        if (cabecalhos.containsKey(coluna)) continue;
                        TableCellRenderer original = coluna.getHeaderRenderer();
                        cabecalhos.put(coluna, original);
                        TableCellRenderer delegado = original != null ? original : tabela.getTableHeader().getDefaultRenderer();
                        coluna.setHeaderRenderer((t, v, s, f, l, c) -> {
                            Component celula = delegado.getTableCellRendererComponent(t, v, s, f, l, c);
                            celula.setBackground(Acessibilidade.getPaleta().getPrimaria());
                            celula.setForeground(Acessibilidade.getPaleta().getTextoSelecao());
                            return celula;
                        });
                    }
                }
                if (campo instanceof JList) {
                    ((JList<?>) campo).setSelectionBackground(p.getSelecao());
                    ((JList<?>) campo).setSelectionForeground(p.getTextoSelecao());
                }
                if (campo instanceof JComboBox && renderizadorCombo != null) {
                    ((JComboBox) campo).setRenderer((lista, valor, indice, selecionado, foco) -> {
                        Component celula = ((ListCellRenderer) renderizadorCombo).getListCellRendererComponent(lista, valor, indice, selecionado, foco);
                        PaletaAcessibilidade atual = Acessibilidade.getPaleta();
                        celula.setBackground(selecionado ? atual.getSelecao() : campo.isEnabled() ? atual.getSuperficie() : atual.getDesabilitado());
                        celula.setForeground(selecionado ? atual.getTextoSelecao() : campo.isEnabled() ? atual.getTexto() : atual.getTextoDesabilitado());
                        return celula;
                    });
                }
            } finally { ajustandoCores = false; }
        }
        @SuppressWarnings({"unchecked", "rawtypes"})
        void restaurar(JComponent campo) {
            campo.removePropertyChangeListener(preferenciaCores);
            campo.putClientProperty("Nimbus.Overrides", tema);
            campo.putClientProperty("Nimbus.Overrides.InheritDefaults", herdarTema);
            campo.setForeground(frente); campo.setBackground(fundo); campo.setBorder(borda);
            if (campo instanceof JLabel) ((JLabel) campo).setIcon(icone);
            if (campo instanceof JTextComponent) {
                JTextComponent texto = (JTextComponent) campo;
                texto.setSelectionColor(selecao); texto.setSelectedTextColor(textoSelecao);
                texto.setDisabledTextColor(desabilitado); texto.setCaretColor(cursor);
            }
            if (campo instanceof JTable) {
                JTable tabela = (JTable) campo;
                tabela.setSelectionBackground(selecao); tabela.setSelectionForeground(textoSelecao);
                cabecalhos.forEach(TableColumn::setHeaderRenderer);
            }
            if (campo instanceof JList) {
                ((JList<?>) campo).setSelectionBackground(selecao); ((JList<?>) campo).setSelectionForeground(textoSelecao);
            }
            if (campo instanceof JComboBox && renderizadorCombo != null) ((JComboBox) campo).setRenderer(renderizadorCombo);
        }
    }

    private static final class IconeMensagem implements Icon {
        private final String tipo;
        private final Icon original;
        IconeMensagem(String tipo, Icon original) { this.tipo = tipo; this.original = original; }
        public int getIconWidth() { return 32; }
        public int getIconHeight() { return 32; }
        public void paintIcon(Component campo, Graphics g, int x, int y) {
            if (!Acessibilidade.isDaltonismoAtivo() && original != null) { original.paintIcon(campo, g, x, y); return; }
            PaletaAcessibilidade p = Acessibilidade.getPaleta();
            Graphics2D desenho = (Graphics2D) g.create();
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean aviso = tipo.equals("warning");
            desenho.setColor(aviso ? p.getAviso() : tipo.equals("error") ? p.getErro() : p.getInformacao());
            if (aviso) desenho.fillPolygon(new int[]{x + 16, x + 1, x + 31}, new int[]{y + 1, y + 30, y + 30}, 3);
            else if (tipo.equals("error")) desenho.fillRoundRect(x + 1, y + 1, 30, 30, 4, 4);
            else desenho.fillOval(x + 1, y + 1, 30, 30);
            desenho.setColor(aviso ? p.getTextoAviso() : Color.WHITE);
            desenho.setFont(new Font("Segoe UI", Font.BOLD, 22));
            String simbolo = aviso ? "!" : tipo.equals("error") ? "×" : tipo.equals("question") ? "?" : "i";
            desenho.drawString(simbolo, x + (32 - desenho.getFontMetrics().stringWidth(simbolo)) / 2, y + 24);
            desenho.dispose();
        }
    }
}
