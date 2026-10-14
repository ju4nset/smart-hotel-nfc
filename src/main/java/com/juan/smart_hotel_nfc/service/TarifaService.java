package com.juan.smart_hotel_nfc.service;
import com.juan.smart_hotel_nfc.model.CategoriaQuarto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class TarifaService {



    //METODO DE CALCULA DE TARIFA
    public BigDecimal calcularDiaria(CategoriaQuarto categoriaquarto, int quantidadeHospedes){

        //Verificando se a categoria foi informada
        if(categoriaquarto == null){throw new IllegalArgumentException("A categoria precisa ser informada");
        }
        //Verificando se a quantidade de hospede foi informada
        if(quantidadeHospedes < 1  || quantidadeHospedes > categoriaquarto.getCapacidadeMaxima()){
            throw new IllegalArgumentException("A quantidade de hospede invalida");
        }

        // bucando os valores cadastrado em cada categoria

        BigDecimal valorBase = categoriaquarto.getValorBase();
        BigDecimal adicional = categoriaquarto.getAdicionalHospede();


        //VERIFICANDO SE A TARIFA ESTA CADASTRADA
        if (valorBase == null || adicional == null) {
            throw new IllegalStateException(
                    "Tarifa da categoria não configurada."
            );
        }

        if (valorBase.signum() < 0 || adicional.signum() < 0) {
            throw new IllegalStateException(
                    "Os valores da tarifa não podem ser negativos."
            );
        }

        // calculando quanto cada pessoa paga adicional
        int quantidadeHospedeAdicionais = quantidadeHospedes -1;

        // calcular os valores de adicionais
            BigDecimal valorAdicional = adicional.multiply(BigDecimal.valueOf(quantidadeHospedeAdicionais));
            // somando agora os valores de base e adicional
        return valorBase.add(valorAdicional);
    }


    public BigDecimal calcularValorTotal(CategoriaQuarto categoria, int quantidadeHospedes, LocalDate dataEntrada
    , LocalDate dataSaida){
        if(dataEntrada == null || dataSaida == null ){
            throw new IllegalArgumentException("Data de entrada e saida são obrigatorias.");
        }

        if(!dataSaida.isAfter(dataEntrada)){
            throw new IllegalArgumentException("Data de saida deve ser superior a data de entrada.");
        }

        long quantidadeDiarias = ChronoUnit.DAYS.between(dataEntrada, dataSaida);

        BigDecimal valorDiaria = calcularDiaria(categoria, quantidadeHospedes);

        return valorDiaria.multiply(BigDecimal.valueOf(quantidadeDiarias));

    }







}
