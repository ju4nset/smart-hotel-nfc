# Smart Hotel NFC

O SMART HOTEL NFC é um projeto pessoal que estou desenvolvendo para aprender e aprimorar meus conhecimentos em Java, Spring Boot, APIs REST, bancos de dados e desenvolvimento de sistemas.

A proposta é criar um sistema de gestão hoteleira (PMS) independente, voltado para pequenos hotéis, pousadas e imóveis anunciados no Airbnb, reunindo em uma única plataforma o gerenciamento de reservas, hóspedes, pagamentos e acesso aos quartos via NFC.

O projeto busca explorar tecnologias de automação, como fechaduras eletrônicas inteligentes e carteiras digitais, além de reduzir os custos para os proprietários dos estabelecimentos.

## Funcionalidades desenvolvidas

- Cadastro de hóspedes
- Cadastro e gerenciamento de reservas
- Cadastro e gerenciamento de quartos e categorias
- Verificação de disponibilidade e conflitos entre datas
- Integração de pagamentos com a API do Asaas (Sandbox)
- Atualização do status de pagamento por webhook

## Tecnologias utilizadas

**Backend**
- Java
- Spring Boot
- APIs REST

**Frontend**
- React
- Vite
- HTML e CSS

Toda essa parte de frontend foi desenvolvida com o auxílio de modelos de inteligência artificial.

## Banco de dados e infraestrutura

- MySQL (utilizado inicialmente)
- PostgreSQL
- Heroku
- Vercel
- Git e GitHub

## Integração com Asaas

- Criação de clientes pela API do Asaas
- Criação dos DTOs para envio e retorno dos dados
- Criação da classe `AsaasService`
- Implementação de endpoints POST
- Retorno do `customerId` gerado pelo Asaas

A integração com o Asaas está funcional no ambiente Sandbox.

## Próximas ideias para desenvolver

- Adicionar check-in e check-out
- Criar um sistema de login
- NFC — base original para tornar o projeto mais autônomo
- Integração com Google Wallet
- Projeto de controle de consumo dos hóspedes
- Entender o funcionamento das fechaduras eletrônicas
- Entender melhor o RevPAR e estudar a integração dessa funcionalidade com inteligência artificial

## Sobre o Projeto

O projeto está em desenvolvimento. O objetivo inicial é o aprendizado, buscando entender melhor como funciona a estrutura de um projeto e aprofundar meus conhecimentos em Java e Spring Boot, tecnologias pelas quais tenho preferência, especialmente na área de backend.

Ao longo do desenvolvimento, a proposta é transformar a plataforma em uma ferramenta funcional e acessível para pequenos empreendimentos hoteleiros, com recursos avançados e de fácil implementação.

O projeto está há 10 meses em desenvolvimento, sem previsão de uma entrega final. Seguirá em constante evolução, com novas funcionalidades sendo implementadas e aprimoradas.

## Informações adicionais

O projeto ainda possui algumas funcionalidades a serem desenvolvidas, como a utilização de chaves digitais por NFC, integração com o Google Wallet, controle de consumo dos hóspedes e operações de check-in e check-out, que ainda precisam ser implementadas no backend.

Mais adiante, será incluído um pequeno diagrama da arquitetura do projeto.

## Pequena demostração do projeto
O SMART HOTEL NFC, possui um site de reservas web, vinculado ao motor de reservas de Spring Boot, irei disponibilizar o LINK. Relembrando que toda a modelagem do site, foi realizado com IA.

(https://aureahotelreservas10.vercel.app/)
