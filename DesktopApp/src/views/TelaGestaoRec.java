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
        setContentPane(new classes.PainelGestaoRecrutadores(btVoltar, jLabel1, botaoAjuda,
                tfPesquisa, jLabel2, btExcluir, btAtribuir, jLabel3, spBarra, tbProcesso));
        pack();
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

        painelDesktopPane = new javax.swing.JDesktopPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tfPesquisa = new javax.swing.JTextField();
        tfPesquisa.setText("Digite aqui...");
        tfPesquisa.setForeground(java.awt.Color.GRAY);
        jLabel1 = new javax.swing.JLabel();
        botaoAjuda = new classes.BotaoAjuda();
        btAtribuir = new javax.swing.JButton();
        btVoltar = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        spBarra = new javax.swing.JScrollPane();
        tbProcesso = new javax.swing.JTable();

        painelDesktopPane.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(233, 243, 255));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel5.setBackground(new java.awt.Color(233, 243, 255));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(233, 243, 255));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lupa.png"))); // NOI18N

        tfPesquisa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfPesquisaFocusGained(evt);
            }
        });
        tfPesquisa.addActionListener(this::tfPesquisaActionPerformed);

        jLabel1.setBackground(new java.awt.Color(29, 45, 68));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 35)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(29, 45, 68));
        jLabel1.setText("Gestão de Recrutadores");

        botaoAjuda.setTextoAjuda("Esta é a tela de Gestão de Recrutadores do sistema MAINRH, utilizada para acompanhar e vincular responsáveis aos processos seletivos da empresa. Use a barra de pesquisa no topo para filtrar um processo seletivo específico. Na tabela central, consulte a relação entre cada **Processo Seletivo** e o seu respectivo **Recrutador Alocado**. Para vincular ou alterar o responsável por uma seleção, utilize o botão **Atribuir Recrutador**, localizado no canto superior direito. Para retornar à tela anterior, clique no botão **Voltar** no canto superior esquerdo.");

        btAtribuir.setBackground(new java.awt.Color(31, 53, 80));
        btAtribuir.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btAtribuir.setForeground(new java.awt.Color(255, 255, 255));
        btAtribuir.setText("Atribuir Recrutador");
        btAtribuir.setToolTipText("Clique aqui para atribuir um novo recrutador a um processo seletivo");
        btAtribuir.addActionListener(this::btAtribuirActionPerformed);

        btVoltar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btVoltar.setForeground(new java.awt.Color(31, 53, 80));
        btVoltar.setText("◄ Voltar");
        btVoltar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(31, 53, 80), 1, true));
        btVoltar.addActionListener(this::btVoltarActionPerformed);

        btExcluir.setBackground(new java.awt.Color(192, 1, 1));
        btExcluir.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btExcluir.setForeground(new java.awt.Color(255, 255, 255));
        btExcluir.setText("Excluir");
        btExcluir.setToolTipText("Selecione um cargo da tabela e clique aqui caso deseje excluir o cargo");
        btExcluir.addActionListener(this::btExcluirActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(688, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(440, 440, 440)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tfPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 276, Short.MAX_VALUE)
                        .addComponent(btExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btAtribuir)
                        .addGap(59, 59, 59))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(botaoAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(btVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(botaoAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tfPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btAtribuir, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel4.setBackground(new java.awt.Color(233, 243, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 32, 20, 32));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 1, 32));

        jLabel3.setBackground(new java.awt.Color(29, 45, 68));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 22)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(29, 45, 68));
        jLabel3.setText("Processos Seletivos");

        spBarra.setBackground(new java.awt.Color(255, 255, 255));
        spBarra.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        spBarra.setForeground(new java.awt.Color(255, 255, 255));

        tbProcesso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        tbProcesso.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tbProcesso.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Processo Seletivo", "Recrutador Alocado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbProcesso.setToolTipText("Aqui aparecem os processos seletivos já cadastrados em sistema, além dos recrutadores atribuidos");
        tbProcesso.getTableHeader().setReorderingAllowed(false);
        tbProcesso.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProcessoMouseClicked(evt);
            }
        });
        spBarra.setViewportView(tbProcesso);
        if (tbProcesso.getColumnModel().getColumnCount() > 0) {
            tbProcesso.getColumnModel().getColumn(0).setResizable(false);
            tbProcesso.getColumnModel().getColumn(1).setResizable(false);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(spBarra, javax.swing.GroupLayout.DEFAULT_SIZE, 975, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(spBarra, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel4, java.awt.BorderLayout.CENTER);

        painelDesktopPane.add(jPanel3, java.awt.BorderLayout.CENTER);

        getContentPane().add(painelDesktopPane, java.awt.BorderLayout.CENTER);

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
    private javax.swing.JButton btAtribuir;
    private javax.swing.JButton btExcluir;
    private javax.swing.JButton btVoltar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JDesktopPane painelDesktopPane;
    private javax.swing.JScrollPane spBarra;
    private javax.swing.JTable tbProcesso;
    private javax.swing.JTextField tfPesquisa;
    // End of variables declaration//GEN-END:variables
}
