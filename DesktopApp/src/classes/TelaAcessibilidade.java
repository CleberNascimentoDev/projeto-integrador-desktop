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
        painel.setBackground(new Color(233, 243, 255));
        painel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        JLabel titulo = new JLabel("Opções de acessibilidade");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(new Color(17, 48, 82));
        JLabel estado = new JLabel();
        JLabel orientacao = new JLabel("<html>Use Tab e Shift+Tab para navegar.<br>Use Espaço nos botões e as setas nas tabelas.</html>");
        JButton baixaVisao = new JButton("Modo de baixa visão");
        baixaVisao.getAccessibleContext().setAccessibleDescription(
                "Amplia textos e controles e aumenta o contraste em todas as telas.");
        JLabel orientacaoBaixaVisao = new JLabel("<html>Textos e controles ampliados, alto contraste<br>e rolagem para acessar todo o formulário.</html>");
        JButton daltonismo = new JButton("Modo daltonismo");
        daltonismo.getAccessibleContext().setAccessibleDescription(
                "Usa uma paleta alternativa de cores, mantendo textos e símbolos das ações.");
        JLabel orientacaoDaltonismo = new JLabel("<html>Ações em azul e laranja, com texto para identificá-las.<br>Campos com * são obrigatórios.</html>");
        JButton fechar = new JButton("Fechar");
        Runnable atualizar = () -> {
            estado.setText("<html>Baixa visão: " + (Acessibilidade.isBaixaVisaoAtiva() ? "ativada" : "desativada")
                    + "<br>Daltonismo: " + (Acessibilidade.isDaltonismoAtivo() ? "ativado" : "desativado") + "</html>");
            baixaVisao.setText(Acessibilidade.isBaixaVisaoAtiva()
                    ? "Desativar modo de baixa visão" : "Ativar modo de baixa visão");
            daltonismo.setText(Acessibilidade.isDaltonismoAtivo()
                    ? "Desativar modo daltonismo" : "Ativar modo daltonismo");
        };
        baixaVisao.addActionListener(e -> {
            Acessibilidade.setBaixaVisaoAtiva(!Acessibilidade.isBaixaVisaoAtiva());
            atualizar.run();
        });
        daltonismo.addActionListener(e -> {
            Acessibilidade.setDaltonismoAtivo(!Acessibilidade.isDaltonismoAtivo());
            atualizar.run();
        });
        fechar.addActionListener(e -> dispose());
        for (JComponent componente : new JComponent[]{titulo, estado, orientacao,
                baixaVisao, orientacaoBaixaVisao, daltonismo, orientacaoDaltonismo, fechar}) {
            componente.setAlignmentX(Component.LEFT_ALIGNMENT);
            if (componente instanceof JButton) componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
            painel.add(componente);
            painel.add(Box.createVerticalStrut(12));
        }
        atualizar.run();
        setContentPane(painel);
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        pack();
        setLocationRelativeTo(owner);
        Acessibilidade.configurarTela(this);
    }
}
