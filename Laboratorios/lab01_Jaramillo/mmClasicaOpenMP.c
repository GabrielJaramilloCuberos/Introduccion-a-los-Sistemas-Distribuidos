/****************************************************************************************
- Fecha: 15/08/2025
- Autor: Gabriel Jaramillo Cuberos
- Tema: 
 	- Programa Multiplicación de Matrices algoritmo clásico
 	- Paralelismo con OpenMP
****************************************************************************************/

// Carga de bibliotecas
#include <stdio.h>
#include <stdlib.h>
#include <string.h>     
#include <time.h>
#include <sys/time.h>
#include <omp.h>

// Variables globales para medir el tiempo
struct timeval inicio, fin; 

// Marca el inicio del tiempo
void InicioMuestra(){
    gettimeofday(&inicio, (void *)0);
}

// Marca el fin y calcula el tiempo transcurrido en microsegundos
void FinMuestra(){
    gettimeofday(&fin, (void *)0);
    fin.tv_usec -= inicio.tv_usec;
    fin.tv_sec  -= inicio.tv_sec;
    double tiempo = (double) (fin.tv_sec*1000000 + fin.tv_usec); 
    printf("%9.0f \n", tiempo); // Imprime el tiempo total
}

// Función para imprimir matrices pequeñas (para verificar resultados)
void impMatrix(double *matrix, int D){
    printf("\n");
    if(D < 9){ // Solo imprime si la matriz es pequeña
        for(int i=0; i<D*D; i++){
            if(i%D==0) printf("\n");
            printf("%.2lf ", matrix[i]); // Dos decimales
        }
        printf("\n**-----------------------------**\n");
    }
}

// Inicializa matrices con valores aleatorios de 0 a 99
void iniMatrix(double *m1, double *m2, int D){
    for(int i=0; i<D*D; i++, m1++, m2++){
        *m1 = (double)(rand() % 100);
        *m2 = (double)(rand() % 100);
    }
}

// Multiplicación de matrices clásica usando hilos
void multiMatrix(double *mA, double *mB, double *mC, int D){
    double Suma, *pA, *pB;
    #pragma omp parallel
    {
        #pragma omp for // Reparte bucles para los hijos
        for(int i=0; i<D; i++){
            for(int j=0; j<D; j++){
                pA = mA + i*D;
                pB = mB + j; 
                Suma = 0.0;
                for(int k=0; k<D; k++, pA++, pB+=D){
                    Suma += (*pA) * (*pB);
                }
                mC[i*D + j] = Suma;
            }
        }
    }
}

int main(int argc, char *argv[]){

    // Verifica tamaño de matriz y número de hilos
    if(argc < 3){
        printf("\n Use: $./clasicaOpenMP SIZE Hilos \n\n");
        exit(0);
    }

    int N = atoi(argv[1]); // Tamaño de la matriz
    int TH = atoi(argv[2]); // Número de hilos

    // Reservar memoria dinámica para 3 matrices N x N
    double *matrixA  = (double *)calloc(N*N, sizeof(double));
    double *matrixB  = (double *)calloc(N*N, sizeof(double));
    double *matrixC  = (double *)calloc(N*N, sizeof(double));

    srand(time(NULL)); // Semilla aleatoria

    omp_set_num_threads(TH); // Fija el número de hilos que se van a utilizar

    iniMatrix(matrixA, matrixB, N); // Inicializa matrices

    impMatrix(matrixA, N); // Imprime matriz A
    impMatrix(matrixB, N); // Imprime matriz B

    InicioMuestra(); // Inicia el cronómetro
    multiMatrix(matrixA, matrixB, matrixC, N); // Multiplica las matrices
    FinMuestra(); // Mide tiempo final

    impMatrix(matrixC, N); // Imprime matriz resultante

    //Liberar memoria
    free(matrixA);
    free(matrixB);
    free(matrixC);

    return 0;
}