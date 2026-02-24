package com.example.lotogestor.api;

import jakarta.validation.constraints.NotBlank;

public record PessoaCreateRequest(@NotBlank String nome) {}
