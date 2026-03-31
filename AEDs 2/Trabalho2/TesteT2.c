#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define MAX_SIZE 20

typedef struct {
    int id;
    int size;
} Process;

typedef struct Node {
    Process process;
    struct Node* next;
} Node;

typedef struct {
    Node* front;
    Node* rear;
} Queue;

// Funções de inicialização e manipulação da fila
void initializeQueue(Queue* queue) {
    queue->front = NULL;
    queue->rear = NULL;
}

void enqueue(Queue* queue, Process process) {
    Node* newNode = (Node*)malloc(sizeof(Node));
    newNode->process = process;
    newNode->next = NULL;

    if (queue->rear == NULL) {
        queue->front = newNode;
        queue->rear = newNode;
    } else {
        queue->rear->next = newNode;
        queue->rear = newNode;
    }
}

Process dequeue(Queue* queue) {
    if (queue->front == NULL) {
        Process emptyProcess = {-1, 0}; // Retorna um processo inválido se a fila estiver vazia
        return emptyProcess;
    }

    Node* temp = queue->front;
    Process process = temp->process;

    queue->front = queue->front->next;
    free(temp);

    if (queue->front == NULL) {
        queue->rear = NULL;
    }

    return process;
}

// Função principal
int main() {
    srand(time(NULL));

    int choice;
    printf("Escolha o método de escalonamento:\n");
    printf("1. FCFS (First Come, First Served)\n");
    printf("2. SJF (Shortest Job First)\n");
    printf("3. Round-Robin\n");
    scanf("%d", &choice);

    Queue queue;
    initializeQueue(&queue);

    int timeUnit = 0;

    while (1) {
        if (rand() % 100 < 30) {
            // 30% de chance de criar um novo processo
            Process newProcess = {timeUnit, rand() % MAX_SIZE + 1};
            enqueue(&queue, newProcess);
        }

        switch (choice) {
            case 1: // FCFS
            case 2: // SJF
                if (queue.front != NULL) {
                    Process currentProcess = dequeue(&queue);
                    printf("Executando processo %d de tamanho %d\n", currentProcess.id, currentProcess.size);
                    timeUnit += currentProcess.size;
                }
                break;
            case 3: // Round-Robin
                if (queue.front != NULL) {
                    Process currentProcess = dequeue(&queue);
                    printf("Executando processo %d de tamanho %d\n", currentProcess.id, currentProcess.size);
                    
                    if (currentProcess.size <= 6) {
                        timeUnit += currentProcess.size;
                    } else {
                        currentProcess.size -= 6;
                        enqueue(&queue, currentProcess);
                        timeUnit += 6;
                    }
                }
                break;
        }

        // Simulação de um intervalo de tempo
        sleep(1);

        // Condição de parada (pode ser ajustada conforme necessário)
        if (timeUnit > 100) {
            break;
        }
    }

    return 0;
}