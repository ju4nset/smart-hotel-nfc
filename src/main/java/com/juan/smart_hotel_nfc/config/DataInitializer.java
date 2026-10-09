package com.juan.smart_hotel_nfc.config;

import com.juan.smart_hotel_nfc.model.CategoriaQuarto;
import com.juan.smart_hotel_nfc.model.Quarto;
import com.juan.smart_hotel_nfc.model.StatusQuarto;
import com.juan.smart_hotel_nfc.repository.CategoriaQuartoRepository;
import com.juan.smart_hotel_nfc.repository.QuartoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class DataInitializer implements CommandLineRunner {

    /*
     * IMPORTANTE:
     * Agora precisamos de DOIS repositories.
     *
     * QuartoRepository:
     * usado para salvar/buscar quartos.
     *
     * CategoriaQuartoRepository:
     * usado para salvar/buscar categorias.
     */
    private final QuartoRepository quartoRepository;
    private final CategoriaQuartoRepository categoriaQuartoRepository;


    /*
     * INJEÇÃO DE DEPENDÊNCIA
     *
     * O Spring automaticamente entrega os dois repositories
     * para esta classe quando iniciar a aplicação.
     */
    public DataInitializer(
            QuartoRepository quartoRepository,
            CategoriaQuartoRepository categoriaQuartoRepository) {

        this.quartoRepository = quartoRepository;
        this.categoriaQuartoRepository = categoriaQuartoRepository;
    }


    /*
     * CommandLineRunner:
     *
     * Esse método é executado automaticamente quando
     * o Spring Boot termina de iniciar.
     *
     * Ou seja:
     *
     * Spring inicia
     *      ↓
     * conecta no banco
     *      ↓
     * executa run()
     *      ↓
     * depois libera a aplicação
     */
    @Override
    public void run(String... args) throws Exception {


        /*
         * =========================================================
         * 1 - CRIAMOS OU BUSCAMOS AS CATEGORIAS
         * =========================================================
         *
         * Antes nós usávamos:
         *
         * QuartoCategoria.CASAL_STANDARD
         *
         * que era um ENUM.
         *
         * Agora CategoriaQuarto é uma ENTIDADE DO BANCO.
         *
         * Por isso precisamos buscar ou criar cada categoria.
         */


        CategoriaQuarto casalStandard = buscarOuCriarCategoria(
                "Casal Standard",
                2,
                2,
                1,
                new BigDecimal("250.00"),
                "Quarto casal standard"
        );


        CategoriaQuarto triplo = buscarOuCriarCategoria(
                "Triplo",
                3,
                3,
                2,
                new BigDecimal("320.00"),
                "Quarto para até três hóspedes"
        );


        CategoriaQuarto quadruplo = buscarOuCriarCategoria(
                "Quádruplo",
                4,
                4,
                3,
                new BigDecimal("400.00"),
                "Quarto para até quatro hóspedes"
        );


        CategoriaQuarto luxo = buscarOuCriarCategoria(
                "Luxo",
                2,
                2,
                1,
                new BigDecimal("500.00"),
                "Quarto categoria luxo"
        );


        /*
         * =========================================================
         * 2 - CRIA OS QUARTOS
         * =========================================================
         *
         * Só criamos os quartos automaticamente
         * se a tabela de quartos estiver vazia.
         *
         * Isso evita criar:
         *
         * 101
         * 101
         * 101
         *
         * toda vez que o servidor reiniciar.
         */
        if (quartoRepository.count() == 0) {


            // 4 QUARTOS CASAL STANDARD

            Quarto q101 = criarQuarto("101", casalStandard);
            Quarto q102 = criarQuarto("102", casalStandard);
            Quarto q103 = criarQuarto("103", casalStandard);
            Quarto q104 = criarQuarto("104", casalStandard);


            // 2 QUARTOS TRIPLOS

            Quarto q201 = criarQuarto("201", triplo);
            Quarto q202 = criarQuarto("202", triplo);


            // 2 QUARTOS QUÁDRUPLOS

            Quarto q203 = criarQuarto("203", quadruplo);
            Quarto q204 = criarQuarto("204", quadruplo);


            // 2 QUARTOS LUXO

            Quarto q301 = criarQuarto("301", luxo);
            Quarto q302 = criarQuarto("302", luxo);


            /*
             * saveAll() salva vários quartos de uma vez.
             *
             * Como aqui temos uma LISTA de quartos,
             * usamos saveAll().
             *
             * Diferente do QuartoService, onde criamos
             * apenas UM quarto e usamos save().
             */
            quartoRepository.saveAll(
                    Arrays.asList(
                            q101,
                            q102,
                            q103,
                            q104,
                            q201,
                            q202,
                            q203,
                            q204,
                            q301,
                            q302
                    )
            );


            System.out.println(
                    "Todos os 10 quartos foram inicializados com sucesso!"
            );
        }

    }


    /*
     * =========================================================
     * MÉTODO AUXILIAR PARA CRIAR QUARTOS
     * =========================================================
     *
     * Evita repetir:
     *
     * new Quarto()
     * setNumero()
     * setCategoria()
     * setStatusQuarto()
     *
     * dez vezes.
     *
     *
     * IMPORTANTE:
     *
     * Antes recebíamos:
     *
     * QuartoCategoria categoria
     *
     * Agora recebemos:
     *
     * CategoriaQuarto categoria
     *
     * porque categoria virou uma entidade do banco.
     */
    private Quarto criarQuarto(
            String numero,
            CategoriaQuarto categoria) {

        Quarto quarto = new Quarto();

        quarto.setNumero(numero);

        /*
         * Aqui estamos criando a relação:
         *
         * Quarto -> CategoriaQuarto
         *
         * Por exemplo:
         *
         * Quarto 101
         *     ↓
         * Categoria Casal Standard
         */
        quarto.setCategoria(categoria);


        /*
         * Todo quarto nasce como ATIVO.
         *
         * ATIVO significa:
         * o quarto pode participar da operação normal do hotel.
         *
         * Isso NÃO significa necessariamente "livre".
         *
         * A disponibilidade será calculada pelas reservas
         * e pelas datas.
         */
        quarto.setStatusQuarto(StatusQuarto.ATIVO);

        return quarto;
    }


    /*
     * =========================================================
     * MÉTODO AUXILIAR PARA BUSCAR OU CRIAR CATEGORIA
     * =========================================================
     *
     * Primeiro tentamos encontrar a categoria pelo nome.
     *
     * Exemplo:
     *
     * "Casal Standard"
     *
     * Se existir:
     * usamos a categoria existente.
     *
     * Se NÃO existir:
     * criamos uma nova categoria e salvamos.
     *
     *
     * Isso evita duplicar categorias sempre que
     * a aplicação reiniciar.
     */
    private CategoriaQuarto buscarOuCriarCategoria(
            String nome,
            int capacidadeMaxima,
            int capacidadeAdultos,
            int capacidadeCriancas,
            BigDecimal valorBase,
            String descricao) {


        return categoriaQuartoRepository
                .findByNome(nome)

                /*
                 * orElseGet:
                 *
                 * "Se não encontrou, execute este código".
                 */
                .orElseGet(() -> {

                    CategoriaQuarto categoria =
                            new CategoriaQuarto();


                    categoria.setNome(nome);

                    categoria.setCapacidadeMaxima(
                            capacidadeMaxima
                    );

                    categoria.setCapacidadeAdultos(
                            capacidadeAdultos
                    );

                    categoria.setCapacidadeCriancas(
                            capacidadeCriancas
                    );

                    categoria.setValorBase(
                            valorBase
                    );

                    categoria.setDescricao(
                            descricao
                    );


                    /*
                     * Salva a nova categoria no banco.
                     *
                     * Depois o próprio save() devolve
                     * a categoria já com ID.
                     */
                    return categoriaQuartoRepository
                            .save(categoria);
                });
    }

}