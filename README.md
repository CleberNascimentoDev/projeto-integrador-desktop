<p align="center">
  <img src="DesktopApp/src/images/Logo.png" alt="Logo MAINRH" width="340">
</p>

# MAINRH — Projeto Integrador Desktop

Aplicação desktop para gestão de cargos, processos seletivos e recrutadores, desenvolvida em Java com interface Swing e banco de dados MySQL.

## Funcionalidades

- Login com e-mail e senha e acesso ao painel para usuários administradores (`ADM`).
- Cadastro de usuários administradores e recrutadores.
- Cadastro, consulta, edição, exclusão e visualização de detalhes de cargos.
- Cadastro, pesquisa, edição e exclusão de processos seletivos vinculados a cargos.
- Atribuição e remoção de recrutadores em processos seletivos.
- Janelas de ajuda nas telas do sistema.

## Acessibilidade

A navegação por teclado está ativa por padrão. As opções visuais ficam no botão **Acessibilidade**, disponível no login e no menu principal.

| Recurso | Comportamento |
| --- | --- |
| Navegação por teclado | Use Tab e Shift+Tab para navegar, Espaço para acionar botões e as setas para percorrer tabelas. |
| Baixa visão | Amplia textos e controles, aumenta o contraste e ajusta o layout à área da tela, sem acrescentar barras de rolagem à janela. |
| Daltonismo | Aplica uma paleta alternativa com azul e laranja, preservando textos e símbolos para identificar as ações. |

Baixa visão e daltonismo podem ser ativados ou desativados por botões e utilizados juntos. As preferências visuais valem durante a execução do sistema.

Atalhos do menu principal:

| Tecla | Ação |
| --- | --- |
| F2 | Gerenciar cargos |
| F3 | Abrir processos seletivos |
| F4 | Vincular recrutadores |
| F6 | Cadastrar usuário |
| Esc | Sair do sistema |

## Tecnologias e requisitos

- **JDK 17 ou superior**: o projeto configura o código-fonte e o destino da compilação para Java 17.
- **Java Swing** para a interface desktop.
- **Apache NetBeans** e **Apache Ant** para edição e compilação do projeto.
- **MySQL 8.0.13 ou superior**, compatível com os valores padrão por expressão usados no script SQL, conforme a [documentação do MySQL](https://dev.mysql.com/doc/refman/8.0/en/data-type-defaults.html).
- **MySQL Connector/J 9.2.0**, incluído em `DesktopApp/lib/`.
- **AbsoluteLayout**, biblioteca de layout distribuída com o NetBeans.

## Como executar

### 1. Obter o projeto

```bash
git clone https://github.com/CleberNascimentoDev/projeto-integrador-desktop.git
cd projeto-integrador-desktop
```

### 2. Preparar o banco de dados

Inicie o servidor MySQL e execute o arquivo [mainrh.sql](DesktopApp/src/database/mainrh.sql), por exemplo pelo MySQL Workbench. O script cria o banco `mainrh` e suas tabelas.

**Compatibilidade do nome da coluna:** os DAOs Java usam `funcao_usu`, sem acento. Se o script criar a coluna como `função_usu`, execute a alteração abaixo após importar o banco. Se a coluna já se chamar `funcao_usu`, não é necessário executá-la.

```sql
USE mainrh;

ALTER TABLE Usuario
    CHANGE COLUMN `função_usu` `funcao_usu`
    ENUM('RECRUTADOR', 'ADM', 'CANDIDATO') NOT NULL;
```

Configure a conexão em [ConexaoBanco.java](DesktopApp/src/database/ConexaoBanco.java), ajustando `URL`, `USER` e `PASSWORD` para seu ambiente. Atualmente, o código aponta para `localhost:3306`, banco `mainrh`, com usuário e senha `root`.

### 3. Abrir e compilar no NetBeans

1. Abra a pasta **DesktopApp** como projeto Java no NetBeans.
2. Selecione um JDK 17 ou superior nas propriedades do projeto.
3. Confira as bibliotecas **MySQL Connector/J** e **AbsoluteLayout**.
4. Ajuste o caminho de `file.reference.AbsoluteLayout.jar` em [project.properties](DesktopApp/nbproject/project.properties) caso sua instalação do NetBeans esteja em outra pasta. Confira também a biblioteca `absolutelayout` nas propriedades do projeto.
5. Execute **Limpar e Construir** para gerar o aplicativo.
6. Execute o projeto. A classe principal é `views.TelaLogin`.

### 4. Criar o primeiro administrador

O script SQL não fornece uma conta pronta para login. Para criar a primeira conta, execute o arquivo `TelaCadastroUsuarioJd.java` diretamente pelo NetBeans, usando **Executar Arquivo**, e escolha a função **Administrador**.

Também é possível abrir essa tela pelo terminal depois de compilar. Na pasta `DesktopApp`, no Windows:

```powershell
java -cp "dist/DesktopApp.jar;dist/lib/*" views.TelaCadastroUsuarioJd
```

Depois, entre no sistema com o e-mail e a senha cadastrados. A tela de login permite acesso ao painel para contas com função `ADM`.

### 5. Executar o JAR

Após a compilação, na pasta `DesktopApp`:

```bash
java -jar dist/DesktopApp.jar
```

Mantenha a pasta `dist/lib` junto do JAR, pois ela contém as dependências da aplicação. O comando `java` deve utilizar o JDK configurado nos requisitos.

## Estrutura do projeto

```text
DesktopApp/
├── src/
│   ├── classes/      # Modelos e componentes compartilhados de acessibilidade
│   ├── database/     # Conexão, DAOs e script SQL
│   ├── views/        # Telas Swing e formulários do NetBeans
│   ├── icons/        # Ícones da interface
│   └── images/       # Logos e imagens
├── test/classes/    # Verificações executáveis de acessibilidade
├── lib/             # MySQL Connector/J
├── nbproject/       # Configuração do projeto NetBeans
└── build.xml        # Compilação com Ant
```

## Verificações de acessibilidade

Há quatro verificações executáveis:

- `BaixaVisaoTeste`: ampliação, contraste e restauração das telas.
- `DaltonismoTeste`: paleta alternativa, contraste, restauração e ativação pelo botão.
- `AcessibilidadeTeste`: uso combinado dos modos, ativação em ordens diferentes e restauração do estado original.
- `AjudaTeste`: visibilidade da interrogação e quebra de linhas da ajuda no tema Nimbus, incluindo baixa visão e modos combinados.

Os testes usam consultas simuladas, sem acessar o banco real. Precisam de um ambiente com suporte gráfico ao Swing, embora não abram janelas visíveis.

Depois de gerar o JAR, execute na pasta `DesktopApp`, com o JDK no `PATH`. Os comandos abaixo usam o separador de classpath do Windows:

```powershell
javac --release 17 -encoding UTF-8 -cp "dist/DesktopApp.jar;dist/lib/*" -d build/test/classes test/classes/BaixaVisaoTeste.java test/classes/DaltonismoTeste.java test/classes/AcessibilidadeTeste.java test/classes/AjudaTeste.java
java -cp "build/test/classes;dist/DesktopApp.jar;dist/lib/*" classes.BaixaVisaoTeste
java -cp "build/test/classes;dist/DesktopApp.jar;dist/lib/*" classes.DaltonismoTeste
java -cp "build/test/classes;dist/DesktopApp.jar;dist/lib/*" classes.AcessibilidadeTeste
java -cp "build/test/classes;dist/DesktopApp.jar;dist/lib/*" classes.AjudaTeste
```

Em Linux ou macOS, substitua `;` por `:` no classpath.
