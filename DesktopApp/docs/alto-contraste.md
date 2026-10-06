# Modo de alto contraste

Disponível em **Acessibilidade → Ativar alto contraste**. O botão também permite
desativar o recurso. A preferência é salva por usuário, como a escolha de daltonismo.

O alto contraste muda cores, sem ampliar fontes ou componentes. Pode ser combinado
com a baixa visão, que continua responsável pela ampliação. Enquanto estiver ativo,
tem prioridade sobre a paleta de daltonismo; a escolha anterior é preservada e volta
a ser aplicada quando o alto contraste é desativado.

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
As logos são adaptadas em memória para branco/amarelo, preservando dimensões e
transparência; os arquivos originais não são alterados.

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

## Validação

`AltoContrasteTeste` verifica combinações com baixa visão e os quatro modos de
daltonismo, restauração, modelos e seleção de tabela, menus, campos, estados
desabilitados, pintura dos ícones e do botão padrão, mensagens e opções em janela
reduzida. Cria as 13 telas reais com JDBC simulado e salva suas renderizações em
`build/alto-contraste/imagens` para inspeção visual. Não grava no banco de dados.

As opções `--gravar` e `--ler`, executadas em JVMs separadas, verificam persistência
e retorno à paleta de daltonismo. Use `-Dmainrh.preferencias=/mainrh/testes/alto-contraste`
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
