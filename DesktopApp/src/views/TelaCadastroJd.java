/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package views;

import classes.Cargo;
import database.CargoDao;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

/**
 *
 * @author rapha
 */
public class TelaCadastroJd extends javax.swing.JDialog {

    private static final String TEXTO_PLACEHOLDER = "Digite aqui...";
    private static final Color COR_PLACEHOLDER = new Color(153, 153, 153);
    private static final Color COR_TEXTO = Color.BLACK;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaCadastroJd.class.getName());
    private boolean cadastroRealizado;

    /**
     * Creates new form TelaCadastroJd
     */
    public TelaCadastroJd(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setUndecorated(false);
        initComponents();
        classes.Acessibilidade.configurarTela(this);
        configurarPlaceholders();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

    public boolean isCadastroRealizado() {
        return cadastroRealizado;
    }

    private void configurarPlaceholders() {
        configurarPlaceholder(tfNomeCargo);
        configurarPlaceholder(ffSalario);
        configurarPlaceholder(tfNivel);
        configurarPlaceholder(tfSetor);
        configurarPlaceholder(taRequisitos);
        configurarPlaceholder(taAtividade);
    }

    private void configurarPlaceholder(JTextComponent campo) {
        campo.setText(TEXTO_PLACEHOLDER);
        campo.setForeground(COR_PLACEHOLDER);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (TEXTO_PLACEHOLDER.equals(campo.getText())) {
                    campo.setText("");
                }
                campo.setForeground(COR_TEXTO);
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (campo.getText().trim().isEmpty()) {
                    campo.setText(TEXTO_PLACEHOLDER);
                    campo.setForeground(COR_PLACEHOLDER);
                } else {
                    campo.setForeground(COR_TEXTO);
                }
            }
        });
    }

    private Cargo obterCargoDoFormulario() {
        String nome = obterTexto(tfNomeCargo.getText().toUpperCase());
        String nivel = obterTexto(tfNivel.getText().toUpperCase());
        String setor = obterTexto(tfSetor.getText().toUpperCase());
        String requisitos = obterTexto(taRequisitos.getText().toUpperCase());
        String atividades = obterTexto(taAtividade.getText().toUpperCase());
        String salarioTexto = obterTexto(ffSalario.getText());

        if (nome.isEmpty() || nivel.isEmpty() || setor.isEmpty()
                || requisitos.isEmpty() || atividades.isEmpty()
                || salarioTexto.isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos do cargo.");
        }

        validarTamanho(nome, "Nome do cargo");
        validarTamanho(nivel, "Nível");
        validarTamanho(setor, "Setor");

        BigDecimal salario = converterSalario(salarioTexto);
        if (salario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O salário deve ser maior que zero.");
        }
        if (salario.scale() > 2
                || salario.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException(
                    "O salário deve ter no máximo 8 dígitos inteiros e 2 casas decimais.");
        }

        return new Cargo(nome, salario, requisitos, nivel, setor, atividades);
    }

    private void validarTamanho(String valor, String campo) {
        if (valor.length() > 100) {
            throw new IllegalArgumentException(
                    campo + " deve ter no máximo 100 caracteres.");
        }
    }

    private String obterTexto(String texto) {
        String valor = texto == null ? "" : texto.trim();
        return TEXTO_PLACEHOLDER.equalsIgnoreCase(valor) ? "" : valor;
    }

    private BigDecimal converterSalario(String texto) {
        String valor = texto.trim()
                .replace("R$", "")
                .replace(" ", "");

        if (valor.contains(",")) {
            valor = valor.replace(".", "").replace(',', '.');
        }

        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Informe um salário válido. Exemplo: 3500,00.");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        painelFormulario = new classes.PainelFormulario();
        btCancelar = new classes.BotaoPadrao();
        painelCabecalho = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        botaoAjuda = new classes.BotaoAjuda();
        lbDescricao = new javax.swing.JLabel();
        painelCampos = new classes.PainelArredondado();
        gradeCampos = new classes.PainelCampos();
        painelCampo1 = new javax.swing.JPanel();
        painelRotulo1 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        tfNomeCargo = new javax.swing.JTextField();
        painelCampo2 = new javax.swing.JPanel();
        painelRotulo2 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        ffSalario = new javax.swing.JFormattedTextField();
        painelCampo3 = new javax.swing.JPanel();
        painelRotulo3 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        tfNivel = new javax.swing.JTextField();
        painelCampo4 = new javax.swing.JPanel();
        painelRotulo4 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        tfSetor = new javax.swing.JTextField();
        painelCampo5 = new javax.swing.JPanel();
        painelRotulo5 = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        taRequisitos = new javax.swing.JTextArea();
        painelCampo6 = new javax.swing.JPanel();
        painelRotulo6 = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        taAtividade = new javax.swing.JTextArea();
        painelAcoes = new classes.PainelFerramentas();
        btCadastrarCargo = new classes.BotaoPadrao();
        setDefaultCloseOperation(2);
        getContentPane().setLayout(new java.awt.BorderLayout(0,0));
        painelFormulario.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(31,53,80),2,false),javax.swing.BorderFactory.createEmptyBorder(24,40,40,40)));
        painelFormulario.setPreferredSize(new java.awt.Dimension(1040, 719));
        painelFormulario.setLayout(new java.awt.GridBagLayout());
        btCancelar.setFont(new java.awt.Font("Segoe UI",1,17));
        btCancelar.setText("‹  Voltar");
        btCancelar.setPreferredSize(new java.awt.Dimension(130, 48));
        btCancelar.addMouseListener(new java.awt.event.MouseAdapter() { @Override public void mouseClicked(java.awt.event.MouseEvent evt) { btCancelarMouseClicked(evt); } });
        btCancelar.addActionListener(this::btCancelarActionPerformed);
        painelFormulario.add(btCancelar,new java.awt.GridBagConstraints(0,0,1,1,0,0.0,18,0,new java.awt.Insets(0,0,20,0),0,0));
        painelCabecalho.setOpaque(false);
        painelCabecalho.setLayout(new java.awt.BorderLayout(16,0));
        jLabel40.setText("Cadastrar Cargo");
        jLabel40.setFont(new java.awt.Font("Segoe UI",1,32));
        jLabel40.setForeground(new java.awt.Color(0,0,0));
        painelCabecalho.add(jLabel40,java.awt.BorderLayout.CENTER);
        botaoAjuda.setTextoAjuda("Esta é a tela de cadastramento de cargos do sistema MAINRH, utilizada para registrar novas posições profissionais na empresa. Para realizar o cadastro, preencha todos os campos obrigatórios indicados com asterisco: informe o **Nome do cargo**, o **Salário base**, o **Nível** hierárquico, o **Setor** correspondente, além de descrever os **Requisitos** necessários para a vaga e as **Atividades** que serão desempenhadas pela função. Após inserir todas as informações, clique no botão **Cadastrar Cargo** para salvar os dados ou no botão **Cancelar** para descartar a operação.");
        botaoAjuda.setPreferredSize(new java.awt.Dimension(42, 42));
        painelCabecalho.add(botaoAjuda,java.awt.BorderLayout.EAST);
        painelFormulario.add(painelCabecalho,new java.awt.GridBagConstraints(0,1,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,4,0),0,0));
        lbDescricao.setText("Preencha os dados do cargo. Campos com * são obrigatórios.");
        lbDescricao.setFont(new java.awt.Font("Segoe UI",0,18));
        lbDescricao.setForeground(new java.awt.Color(64,64,64));
        painelFormulario.add(lbDescricao,new java.awt.GridBagConstraints(0,2,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,24,0),0,0));
        painelCampos.setLayout(new java.awt.BorderLayout(0,0));
        gradeCampos.setLayout(new java.awt.GridBagLayout());
        painelCampo1.setOpaque(false);
        painelCampo1.setLayout(new java.awt.BorderLayout(0,10));
        painelRotulo1.setOpaque(false);
        painelRotulo1.setLayout(new java.awt.FlowLayout(0,0,0));
        jLabel41.setText("Nome do cargo");
        jLabel41.setFont(new java.awt.Font("Segoe UI",1,18));
        jLabel41.setForeground(new java.awt.Color(0,0,0));
        painelRotulo1.add(jLabel41);
        jLabel42.setForeground(new java.awt.Color(204,0,0));
        jLabel42.setText("*");
        jLabel42.setFont(new java.awt.Font("Segoe UI",1,18));
        painelRotulo1.add(jLabel42);
        painelCampo1.add(painelRotulo1,java.awt.BorderLayout.NORTH);
        tfNomeCargo.setFont(new java.awt.Font("Segoe UI",0,18));
        tfNomeCargo.setBackground(new java.awt.Color(255,255,255));
        tfNomeCargo.setPreferredSize(new java.awt.Dimension(320, 52));
        tfNomeCargo.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(153,153,153),1,false),javax.swing.BorderFactory.createEmptyBorder(8,12,8,12)));
        tfNomeCargo.addActionListener(this::tfNomeCargojTextField2ActionPerformed);
        painelCampo1.add(tfNomeCargo,java.awt.BorderLayout.CENTER);
        gradeCampos.add(painelCampo1,new java.awt.GridBagConstraints(0,0,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,20,14),0,0));
        painelCampo2.setOpaque(false);
        painelCampo2.setLayout(new java.awt.BorderLayout(0,10));
        painelRotulo2.setOpaque(false);
        painelRotulo2.setLayout(new java.awt.FlowLayout(0,0,0));
        jLabel43.setText("Salário base");
        jLabel43.setFont(new java.awt.Font("Segoe UI",1,18));
        jLabel43.setForeground(new java.awt.Color(0,0,0));
        painelRotulo2.add(jLabel43);
        jLabel44.setForeground(new java.awt.Color(204,0,0));
        jLabel44.setText("*");
        jLabel44.setFont(new java.awt.Font("Segoe UI",1,18));
        painelRotulo2.add(jLabel44);
        painelCampo2.add(painelRotulo2,java.awt.BorderLayout.NORTH);
        ffSalario.setFont(new java.awt.Font("Segoe UI",0,18));
        ffSalario.setBackground(new java.awt.Color(255,255,255));
        ffSalario.setPreferredSize(new java.awt.Dimension(320, 52));
        ffSalario.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(153,153,153),1,false),javax.swing.BorderFactory.createEmptyBorder(8,12,8,12)));
        painelCampo2.add(ffSalario,java.awt.BorderLayout.CENTER);
        gradeCampos.add(painelCampo2,new java.awt.GridBagConstraints(1,0,1,1,1.0,0.0,18,2,new java.awt.Insets(0,14,20,0),0,0));
        painelCampo3.setOpaque(false);
        painelCampo3.setLayout(new java.awt.BorderLayout(0,10));
        painelRotulo3.setOpaque(false);
        painelRotulo3.setLayout(new java.awt.FlowLayout(0,0,0));
        jLabel45.setText("Nível");
        jLabel45.setFont(new java.awt.Font("Segoe UI",1,18));
        jLabel45.setForeground(new java.awt.Color(0,0,0));
        painelRotulo3.add(jLabel45);
        jLabel46.setForeground(new java.awt.Color(204,0,0));
        jLabel46.setText("*");
        jLabel46.setFont(new java.awt.Font("Segoe UI",1,18));
        painelRotulo3.add(jLabel46);
        painelCampo3.add(painelRotulo3,java.awt.BorderLayout.NORTH);
        tfNivel.setFont(new java.awt.Font("Segoe UI",0,18));
        tfNivel.setBackground(new java.awt.Color(255,255,255));
        tfNivel.setPreferredSize(new java.awt.Dimension(320, 52));
        tfNivel.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(153,153,153),1,false),javax.swing.BorderFactory.createEmptyBorder(8,12,8,12)));
        painelCampo3.add(tfNivel,java.awt.BorderLayout.CENTER);
        gradeCampos.add(painelCampo3,new java.awt.GridBagConstraints(0,1,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,20,14),0,0));
        painelCampo4.setOpaque(false);
        painelCampo4.setLayout(new java.awt.BorderLayout(0,10));
        painelRotulo4.setOpaque(false);
        painelRotulo4.setLayout(new java.awt.FlowLayout(0,0,0));
        jLabel47.setText("Setor");
        jLabel47.setFont(new java.awt.Font("Segoe UI",1,18));
        jLabel47.setForeground(new java.awt.Color(0,0,0));
        painelRotulo4.add(jLabel47);
        jLabel48.setForeground(new java.awt.Color(204,0,0));
        jLabel48.setText("*");
        jLabel48.setFont(new java.awt.Font("Segoe UI",1,18));
        painelRotulo4.add(jLabel48);
        painelCampo4.add(painelRotulo4,java.awt.BorderLayout.NORTH);
        tfSetor.setFont(new java.awt.Font("Segoe UI",0,18));
        tfSetor.setBackground(new java.awt.Color(255,255,255));
        tfSetor.setPreferredSize(new java.awt.Dimension(320, 52));
        tfSetor.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(153,153,153),1,false),javax.swing.BorderFactory.createEmptyBorder(8,12,8,12)));
        tfSetor.addActionListener(this::tfSetorjTextField4ActionPerformed);
        painelCampo4.add(tfSetor,java.awt.BorderLayout.CENTER);
        gradeCampos.add(painelCampo4,new java.awt.GridBagConstraints(1,1,1,1,1.0,0.0,18,2,new java.awt.Insets(0,14,20,0),0,0));
        painelCampo5.setOpaque(false);
        painelCampo5.setLayout(new java.awt.BorderLayout(0,10));
        painelRotulo5.setOpaque(false);
        painelRotulo5.setLayout(new java.awt.FlowLayout(0,0,0));
        jLabel49.setText("Requisitos");
        jLabel49.setFont(new java.awt.Font("Segoe UI",1,18));
        jLabel49.setForeground(new java.awt.Color(0,0,0));
        painelRotulo5.add(jLabel49);
        jLabel50.setForeground(new java.awt.Color(204,0,0));
        jLabel50.setText("*");
        jLabel50.setFont(new java.awt.Font("Segoe UI",1,18));
        painelRotulo5.add(jLabel50);
        painelCampo5.add(painelRotulo5,java.awt.BorderLayout.NORTH);
        jScrollPane7.setFont(new java.awt.Font("Segoe UI",0,18));
        jScrollPane7.setBackground(new java.awt.Color(255,255,255));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(320, 110));
        taRequisitos.setColumns(20);
        taRequisitos.setRows(5);
        taRequisitos.setBorder(null);
        taRequisitos.setFont(new java.awt.Font("Segoe UI",0,18));
        taRequisitos.setBackground(new java.awt.Color(255,255,255));
        taRequisitos.setMargin(new java.awt.Insets(12, 14, 12, 14));
        jScrollPane7.setViewportView(taRequisitos);
        painelCampo5.add(jScrollPane7,java.awt.BorderLayout.CENTER);
        gradeCampos.add(painelCampo5,new java.awt.GridBagConstraints(0,2,1,1,1.0,0.0,18,2,new java.awt.Insets(0,0,0,14),0,0));
        painelCampo6.setOpaque(false);
        painelCampo6.setLayout(new java.awt.BorderLayout(0,10));
        painelRotulo6.setOpaque(false);
        painelRotulo6.setLayout(new java.awt.FlowLayout(0,0,0));
        jLabel51.setText("Atividades");
        jLabel51.setFont(new java.awt.Font("Segoe UI",1,18));
        jLabel51.setForeground(new java.awt.Color(0,0,0));
        painelRotulo6.add(jLabel51);
        jLabel52.setForeground(new java.awt.Color(204,0,0));
        jLabel52.setText("*");
        jLabel52.setFont(new java.awt.Font("Segoe UI",1,18));
        painelRotulo6.add(jLabel52);
        painelCampo6.add(painelRotulo6,java.awt.BorderLayout.NORTH);
        jScrollPane8.setFont(new java.awt.Font("Segoe UI",0,18));
        jScrollPane8.setBackground(new java.awt.Color(255,255,255));
        jScrollPane8.setPreferredSize(new java.awt.Dimension(320, 110));
        taAtividade.setColumns(20);
        taAtividade.setRows(5);
        taAtividade.setBorder(null);
        taAtividade.setFont(new java.awt.Font("Segoe UI",0,18));
        taAtividade.setBackground(new java.awt.Color(255,255,255));
        taAtividade.setMargin(new java.awt.Insets(12, 14, 12, 14));
        jScrollPane8.setViewportView(taAtividade);
        painelCampo6.add(jScrollPane8,java.awt.BorderLayout.CENTER);
        gradeCampos.add(painelCampo6,new java.awt.GridBagConstraints(1,2,1,1,1.0,0.0,18,2,new java.awt.Insets(0,14,0,0),0,0));
        painelCampos.add(gradeCampos,java.awt.BorderLayout.NORTH);
        painelFormulario.add(painelCampos,new java.awt.GridBagConstraints(0,3,1,1,1.0,1,18,1,new java.awt.Insets(0,0,0,0),0,0));
        painelAcoes.setLayout(new java.awt.FlowLayout(2,16,0));
        btCadastrarCargo.setText("Cadastrar Cargo");
        btCadastrarCargo.setMargin(new java.awt.Insets(10, 20, 10, 20));
        btCadastrarCargo.setFont(new java.awt.Font("Segoe UI",1,18));
        btCadastrarCargo.addActionListener(this::btCadastrarCargoActionPerformed);
        painelAcoes.add(btCadastrarCargo);
        painelFormulario.add(painelAcoes,new java.awt.GridBagConstraints(0,4,1,1,1.0,0.0,18,2,new java.awt.Insets(20,0,0,0),0,0));
        getContentPane().add(painelFormulario,java.awt.BorderLayout.CENTER);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfNomeCargojTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfNomeCargojTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfNomeCargojTextField2ActionPerformed

    private void tfSetorjTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfSetorjTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfSetorjTextField4ActionPerformed

    private void btCancelarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btCancelarMouseClicked

    }//GEN-LAST:event_btCancelarMouseClicked

    private void btCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btCancelarActionPerformed

    private void btCadastrarCargoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarCargoActionPerformed
        try {
            Cargo cargo = obterCargoDoFormulario();
            CargoDao cargoDao = new CargoDao();

            if (cargoDao.cadastrar(cargo)) {
                cadastroRealizado = true;
                JOptionPane.showMessageDialog(
                        this,
                        "Cargo cadastrado com sucesso!",
                        "Cadastro de cargo",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Não foi possível cadastrar o cargo.",
                        "Cadastro de cargo",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao cadastrar o cargo:\n" + e.getMessage(),
                    "Erro no banco de dados",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btCadastrarCargoActionPerformed

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
                TelaCadastroJd dialog = new TelaCadastroJd(new javax.swing.JFrame(), true);
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
    private classes.BotaoPadrao btCadastrarCargo;
    private classes.BotaoPadrao btCancelar;
    private javax.swing.JFormattedTextField ffSalario;
    private classes.PainelCampos gradeCampos;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lbDescricao;
    private classes.PainelFerramentas painelAcoes;
    private javax.swing.JPanel painelCabecalho;
    private javax.swing.JPanel painelCampo1;
    private javax.swing.JPanel painelCampo2;
    private javax.swing.JPanel painelCampo3;
    private javax.swing.JPanel painelCampo4;
    private javax.swing.JPanel painelCampo5;
    private javax.swing.JPanel painelCampo6;
    private classes.PainelArredondado painelCampos;
    private classes.PainelFormulario painelFormulario;
    private javax.swing.JPanel painelRotulo1;
    private javax.swing.JPanel painelRotulo2;
    private javax.swing.JPanel painelRotulo3;
    private javax.swing.JPanel painelRotulo4;
    private javax.swing.JPanel painelRotulo5;
    private javax.swing.JPanel painelRotulo6;
    private javax.swing.JTextArea taAtividade;
    private javax.swing.JTextArea taRequisitos;
    private javax.swing.JTextField tfNivel;
    private javax.swing.JTextField tfNomeCargo;
    private javax.swing.JTextField tfSetor;
    //GEN-END:variables
}
