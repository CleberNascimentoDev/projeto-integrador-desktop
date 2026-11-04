package classes;

import java.awt.Window;
import javax.swing.*;

/** Opções editáveis no GUI Builder; somente o estado dos modos é atualizado em execução. */
public class TelaAcessibilidade extends JDialog {
    private final java.beans.PropertyChangeListener preferencia = e -> atualizarEstado();

    public TelaAcessibilidade(Window owner) {
        super(owner, "Acessibilidade", ModalityType.APPLICATION_MODAL);
        initComponents();
        baixaVisao.getAccessibleContext().setAccessibleDescription(
                "Amplia textos e controles e aumenta o contraste em todas as telas.");
        JButton[] botoes = {padrao, protanopia, deuteranopia, tritanopia};
        ModoDaltonismo[] tipos = ModoDaltonismo.values();
        for (int i = 0; i < botoes.length; i++) {
            botoes[i].putClientProperty("modoDaltonismo", tipos[i]);
            botoes[i].getAccessibleContext().setAccessibleName(tipos[i].toString());
        }
        altoContraste.putClientProperty("altoContraste", true);
        Acessibilidade.adicionarListener(preferencia);
        atualizarEstado();
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        pack();
        setLocationRelativeTo(owner);
        Acessibilidade.configurarTela(this);
    }

    private void atualizarEstado() {
        estado.setText("Baixa visão: " + (Acessibilidade.isBaixaVisaoAtiva() ? "selecionada" : "não selecionada")
                + " | Cores: " + (Acessibilidade.isAltoContrasteAtivo() ? "Alto contraste" : Acessibilidade.getModoDaltonismo()));
        marcarSelecao(baixaVisao, Acessibilidade.isBaixaVisaoAtiva());
        marcarSelecao(altoContraste, Acessibilidade.isAltoContrasteAtivo());
        for (JButton botao : new JButton[]{padrao, protanopia, deuteranopia, tritanopia}) {
            boolean selecionado = Acessibilidade.getModoDaltonismo() == botao.getClientProperty("modoDaltonismo")
                    && !Acessibilidade.isAltoContrasteAtivo()
                    && (botao != padrao || !Acessibilidade.isBaixaVisaoAtiva());
            marcarSelecao(botao, selecionado);
        }
        // O tamanho acompanha fontes e ícones; não depende do foco ou do texto da ação.
        getContentPane().setPreferredSize(null);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void marcarSelecao(JButton botao, boolean selecionado) {
        botao.setSelected(selecionado);
        botao.setIcon(selecionado ? new IconeSelecao(Math.max(18, botao.getFont().getSize())) : null);
        botao.setPreferredSize(null);
        botao.getAccessibleContext().setAccessibleDescription(
                botao.getText() + (selecionado ? ": selecionado." : ": não selecionado."));
    }

    private void altoContrasteActionPerformed(java.awt.event.ActionEvent evt) {
        Acessibilidade.setAltoContrasteAtivo(!Acessibilidade.isAltoContrasteAtivo());
    }

    @Override public void dispose() {
        Acessibilidade.removerListener(preferencia);
        super.dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        painelOpcoes = new javax.swing.JPanel();
        titulo = new javax.swing.JLabel();
        estado = new javax.swing.JLabel();
        orientacao = new javax.swing.JLabel();
        baixaVisao = new classes.BotaoPadrao();
        altoContraste = new classes.BotaoPadrao();
        orientacaoBaixaVisao = new javax.swing.JLabel();
        modos = new javax.swing.JPanel();
        padrao = new classes.BotaoPadrao();
        protanopia = new classes.BotaoPadrao();
        deuteranopia = new classes.BotaoPadrao();
        tritanopia = new classes.BotaoPadrao();
        orientacaoDaltonismo = new javax.swing.JLabel();
        fechar = new classes.BotaoPadrao();
        setDefaultCloseOperation(2);
        setResizable(false);
        getContentPane().setLayout(new java.awt.BorderLayout());
        painelOpcoes.setOpaque(true);
        painelOpcoes.setBackground(new java.awt.Color(255,255,255));
        painelOpcoes.setBorder(javax.swing.BorderFactory.createEmptyBorder(16,28,16,28));
        painelOpcoes.setLayout(new java.awt.GridBagLayout());
        titulo.setText("Opções de acessibilidade");
        titulo.setFont(new java.awt.Font("Segoe UI",1,24));
        titulo.setForeground(new java.awt.Color(17,48,82));
        painelOpcoes.add(titulo,new java.awt.GridBagConstraints(0,0,2,1,1.0,0.0,18,2,new java.awt.Insets(0,0,8,0),0,0));
        estado.setText("Baixa visão: não selecionada | Cores: Padrão");
        estado.setFont(new java.awt.Font("Segoe UI",0,14));
        estado.setForeground(new java.awt.Color(64,64,64));
        painelOpcoes.add(estado,new java.awt.GridBagConstraints(0,1,2,1,1.0,0.0,18,2,new java.awt.Insets(0,0,8,0),0,0));
        orientacao.setText("Tab: navegar • Espaço: selecionar • Esc: fechar");
        orientacao.setFont(new java.awt.Font("Segoe UI",0,14));
        orientacao.setForeground(new java.awt.Color(64,64,64));
        painelOpcoes.add(orientacao,new java.awt.GridBagConstraints(0,2,2,1,1.0,0.0,18,2,new java.awt.Insets(0,0,8,0),0,0));
        baixaVisao.setText("Baixa visão");
        baixaVisao.setFont(new java.awt.Font("Segoe UI",1,16));
        baixaVisao.addActionListener(this::baixaVisaoActionPerformed);
        painelOpcoes.add(baixaVisao,new java.awt.GridBagConstraints(0,3,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,8,6),0,0));
        altoContraste.setText("Alto contraste");
        altoContraste.setFont(new java.awt.Font("Segoe UI",1,16));
        altoContraste.addActionListener(this::altoContrasteActionPerformed);
        painelOpcoes.add(altoContraste,new java.awt.GridBagConstraints(1,3,1,1,1.0,0.0,18,2,new java.awt.Insets(0,6,8,0),0,0));
        orientacaoBaixaVisao.setText("<html>Baixa visão amplia textos e controles.<br>Alto contraste usa fundo escuro e texto claro.</html>");
        orientacaoBaixaVisao.setFont(new java.awt.Font("Segoe UI",0,14));
        orientacaoBaixaVisao.setForeground(new java.awt.Color(64,64,64));
        painelOpcoes.add(orientacaoBaixaVisao,new java.awt.GridBagConstraints(0,4,2,1,1.0,0.0,18,2,new java.awt.Insets(0,0,8,0),0,0));
        modos.setOpaque(false);
        modos.setLayout(new java.awt.GridLayout(2,2,12,12));
        padrao.setText("Padrão");
        padrao.setToolTipText("Restaura cores, fontes e dimensões originais do sistema.");
        padrao.setFont(new java.awt.Font("Segoe UI",1,16));
        padrao.addActionListener(this::padraoActionPerformed);
        modos.add(padrao);
        protanopia.setText("Protanopia");
        protanopia.setToolTipText("Adapta cores para dificuldade de distinção no eixo vermelho/verde.");
        protanopia.setFont(new java.awt.Font("Segoe UI",1,16));
        protanopia.addActionListener(this::protanopiaActionPerformed);
        modos.add(protanopia);
        deuteranopia.setText("Deuteranopia");
        deuteranopia.setToolTipText("Adapta cores para dificuldade de distinção entre verde, vermelho e tons próximos.");
        deuteranopia.setFont(new java.awt.Font("Segoe UI",1,16));
        deuteranopia.addActionListener(this::deuteranopiaActionPerformed);
        modos.add(deuteranopia);
        tritanopia.setText("Tritanopia");
        tritanopia.setToolTipText("Adapta cores para dificuldade de distinção no eixo azul/amarelo.");
        tritanopia.setFont(new java.awt.Font("Segoe UI",1,16));
        tritanopia.addActionListener(this::tritanopiaActionPerformed);
        modos.add(tritanopia);
        painelOpcoes.add(modos,new java.awt.GridBagConstraints(0,5,2,1,1.0,0.0,18,2,new java.awt.Insets(0,0,8,0),0,0));
        orientacaoDaltonismo.setText("<html>Alto contraste e daltonismo são exclusivos.<br>Padrão restaura todas as opções visuais.</html>");
        orientacaoDaltonismo.setFont(new java.awt.Font("Segoe UI",0,14));
        orientacaoDaltonismo.setForeground(new java.awt.Color(64,64,64));
        painelOpcoes.add(orientacaoDaltonismo,new java.awt.GridBagConstraints(0,6,2,1,1.0,0.0,18,2,new java.awt.Insets(0,0,8,0),0,0));
        fechar.setText("Fechar");
        fechar.setFont(new java.awt.Font("Segoe UI",1,16));
        fechar.addActionListener(this::fecharActionPerformed);
        painelOpcoes.add(fechar,new java.awt.GridBagConstraints(0,7,2,1,1.0,0.0,18,2,new java.awt.Insets(0,0,0,0),0,0));
        getContentPane().add(painelOpcoes,java.awt.BorderLayout.CENTER);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void baixaVisaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baixaVisaoActionPerformed
        Acessibilidade.setBaixaVisaoAtiva(!Acessibilidade.isBaixaVisaoAtiva());
    }//GEN-LAST:event_baixaVisaoActionPerformed

    private void padraoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_padraoActionPerformed
        Acessibilidade.restaurarPadrao();
    }//GEN-LAST:event_padraoActionPerformed

    private void protanopiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_protanopiaActionPerformed
        Acessibilidade.setModoDaltonismo(ModoDaltonismo.PROTANOPIA);
    }//GEN-LAST:event_protanopiaActionPerformed

    private void deuteranopiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deuteranopiaActionPerformed
        Acessibilidade.setModoDaltonismo(ModoDaltonismo.DEUTERANOPIA);
    }//GEN-LAST:event_deuteranopiaActionPerformed

    private void tritanopiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tritanopiaActionPerformed
        Acessibilidade.setModoDaltonismo(ModoDaltonismo.TRITANOPIA);
    }//GEN-LAST:event_tritanopiaActionPerformed

    private void fecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fecharActionPerformed
        dispose();
    }//GEN-LAST:event_fecharActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel painelOpcoes;
    private javax.swing.JLabel titulo;
    private javax.swing.JLabel estado;
    private javax.swing.JLabel orientacao;
    private classes.BotaoPadrao baixaVisao;
    private classes.BotaoPadrao altoContraste;
    private javax.swing.JLabel orientacaoBaixaVisao;
    private javax.swing.JPanel modos;
    private classes.BotaoPadrao padrao;
    private classes.BotaoPadrao protanopia;
    private classes.BotaoPadrao deuteranopia;
    private classes.BotaoPadrao tritanopia;
    private javax.swing.JLabel orientacaoDaltonismo;
    private classes.BotaoPadrao fechar;
    // End of variables declaration//GEN-END:variables
}
