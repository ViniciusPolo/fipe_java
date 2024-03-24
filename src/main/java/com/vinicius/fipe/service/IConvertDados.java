package com.vinicius.fipe.service;

import java.util.List;

public interface IConvertDados {
    <T> T obterDados(String json, Class<T> classe);

    <T> List<T> obterList(String json, Class<T> classe);
}
