# **Projeto de Pré-Cadastro de Clientes e Fila de Atendimento**

Este repositório contém a implementação de um sistema de pré-cadastro de clientes e uma fila de atendimento, conforme especificado nos Desafios 1 e 2, além de uma tentativa de resolver os Desafios 3 e 4 (Débito Técnico).

## **Desafio 1: Pré-Cadastro de Clientes**

Este desafio envolve a criação de uma API REST para o pré-cadastro de clientes, com as operações de criação, alteração, exclusão e consulta. Os dados podem ser armazenados em memória.

### **Tecnologias Utilizadas**
- Java
- Spring Boot
- Swagger para documentação da API

### **Instruções de Uso**
1. Clone este repositório.
2. Build e execute a aplicação.
3. Acesse a documentação da API através da URL: [http://localhost:8080/swagger-ui/index.html#/] - link padrão de uma aplicação localhost verifique se é o mesmo em sua máquina local.

### **Testes Unitários**
A implementação inclui uma cobertura de testes unitários.

## **Desafio 2: Fila de Atendimento**

Neste desafio, foi adicionada uma funcionalidade de fila de atendimento aos clientes. Os clientes entram na fila após o cadastro ou alteração.

### **Tecnologias Utilizadas**
- Java
- SpringBoot

### **Instruções de Uso**
1. Para retirar o próximo cliente da fila, faça uma chamada à operação da API.
2. Certifique-se de que a fila está preenchida antes de chamar a operação de retirada.

### **Testes Unitários**
A implementação inclui testes unitários para garantir o funcionamento correto da fila.

## **Desafio 3: Escalabilidade da Fila de Atendimento (Não Implementado)**
Este desafio tinha como objetivo melhorar a escalabilidade da fila de atendimento utilizando a solução de mensageria SQS da AWS. No entanto, devido a restrições de prazo, esta implementação não foi concluída.

## **Desafio 4: Segurança da Informação (Não Implementado)**
Este desafio envolve a identificação e resolução de débitos técnicos de segurança da informação. No entanto, devido a restrições de prazo, esta implementação não foi concluída.

## **Instruções de Instalação**
1. Clone o repositório localmente através do seguinte comando:
```bash
$ git clone https://github.com/gabrielmelim/ada-tech-cielo-challange.git
```
2. abra a pasta do desafio desejado em uma idea de sua preferencia que já esteja configurada para a utilização do java (JDK).

3. rode o arquivo que está fora dos pacotes na aplicação exemplo -> "PreCadastroClientesApplication"

4. Acesse a documentação da API através da URL: [http://localhost:8080/swagger-ui/index.html#/] - link padrão de uma aplicação localhost verifique se é o mesmo em sua máquina local.

5. a aplicação possui um banco de dados em memoria local, você pode acessar através da URL: [http://localhost:8080/h2-console] 

6. lembre-se de verificar as credencias do h2 dentro da aplicação em RESOURCES/application.properties, sinta-se a vontade para alterar caso necessário.

## **Contato**
Para qualquer dúvida ou feedback, entre em contato através do email gabrielmelim2012@hotmail.com