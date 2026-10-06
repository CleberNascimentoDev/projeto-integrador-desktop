# Modos de acessibilidade para visão de cores

Implementação na branch `feat/daltonismo`, derivada de `develop`. As alterações permanecem locais, sem commit, push ou merge.

## Análise da estrutura existente

O projeto utiliza Java Swing/NetBeans, telas em `views` e componentes compartilhados em `classes`. A acessibilidade já era coordenada por `Acessibilidade`, `BaixaVisao` e `Daltonismo`. O modo anterior de daltonismo tinha uma única adaptação de cores, sem persistência. Não havia uma estrutura equivalente de preferências ou paletas semânticas que atendesse aos quatro modos.

A varredura dos fontes e formulários encontrou cores em componentes gerados pelo NetBeans, menus, formulários, tabelas, renderizadores e desenhos personalizados. A implementação reutiliza os gerenciadores existentes e adapta os componentes em execução; não modifica indiscriminadamente os arquivos gerados nem cria versões alternativas das telas.

## Arquivos criados

- `src/classes/ModoDaltonismo.java`: enum Padrão, Protanopia, Deuteranopia e Tritanopia.
- `src/classes/PaletaAcessibilidade.java`: paletas e papéis semânticos centralizados.
- `src/classes/IconeSelecao.java`: marca vetorial de seleção, independente da disponibilidade de caracteres na fonte.
- `test/classes/ModosDaltonismoTeste.java`: transições, componentes Swing, contraste, persistência e combinação com baixa visão.
- `test/classes/LogoDaltonismoTeste.java`: cores da marca, contraste, transparência e restauração dos pixels originais.
- `docs/daltonismo.md`: este relatório.

## Arquivos modificados

- `src/classes/Acessibilidade.java`: modo atual, seleção da paleta, eventos, persistência e coordenação dos modos.
- `src/classes/Daltonismo.java`: atualização das janelas, adaptação dos componentes, estados Nimbus, restauração e ícones de mensagens.
- `src/classes/BaixaVisao.java`: integração das cores e do cabeçalho com as paletas, preservando ampliação e comportamento existentes.
- `src/classes/TelaAcessibilidade.java`: quatro botões de modo, indicação de seleção e descrição do estado atual.
- `src/classes/BotaoAcessibilidade.java`: cores dos desenhos e estados do botão.
- `src/classes/BotaoAjuda.java`: cores semânticas do aviso e dos símbolos desenhados.
- `src/classes/PainelListagem.java`: botões, bordas, foco, cabeçalhos, seleção e badge de recrutador.
- `src/classes/IconeImagem.java`: pintura da logo com duas cores semânticas e cache por modo, preservando resolução e transparência.
- `src/views/TelaMenu.java`: utilização do ícone compartilhado para a logo do menu, preservando as dimensões originais.
- `test/classes/DaltonismoTeste.java`: cobertura das três paletas nas telas existentes.

Nenhuma entidade, DAO, regra de negócio, validação ou integração foi alterada nesta implementação. A modificação que já existia em `src/database/mainrh.sql` não faz parte deste trabalho.

## Arquitetura

`Acessibilidade` mantém o modo e disponibiliza a `PaletaAcessibilidade`. `Daltonismo` registra as raízes das janelas, observa novas janelas e atualiza as abertas na thread de eventos do Swing. Cada componente mantém um retrato das propriedades originais para retornar ao padrão. A captura ocorre antes da atualização dos pais, evitando que cores herdadas substituam as originais.

O gerenciador adapta cores de texto, fundo, borda, seleção, cursor e controles desabilitados. Também trata cabeçalhos de JTable, renderizadores de JComboBox e menus. Alterações posteriores de propriedades recebem a adaptação sem substituir os modelos, os eventos ou os dados.

O Look and Feel permanece o mesmo. Os estados pintados pelo Nimbus recebem overrides locais. Apenas os ícones padrão de JOptionPane são ajustados globalmente pelo UIManager. Não se usa `updateComponentTreeUI` indiscriminadamente, pois o projeto possui UIs e renderizadores personalizados que precisam ser preservados. Ao retornar ao padrão, os overrides e as propriedades capturadas são restaurados.

A baixa visão continua independente: mantém a ampliação e o ajuste de layout, enquanto a paleta selecionada determina as cores. A navegação por teclado permanece nativa.

## Paletas e cores encontradas

| Papel | Protanopia | Deuteranopia | Tritanopia |
| --- | --- | --- | --- |
| Primária / seleção | `#00558C` | `#41417D` | `#6E2455` |
| Sucesso | `#285870` | `#22566A` | `#245A49` |
| Aviso | `#FFD280` | `#FFD280` | `#E5DDD9` |
| Erro | `#713E00` | `#6F3C00` | `#7A3038` |
| Informação | `#00558C` | `#41417D` | `#6E2455` |
| Fundo | `#F8F5EE` | `#F3F3F8` | `#F7F3F5` |

As superfícies dos campos e painéis permanecem brancas. A paleta também define texto secundário, bordas, hover, estados desabilitados, exclusão e badges, com pares explícitos de texto/fundo.

Foram adaptados os azuis fixos dos cabeçalhos, botões e seleção; o amarelo dos avisos de ajuda; as cores de texto de erro e obrigatoriedade; as cores dos badges; e os desenhos de bordas e botões. Componentes legados recebem a adaptação centralizada, enquanto os componentes compartilhados utilizam papéis explícitos.

A logo também acompanha a escolha: mantém a base azul escura `#071F40` e substitui o verde por `#986000` em Protanopia, `#9A6200` em Deuteranopia e `#965870` em Tritanopia. As duas regiões têm contraste de luminosidade acima de 3:1 entre si, e os destaques superam 4,5:1 contra branco e contra o fundo do modo. Essa reatribuição se limita às duas regiões de cor da marca, sem filtro global da interface. O desenho, os textos, a transparência e os arquivos PNG originais permanecem intactos. Login, cadastro e menu utilizam a mesma pintura; no padrão, os pixels originais são preservados.

Protanopia e deuteranopia evitam usar vermelho versus verde como distinção exclusiva. Tritanopia utiliza ameixa e neutros para as principais ações e avisos. A compreensão dos estados se apoia também em texto e formas, não somente na separação entre matizes.

## Informações que dependiam da cor

- Seleção das linhas: recebeu uma marca vetorial na primeira coluna nos modos adaptados. O valor do modelo e o comportamento da seleção permanecem intactos.
- Modo de cores ativo: indicado por marca e nome na tela de acessibilidade, com descrição acessível.
- Mensagens: preservam os textos e recebem símbolos e formas distintos: aviso triangular com `!`, erro quadrado com `×`, informação circular com `i` e pergunta com `?`.
- Recrutador ausente: preservado o texto `Não atribuído` no badge, com contraste inclusive na linha selecionada.
- Campos obrigatórios: preservados os asteriscos e o texto explicativo existente.

Não foram encontrados fluxos de aprovação/reprovação nem gráficos que codificassem esses estados exclusivamente por vermelho/verde. Não foram introduzidos novos estados de negócio.

## Persistência

É usada `java.util.prefs.Preferences.userRoot()` no nó `/mainrh/acessibilidade`, chave `modoDaltonismo`. Apenas o nome do enum é salvo, por exemplo `TRITANOPIA`. No Windows, o backend de Preferences utiliza o registro do usuário. O modo é carregado ao iniciar e aplicado às janelas que forem abertas.

Preferência ausente ou inválida retorna ao padrão. Se o sistema operacional negar acesso às preferências, o modo continua funcionando na sessão e a falha é registrada no log. Os testes utilizaram nós separados sob `/mainrh/testes`, sem alterar a preferência normal do usuário.

## Componentes especiais

Tratamento explícito para o desenho de `BotaoAcessibilidade`, avisos de `BotaoAjuda`, botões planos e bordas arredondadas de `PainelListagem`, cabeçalhos, badge `Não atribuído`, marca de seleção e ícones JOptionPane. Renderizadores preservam fontes, valores e eventos; as cores selecionadas não são sobrescritas pelas cores de status.

## Validação realizada

- Todas as 16 transições entre os quatro modos, com e sem baixa visão.
- Atualização de múltiplas janelas e aplicação a novas janelas.
- Persistência em duas JVMs separadas: gravação e carregamento de Tritanopia.
- Verificação dos três modos nas 13 telas existentes e em mensagem de diálogo, incluindo restauração do padrão.
- Campos de texto/senha, texto selecionado, cursor, controles desabilitados e reativados, JComboBox, JList, JCheckBox, JRadioButton, JTabbedPane, menus e JTable.
- Contraste calculado de pares semânticos: pelo menos 4,5:1 para os textos testados e 3:1 para bordas testadas; também verificados seleção, badge, aviso, exclusão e campos desabilitados.
- Testes existentes: `AcessibilidadeTeste`, `BaixaVisaoTeste`, `AjudaTeste`, `SelecaoTabelaTeste`, `SubtelasTeste` e `GestaoRecrutadoresTeste`, todos aprovados.
- Revisão de imagens renderizadas pelos próprios componentes Swing: opções de acessibilidade, ajuda, listagens e formulários. A revisão identificou e corrigiu uma marca de seleção que aparecia como quadrado por incompatibilidade de fonte.
- Logos: três recursos existentes, quatro modos com/sem baixa visão, preservação de dimensões e transparência, igualdade dos pixels no padrão e contraste das duas cores da marca. Revisão visual das versões Protanopia e Tritanopia.

Os testes de telas usam dados e conexões simulados. Não executam operações de alteração no banco real. A validação funcional cobre os comportamentos exercitados pelos testes; não equivale a uma avaliação completa de uso com pessoas com deficiência de visão de cores.

## Build final

**BUILD SUCCESSFUL** no build completo Ant/NetBeans, com geração do JAR. A compilação adicional de fontes e testes com `javac --release 17` também passou.

O Ant exibiu um aviso da configuração existente `-source 17 -target 17` ao utilizar JDK 26: recomenda `--release 17` para definir os módulos da plataforma. A compilação adicional utilizou essa opção sem alterar a configuração geral do projeto.

## Referências de acessibilidade

As verificações foram orientadas pelos critérios WCAG de [uso de cor](https://www.w3.org/WAI/WCAG22/Understanding/use-of-color.html), [contraste de texto](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html) e [contraste de componentes](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html). Os resultados relatados são verificações dos pares e componentes testados, sem declarar certificação WCAG do sistema inteiro.
