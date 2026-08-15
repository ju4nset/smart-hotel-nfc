package com.juan.smart_hotel_nfc.config;

import com.juan.smart_hotel_nfc.model.Quarto;
import com.juan.smart_hotel_nfc.model.QuartoCategoria;
import com.juan.smart_hotel_nfc.model.StatusQuarto;
import com.juan.smart_hotel_nfc.repository.QuartoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class DataInitializer implements CommandLineRunner {


    private final QuartoRepository quartoRepository;


    public DataInitializer(QuartoRepository quartoRepository) {
        this.quartoRepository = quartoRepository;
    }

    // Sempre que eu for cirar uma classe que implementa a interface CommandLineRunner como essa classe Data Initializer
    //o Spring boot olha para ela e pensa "OPA, ASSIM QUE EU TERMINAR DE LIGAR O SERVIDOR E ME CONECTAR COM O BANCO DE
    //DADOS, EU PRECISO EXECUTOR TUDO QUE ESTÁ DENTRO DESSE METODO, ANTES DE LIBERAR O SISTEMA PARA USO.

    //@OVERRIDE - AVISO O JAVA QUE VOCÊ ESTÁ ASSUMIONDO O CONTROLE E REESCREVENDO UMA REGRA ( UM METODO) QUE VEIO DE FÁBRICA
    //DA INTERFACE COMMANDLINERUNNE.

    //STRING... ARGS - SÃO ARGUMENTOS DE LINHA DE COMANDO. Sabe quando rodamos um programa not erminal tipo ( java -jar hotel.jar --teste )
    //A palavra teste entra nesse args. Para o projeto, nao vamos usar isso, mas o java obriga a deixar ai.

    //THROWS EXCEPTION - É a valvula de segurança. Vocês esta dizendo: Olha, o Spring se der algum BO muito grande aqui dentro
    // (banco de dados cair bem na hora de salvar, eu vou estourar uma exceção, Se isso aconter o Spring Boot aborta
    //a inizialização para não rodar um programa bugado.


    // ENTÃO TODA VEZ QUE EU QUISER INICIALIZAR ALGO AUTOMÁTICO EU VENHO AQUI E CONFIGURO


    @Override
    public void run(String... args) throws Exception{

        //SÓ CRIA OS QUARTOS SE O BANCO DE DADOS ESTIVER VAZIO

        if(quartoRepository.count() == 0){

            //4 Duplos
            Quarto q101 = criarQuarto("101", QuartoCategoria.CASAL_STANDARD);
            Quarto q102 = criarQuarto("102", QuartoCategoria.CASAL_STANDARD);
            Quarto q103 = criarQuarto("103", QuartoCategoria.CASAL_STANDARD);
            Quarto q104 = criarQuarto("104", QuartoCategoria.CASAL_STANDARD);

            // 2 Triplos
            Quarto q201 = criarQuarto("201", QuartoCategoria.QUARTO_TRIPLO);
            Quarto q202 = criarQuarto("202", QuartoCategoria.QUARTO_TRIPLO);

            // 2 Quádruplos
            Quarto q203 = criarQuarto("203", QuartoCategoria.QUARTO_QUADRUPLO);
            Quarto q204 = criarQuarto("204", QuartoCategoria.QUARTO_QUADRUPLO);

            // 2 Luxo
            Quarto q301 = criarQuarto("301", QuartoCategoria.QUARTO_LUXO);
            Quarto q302 = criarQuarto("302", QuartoCategoria.QUARTO_LUXO);

            quartoRepository.saveAll(Arrays.asList(q101, q102, q103, q104, q201, q202, q203, q204, q301, q302));

            System.out.println("Todos os 10 quartos, foram inicializado com sucesso!");
        }

    }

    //METODO PARA AUXILIAR PARA ÃO REPETIR CODIGO
    private Quarto criarQuarto(String numero, QuartoCategoria categoria){
        Quarto quarto = new Quarto();
        quarto.setNumero(numero);
        quarto.setCategoria(categoria);
        quarto.setStatusQuarto(StatusQuarto.LIMPEZA);  //TODOS OS QUARTOS NASCEM NA LIMPEZA
        return quarto;
    }

}
