# Multiplicación de Matrices con OpenMP

## 📌 Descripción
Este proyecto implementa la multiplicación de matrices utilizando:
- **Algoritmo clásico** de multiplicación.
- **Paralelismo con OpenMP**.
- Automatización de pruebas por lotes con un **script en Perl**.
- Compilación organizada mediante un **Makefile**.

El objetivo es evaluar el rendimiento de la multiplicación de matrices variando:
- El tamaño de la matriz.
- El número de hilos.

---

## ⚙️ Estructura del Proyecto
- **`mmClasicaOpenMP.c`** → Código fuente en C (multiplicación de matrices).
- **`Makefile`** → Script de compilación.
- **`script.pl`** → Automatiza ejecuciones por lotes.
- **`resultados/`** → Carpeta sugerida para guardar las salidas (`.dat`).

---

## 🖥️ Código en C (mmClasicaOpenMP.c)
Programa que multiplica dos matrices cuadradas de tamaño **N x N** usando el algoritmo clásico y paralelismo con OpenMP.

Puntos clave:
- **`iniMatrix`** inicializa las matrices con valores aleatorios.
- **`multiMatrix`** realiza la multiplicación usando tres bucles anidados y `#pragma omp for`.
- **`InicioMuestra / FinMuestra`** miden el tiempo de ejecución en microsegundos.
- **`impMatrix`** imprime la matriz solo si es pequeña (para depuración).

Ejecución:
```bash
./mmClasicaOpenMP <SIZE> <HILOS>
