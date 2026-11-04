/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package views;
import classes.Usuario;
import database.GestaoDao;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import classes.ProcessoSeletivo;
/**
 *
 * @author rapha
 */
public class TelaAtribuirRecJd extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaAtribuirRecJd.class.getName());
    private int idProcesso;
    /**
     * Creates new form TelaAtribuirRecJd
     */
    public TelaAtribuirRecJd(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setUndecorated(false);
        initComponents();
        classes.Acessibilidade.configurarTela(this);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        customizarTabela();
        configurarBuscaDinamica();
        carregarTabela("");
        
        java.awt.EventQueue.invokeLater(() -> {
        if (classes.Acessibilidade.isTecladoAtivo()) btCancelar.requestFocusInWindow();
        acessibilidade();

        });
        
    }
    
    public TelaAtribuirRecJd(
        java.awt.Frame parent,
        boolean modal,
        int idProcesso) {

    this(parent, modal);

    this.idProcesso = idProcesso;

    carregarProcesso();
    }
    
    
    
    
    
        private void acessibilidade(){
        javax.swing.InputMap mapa = tbRecrutador.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Object tabOriginal = mapa.get(javax.swing.KeyStroke.getKeyStroke("TAB"));
        Object shiftOriginal = mapa.get(javax.swing.KeyStroke.getKeyStroke("shift pressed TAB"));
        
        // 1. Permite que a tabela receba o foco ao navegar com o TAB
        tbRecrutador.setFocusable(true);

        // 2. Altera o comportamento do TAB dentro da tabela para APENAS passar o foco adiante
        tbRecrutador.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(javax.swing.KeyStroke.getKeyStroke("TAB"), "proximoCampo");

        tbRecrutador.getActionMap().put("proximoCampo", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                tbRecrutador.transferFocus(); 
            }
        });

        // 3. Faz o mesmo para o Shift+TAB voltar para o campo anterior
        tbRecrutador.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(javax.swing.KeyStroke.getKeyStroke("shift pressed TAB"), "campoAnterior");

        tbRecrutador.getActionMap().put("campoAnterior", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                tbRecrutador.transferFocusBackward(); 
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
                if (fonte == tbRecrutador) {
                    spBarra.setBorder(bordaFoco);
                    if (classes.Acessibilidade.isTecladoAtivo() && tbRecrutador.getRowCount() > 0 && tbRecrutador.getSelectedRow() == -1) {
                        tbRecrutador.setRowSelectionInterval(0, 0);
                    }
                } else if (fonte instanceof javax.swing.JButton) {
                    javax.swing.JButton btn = (javax.swing.JButton) fonte;
                    if (btn.getClientProperty("bordaOriginal") == null) {
                        btn.putClientProperty("bordaOriginal", btn.getBorder());
                    }
                    btn.setBorder(classes.PainelListagem.criarBordaFoco(btn, (javax.swing.border.Border) btn.getClientProperty("bordaOriginal")));
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                java.awt.Component fonte = (java.awt.Component) e.getSource();

                // Restaura a borda original de quem perdeu o foco
                if (fonte == tbRecrutador) {
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

        tbRecrutador.addFocusListener(controladorDeFoco);
        
        btCancelar.addFocusListener(controladorDeFoco);
        
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
    
    
    
    
    
    private void carregarProcesso() {

    try {

        GestaoDao dao = new GestaoDao();

        ProcessoSeletivo processo =
                dao.buscarProcessoPorId(idProcesso);

        if (processo != null) {

            lbProcesso.setText(
                    processo.getNomeProcesso()
            );

            if (processo.getDataInicio() != null) {

                java.time.format.DateTimeFormatter formato =
                        java.time.format.DateTimeFormatter
                                .ofPattern("dd/MM/yyyy");

                lbData.setText(
                        processo.getDataInicio().format(formato)
                );

            } else {

                lbData.setText("-");
            }
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Não foi possível carregar os dados do processo.",
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
    
    private void carregarTabela(String termo) {

    DefaultTableModel model =
            (DefaultTableModel) tbRecrutador.getModel();

    model.setRowCount(0);

    try {

        GestaoDao dao = new GestaoDao();

        List<Usuario> recrutadores =
                dao.listarRecrutadores(termo);

        for (Usuario usuario : recrutadores) {

            model.addRow(new Object[]{
                usuario.getNome(),
                usuario.getEmail()
            });
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Não foi possível carregar os recrutadores.",
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

    
    
        private void customizarTabela() {
        classes.PainelListagem.configurarTabela(tbRecrutador, spBarra, false);
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
        btCancelar = new classes.BotaoPadrao();
        painelCabecalho = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        botaoAjuda = new classes.BotaoAjuda();
        lbDescricao = new javax.swing.JLabel();
        painelInformacoes = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        lbProcesso = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lbData = new javax.swing.JLabel();
        painelFerramentas = new classes.PainelFerramentas();
        painelBusca = new classes.PainelBusca();
        lbLupa = new classes.RotuloLupa();
        tfPesquisa = new classes.CampoPesquisa();
        btAtribuir = new classes.BotaoPadrao();
        painelTabela = new classes.PainelArredondado();
        painelTituloTabela = new javax.swing.JPanel();
        lbTituloTabela = new javax.swing.JLabel();
        lbQuantidade = new classes.ContadorRegistros();
        spBarra = new javax.swing.JScrollPane();
        tbRecrutador = new classes.TabelaPadrao();
        setDefaultCloseOperation(2);
        getContentPane().setLayout(new java.awt.BorderLayout(0,0));
        painelListagem.setSubtela(true);
        painelListagem.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(31,53,80),2,false),javax.swing.BorderFactory.createEmptyBorder(28,40,40,40)));
        painelListagem.setLayout(new java.awt.GridBagLayout());
        btCancelar.setFont(new java.awt.Font("Segoe UI",1,17));
        btCancelar.setText("‹  Voltar");
        btCancelar.setPreferredSize(new java.awt.Dimension(130, 48));
        btCancelar.addMouseListener(new java.awt.event.MouseAdapter() { @Override public void mouseClicked(java.awt.event.MouseEvent evt) { btCancelarMouseClicked(evt); } });
        btCancelar.addActionListener(this::btCancelarActionPerformed);
        painelListagem.add(btCancelar,new java.awt.GridBagConstraints(0,0,1,1,0,0.0,18,0,new java.awt.Insets(0,0,24,0),0,0));
        painelCabecalho.setOpaque(false);
        painelCabecalho.setLayout(new java.awt.BorderLayout(16,0));
        jLabel9.setText("Atribuir Recrutador");
        jLabel9.setFont(new java.awt.Font("Segoe UI",1,36));
        jLabel9.setForeground(new java.awt.Color(0,0,0));
        painelCabecalho.add(jLabel9,java.awt.BorderLayout.CENTER);
        botaoAjuda.setTextoAjuda("Esta é a tela de Selecionar Recrutador do sistema MAINRH, dedicada à vinculação de um responsável a um processo seletivo específico. No topo, confirme as informações do **Processo** e da **Data** correspondentes. Utilize a barra de busca para pesquisar um recrutador cadastrado pelo nome ou e-mail. Na tabela inferior, selecione o profissional desejado da lista contendo **Nome** e **E-mail** e clique no botão **Atribuir** para concluir a vinculação, ou no botão **Cancelar** para fechar a janela sem salvar.");
        botaoAjuda.setPreferredSize(new java.awt.Dimension(42, 42));
        painelCabecalho.add(botaoAjuda,java.awt.BorderLayout.EAST);
        painelListagem.add(painelCabecalho,new java.awt.GridBagConstraints(0,1,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,4,0),0,0));
        lbDescricao.setText("Escolha um recrutador cadastrado para este processo seletivo.");
        lbDescricao.setFont(new java.awt.Font("Segoe UI",0,19));
        lbDescricao.setForeground(new java.awt.Color(64,64,64));
        painelListagem.add(lbDescricao,new java.awt.GridBagConstraints(0,2,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,28,0),0,0));
        painelInformacoes.setOpaque(false);
        painelInformacoes.setLayout(new java.awt.FlowLayout(0,16,0));
        jLabel11.setForeground(new java.awt.Color(29,45,68));
        jLabel11.setText("Processo:");
        jLabel11.setFont(new java.awt.Font("Segoe UI",0,18));
        painelInformacoes.add(jLabel11);
        lbProcesso.setText("Desenvolvedor Java");
        lbProcesso.setFont(new java.awt.Font("Segoe UI",0,18));
        painelInformacoes.add(lbProcesso);
        jLabel12.setForeground(new java.awt.Color(29,45,68));
        jLabel12.setText("Data:");
        jLabel12.setFont(new java.awt.Font("Segoe UI",0,18));
        painelInformacoes.add(jLabel12);
        lbData.setText("10/10/2010");
        lbData.setFont(new java.awt.Font("Segoe UI",0,18));
        painelInformacoes.add(lbData);
        painelListagem.add(painelInformacoes,new java.awt.GridBagConstraints(0,3,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,20,0),0,0));
        painelFerramentas.setLayout(new java.awt.FlowLayout(0,16,0));
        painelBusca.setLayout(new java.awt.BorderLayout(16,0));
        painelBusca.add(lbLupa,java.awt.BorderLayout.WEST);
        tfPesquisa.setPlaceholder("Buscar recrutador...");
        painelBusca.add(tfPesquisa,java.awt.BorderLayout.CENTER);
        painelFerramentas.add(painelBusca);
        btAtribuir.setMargin(new java.awt.Insets(10, 20, 10, 20));
        btAtribuir.setFont(new java.awt.Font("Segoe UI",1,18));
        btAtribuir.setText("Atribuir");
        btAtribuir.setPreferredSize(new java.awt.Dimension(235, 56));
        btAtribuir.addActionListener(this::btAtribuirActionPerformed);
        painelFerramentas.add(btAtribuir);
        painelListagem.add(painelFerramentas,new java.awt.GridBagConstraints(0,4,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,30,0),0,0));
        painelTabela.setMinimumSize(new java.awt.Dimension(0, 0));
        painelTabela.setLayout(new java.awt.BorderLayout(0,18));
        painelTituloTabela.setOpaque(false);
        painelTituloTabela.setLayout(new java.awt.BorderLayout(16,0));
        lbTituloTabela.setText("Recrutadores");
        lbTituloTabela.setFont(new java.awt.Font("Segoe UI",1,30));
        lbTituloTabela.setForeground(new java.awt.Color(0,0,0));
        painelTituloTabela.add(lbTituloTabela,java.awt.BorderLayout.WEST);
        lbQuantidade.setSingular("recrutador");
        lbQuantidade.setPlural("recrutadores");
        painelTituloTabela.add(lbQuantidade,java.awt.BorderLayout.EAST);
        painelTabela.add(painelTituloTabela,java.awt.BorderLayout.NORTH);
        spBarra.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(230,235,240),1,true));
        tbRecrutador.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLACK,1,true));
        tbRecrutador.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{},new String[]{"Nome","E-mail"}) { @Override public boolean isCellEditable(int row,int column) { return false; } });
        tbRecrutador.setToolTipText("Aqui aparecem os processos seletivos já cadastrados em sistema, além dos recrutadores atribuidos");
        tbRecrutador.addMouseListener(new java.awt.event.MouseAdapter() { @Override public void mouseClicked(java.awt.event.MouseEvent evt) { tbRecrutadorMouseClicked(evt); } });
        spBarra.setViewportView(tbRecrutador);
        painelTabela.add(spBarra,java.awt.BorderLayout.CENTER);
        painelListagem.add(painelTabela,new java.awt.GridBagConstraints(0,5,1,1,1.0,1,18,1,new java.awt.Insets(0,0,0,0),0,0));
        getContentPane().add(painelListagem,java.awt.BorderLayout.CENTER);
        lbQuantidade.setTabela(tbRecrutador);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbRecrutadorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbRecrutadorMouseClicked

    }//GEN-LAST:event_tbRecrutadorMouseClicked

    private void btAtribuirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAtribuirActionPerformed

    int linhaSelecionada = tbRecrutador.getSelectedRow();

    if (linhaSelecionada == -1) {

        JOptionPane.showMessageDialog(
                this,
                "Selecione um recrutador na tabela.",
                "Nenhum recrutador selecionado",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    try {

        GestaoDao dao = new GestaoDao();

        List<Usuario> recrutadores =
                dao.listarRecrutadores(
                        tfPesquisa.getText()
                );

        Usuario recrutadorSelecionado =
                recrutadores.get(linhaSelecionada);

        // Verifica se o recrutador selecionado já está atribuído
        if (dao.mesmoRecrutador(
                idProcesso,
                recrutadorSelecionado.getIdUsuario())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Este recrutador já está atribuído a este processo seletivo.",
                    "Recrutador já atribuído",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        boolean atribuiu =
                dao.atribuirRecrutador(
                        idProcesso,
                        recrutadorSelecionado.getIdUsuario()
                );

        if (atribuiu) {

            JOptionPane.showMessageDialog(
                    this,
                    "Recrutador atribuído com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível atribuir o recrutador.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    } catch (SQLException e) {

        JOptionPane.showMessageDialog(
                this,
                "Não foi possível atribuir o recrutador.",
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
    }//GEN-LAST:event_btAtribuirActionPerformed

    private void btCancelarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btCancelarMouseClicked

    }//GEN-LAST:event_btCancelarMouseClicked

    private void btCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btCancelarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaAtribuirRecJd dialog = new TelaAtribuirRecJd(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private classes.BotaoAjuda botaoAjuda;
    private classes.BotaoPadrao btAtribuir;
    private classes.BotaoPadrao btCancelar;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lbData;
    private javax.swing.JLabel lbDescricao;
    private classes.RotuloLupa lbLupa;
    private javax.swing.JLabel lbProcesso;
    private classes.ContadorRegistros lbQuantidade;
    private javax.swing.JLabel lbTituloTabela;
    private classes.PainelBusca painelBusca;
    private javax.swing.JPanel painelCabecalho;
    private classes.PainelFerramentas painelFerramentas;
    private javax.swing.JPanel painelInformacoes;
    private classes.PainelListagem painelListagem;
    private classes.PainelArredondado painelTabela;
    private javax.swing.JPanel painelTituloTabela;
    private javax.swing.JScrollPane spBarra;
    private classes.TabelaPadrao tbRecrutador;
    private classes.CampoPesquisa tfPesquisa;
    //GEN-END:variables
}
