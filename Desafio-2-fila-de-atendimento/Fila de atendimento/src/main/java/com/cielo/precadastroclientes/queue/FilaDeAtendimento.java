package com.cielo.precadastroclientes.queue;

import org.springframework.stereotype.Component;

@Component
public class FilaDeAtendimento {
    private static final int TAMANHO_MAXIMO = 100; // Defina o tamanho máximo da fila

    private String[] fila;
    private int tamanho;
    private int inicio;
    private int fim;

    public FilaDeAtendimento() {
        this.fila = new String[TAMANHO_MAXIMO];
        this.tamanho = 0;
        this.inicio = 0;
        this.fim = -1;
    }

    public synchronized boolean adicionarClienteNaFila(String cliente) {
        if (tamanho < TAMANHO_MAXIMO) {
            fim = (fim + 1) % TAMANHO_MAXIMO;
            fila[fim] = cliente;
            tamanho++;
            return true;
        }
        return false; // A fila está cheia
    }

    public synchronized String proximoCliente() {
        if (tamanho > 0) {
            String cliente = fila[inicio];
            inicio = (inicio + 1) % TAMANHO_MAXIMO;
            tamanho--;
            return cliente;
        }
        return null; // A fila está vazia
    }


    public synchronized boolean filaVazia() {
        return tamanho == 0;
    }



    public synchronized String[] obterConteudoDaFila() {
        String[] conteudoDaFila = new String[tamanho];
        int indiceAtual = inicio;
        for (int i = 0; i < tamanho; i++) {
            conteudoDaFila[i] = fila[indiceAtual];
            indiceAtual = (indiceAtual + 1) % TAMANHO_MAXIMO;
        }
        return conteudoDaFila;
    }

    public int getTamanho() {
        return tamanho;
    }

}
