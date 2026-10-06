package classes;

import java.awt.*;
import javax.swing.*;

/** Janela de opções; pode ser aberta e editada como código no NetBeans. */
public class TelaAcessibilidade extends JDialog {
    public TelaAcessibilidade(Window owner) {
        super(owner, "Acessibilidade", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));
        JLabel titulo = new JLabel("Opções de acessibilidade");
        titulo.setFont(PainelListagem.fonte(Font.BOLD, 28));
        titulo.setForeground(new Color(17, 48, 82));
        JLabel estado = new JLabel();
        JLabel orientacao = new JLabel("<html>Use Tab e Shift+Tab para navegar.<br>Use Espaço nos botões e as setas nas tabelas.</html>");
        JButton baixaVisao = new JButton("Modo de baixa visão");
        baixaVisao.getAccessibleContext().setAccessibleDescription(
                "Amplia textos e controles e aumenta o contraste em todas as telas.");
        baixaVisao.setToolTipText("Amplia textos e controles e ajusta o layout ao espaço da tela.");
        JButton altoContraste = new JButton("Ativar alto contraste");
        altoContraste.putClientProperty("altoContraste", true);
        altoContraste.getAccessibleContext().setAccessibleDescription(
                "Fundo escuro, textos claros e foco destacado, sem ampliar os componentes.");
        altoContraste.setToolTipText("Altera as cores sem ampliar; tem prioridade sobre a paleta de daltonismo.");
        JPanel recursos = new JPanel(new GridLayout(1, 2, 12, 0)) {
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        recursos.setOpaque(false);
        recursos.add(baixaVisao);
        recursos.add(altoContraste);
        JPanel modos = new JPanel(new GridLayout(2, 2, 12, 12)) {
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        modos.setOpaque(false);
        java.util.Map<ModoDaltonismo, JButton> botoesModo = new java.util.EnumMap<>(ModoDaltonismo.class);
        for (ModoDaltonismo modo : ModoDaltonismo.values()) {
            JButton botao = new JButton(modo.toString());
            PainelListagem.configurarBotao(botao, botao.getText(), 18);
            botao.putClientProperty("modoDaltonismo", modo);
            botao.getAccessibleContext().setAccessibleName(modo.toString());
            botao.setToolTipText(switch (modo) {
                case PADRAO -> "Restaura as cores originais do sistema.";
                case PROTANOPIA -> "Adapta cores para dificuldade de distinção no eixo vermelho/verde.";
                case DEUTERANOPIA -> "Adapta cores para dificuldade de distinção entre verde, vermelho e tons próximos.";
                case TRITANOPIA -> "Adapta cores para dificuldade de distinção no eixo azul/amarelo.";
            });
            botao.getAccessibleContext().setAccessibleDescription(botao.getToolTipText());
            botoesModo.put(modo, botao);
            modos.add(botao);
        }
        JLabel orientacaoDaltonismo = new JLabel("A marca indica o modo de daltonismo selecionado.");
        JButton fechar = new JButton("Fechar");
        for (JButton botao : new JButton[]{baixaVisao, altoContraste, fechar})
            PainelListagem.configurarBotao(botao, botao.getText(), 18);
        for (JLabel rotulo : new JLabel[]{estado, orientacao, orientacaoDaltonismo}) {
            rotulo.setFont(PainelListagem.fonte(Font.PLAIN, 18));
            rotulo.setForeground(new Color(64, 64, 64));
        }
        Runnable atualizar = () -> {
            estado.setText("<html>Baixa visão: " + (Acessibilidade.isBaixaVisaoAtiva() ? "ativada" : "desativada")
                    + "<br>Alto contraste: " + (Acessibilidade.isAltoContrasteAtivo() ? "ativado" : "desativado")
                    + "<br>Daltonismo: " + Acessibilidade.getModoDaltonismo()
                    + (Acessibilidade.isAltoContrasteAtivo() ? " (paleta em espera)" : "") + "</html>");
            altoContraste.setText(Acessibilidade.isAltoContrasteAtivo() ? "Desativar alto contraste" : "Ativar alto contraste");
            baixaVisao.setText(Acessibilidade.isBaixaVisaoAtiva()
                    ? "Desativar modo de baixa visão" : "Ativar modo de baixa visão");
            botoesModo.forEach((modo, botao) -> {
                boolean selecionado = Acessibilidade.getModoDaltonismo() == modo;
                botao.setIcon(selecionado ? new IconeSelecao(Math.max(18, botao.getFont().getSize())) : null);
                botao.getAccessibleContext().setAccessibleDescription(botao.getToolTipText()
                        + (selecionado ? " Modo selecionado." : ""));
            });
        };
        baixaVisao.addActionListener(e -> {
            Acessibilidade.setBaixaVisaoAtiva(!Acessibilidade.isBaixaVisaoAtiva());
            atualizar.run();
        });
        altoContraste.addActionListener(e -> Acessibilidade.setAltoContrasteAtivo(!Acessibilidade.isAltoContrasteAtivo()));
        botoesModo.forEach((modo, botao) -> botao.addActionListener(e -> Acessibilidade.setModoDaltonismo(modo)));
        java.beans.PropertyChangeListener preferencia = e -> atualizar.run();
        Acessibilidade.adicionarListener(preferencia);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) { Acessibilidade.removerListener(preferencia); }
        });
        fechar.addActionListener(e -> dispose());
        for (JComponent componente : new JComponent[]{titulo, estado, orientacao,
                recursos, modos, orientacaoDaltonismo, fechar}) {
            componente.setAlignmentX(Component.LEFT_ALIGNMENT);
            if (componente instanceof JButton) componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            painel.add(componente);
            painel.add(Box.createVerticalStrut(6));
        }
        atualizar.run();
        setContentPane(painel);
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        pack();
        setLocationRelativeTo(owner);
        Acessibilidade.configurarTela(this);
    }
}
