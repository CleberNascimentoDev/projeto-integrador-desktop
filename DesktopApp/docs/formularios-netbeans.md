# Formulários e componentes visuais no NetBeans

A composição padrão das telas é declarada nos arquivos `.form`, acompanhados pelo
`initComponents()` correspondente no `.java`. Os construtores não substituem mais
a listagem ou o formulário por uma árvore de componentes criada separadamente.

## Abrir e editar

1. Abra o projeto `DesktopApp` no NetBeans com JDK 17 ou superior.
2. Execute **Limpar e Construir** para compilar os componentes compartilhados.
3. Abra o arquivo `.java` da tela e selecione **Design**. Se a tela já estava
   aberta antes da compilação, reabra o formulário para atualizar as classes.
4. Use o **Navigator/Inspector** para selecionar os painéis, rótulos, campos,
   botões e tabelas. Edite a composição e suas propriedades no GUI Builder.
5. Salve pelo NetBeans, mantendo o par `.form`/`.java` atualizado. Não edite
   separadamente o bloco de código gerado.

Não é necessário cadastrar manualmente os componentes na paleta para editar as
instâncias que já estão nos formulários. Ao adicionar novas instâncias, é possível
usar **Choose Bean** ou **Add from Project**, selecionando uma classe compilada.

## O que está no formulário

- Listagens de cargos, processos e gestão de recrutadores: cabeçalho, descrição,
  busca, ações, contador e painel da tabela.
- Atribuição de recrutador: a mesma composição, com informações do processo.
- Cadastro, edição e detalhes de cargos; cadastro e edição de processos;
  cadastro de usuários: cabeçalho, grupos de campos, máscaras, senhas e ações.
- Login: cabeçalho de acessibilidade, separador, ajuda e imagem compartilhada.
- Menu principal: acessibilidade incluída na própria barra declarada no `.form`.
- `classes/TelaAcessibilidade.form`: opções que antes só existiam como código Java.
- O mapa de atalhos já possuía composição no formulário e foi preservado.

## Componentes compartilhados

| Componente | Responsabilidade |
| --- | --- |
| `BotaoPadrao` | Aparência, medida mínima e foco dos botões |
| `TabelaPadrao` | Cabeçalhos, linhas, seleção e apresentação de recrutadores |
| `CampoPesquisa` / `RotuloLupa` | Placeholder e ícone de busca |
| `ContadorRegistros` | Quantidade ligada ao modelo da tabela |
| `PainelArredondado` | Fundo, borda, raio e margens internas editáveis |
| `PainelFerramentas` / `PainelBusca` | Largura flexível e quebra de linha das ações |
| `PainelCampos` | Grade de campos com adaptação para uma coluna em execução |
| `RotuloImagem` | Recurso, dimensões e adaptação de cores da imagem |
| `DesktopListagem` | Listagem e janelas de detalhes sobrepostas |
| `BarraMenu` | Menus editáveis e botão de acessibilidade à direita |

Os componentes têm construtor público sem argumentos. O `.form` usa layouts
suportados pelo GUI Builder: BorderLayout, GridBagLayout, FlowLayout e GridLayout.
O `PainelCampos` respeita as restrições do editor em tempo de design; a adaptação
para telas estreitas ocorre durante a execução.

O GUI Builder trata os filhos de `JMenuBar` como menus. Por isso `BarraMenu`
encapsula o espaçador e o botão de acessibilidade e recebe os menus declarados no
`.form`. Seu texto de acessibilidade também está disponível como propriedade do
componente. Isso evita uma composição adicional no construtor da tela e erros de
carregamento do Design.

## Diferenças esperadas entre Design e execução

O Design mostra a composição padrão, não consulta o banco e não executa os eventos
de navegação. As tabelas começam vazias e os contadores acompanham o modelo. Ao
executar, aparecem os registros reais, os dados carregados para edição e o estado
das preferências de acessibilidade. Ampliação, paletas, foco, seleção e tamanho da
janela também dependem do estado da aplicação.

Essa diferença de estado é esperada; a estrutura visual permanece a mesma.

## Validação

`FormulariosNetBeansTeste` verifica os 14 formulários, os construtores JavaBean,
as propriedades, os métodos dos eventos e a correspondência da hierarquia visual
entre o trecho gerado e a tela construída. Usa JDBC simulado e preferências
isoladas; não grava no banco real.

Os testes existentes de pesquisa, contagem, seleção, atalhos, detalhes, ajuda,
baixa visão, daltonismo e restauração foram executados. As verificações de subtelas
e opções passaram a considerar o painel visual declarado dentro do content pane
padrão do Swing, preservando as exigências de fundo, borda e contraste.
