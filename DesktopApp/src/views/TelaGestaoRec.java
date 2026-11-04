/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package views;
import database.GestaoDao;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import views.TelaAtribuirRecJd;

/**
 *
 * @author RaphaelBispoIssa
 */
public class TelaGestaoRec extends javax.swing.JInternalFrame {

    /**
     * Creates new form TelaGestaoRec
     */
    public TelaGestaoRec() {
        initComponents();
        classes.Acessibilidade.configurarTela(this);
        configurarAtalhos();
        configurarBuscaDinamica();
        carregarTabela("");
        java.awt.EventQueue.invokeLater(() -> {
        if (classes.Acessibilidade.isTecladoAtivo()) btVoltar.requestFocusInWindow();
        acessibilidade();

        });  
        ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
    }
    
    
    
    
        private void configurarAtalhos() {
        javax.swing.InputMap inputMap = this.getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        javax.swing.ActionMap actionMap = this.getRootPane().getActionMap();

        // 1.1 Atalho para Cadastrar: Ctrl + N
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK), "cadastrar");
        actionMap.put("cadastrar", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                btAtribuir.doClick(); 
            }
        });

        // 1.3 Atalho para Excluir: Ctrl + D
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_DOWN_MASK), "excluir");
        actionMap.put("excluir", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Dispara o clique diretamente; a validação e o JOptionPane já ocorrem dentro da ação do botão
                btExcluir.doClick();
            }
        });
        
        btAtribuir.setToolTipText(btAtribuir.getToolTipText() + " (Ctrl + N)");
        btExcluir.setToolTipText(btExcluir.getToolTipText() + " (Ctrl + D)");
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
        private void acessibilidade(){
        javax.swing.InputMap mapa = tbProcesso.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Object tabOriginal = mapa.get(javax.swing.KeyStroke.getKeyStroke("TAB"));
        Object shiftOriginal = mapa.get(javax.swing.KeyStroke.getKeyStroke("shift pressed TAB"));
        
        // 1. Permite que a tabela receba o foco ao navegar com o TAB
        tbProcesso.setFocusable(true);

        // 2. Altera o comportamento do TAB dentro da tabela para APENAS passar o foco adiante
        tbProcesso.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(javax.swing.KeyStroke.getKeyStroke("TAB"), "proximoCampo");

        tbProcesso.getActionMap().put("proximoCampo", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                tbProcesso.transferFocus(); 
            }
        });

        // 3. Faz o mesmo para o Shift+TAB voltar para o campo anterior
        tbProcesso.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(javax.swing.KeyStroke.getKeyStroke("shift pressed TAB"), "campoAnterior");

        tbProcesso.getActionMap().put("campoAnterior", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                tbProcesso.transferFocusBackward(); 
            }
        });

        // 4. Controla visual (Borda de Foco) e seleção baseado no ganho/perda de foco
        javax.swing.border.Border bordaFoco = javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 120, 215), 3);
        javax.swing.border.Border bordaSpOriginal = spBarra.getBorder();

        java.awt.event.FocusAdapter controladorDeFoco = new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                java.awt.Component fonte = (java.awt.Component) e.getSource();

                // Adiciona a borda de destaque visualmente
                if (fonte == tbProcesso) {
                    spBarra.setBorder(bordaFoco);
                    if (classes.Acessibilidade.isTecladoAtivo() && tbProcesso.getRowCount() > 0 && tbProcesso.getSelectedRow() == -1) {
                        tbProcesso.setRowSelectionInterval(0, 0);
                    }
                } else if (fonte instanceof javax.swing.JButton) {
                    javax.swing.JButton btn = (javax.swing.JButton) fonte;
                    if (btn.getClientProperty("bordaOriginal") == null) {
                        btn.putClientProperty("bordaOriginal", btn.getBorder());
                    }
                    javax.swing.border.Border original = (javax.swing.border.Border) btn.getClientProperty("bordaOriginal");
                    btn.setBorder(classes.PainelGestaoRecrutadores.criarBordaFoco(btn, original));
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                java.awt.Component fonte = (java.awt.Component) e.getSource();

                // Restaura a borda original de quem perdeu o foco
                if (fonte == tbProcesso) {
                    spBarra.setBorder(bordaSpOriginal);
                } else if (fonte instanceof javax.swing.JButton) {
                    javax.swing.JButton btn = (javax.swing.JButton) fonte;
                    javax.swing.border.Border original = (javax.swing.border.Border) btn.getClientProperty("bordaOriginal");
                    if (original != null) {
                        btn.setBorder(original);
                    }
                }

                // A mudança de foco não altera a linha selecionada.
            }
        };

        tbProcesso.addFocusListener(controladorDeFoco);
        btAtribuir.addFocusListener(controladorDeFoco);
        btExcluir.addFocusListener(controladorDeFoco);
        btVoltar.addFocusListener(controladorDeFoco);

        preferenciaTeclado = e -> aplicarNavegacao(mapa, tabOriginal, shiftOriginal);
        classes.Acessibilidade.adicionarListener(preferenciaTeclado);
        aplicarNavegacao(mapa, tabOriginal, shiftOriginal);
    }
    
    private java.beans.PropertyChangeListener preferenciaTeclado;

    private void aplicarNavegacao(javax.swing.InputMap mapa, Object tabOriginal, Object shiftOriginal) {
        boolean ativo = classes.Acessibilidade.isTecladoAtivo();
        mapa.put(javax.swing.KeyStroke.getKeyStroke("TAB"), ativo ? "proximoCampo" : tabOriginal);
        mapa.put(javax.swing.KeyStroke.getKeyStroke("shift pressed TAB"), ativo ? "campoAnterior" : shiftOriginal);
        botaoAjuda.setFocusPainted(ativo || classes.Acessibilidade.isBaixaVisaoAtiva());
    }

    @Override public void dispose() {
        if (preferenciaTeclado != null) classes.Acessibilidade.removerListener(preferenciaTeclado);
        super.dispose();
    }
    
    
    
    
    
    
    
    
    
    
    
    private void carregarTabela(String termo) {

    DefaultTableModel model =
            (DefaultTableModel) tbProcesso.getModel();

    model.setRowCount(0);

    try {

        GestaoDao dao = new GestaoDao();

        List<Object[]> lista = dao.listar(termo);

        for (Object[] item : lista) {

            String nomeProcesso = (String) item[1];
            String recrutador = (String) item[2];

            if (recrutador == null || recrutador.isBlank()) {
                recrutador = "Não atribuído";
            }

            model.addRow(new Object[]{
                nomeProcesso,
                recrutador
            });
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Não foi possível carregar os processos seletivos.",
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void configurarBuscaDinamica() {

    tfPesquisa.getDocument().addDocumentListener(
            new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            carregarTabela(tfPesquisa.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            carregarTabela(tfPesquisa.getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            carregarTabela(tfPesquisa.getText());
        }
    });
}
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    
    
    
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        painelListagem = new classes.PainelListagem();
        btVoltar = new classes.BotaoPadrao();
        painelCabecalho = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        botaoAjuda = new classes.BotaoAjuda();
        lbDescricao = new javax.swing.JLabel();
        painelFerramentas = new classes.PainelFerramentas();
        painelBusca = new classes.PainelBusca();
        lbLupa = new classes.RotuloLupa();
        tfPesquisa = new classes.CampoPesquisa();
        btExcluir = new classes.BotaoPadrao();
        btAtribuir = new classes.BotaoPadrao();
        painelTabela = new classes.PainelArredondado();
        painelTituloTabela = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lbQuantidade = new classes.ContadorRegistros();
        spBarra = new javax.swing.JScrollPane();
        tbProcesso = new classes.TabelaPadrao();
        getContentPane().setLayout(new java.awt.BorderLayout(0,0));
        painelListagem.setBorder(javax.swing.BorderFactory.createEmptyBorder(28,40,40,40));
        painelListagem.setLayout(new java.awt.GridBagLayout());
        btVoltar.setFont(new java.awt.Font("Segoe UI",1,17));
        btVoltar.setText("‹  Voltar");
        btVoltar.setPreferredSize(new java.awt.Dimension(130, 48));
        btVoltar.addActionListener(this::btVoltarActionPerformed);
        painelListagem.add(btVoltar,new java.awt.GridBagConstraints(0,0,1,1,0,0.0,18,0,new java.awt.Insets(0,0,24,0),0,0));
        painelCabecalho.setOpaque(false);
        painelCabecalho.setLayout(new java.awt.BorderLayout(16,0));
        jLabel1.setBackground(new java.awt.Color(29,45,68));
        jLabel1.setText("Gestão de Recrutadores");
        jLabel1.setFont(new java.awt.Font("Segoe UI",1,36));
        jLabel1.setForeground(new java.awt.Color(0,0,0));
        painelCabecalho.add(jLabel1,java.awt.BorderLayout.CENTER);
        botaoAjuda.setTextoAjuda("Esta é a tela de Gestão de Recrutadores do sistema MAINRH, utilizada para acompanhar e vincular responsáveis aos processos seletivos da empresa. Use a barra de pesquisa no topo para filtrar um processo seletivo específico. Na tabela central, consulte a relação entre cada **Processo Seletivo** e o seu respectivo **Recrutador Alocado**. Para vincular ou alterar o responsável por uma seleção, utilize o botão **Atribuir Recrutador**, localizado no canto superior direito. Para retornar à tela anterior, clique no botão **Voltar** no canto superior esquerdo.");
        botaoAjuda.setPreferredSize(new java.awt.Dimension(42, 42));
        painelCabecalho.add(botaoAjuda,java.awt.BorderLayout.EAST);
        painelListagem.add(painelCabecalho,new java.awt.GridBagConstraints(0,1,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,4,0),0,0));
        lbDescricao.setText("Gerencie responsáveis pelos processos seletivos.");
        lbDescricao.setFont(new java.awt.Font("Segoe UI",0,19));
        lbDescricao.setForeground(new java.awt.Color(64,64,64));
        painelListagem.add(lbDescricao,new java.awt.GridBagConstraints(0,2,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,28,0),0,0));
        painelFerramentas.setLayout(new java.awt.FlowLayout(0,16,0));
        painelBusca.setLayout(new java.awt.BorderLayout(16,0));
        painelBusca.add(lbLupa,java.awt.BorderLayout.WEST);
        tfPesquisa.setPlaceholder("Buscar processo ou recrutador...");
        tfPesquisa.addFocusListener(new java.awt.event.FocusAdapter() { @Override public void focusGained(java.awt.event.FocusEvent evt) { tfPesquisaFocusGained(evt); } });
        tfPesquisa.addActionListener(this::tfPesquisaActionPerformed);
        painelBusca.add(tfPesquisa,java.awt.BorderLayout.CENTER);
        painelFerramentas.add(painelBusca);
        btExcluir.setToolTipText("Selecione um cargo da tabela e clique aqui caso deseje excluir o cargo");
        btExcluir.setFont(new java.awt.Font("Segoe UI",0,18));
        btExcluir.setText("Excluir");
        btExcluir.addActionListener(this::btExcluirActionPerformed);
        painelFerramentas.add(btExcluir);
        btAtribuir.setToolTipText("Clique aqui para atribuir um novo recrutador a um processo seletivo");
        btAtribuir.setFont(new java.awt.Font("Segoe UI",1,18));
        btAtribuir.setText("+  Atribuir Recrutador");
        btAtribuir.setPreferredSize(new java.awt.Dimension(235, 56));
        btAtribuir.addActionListener(this::btAtribuirActionPerformed);
        painelFerramentas.add(btAtribuir);
        painelListagem.add(painelFerramentas,new java.awt.GridBagConstraints(0,3,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,30,0),0,0));
        painelTabela.setMinimumSize(new java.awt.Dimension(0, 0));
        painelTabela.setLayout(new java.awt.BorderLayout(0,18));
        painelTituloTabela.setOpaque(false);
        painelTituloTabela.setLayout(new java.awt.BorderLayout(16,0));
        jLabel3.setBackground(new java.awt.Color(29,45,68));
        jLabel3.setText("Processos Seletivos");
        jLabel3.setFont(new java.awt.Font("Segoe UI",1,30));
        jLabel3.setForeground(new java.awt.Color(0,0,0));
        painelTituloTabela.add(jLabel3,java.awt.BorderLayout.WEST);
        lbQuantidade.setSingular("processo");
        lbQuantidade.setPlural("processos");
        painelTituloTabela.add(lbQuantidade,java.awt.BorderLayout.EAST);
        painelTabela.add(painelTituloTabela,java.awt.BorderLayout.NORTH);
        spBarra.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(230,235,240),1,true));
        tbProcesso.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLACK,1,true));
        tbProcesso.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{},new String[]{"Processo Seletivo","Recrutador Alocado"}) { @Override public boolean isCellEditable(int row,int column) { return false; } });
        tbProcesso.setToolTipText("Aqui aparecem os processos seletivos já cadastrados em sistema, além dos recrutadores atribuidos");
        tbProcesso.setRecrutadores(true);
        tbProcesso.addMouseListener(new java.awt.event.MouseAdapter() { @Override public void mouseClicked(java.awt.event.MouseEvent evt) { tbProcessoMouseClicked(evt); } });
        spBarra.setViewportView(tbProcesso);
        painelTabela.add(spBarra,java.awt.BorderLayout.CENTER);
        painelListagem.add(painelTabela,new java.awt.GridBagConstraints(0,4,1,1,1.0,1,18,1,new java.awt.Insets(0,0,0,0),0,0));
        getContentPane().add(painelListagem,java.awt.BorderLayout.CENTER);
        lbQuantidade.setTabela(tbProcesso);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfPesquisaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfPesquisaFocusGained

        tfPesquisa.setForeground(java.awt.Color.BLACK);

    }//GEN-LAST:event_tfPesquisaFocusGained

    private void tfPesquisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPesquisaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPesquisaActionPerformed

    private void tbProcessoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProcessoMouseClicked
    
    }//GEN-LAST:event_tbProcessoMouseClicked

    private void btAtribuirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAtribuirActionPerformed

    int linhaSelecionada = tbProcesso.getSelectedRow();

    if (linhaSelecionada == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Selecione um processo seletivo na tabela.",
                "Nenhum processo selecionado",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {

        GestaoDao dao = new GestaoDao();

        List<Object[]> lista = dao.listar(tfPesquisa.getText());

        int idProcesso =
                (int) lista.get(linhaSelecionada)[0];

        TelaAtribuirRecJd atribuir =
                new TelaAtribuirRecJd(
                        null,
                        true,
                        idProcesso
                );

        atribuir.setVisible(true);

        // Atualiza a tabela depois de fechar a tela
        carregarTabela(tfPesquisa.getText());

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Não foi possível abrir o processo selecionado.",
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
    }//GEN-LAST:event_btAtribuirActionPerformed

    private void btVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btVoltarActionPerformed
        this.dispose();         // TODO add your handling code here:
    }//GEN-LAST:event_btVoltarActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
    int linhaSelecionada = tbProcesso.getSelectedRow();

    if (linhaSelecionada == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Selecione um processo seletivo na tabela.",
                "Nenhum processo selecionado",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // Verifica se já está sem recrutador
    String recrutador = tbProcesso.getValueAt(linhaSelecionada, 1).toString();

    if (recrutador.equals("Não atribuído")) {
        JOptionPane.showMessageDialog(
                this,
                "Este processo não possui um recrutador atribuído.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Deseja remover o recrutador deste processo seletivo?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
    );

    if (confirmacao != JOptionPane.YES_OPTION) {
        return;
    }

    try {

        GestaoDao dao = new GestaoDao();

        // Pega novamente a lista para descobrir o ID do processo
        List<Object[]> lista = dao.listar(tfPesquisa.getText());

        int idProcesso = (int) lista.get(linhaSelecionada)[0];

        boolean excluiu = dao.excluirRecrutador(idProcesso);

        if (excluiu) {

            JOptionPane.showMessageDialog(
                    this,
                    "Recrutador removido do processo com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarTabela(tfPesquisa.getText());

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível remover o recrutador.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Não foi possível remover o recrutador.",
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
    }//GEN-LAST:event_btExcluirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private classes.BotaoAjuda botaoAjuda;
    private classes.BotaoPadrao btAtribuir;
    private classes.BotaoPadrao btExcluir;
    private classes.BotaoPadrao btVoltar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lbDescricao;
    private classes.RotuloLupa lbLupa;
    private classes.ContadorRegistros lbQuantidade;
    private classes.PainelBusca painelBusca;
    private javax.swing.JPanel painelCabecalho;
    private classes.PainelFerramentas painelFerramentas;
    private classes.PainelListagem painelListagem;
    private classes.PainelArredondado painelTabela;
    private javax.swing.JPanel painelTituloTabela;
    private javax.swing.JScrollPane spBarra;
    private classes.TabelaPadrao tbProcesso;
    private classes.CampoPesquisa tfPesquisa;
    //GEN-END:variables
}
