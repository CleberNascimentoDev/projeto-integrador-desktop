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
        Acessibilidade.adicionarListener(preferencia);
        atualizarEstado();
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        pack();
        setLocationRelativeTo(owner);
        Acessibilidade.configurarTela(this);
    }

    private void atualizarEstado() {
        estado.setText("<html>Baixa visão: " + (Acessibilidade.isBaixaVisaoAtiva() ? "ativada" : "desativada")
                + "<br>Cores: " + Acessibilidade.getModoDaltonismo() + "</html>");
        baixaVisao.setText(Acessibilidade.isBaixaVisaoAtiva() ? "Desativar modo de baixa visão" : "Ativar modo de baixa visão");
        for (JButton botao : new JButton[]{padrao, protanopia, deuteranopia, tritanopia}) {
            boolean selecionado = Acessibilidade.getModoDaltonismo() == botao.getClientProperty("modoDaltonismo");
            botao.setIcon(selecionado ? new IconeSelecao(Math.max(18,botao.getFont().getSize())) : null);
            botao.getAccessibleContext().setAccessibleDescription(botao.getToolTipText() + (selecionado ? " Modo selecionado." : ""));
        }
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
        getContentPane().setLayout(new java.awt.BorderLayout(0,0));
        painelOpcoes.setOpaque(true);
        painelOpcoes.setBackground(new java.awt.Color(255,255,255));
        painelOpcoes.setBorder(javax.swing.BorderFactory.createEmptyBorder(16,28,16,28));
        painelOpcoes.setLayout(new java.awt.GridBagLayout());
        titulo.setText("Opções de acessibilidade");
        titulo.setFont(new java.awt.Font("Segoe UI",1,28));
        titulo.setForeground(new java.awt.Color(17,48,82));
        painelOpcoes.add(titulo,new java.awt.GridBagConstraints(0,0,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,12,0),0,0));
        estado.setText("<html>Baixa visão: desativada<br>Cores: Padrão</html>");
        estado.setFont(new java.awt.Font("Segoe UI",0,18));
        estado.setForeground(new java.awt.Color(64,64,64));
        painelOpcoes.add(estado,new java.awt.GridBagConstraints(0,1,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,12,0),0,0));
        orientacao.setText("<html>Use Tab e Shift+Tab para navegar.<br>Use Espaço nos botões e as setas nas tabelas.</html>");
        orientacao.setFont(new java.awt.Font("Segoe UI",0,18));
        orientacao.setForeground(new java.awt.Color(64,64,64));
        painelOpcoes.add(orientacao,new java.awt.GridBagConstraints(0,2,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,12,0),0,0));
        baixaVisao.setText("Ativar modo de baixa visão");
        baixaVisao.addActionListener(this::baixaVisaoActionPerformed);
        painelOpcoes.add(baixaVisao,new java.awt.GridBagConstraints(0,3,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,12,0),0,0));
        orientacaoBaixaVisao.setText("<html>Textos e controles ampliados, alto contraste<br>e layout ajustado ao espaço da tela.</html>");
        orientacaoBaixaVisao.setFont(new java.awt.Font("Segoe UI",0,18));
        orientacaoBaixaVisao.setForeground(new java.awt.Color(64,64,64));
        painelOpcoes.add(orientacaoBaixaVisao,new java.awt.GridBagConstraints(0,4,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,12,0),0,0));
        modos.setOpaque(false);
        modos.setLayout(new java.awt.GridLayout(2,2,12,12));
        padrao.setText("Padrão");
        padrao.setToolTipText("Restaura as cores originais do sistema.");
        padrao.addActionListener(this::padraoActionPerformed);
        modos.add(padrao);
        protanopia.setText("Protanopia");
        protanopia.setToolTipText("Adapta cores para dificuldade de distinção no eixo vermelho/verde.");
        protanopia.addActionListener(this::protanopiaActionPerformed);
        modos.add(protanopia);
        deuteranopia.setText("Deuteranopia");
        deuteranopia.setToolTipText("Adapta cores para dificuldade de distinção entre verde, vermelho e tons próximos.");
        deuteranopia.addActionListener(this::deuteranopiaActionPerformed);
        modos.add(deuteranopia);
        tritanopia.setText("Tritanopia");
        tritanopia.setToolTipText("Adapta cores para dificuldade de distinção no eixo azul/amarelo.");
        tritanopia.addActionListener(this::tritanopiaActionPerformed);
        modos.add(tritanopia);
        painelOpcoes.add(modos,new java.awt.GridBagConstraints(0,5,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,12,0),0,0));
        orientacaoDaltonismo.setText("<html>Adaptação de cores com textos e símbolos.<br>A marca indica o modo selecionado.<br>Campos com * são obrigatórios.</html>");
        orientacaoDaltonismo.setFont(new java.awt.Font("Segoe UI",0,18));
        orientacaoDaltonismo.setForeground(new java.awt.Color(64,64,64));
        painelOpcoes.add(orientacaoDaltonismo,new java.awt.GridBagConstraints(0,6,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,12,0),0,0));
        fechar.setText("Fechar");
        fechar.addActionListener(this::fecharActionPerformed);
        painelOpcoes.add(fechar,new java.awt.GridBagConstraints(0,7,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,0,0),0,0));
        getContentPane().add(painelOpcoes,java.awt.BorderLayout.CENTER);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void baixaVisaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baixaVisaoActionPerformed
        Acessibilidade.setBaixaVisaoAtiva(!Acessibilidade.isBaixaVisaoAtiva());
    }//GEN-LAST:event_baixaVisaoActionPerformed

    private void padraoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_padraoActionPerformed
        Acessibilidade.setModoDaltonismo(ModoDaltonismo.PADRAO);
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
