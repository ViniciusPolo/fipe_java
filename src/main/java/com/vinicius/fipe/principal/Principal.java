package com.vinicius.fipe.principal;

import com.vinicius.fipe.model.DadosGeral;
import com.vinicius.fipe.model.Modelos;
import com.vinicius.fipe.model.Veiculo;
import com.vinicius.fipe.service.ConsumoApi;
import com.vinicius.fipe.service.ConverteDados;
import org.w3c.dom.ls.LSOutput;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private final String ENDERECO = "https://parallelum.com.br/fipe/api/v1/";
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversorJson = new ConverteDados();
    public void exibeFipe(){
        System.out.println("Eu estou bem!");
        System.out.println("Tipo de veículo: ");
        var tipoVeiculo = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + tipoVeiculo + "/marcas");
        System.out.println("Digite a marca: ");
        var marca = leitura.nextLine();
        String codigoMarca;
        var marcas = conversorJson.obterList(json, DadosGeral.class);
        marcas.stream()
                .sorted(Comparator.comparing(DadosGeral::codigo));

        Optional<DadosGeral> marcaEscolhida= marcas.stream().filter(m -> m.nome().equalsIgnoreCase(marca)).findFirst();
        codigoMarca = marcaEscolhida.get().codigo();
        if(marcaEscolhida.isPresent()){
            json = consumo.obterDados(ENDERECO + tipoVeiculo + "/marcas/" + codigoMarca + "/modelos" );
        } else {
            System.out.println("Marca não encontrada");
        }
        System.out.println("\nModelos da marca " + marca);
        var modelosLista = conversorJson.obterDados(json, Modelos.class);
        modelosLista.modelos().stream()
                .sorted(Comparator.comparing(DadosGeral::codigo))
                .forEach(System.out::println);

        System.out.println("Digite o nome do modelo: ");
        var modelo = leitura.nextLine();

        List<DadosGeral> modelosFiltrados = modelosLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(modelo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelo Filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite o código do modelo");
        String codigoModelo = leitura.nextLine();
        json = consumo.obterDados(ENDERECO + tipoVeiculo + "/marcas/" + codigoMarca + "/modelos/" + codigoModelo + "/anos" );
        List<DadosGeral> anos = conversorJson.obterList(json, DadosGeral.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++){
            var enderecoAnos = ENDERECO + tipoVeiculo + "/marcas/" + codigoMarca + "/modelos/" + codigoModelo + "/anos/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversorJson.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }
        System.out.println("Todos os veiculos do ano " + anos + "com classificação");
        veiculos.forEach(System.out::println);
    }
}
