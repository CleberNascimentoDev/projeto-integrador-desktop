package classes;

import java.awt.*;
import javax.swing.*;

/** Exercita os dois modos juntos pelo mesmo caminho público dos botões. */
public class AcessibilidadeTeste {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> verificar());
        System.out.println("OK: modos combinados em ambas as ordens, restauração e teclado independente.");
    }

    private static void verificar() {
        JFrame tela = new JFrame() {
            @Override protected JRootPane createRootPane() {
                return new JRootPane() {
                    @Override public boolean isShowing() { return true; }
                };
            }
        };
        JPanel painel = new JPanel();
        painel.setBackground(new Color(233, 243, 255));
        JButton editar = new JButton("Editar");
        JButton excluir = new JButton("Excluir");
        editar.setBackground(new Color(11, 176, 142));
        excluir.setBackground(new Color(192, 1, 1));
        editar.setForeground(Color.WHITE);
        excluir.setForeground(Color.WHITE);
        editar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        excluir.setFont(editar.getFont());
        GroupLayout layout = new GroupLayout(painel);
        painel.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(editar).addComponent(excluir));
        layout.setVerticalGroup(layout.createParallelGroup().addComponent(editar).addComponent(excluir));
        tela.setContentPane(painel);
        tela.pack();
        Acessibilidade.configurarTela(tela);
        Dimension tamanho = tela.getSize();
        Acessibilidade.setTecladoAtivo(false);
        try {
            for (int repeticao = 0; repeticao < 3; repeticao++) {
                Acessibilidade.setDaltonismoAtivo(true);
                verificarPaleta(editar, excluir);
                Acessibilidade.setBaixaVisaoAtiva(true);
                verificarAmpliacao(editar, excluir);
                verificarPaleta(editar, excluir);
                Acessibilidade.setDaltonismoAtivo(false);
                verificarAmpliacao(editar, excluir);
                exigir(Acessibilidade.isBaixaVisaoAtiva(), "Daltonismo desativou baixa visão");
                Acessibilidade.setBaixaVisaoAtiva(false);
                verificarOriginal(tela, painel, layout, editar, excluir, tamanho);

                Acessibilidade.setBaixaVisaoAtiva(true);
                Acessibilidade.setDaltonismoAtivo(true);
                verificarAmpliacao(editar, excluir);
                verificarPaleta(editar, excluir);
                Acessibilidade.setBaixaVisaoAtiva(false);
                verificarPaleta(editar, excluir);
                exigir(editar.getFont().getSize() == 14, "Fonte continuou ampliada após desativar baixa visão");
                exigir(Acessibilidade.isDaltonismoAtivo(), "Baixa visão desativou daltonismo");
                Acessibilidade.setDaltonismoAtivo(false);
                verificarOriginal(tela, painel, layout, editar, excluir, tamanho);
                exigir(!Acessibilidade.isTecladoAtivo(), "Modos visuais alteraram a navegação");
            }
        } finally {
            Acessibilidade.setDaltonismoAtivo(false);
            Acessibilidade.setBaixaVisaoAtiva(false);
            Acessibilidade.setTecladoAtivo(true);
            tela.dispose();
        }
    }

    private static void verificarPaleta(JButton editar, JButton excluir) {
        exigir(editar.getBackground().equals(new Color(0, 85, 140))
                && editar.getForeground().equals(Color.WHITE), "Editar perdeu a paleta azul");
        exigir(excluir.getBackground().equals(new Color(255, 210, 128))
                && excluir.getForeground().equals(Color.BLACK), "Excluir perdeu a paleta laranja");
    }

    private static void verificarAmpliacao(JButton editar, JButton excluir) {
        exigir(editar.getFont().getSize() == 21 && excluir.getFont().getSize() == 21,
                "Fontes foram perdidas ou ampliadas mais de uma vez");
    }

    private static void verificarOriginal(JFrame tela, JPanel painel, GroupLayout layout,
            JButton editar, JButton excluir, Dimension tamanho) {
        exigir(tela.getContentPane() == painel && painel.getLayout() == layout, "Layout não restaurado");
        exigir(tela.getSize().equals(tamanho), "Dimensões não restauradas");
        exigir(editar.getBackground().equals(new Color(11, 176, 142))
                && excluir.getBackground().equals(new Color(192, 1, 1)), "Cores originais não restauradas");
        exigir(editar.getFont().getSize() == 14 && excluir.getFont().getSize() == 14, "Fonte original não restaurada");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
