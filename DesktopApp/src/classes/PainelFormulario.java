package classes;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;

/** Cabeçalho e formulário reutilizáveis; os campos recebidos mantêm modelos, máscaras e eventos. */
public final class PainelFormulario extends JPanel {
    private final JButton voltar, ajuda;
    private final JLabel titulo, descricao;
    private final JPanel conteudo, acoes;

    public PainelFormulario(JButton voltar, JLabel titulo, JButton ajuda, String descricao,
            JPanel campos, JButton... botoes) {
        this.voltar = voltar;
        this.ajuda = ajuda;
        this.titulo = titulo;
        this.descricao = new JLabel(descricao);
        PainelListagem.configurarSubtela(this);
        titulo.setFont(PainelListagem.fonte(Font.BOLD, 32));
        titulo.setForeground(Color.BLACK);
        this.descricao.setFont(PainelListagem.fonte(Font.PLAIN, 18));
        this.descricao.setForeground(PainelListagem.cinza);
        if (voltar != null) PainelListagem.configurarBotao(voltar, "‹  Voltar", 17);
        if (ajuda != null) ajuda.putClientProperty("baixaVisao.alinharAjuda", null);
        conteudo = PainelListagem.painelArredondado(Color.WHITE, PainelListagem.linha, 8, 24, 28);
        conteudo.setLayout(new BorderLayout());
        conteudo.add(campos, BorderLayout.CENTER);
        acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        acoes.setOpaque(false);
        for (JButton botao : botoes) {
            PainelListagem.configurarBotao(botao, botao.getText(), 18);
            acoes.add(botao);
        }
        setLayout(new LayoutFormulario());
        if (voltar != null) add(voltar);
        add(titulo);
        add(this.descricao);
        if (ajuda != null) add(ajuda);
        add(conteudo);
        if (botoes.length > 0) add(acoes);
        setPreferredSize(new Dimension(1040, Math.max(700, campos.getPreferredSize().height + 360)));
    }

    public static JPanel campos(JPanel... campos) {
        JPanel painel = new JPanel(new LayoutCampos());
        painel.setOpaque(false);
        for (JPanel campo : campos) painel.add(campo);
        return painel;
    }

    public static JPanel campo(JLabel rotulo, JComponent entrada, JLabel... obrigatorios) {
        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.setOpaque(false);
        JPanel nome = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nome.setOpaque(false);
        rotulo.setFont(PainelListagem.fonte(Font.BOLD, 18));
        rotulo.setForeground(Color.BLACK);
        nome.add(rotulo);
        for (JLabel obrigatorio : obrigatorios) {
            obrigatorio.setFont(rotulo.getFont());
            nome.add(obrigatorio);
        }
        painel.add(nome, BorderLayout.NORTH);
        estilizarEntrada(entrada);
        painel.add(entrada, BorderLayout.CENTER);
        return painel;
    }

    public static JPanel senha(JPanel painel, JPasswordField senha, JButton olho) {
        painel.removeAll();
        painel.setLayout(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(153, 153, 153)),
                BorderFactory.createEmptyBorder(0, 12, 0, 8)));
        estilizarEntrada(senha);
        senha.setBorder(BorderFactory.createEmptyBorder());
        olho.setMargin(new Insets(0, 0, 0, 0));
        olho.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        olho.setPreferredSize(new Dimension(36, 36));
        painel.add(senha, BorderLayout.CENTER);
        painel.add(olho, BorderLayout.EAST);
        return painel;
    }

    private static void estilizarEntrada(JComponent entrada) {
        entrada.setFont(PainelListagem.fonte(Font.PLAIN, 18));
        entrada.setBackground(Color.WHITE);
        if (entrada instanceof JScrollPane) {
            Component texto = ((JScrollPane) entrada).getViewport().getView();
            if (texto instanceof JTextComponent) {
                texto.setFont(entrada.getFont());
                texto.setBackground(Color.WHITE);
                ((JTextComponent) texto).setMargin(new Insets(12, 14, 12, 14));
            }
            entrada.setPreferredSize(new Dimension(320, 110));
        } else {
            entrada.setPreferredSize(new Dimension(320, 52));
        }
        if (entrada instanceof JTextComponent || entrada instanceof JScrollPane) {
            entrada.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(153, 153, 153)),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        }
    }

    private final class LayoutFormulario implements LayoutManager {
        @Override public void addLayoutComponent(String nome, Component campo) { }
        @Override public void removeLayoutComponent(Component campo) { }
        @Override public Dimension minimumLayoutSize(Container painel) { return new Dimension(640, 540); }
        @Override public Dimension preferredLayoutSize(Container painel) { return new Dimension(1040, 720); }
        @Override public void layoutContainer(Container painel) {
            int margem = painel.getWidth() < 900 ? 24 : 40;
            int largura = Math.max(1, painel.getWidth() - margem * 2);
            int y = 24;
            if (voltar != null) {
                int altura = Math.max(48, voltar.getPreferredSize().height);
                voltar.setBounds(margem, y, voltar.getPreferredSize().width, altura);
                y += altura + 20;
            }
            int alturaTitulo = titulo.getPreferredSize().height;
            int tamanhoAjuda = ajuda == null ? 0 : Math.max(42, ajuda.getPreferredSize().height);
            titulo.setBounds(margem, y, largura - tamanhoAjuda - 16, alturaTitulo);
            if (ajuda != null) ajuda.setBounds(margem + largura - tamanhoAjuda, y, tamanhoAjuda, tamanhoAjuda);
            y += alturaTitulo + 4;
            descricao.setBounds(margem, y, largura, descricao.getPreferredSize().height);
            y += descricao.getHeight() + 24;
            int alturaAcoes = acoes.getComponentCount() == 0 ? 0 : acoes.getPreferredSize().height + 20;
            conteudo.setBounds(margem, y, largura, Math.max(1, painel.getHeight() - y - margem - alturaAcoes));
            if (alturaAcoes > 0) acoes.setBounds(margem, painel.getHeight() - margem - alturaAcoes + 20,
                    largura, alturaAcoes - 20);
        }
    }

    private static final class LayoutCampos implements LayoutManager {
        @Override public void addLayoutComponent(String nome, Component campo) { }
        @Override public void removeLayoutComponent(Component campo) { }
        private int colunas(Container painel) { return painel.getWidth() > 0 && painel.getWidth() < 650 ? 1 : 2; }
        @Override public Dimension minimumLayoutSize(Container painel) { return new Dimension(320, 300); }
        @Override public Dimension preferredLayoutSize(Container painel) {
            int altura = 0;
            int colunas = colunas(painel);
            Component[] campos = painel.getComponents();
            for (int i = 0; i < campos.length; i += colunas) {
                int linha = 0;
                for (int j = i; j < Math.min(i + colunas, campos.length); j++)
                    linha = Math.max(linha, campos[j].getPreferredSize().height);
                altura += linha + (i > 0 ? 20 : 0);
            }
            return new Dimension(880, altura);
        }
        @Override public void layoutContainer(Container painel) {
            int colunas = colunas(painel);
            int largura = (painel.getWidth() - (colunas - 1) * 28) / colunas;
            int y = 0;
            Component[] campos = painel.getComponents();
            for (int i = 0; i < campos.length; i += colunas) {
                int altura = 0;
                for (int j = i; j < Math.min(i + colunas, campos.length); j++)
                    altura = Math.max(altura, campos[j].getPreferredSize().height);
                for (int j = i; j < Math.min(i + colunas, campos.length); j++)
                    campos[j].setBounds((j - i) * (largura + 28), y, largura, altura);
                y += altura + 20;
            }
        }
    }
}
