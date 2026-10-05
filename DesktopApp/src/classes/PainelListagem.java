package classes;

import java.awt.*;
import java.text.Normalizer;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.table.DefaultTableCellRenderer;

/** Composição visual da gestão; recebe os controles e preserva seus eventos e modelo. */
public class PainelListagem extends JPanel {
    static final Color azul = new Color(31, 53, 80);
    static final Color azulTabela = new Color(29, 45, 68);
    static final Color fundoTela = new Color(233, 243, 255);
    static final Color fundoSubtela = new Color(245, 245, 245);
    static final Color linha = new Color(230, 235, 240);
    static final Color cinza = new Color(64, 64, 64);
    private static final int espaco = 16;
    private final JButton btVoltar;
    private final JButton botaoAjuda;
    private final JLabel titulo;
    private final JLabel subtitulo;
    private final JPanel ferramentas;
    private final JPanel processos;
    private JComponent informacoes;

    /** Diferencia as janelas de apoio sem alterar o fundo das listagens principais. */
    public static void configurarSubtela(JComponent painel) {
        painel.putClientProperty("acessibilidade.subtela", true);
        painel.setBackground(fundoSubtela);
        painel.setBorder(BorderFactory.createLineBorder(azul, 2));
    }

    public void adicionarInformacoes(JComponent informacoes) {
        this.informacoes = informacoes;
        add(informacoes);
    }

    public PainelListagem(JButton btVoltar, JLabel titulo, JButton botaoAjuda,
            JTextField pesquisa, JLabel lupa, JLabel tituloTabela, JScrollPane rolagem, JTable tabela,
            String descricao, String placeholder, String singular, String plural, boolean recrutadores, JButton... botoes) {
        this.btVoltar = btVoltar;
        this.titulo = titulo;
        this.botaoAjuda = botaoAjuda;
        subtitulo = new JLabel(descricao);
        setBackground(fundoTela);
        setLayout(new LayoutConteudo());
        setPreferredSize(new Dimension(1280, 740));
        configurarBotao(btVoltar, "‹  Voltar", 17);
        for (JButton botao : botoes) {
            configurarBotao(botao, botao.getText(), 18);
            if (botao.getText().startsWith("Excluir")) botao.setFont(fonte(Font.PLAIN, 18));
        }
        titulo.setFont(fonte(Font.BOLD, 36));
        titulo.setForeground(Color.BLACK);
        subtitulo.setFont(fonte(Font.PLAIN, 19));
        subtitulo.setForeground(cinza);
        botaoAjuda.putClientProperty("baixaVisao.alinharAjuda", null);

        pesquisa.setText("");
        pesquisa.setFont(fonte(Font.PLAIN, 18));
        pesquisa.setForeground(cinza);
        pesquisa.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        pesquisa.setUI(new CampoBusca(placeholder));
        pesquisa.getAccessibleContext().setAccessibleName(placeholder);
        JPanel busca = painelArredondado(Color.WHITE, new Color(153, 153, 153), 8, 0, 16);
        busca.setLayout(new BorderLayout(16, 0));
        lupa.setIcon(new Lupa());
        lupa.setForeground(new Color(153, 153, 153));
        busca.add(lupa, BorderLayout.WEST);
        busca.add(pesquisa, BorderLayout.CENTER);
        ferramentas = new JPanel(new LayoutFerramentas(busca, pesquisa, placeholder, botoes));
        ferramentas.setOpaque(false);
        ferramentas.add(busca);
        for (JButton botao : botoes) ferramentas.add(botao);

        processos = painelArredondado(Color.WHITE, linha, 8, 24, 28);
        processos.setLayout(new BorderLayout(0, 18));
        tituloTabela.setFont(fonte(Font.BOLD, 30));
        tituloTabela.setForeground(Color.BLACK);
        JLabel quantidade = new JLabel();
        quantidade.setFont(fonte(Font.PLAIN, 16));
        quantidade.setForeground(cinza);
        Runnable atualizarQuantidade = () -> {
            int total = tabela.getRowCount();
            quantidade.setText(total + " " + (total == 1 ? singular : plural));
        };
        javax.swing.event.TableModelListener atualizarContador = e -> atualizarQuantidade.run();
        tabela.getModel().addTableModelListener(atualizarContador);
        tabela.addPropertyChangeListener("model", e -> {
            ((javax.swing.table.TableModel) e.getOldValue()).removeTableModelListener(atualizarContador);
            ((javax.swing.table.TableModel) e.getNewValue()).addTableModelListener(atualizarContador);
            atualizarQuantidade.run();
        });
        atualizarQuantidade.run();
        JPanel cabecalho = new JPanel(new BorderLayout(16, 0));
        cabecalho.setOpaque(false);
        cabecalho.add(tituloTabela, BorderLayout.WEST);
        cabecalho.add(quantidade, BorderLayout.EAST);
        processos.add(cabecalho, BorderLayout.NORTH);
        configurarTabela(tabela, rolagem, recrutadores);
        processos.add(rolagem, BorderLayout.CENTER);
        add(btVoltar);
        add(titulo);
        add(subtitulo);
        add(botaoAjuda);
        add(ferramentas);
        add(processos);
    }

    public static Font fonte(int estilo, int tamanho) {
        return new Font("Segoe UI", estilo, tamanho);
    }

    /** O destaque de teclado não pode alterar o espaço reservado ao conteúdo do botão. */
    public static javax.swing.border.Border criarBordaFoco(JButton botao, javax.swing.border.Border original) {
        Insets margens = original.getBorderInsets(botao);
        return new AbstractBorder() {
            @Override public Insets getBorderInsets(Component campo) {
                return new Insets(margens.top, margens.left, margens.bottom, margens.right);
            }
            @Override public Insets getBorderInsets(Component campo, Insets resultado) {
                resultado.set(margens.top, margens.left, margens.bottom, margens.right);
                return resultado;
            }
            @Override public void paintBorder(Component campo, Graphics g, int x, int y, int largura, int altura) {
                Graphics2D grafico = preparar(g);
                grafico.setColor(new Color(0, 120, 215));
                grafico.setStroke(new BasicStroke(3));
                grafico.drawRoundRect(x + 2, y + 2, largura - 5, altura - 5, 6, 6);
                grafico.dispose();
            }
        };
    }

    public static void configurarBotao(JButton botao, String texto, int tamanho) {
        botao.setText(texto);
        botao.setFont(fonte(Font.BOLD, tamanho));
        botao.setBackground(azul);
        botao.setForeground(Color.WHITE);
        botao.setOpaque(false);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(true);
        botao.setUI(new BotaoPlano());
        botao.setBorder(new BordaArredondada(azul, 6, 10, 22));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static JPanel painelArredondado(Color fundo, Color borda, int raio, int vertical, int horizontal) {
        JPanel painel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D grafico = preparar(g);
                grafico.setColor(getBackground());
                grafico.fillRoundRect(0, 0, getWidth(), getHeight(), raio * 2, raio * 2);
                grafico.dispose();
            }
        };
        painel.setOpaque(false);
        painel.setBackground(fundo);
        painel.setBorder(new BordaArredondada(borda, raio * 2, vertical, horizontal));
        return painel;
    }

    private static Graphics2D preparar(Graphics g) {
        Graphics2D grafico = (Graphics2D) g.create();
        grafico.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return grafico;
    }

    public static void configurarTabela(JTable tabela, JScrollPane rolagem, boolean recrutadores) {
        boolean ampliado = Acessibilidade.isBaixaVisaoAtiva() && tabela.getClientProperty("baixaVisao.estado") != null;
        rolagem.putClientProperty("baixaVisao.alinharDireita", null);
        rolagem.setBorder(new BordaArredondada(linha, 6, 0, 0));
        rolagem.getViewport().setBackground(Color.WHITE);
        tabela.setBorder(null);
        tabela.setBackground(Color.WHITE);
        tabela.setForeground(Color.BLACK);
        tabela.setFont(fonte(Font.PLAIN, ampliado ? 27 : 18));
        tabela.setRowHeight(ampliado ? 105 : 70);
        tabela.setGridColor(linha);
        tabela.setShowGrid(true);
        tabela.setIntercellSpacing(new Dimension(1, 1));
        tabela.setSelectionBackground(azul);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setDefaultRenderer(Object.class, new CelulaProcesso(recrutadores));
        tabela.getTableHeader().setFont(fonte(Font.BOLD, 19).deriveFont(ampliado ? 28.5f : 19f));
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 60));
        DefaultTableCellRenderer cabecalho = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object valor,
                    boolean selecionado, boolean foco, int linha, int coluna) {
                super.getTableCellRendererComponent(tb, valor, selecionado, foco, linha, coluna);
                setFont(tb.getTableHeader().getFont());
                setBackground(Acessibilidade.isBaixaVisaoAtiva() ? new Color(17, 48, 82) : azulTabela);
                setForeground(Color.WHITE);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));
                return this;
            }
        };
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setHeaderRenderer(cabecalho);
            tabela.getColumnModel().getColumn(i).setPreferredWidth(600);
            tabela.getColumnModel().getColumn(i).setMinWidth(220);
        }
    }

    private final class LayoutConteudo extends LayoutBase {
        @Override public void layoutContainer(Container painel) {
            int margem = painel.getWidth() < 900 ? 24 : 40;
            int largura = Math.max(1, painel.getWidth() - margem * 2);
            int y = 28;
            int alturaVoltar = Math.max(48, btVoltar.getPreferredSize().height);
            btVoltar.setBounds(margem, y, btVoltar.getPreferredSize().width, alturaVoltar);
            y += alturaVoltar + 24;
            int alturaTitulo = titulo.getPreferredSize().height;
            int ajuda = Math.max(42, botaoAjuda.getPreferredSize().height);
            titulo.setBounds(margem, y, Math.max(1, largura - ajuda - 16), alturaTitulo);
            botaoAjuda.setBounds(margem + largura - ajuda, y + Math.max(0, (alturaTitulo - ajuda) / 2), ajuda, ajuda);
            y += alturaTitulo + 4;
            int alturaSubtitulo = subtitulo.getPreferredSize().height;
            subtitulo.setBounds(margem, y, largura, alturaSubtitulo);
            y += alturaSubtitulo + 28;
            if (informacoes != null) {
                informacoes.setBounds(margem, y, largura, informacoes.getPreferredSize().height);
                y += informacoes.getHeight() + 20;
            }
            ferramentas.setSize(largura, 1);
            int alturaFerramentas = ferramentas.getPreferredSize().height;
            ferramentas.setBounds(margem, y, largura, alturaFerramentas);
            y += alturaFerramentas + 30;
            processos.setBounds(margem, y, largura, Math.max(1, painel.getHeight() - y - 40));
        }
        @Override public Dimension preferredLayoutSize(Container painel) { return new Dimension(1280, 740); }
    }

    private static final class LayoutFerramentas extends LayoutBase {
        private final JPanel busca;
        private final JTextField pesquisa;
        private final JButton[] botoes;
        private final String placeholder;
        LayoutFerramentas(JPanel busca, JTextField pesquisa, String placeholder, JButton... botoes) {
            this.busca = busca;
            this.pesquisa = pesquisa;
            this.botoes = botoes;
            this.placeholder = placeholder;
        }
        private int altura() {
            int altura = 56;
            for (JButton botao : botoes) altura = Math.max(altura, botao.getPreferredSize().height);
            return altura;
        }
        private int larguraAcoes() {
            int largura = Math.max(0, botoes.length - 1) * espaco;
            for (JButton botao : botoes) largura += botao.getPreferredSize().width;
            return largura;
        }
        private boolean quebrar(Container painel) {
            int larguraBusca = pesquisa.getFontMetrics(pesquisa.getFont()).stringWidth(placeholder) + 90;
            return painel.getWidth() < larguraAcoes() + Math.max(320, larguraBusca) + espaco;
        }
        @Override public Dimension preferredLayoutSize(Container painel) {
            return new Dimension(800, altura() * (quebrar(painel) ? 2 : 1) + (quebrar(painel) ? espaco : 0));
        }
        @Override public void layoutContainer(Container painel) {
            int altura = altura();
            boolean quebrar = quebrar(painel);
            int y = quebrar ? altura + espaco : 0;
            busca.setBounds(0, 0, quebrar ? painel.getWidth() : painel.getWidth() - larguraAcoes() - espaco, altura);
            int x = painel.getWidth() - larguraAcoes();
            for (JButton botao : botoes) {
                botao.setBounds(x, y, botao.getPreferredSize().width, altura);
                x += botao.getWidth() + espaco;
            }
        }
    }

    private abstract static class LayoutBase implements LayoutManager {
        @Override public void addLayoutComponent(String nome, Component campo) { }
        @Override public void removeLayoutComponent(Component campo) { }
        @Override public Dimension minimumLayoutSize(Container painel) { return new Dimension(640, 540); }
    }

    private static final class BotaoPlano extends BasicButtonUI {
        @Override public void paint(Graphics g, JComponent campo) {
            AbstractButton botao = (AbstractButton) campo;
            if (botao.isContentAreaFilled()) {
                Graphics2D grafico = preparar(g);
                grafico.setColor(botao.getBackground());
                grafico.fillRoundRect(0, 0, campo.getWidth(), campo.getHeight(), 12, 12);
                if (botao.getModel().isPressed()) {
                    grafico.setColor(new Color(0, 0, 0, 35));
                    grafico.fillRoundRect(0, 0, campo.getWidth(), campo.getHeight(), 12, 12);
                }
                grafico.dispose();
            }
            super.paint(g, campo);
        }
        @Override protected void paintFocus(Graphics g, AbstractButton botao, Rectangle area, Rectangle texto, Rectangle icone) {
            Graphics2D grafico = preparar(g);
            grafico.setColor(botao.getForeground());
            grafico.setStroke(new BasicStroke(2));
            grafico.drawRoundRect(4, 4, botao.getWidth() - 9, botao.getHeight() - 9, 8, 8);
            grafico.dispose();
        }
    }

    private static final class BordaArredondada extends AbstractBorder {
        private final Color cor;
        private final int raio, vertical, horizontal;
        BordaArredondada(Color cor, int raio, int vertical, int horizontal) {
            this.cor = cor;
            this.raio = raio;
            this.vertical = vertical;
            this.horizontal = horizontal;
        }
        @Override public Insets getBorderInsets(Component campo) { return new Insets(vertical + 1, horizontal + 1, vertical + 1, horizontal + 1); }
        @Override public Insets getBorderInsets(Component campo, Insets margens) {
            margens.set(vertical + 1, horizontal + 1, vertical + 1, horizontal + 1);
            return margens;
        }
        @Override public void paintBorder(Component campo, Graphics g, int x, int y, int largura, int altura) {
            Graphics2D grafico = preparar(g);
            grafico.setColor(cor);
            grafico.drawRoundRect(x, y, largura - 1, altura - 1, raio, raio);
            grafico.dispose();
        }
    }

    private static final class CampoBusca extends BasicTextFieldUI {
        private final String placeholder;
        CampoBusca(String placeholder) { this.placeholder = placeholder; }
        @Override protected void paintSafely(Graphics g) {
            super.paintSafely(g);
            JTextField campo = (JTextField) getComponent();
            if (campo.getText().isEmpty()) {
                Graphics2D grafico = preparar(g);
                grafico.setColor(campo.getForeground());
                grafico.setFont(campo.getFont());
                FontMetrics fonte = grafico.getFontMetrics();
                grafico.clipRect(campo.getInsets().left, 0,
                        campo.getWidth() - campo.getInsets().left - campo.getInsets().right, campo.getHeight());
                grafico.drawString(placeholder, campo.getInsets().left,
                        (campo.getHeight() - fonte.getHeight()) / 2 + fonte.getAscent());
                grafico.dispose();
            }
        }
    }

    private static final class Lupa implements Icon {
        @Override public int getIconWidth() { return 24; }
        @Override public int getIconHeight() { return 24; }
        @Override public void paintIcon(Component campo, Graphics g, int x, int y) {
            Graphics2D grafico = preparar(g);
            grafico.setColor(campo.getForeground());
            grafico.setStroke(new BasicStroke(2.2f));
            grafico.drawOval(x + 2, y + 2, 14, 14);
            grafico.drawLine(x + 15, y + 15, x + 22, y + 22);
            grafico.dispose();
        }
    }

    private static final class CelulaProcesso extends DefaultTableCellRenderer {
        private final boolean recrutadores;
        CelulaProcesso(boolean recrutadores) { this.recrutadores = recrutadores; }
        private boolean badge;
        @Override public Component getTableCellRendererComponent(JTable tabela, Object valor,
                boolean selecionado, boolean foco, int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, selecionado, foco, linha, coluna);
            setFont(tabela.getFont());
            setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));
            badge = recrutadores && coluna == 1 && "Não atribuído".equals(valor);
            if (recrutadores && !badge && valor instanceof String) setText(apresentarNome((String) valor, coluna == 0));
            return this;
        }
        @Override protected void paintComponent(Graphics g) {
            if (!badge) { super.paintComponent(g); return; }
            Graphics2D grafico = preparar(g);
            grafico.setColor(getBackground());
            grafico.fillRect(0, 0, getWidth(), getHeight());
            grafico.setFont(getFont());
            FontMetrics fonte = grafico.getFontMetrics();
            int altura = fonte.getHeight() + 12;
            int largura = Math.min(getWidth() - 56, fonte.stringWidth(getText()) + 28);
            int y = (getHeight() - altura) / 2;
            grafico.setColor(fundoTela);
            grafico.fillRoundRect(28, y, largura, altura, altura, altura);
            grafico.setColor(azulTabela);
            grafico.drawString(getText(), 42, y + 6 + fonte.getAscent());
            grafico.dispose();
        }
    }

    private static String apresentarNome(String valor, boolean processo) {
        String normalizado = Normalizer.normalize(valor, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT).trim();
        if (processo && (normalizado.equals("DEV SENIOR") || normalizado.equals("DESENVOLVEDOR SENIOR"))) return "Desenvolvedor Sênior";
        if (processo && (normalizado.equals("DEV JUNIOR") || normalizado.equals("DESENVOLVEDOR JUNIOR"))) return "Desenvolvedor Júnior";
        if (!valor.equals(valor.toUpperCase(Locale.ROOT))) return valor;
        StringBuilder resultado = new StringBuilder();
        for (String palavra : valor.toLowerCase(Locale.forLanguageTag("pt-BR")).split(" ")) {
            if (resultado.length() > 0) resultado.append(' ');
            if (!palavra.isEmpty()) resultado.append(Character.toUpperCase(palavra.charAt(0))).append(palavra.substring(1));
        }
        return resultado.toString();
    }
}
