
package com.juan.smart_hotel_nfc.config;

import com.juan.smart_hotel_nfc.model.CategoriaQuarto;
import com.juan.smart_hotel_nfc.model.Quarto;
import com.juan.smart_hotel_nfc.model.StatusQuarto;
import com.juan.smart_hotel_nfc.repository.CategoriaQuartoRepository;
import com.juan.smart_hotel_nfc.repository.QuartoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final QuartoRepository quartoRepository;
    private final CategoriaQuartoRepository categoriaQuartoRepository;

    // Injeção dos repositories utilizados na inicialização.
    public DataInitializer(
            QuartoRepository quartoRepository,
            CategoriaQuartoRepository categoriaQuartoRepository) {

        this.quartoRepository = quartoRepository;
        this.categoriaQuartoRepository = categoriaQuartoRepository;
    }

    @Override
    public void run(String... args) {

        /*
          1 - CATEGORIAS E TARIFAS
          Busca as categorias pelo nome.
          Se não existirem, cria com as tarifas iniciais.
         */

        CategoriaQuarto casalStandard = buscarOuCriarCategoria(
                "Casal Standard",
                2, 2, 1,
                new BigDecimal("289.00"),
                new BigDecimal("70.00"),
                "Quarto casal standard"
        );

        CategoriaQuarto triplo = buscarOuCriarCategoria(
                "Triplo",
                3, 3, 2,
                new BigDecimal("289.00"),
                new BigDecimal("70.00"),
                "Quarto para até três hóspedes"
        );

        CategoriaQuarto quadruplo = buscarOuCriarCategoria(
                "Quádruplo",
                4, 4, 3,
                new BigDecimal("289.00"),
                new BigDecimal("70.00"),
                "Quarto para até quatro hóspedes"
        );

        CategoriaQuarto luxo = buscarOuCriarCategoria(
                "Luxo",
                2, 2, 1,
                new BigDecimal("349.00"),
                new BigDecimal("110.00"),
                "Quarto categoria luxo"
        );

        /*
         *2 - INICIALIZAÇÃO DOS QUARTOS
          Só cria quartos se a tabela estiver vazia.
          Não exclui nem duplica os quartos existentes.
         */

        if (quartoRepository.count() == 0) {

            Quarto q101 = criarQuarto("101", casalStandard);
            Quarto q201 = criarQuarto("201", triplo);
            Quarto q203 = criarQuarto("203", quadruplo);
            Quarto q301 = criarQuarto("301", luxo);

            quartoRepository.saveAll(
                    List.of(q101, q201, q203, q301)
            );

            System.out.println(
                    "4 quartos inicializados com sucesso!"
            );
        }
    }

    /*
     * 3 - CRIAÇÃO DE QUARTOS
      Associa o quarto à categoria e define o status inicial.
      ATIVO não significa disponível: a disponibilidade
      depende das reservas e das datas.
     */

    private Quarto criarQuarto(
            String numero,
            CategoriaQuarto categoria) {

        Quarto quarto = new Quarto();

        quarto.setNumero(numero);
        quarto.setCategoria(categoria);
        quarto.setStatusQuarto(StatusQuarto.ATIVO);

        return quarto;
    }

    /*
     * 4 - BUSCAR OU CRIAR CATEGORIA
     Se a categoria existir, preenche o adicional
     somente quando estiver nulo.

      Se não existir, cadastra todos os dados.

      A tarifa-base de categorias existentes
      NÃO é alterada automaticamente.
     */

    private CategoriaQuarto buscarOuCriarCategoria(
            String nome,
            int capacidadeMaxima,
            int capacidadeAdultos,
            int capacidadeCriancas,
            BigDecimal valorBase,
            BigDecimal adicionalHospede,
            String descricao) {

        return categoriaQuartoRepository
                .findByNome(nome)

                .map(categoria -> {

                    if (categoria.getAdicionalHospede() == null) {

                        categoria.setAdicionalHospede(
                                adicionalHospede
                        );

                        return categoriaQuartoRepository.save(
                                categoria
                        );
                    }

                    return categoria;
                })

                .orElseGet(() -> {

                    CategoriaQuarto categoria = new CategoriaQuarto();

                    categoria.setNome(nome);
                    categoria.setCapacidadeMaxima(capacidadeMaxima);
                    categoria.setCapacidadeAdultos(capacidadeAdultos);
                    categoria.setCapacidadeCriancas(capacidadeCriancas);
                    categoria.setValorBase(valorBase);
                    categoria.setAdicionalHospede(adicionalHospede);
                    categoria.setDescricao(descricao);

                    return categoriaQuartoRepository.save(
                            categoria
                    );
                });
    }
}
