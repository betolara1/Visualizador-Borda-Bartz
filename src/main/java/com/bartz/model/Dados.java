package com.bartz.model;

import java.time.LocalDateTime;

public record Dados(
    Integer id,
    String codBarras,
    String nomeArquivo,
    int qtdBordaTotal,
    int qtdBordabipada,
    LocalDateTime data
) {}
