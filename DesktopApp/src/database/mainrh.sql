CREATE DATABASE IF NOT EXISTS `mainrh`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `mainrh`;

CREATE TABLE IF NOT EXISTS `Usuario` (
    `id_usu_pk` INT NOT NULL AUTO_INCREMENT,
    `email_usu` VARCHAR(100) NOT NULL,
    `senha_hash_usu` VARCHAR(255) NOT NULL,
    `nome_usu` VARCHAR(100) NOT NULL,
    `função_usu` ENUM('RECRUTADOR', 'ADM', 'CANDIDATO') NOT NULL,
    `cpf_usu` VARCHAR(11) NOT NULL,
    `telefone_usu` VARCHAR(15) NOT NULL,
    `data_nascimento_usu` DATE NOT NULL,
    PRIMARY KEY (`id_usu_pk`),
    CONSTRAINT `uk_usuario_email` UNIQUE (`email_usu`),
    CONSTRAINT `uk_usuario_cpf` UNIQUE (`cpf_usu`),
    CONSTRAINT `uk_usuario_telefone` UNIQUE (`telefone_usu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Cargo` (
    `id_cargo_pk` INT NOT NULL AUTO_INCREMENT,
    `nome_cargo` VARCHAR(100) NOT NULL,
    `salario_base_cargo` DECIMAL(10,2) NOT NULL,
    `requisitos_cargo` MEDIUMTEXT NOT NULL,
    `nivel_cargo` VARCHAR(100) NOT NULL,
    `setor_cargo` VARCHAR(100) NOT NULL,
    `atividades_cargo` LONGTEXT NOT NULL,
    PRIMARY KEY (`id_cargo_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Processo_seletivo` (
    `id_proce_pk` INT NOT NULL AUTO_INCREMENT,
    `nome_proce` VARCHAR(100) NOT NULL,
    `data_inicio_proce` DATE NOT NULL DEFAULT (CURRENT_DATE),
    `data_fim_proce` DATE,
    `id_cargo_fk` INT NOT NULL,
    `id_usuario_fk` INT NOT NULL,
    PRIMARY KEY (`id_proce_pk`),
    CONSTRAINT `fk_proce_cargo`
        FOREIGN KEY (`id_cargo_fk`) REFERENCES `Cargo` (`id_cargo_pk`),
    CONSTRAINT `fk_proce_usuario`
        FOREIGN KEY (`id_usuario_fk`) REFERENCES `Usuario` (`id_usu_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Candidato_Processo` (
    `id_cand_pk` INT NOT NULL AUTO_INCREMENT,
    `usuario_fk` INT NOT NULL,
    `processo_fk` INT NOT NULL,
    `curriculo_pdf_cand` VARCHAR(512),
    PRIMARY KEY (`id_cand_pk`),
    CONSTRAINT `fk_candproc_usuario`
        FOREIGN KEY (`usuario_fk`) REFERENCES `Usuario` (`id_usu_pk`),
    CONSTRAINT `fk_candproc_processo`
        FOREIGN KEY (`processo_fk`) REFERENCES `Processo_seletivo` (`id_proce_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Prova` (
    `id_prov_pk` INT NOT NULL AUTO_INCREMENT,
    `titulo_prov` VARCHAR(100) NOT NULL,
    `valor_prov` INT NOT NULL,
    `materia_prov` VARCHAR(100) NOT NULL,
    `id_proce_fk` INT NOT NULL,
    PRIMARY KEY (`id_prov_pk`),
    CONSTRAINT `uk_prova_titulo` UNIQUE (`titulo_prov`),
    CONSTRAINT `fk_prova_processo`
        FOREIGN KEY (`id_proce_fk`) REFERENCES `Processo_seletivo` (`id_proce_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Questoes` (
    `id_quest_pk` INT NOT NULL AUTO_INCREMENT,
    `enunciado_quest` LONGTEXT NOT NULL,
    `valor_quest` INT NOT NULL,
    `alternativa_a_quest` LONGTEXT NOT NULL,
    `alternativa_b_quest` LONGTEXT NOT NULL,
    `alternativa_c_quest` LONGTEXT NOT NULL,
    `alternativa_d_quest` LONGTEXT NOT NULL,
    `alternativa_e_quest` LONGTEXT NOT NULL,
    `nivel_dificuldade_quest` ENUM('FACIL', 'MEDIA', 'DIFICIL') NOT NULL,
    `resposta_correta_quest` LONGTEXT NOT NULL,
    PRIMARY KEY (`id_quest_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Questoes_prova` (
    `id_ques_pk` INT NOT NULL AUTO_INCREMENT,
    `id_prov_fk` INT NOT NULL,
    `id_quest_fk` INT NOT NULL,
    PRIMARY KEY (`id_ques_pk`),
    CONSTRAINT `fk_quesprov_prova`
        FOREIGN KEY (`id_prov_fk`) REFERENCES `Prova` (`id_prov_pk`),
    CONSTRAINT `fk_quesprov_questao`
        FOREIGN KEY (`id_quest_fk`) REFERENCES `Questoes` (`id_quest_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Provas_respostas` (
    `id_provres_pk` INT NOT NULL AUTO_INCREMENT,
    `id_cand_fk` INT NOT NULL,
    `id_ques_fk` INT NOT NULL,
    `resposta_provres` LONGTEXT NOT NULL,
    PRIMARY KEY (`id_provres_pk`),
    CONSTRAINT `fk_provres_candidato`
        FOREIGN KEY (`id_cand_fk`) REFERENCES `Candidato_Processo` (`id_cand_pk`),
    CONSTRAINT `fk_provres_questao`
        FOREIGN KEY (`id_ques_fk`) REFERENCES `Questoes_prova` (`id_ques_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Entrevista_RH` (
    `id_entre_pk` INT NOT NULL AUTO_INCREMENT,
    `data_hora_entrevista_entre` DATETIME NOT NULL,
    `ponto_Storytelling_entre` INT,
    `feedback_geral_entre` MEDIUMTEXT,
    `ponto_Autoconhecimento_entre` INT,
    `ponto_foco_resultados_individuais_entre` INT,
    `ponto_objetivos_carreira_entre` INT,
    `ponto_trajetoria_academica_profissional` INT,
    PRIMARY KEY (`id_entre_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Dinamica_grupo` (
    `id_din_pk` INT NOT NULL AUTO_INCREMENT,
    `ponto_escuta_ativa_din` INT,
    `data_hora_dinamica_grupo_din` DATETIME NOT NULL,
    `feedback_geral_din` MEDIUMTEXT,
    `ponto_lideranca_situacional_din` INT,
    `ponto_colaboracao_din` INT,
    `ponto_flexibilidade_adaptabilidade_din` INT,
    `ponto_inteligencia_emocional_din` INT,
    PRIMARY KEY (`id_din_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `Pontuacoes` (
    `id_pont_pk` INT NOT NULL AUTO_INCREMENT,
    `data_hora_prova_pont` DATETIME NOT NULL,
    `ponto_prova_pont` INT,
    `status_candidato_pont`
        ENUM('APROVADO', 'REPROVADO', 'EM ANALISE')
        NOT NULL DEFAULT 'EM ANALISE',
    `id_cand_fk` INT NOT NULL,
    `id_entre_fk` INT NOT NULL,
    `id_din_fk` INT NOT NULL,
    PRIMARY KEY (`id_pont_pk`),
    CONSTRAINT `fk_pont_candidato`
        FOREIGN KEY (`id_cand_fk`) REFERENCES `Candidato_Processo` (`id_cand_pk`),
    CONSTRAINT `fk_pont_entrevista`
        FOREIGN KEY (`id_entre_fk`) REFERENCES `Entrevista_RH` (`id_entre_pk`),
    CONSTRAINT `fk_pont_dinamica`
        FOREIGN KEY (`id_din_fk`) REFERENCES `Dinamica_grupo` (`id_din_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
