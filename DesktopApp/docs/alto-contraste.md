# Modo de alto contraste

Disponível em **Acessibilidade → Alto contraste**. O rótulo permanece fixo e uma
marca indica a seleção. Clicar novamente retira a seleção. A preferência é salva
por usuário, como a escolha de daltonismo.

O alto contraste muda cores, sem ampliar fontes ou componentes. Pode ser combinado
com a baixa visão, que continua responsável pela ampliação. Alto contraste e
daltonismo são mutuamente exclusivos: selecionar um retira o outro, inclusive
quando a preferência é alterada por código. A última escolha é persistida.
**Padrão** restaura cores, fontes e dimensões originais e desliga todos os modos
visuais; a navegação por teclado permanece disponível.

## Paleta e estados

| Papel | Cor |
| --- | --- |
| Fundo e ações | Preto `#000000` |
| Campos e painéis | `#121212` |
| Subtelas | `#1B1B1B` |
| Texto, bordas e ícones | Branco `#FFFFFF` |
| Seleção, foco e avisos | Amarelo `#FFFF00` |
| Texto sobre seleção/avisos | Preto `#000000` |
| Controles desabilitados | Fundo `#202020`, texto `#BFBFBF` |

As cores são aplicadas por função visual, não pela inversão da imagem da tela.
Mensagens mantêm símbolos diferentes para erro, aviso e informação. A seleção das
tabelas mantém a marca visual e a regra de limpeza de seleção já existente.
As logos são carregadas de PNGs em `src/images`, preservando dimensões e
transparência. Para cada original (`Logo`, `LogoLogin`, `LogoMenor`), existem
quatro variantes com os sufixos `-protanopia`, `-deuteranopia`, `-tritanopia` e
`-alto-contraste`. Edite esses arquivos para manter as versões acessíveis;
as logos não são mais recoloridas em memória. Os originais continuam intactos.

## Implementação

- `PaletaAcessibilidade`: cores e pares de texto/fundo.
- `Acessibilidade`: estado, preferência e coordenação dos modos na EDT do Swing.
- `Daltonismo`: aplicação e restauração da paleta nos componentes reais, menus,
  campos, seleções e mensagens; reaproveitado para evitar outro gerenciador de tema.
- Componentes compartilhados: pintura compatível com a paleta ativa, incluindo
  ajuda, acessibilidade, tabelas, foco e imagens.
- `TelaAcessibilidade`: botão e estado do novo modo; opções compactadas para caber
  também com a baixa visão em janelas de menor altura.

Os controles, modelos, eventos e layouts existentes são preservados. O Nimbus
recebe substituições locais de pintura, inclusive no botão padrão de mensagens e
na seta das caixas de seleção; o Look and Feel não é substituído.

Durante a restauração, os observadores de cores ficam suspensos para não
reaplicar a paleta anterior. Janelas abertas com modos ativos também restauram
a base antes de registrar o estado da ampliação. O `.form` e o trecho gerado
da janela de opções mantêm a mesma composição editável no NetBeans.

Os estilos locais do Nimbus copiam os padrões e usam herança desativada para
evitar contaminar o cache compartilhado. A barra inteira recebe a mesma pintura
dos menus, e sua borda original é reposta após a troca de estilo. `BarraMenuTeste`
confere a área livre, as três opções e a restauração pixel a pixel do padrão,
incluindo transições com baixa visão.

## Validação

`AltoContrasteTeste` verifica combinações com baixa visão e os quatro modos de
daltonismo, restauração, modelos e seleção de tabela, menus, campos, estados
desabilitados, pintura dos ícones e do botão padrão, mensagens e opções em janela
reduzida. Cria as 13 telas reais com JDBC simulado e salva suas renderizações em
`build/alto-contraste/imagens` para inspeção visual. Não grava no banco de dados.

`OpcoesAcessibilidadeTeste` abre janelas e aciona os botões na fila real do Swing.
Verifica exclusão mútua, restauração das cores/fontes, modelos e seleção,
rótulos sem reticências e tamanho compatível com a área útil de 1366×768.
Inclui uma janela criada com alto contraste e baixa visão já selecionados.
As imagens de conferência ficam em `build/acessibilidade/imagens`.

As opções `--gravar` e `--ler`, executadas em JVMs separadas, verificam persistência
e exclusão mútua dos modos. Use `-Dmainrh.preferencias=/mainrh/testes/alto-contraste`
para manter as preferências de teste separadas das preferências do usuário.

Os testes adotam 7:1 para os pares de texto avaliados e pelo menos 3:1 para bordas
significativas. Isso não representa certificação WCAG da aplicação inteira nem
substitui avaliação com pessoas usuárias e tecnologias assistivas.

## Referências

- [W3C: contraste aprimorado](https://www.w3.org/WAI/WCAG22/Understanding/contrast-enhanced.html)
  — referência para contraste de texto.
- [W3C: contraste não textual](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html)
  — referência para controles e estados significativos.
- [Microsoft: temas de alto contraste](https://learn.microsoft.com/en-us/windows/apps/design/accessibility/high-contrast-themes)
  — referência para aplicação de cores por papel semântico. O modo do MAINRH é
  ativado no aplicativo; não depende da preferência de alto contraste do Windows.
