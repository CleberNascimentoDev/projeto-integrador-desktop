package classes;

import javax.swing.*;

/** Mantém a configuração específica da gestão sobre a composição compartilhada. */
public final class PainelGestaoRecrutadores extends PainelListagem {
    public PainelGestaoRecrutadores(JButton voltar, JLabel titulo, JButton ajuda, JTextField pesquisa,
            JLabel lupa, JButton excluir, JButton atribuir, JLabel tituloTabela, JScrollPane rolagem, JTable tabela) {
        super(voltar, titulo, ajuda, pesquisa, lupa, tituloTabela, rolagem, tabela,
                "Gerencie responsáveis pelos processos seletivos.", "Buscar processo ou recrutador...",
                "processo", "processos", true, excluir, atribuir);
        atribuir.setText("+  Atribuir Recrutador");
    }
}