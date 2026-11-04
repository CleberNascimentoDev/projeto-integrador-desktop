package classes;

import java.awt.Color;

/** Cores semânticas e pares de texto/fundo. Estados também usam texto e símbolos. */
public final class PaletaAcessibilidade {
    public enum Papel { PRIMARIO, EXCLUSAO, SUCESSO, AVISO, ERRO, INFORMACAO, OBRIGATORIO }
    private final Color primaria, sucesso, aviso, erro, informacao, fundo, superficie, subtela;
    private final Color texto, textoSecundario, borda, desabilitado, textoDesabilitado;
    private final Color exclusao, textoExclusao, textoAviso, badge, textoBadge;
    private final boolean altoContraste;

    private PaletaAcessibilidade(int primaria, int sucesso, int aviso, int erro, int informacao,
            int fundo, int badge, int textoBadge) {
        this(primaria, sucesso, aviso, erro, informacao, fundo, badge, textoBadge, false);
    }
    private PaletaAcessibilidade(int primaria, int sucesso, int aviso, int erro, int informacao,
            int fundo, int badge, int textoBadge, boolean altoContraste) {
        this.altoContraste = altoContraste;
        this.primaria = new Color(primaria);
        this.sucesso = new Color(sucesso);
        this.aviso = new Color(aviso);
        this.erro = new Color(erro);
        this.informacao = new Color(informacao);
        this.fundo = new Color(fundo);
        this.badge = new Color(badge);
        this.textoBadge = new Color(textoBadge);
        superficie = altoContraste ? new Color(18, 18, 18) : Color.WHITE;
        subtela = altoContraste ? new Color(27, 27, 27) : new Color(245, 245, 245);
        texto = altoContraste ? Color.WHITE : new Color(29, 45, 68);
        textoSecundario = altoContraste ? new Color(218, 218, 218) : new Color(64, 64, 64);
        borda = altoContraste ? Color.WHITE : new Color(104, 116, 130);
        desabilitado = altoContraste ? new Color(32, 32, 32) : new Color(230, 233, 238);
        textoDesabilitado = altoContraste ? new Color(191, 191, 191) : new Color(64, 64, 64);
        exclusao = altoContraste ? Color.BLACK : primaria == 0x6e2455 ? new Color(237, 199, 217) : new Color(255, 210, 128);
        textoExclusao = altoContraste ? Color.WHITE : Color.BLACK;
        textoAviso = altoContraste ? Color.BLACK : new Color(83, 49, 0);
    }

    public static PaletaAcessibilidade paraModo(ModoDaltonismo modo) {
        return switch (modo) {
            case PADRAO -> PADRAO;
            case PROTANOPIA -> PROTANOPIA;
            case DEUTERANOPIA -> DEUTERANOPIA;
            case TRITANOPIA -> TRITANOPIA;
        };
    }
    private static final PaletaAcessibilidade PADRAO = new PaletaAcessibilidade(
            0x1f3550, 0x0bb08e, 0xffc107, 0xc00101, 0x113052, 0xe9f3ff, 0xe9f3ff, 0x1d2d44);
    // Azul/âmbar com diferenças de luminosidade para os eixos vermelho/verde.
    private static final PaletaAcessibilidade PROTANOPIA = new PaletaAcessibilidade(
            0x00558c, 0x285870, 0xffd280, 0x713e00, 0x00558c, 0xf8f5ee, 0xe4edf4, 0x1d2d44);
    private static final PaletaAcessibilidade DEUTERANOPIA = new PaletaAcessibilidade(
            0x41417d, 0x22566a, 0xffd280, 0x6f3c00, 0x41417d, 0xf3f3f8, 0xe8e8f3, 0x30305b);
    // Ameixa/neutros: avisos e estados não dependem da oposição azul/amarelo.
    private static final PaletaAcessibilidade TRITANOPIA = new PaletaAcessibilidade(
            0x6e2455, 0x245a49, 0xe5ddd9, 0x7a3038, 0x6e2455, 0xf7f3f5, 0xeee3ea, 0x582044);
    private static final PaletaAcessibilidade ALTO_CONTRASTE = new PaletaAcessibilidade(
            0x000000, 0xffffff, 0xffff00, 0xffffff, 0xffffff, 0x000000, 0x000000, 0xffffff, true);
    public static PaletaAcessibilidade altoContraste() { return ALTO_CONTRASTE; }

    public Color getPrimaria() { return primaria; }
    public Color getSecundaria() { return textoSecundario; }
    public Color getSucesso() { return sucesso; }
    public Color getAviso() { return aviso; }
    public Color getErro() { return erro; }
    public Color getInformacao() { return informacao; }
    public Color getFundo() { return fundo; }
    public Color getSuperficie() { return superficie; }
    public Color getSubtela() { return subtela; }
    public Color getTexto() { return texto; }
    public Color getTextoSecundario() { return textoSecundario; }
    public Color getBorda() { return borda; }
    public Color getSelecao() { return altoContraste ? Color.YELLOW : primaria; }
    public Color getTextoSelecao() { return altoContraste ? Color.BLACK : Color.WHITE; }
    public Color getHover() { return primaria.darker(); }
    public Color getDesabilitado() { return desabilitado; }
    public Color getTextoDesabilitado() { return textoDesabilitado; }
    public Color getExclusao() { return exclusao; }
    public Color getTextoExclusao() { return textoExclusao; }
    public Color getTextoAviso() { return textoAviso; }
    public Color getBadge() { return badge; }
    public Color getTextoBadge() { return textoBadge; }
    public Color getLogoBase() { return altoContraste ? Color.WHITE : new Color(7, 31, 64); }
    public Color getLogoDestaque() {
        if (altoContraste) return Color.YELLOW;
        if (this == PROTANOPIA) return new Color(152, 96, 0);
        if (this == DEUTERANOPIA) return new Color(154, 98, 0);
        if (this == TRITANOPIA) return new Color(150, 88, 112);
        return new Color(11, 176, 142);
    }
    public Color getCor(Papel papel) {
        return switch (papel) {
            case SUCESSO -> sucesso;
            case AVISO -> aviso;
            case ERRO, OBRIGATORIO -> erro;
            case INFORMACAO -> informacao;
            case EXCLUSAO -> exclusao;
            case PRIMARIO -> primaria;
        };
    }
    public Color getTexto(Papel papel) {
        if (altoContraste && (papel == Papel.SUCESSO || papel == Papel.ERRO
                || papel == Papel.INFORMACAO || papel == Papel.OBRIGATORIO)) return Color.BLACK;
        return switch (papel) {
            case EXCLUSAO -> textoExclusao;
            case AVISO -> textoAviso;
            default -> Color.WHITE;
        };
    }
}
