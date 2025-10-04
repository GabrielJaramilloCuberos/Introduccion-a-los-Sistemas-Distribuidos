# 📘 Sistema Distribuido de Préstamo, Renovación y Devolución de Libros

Autores: Gabriel Jaramillo Cuberos, Roberth Méndez, Mariana Osorio, Juan Esteban Vera

## 🧩 Descripción general
Este proyecto implementa un sistema distribuido para la gestión de préstamos, devoluciones y renovaciones de libros en una biblioteca con múltiples sedes.
La arquitectura se basa en ZeroMQ (JeroMQ para Java) y usa los patrones REQ/REP y PUB/SUB para permitir comunicación entre los componentes.

## 🏗️ Arquitectura del sistema

```mermaid
graph LR 

subgraph Sede_1 

GC1[Gestor de Carga 1] 

A1D[Actor Devolución 1] 

A1R[Actor Renovación 1] 

GA1[Gestor de Almacenamiento 1<br/>BD Primaria Réplica líder] 

end 

 

subgraph Sede_2 

GC2[Gestor de Carga 2] 

A2D[Actor Devolución 2] 

A2R[Actor Renovación 2] 

GA2[Gestor de Almacenamiento 2<br/>BD Secundaria Réplica seguidora] 

end 

 

subgraph Clientes 

PSs[Procesos Solicitantes N por sede] 

end 

 

%% Enlaces 

PSs -- Req Devolución/Renovación REQ --> GC1 

PSs -- Req Devolución/Renovación REQ --> GC2 

 

GC1 -- PUB topic: Devolucion --> A1D 

GC1 -- PUB topic: Renovacion --> A1R 

GC2 -- PUB topic: Devolucion --> A2D 

GC2 -- PUB topic: Renovacion --> A2R 

 

A1D -- Actualización --> GA1 

A1R -- Actualización --> GA1 

A2D -- Actualización --> GA2 

A2R -- Actualización --> GA2 

 

GA1 <-. Replicación async .-> GA2

```
## 🖥️ Despliegue

### Requisitos:

Java 17 o superior
Librería JeroMQ
Dos o más máquinas en red local (LAN)
Archivos CSV y de carga en la carpeta data/

### Estructura de carpetas:
Lab3/
│── src/
│   ├── GC.java
│   ├── PS.java
│   ├── Actor.java
│   ├── GA.java
│   ├── Persistencia.java
│── data/
│   ├── libros.csv
│   ├── carga_ps1.txt
│── logs/
│── README.md


## ⚙️ Ejecución paso a paso
1. Compilar
2. Ejecutar

## 📊 Pruebas y métricas

Casos verificados:
- Devolución procesada en tiempo real.
- Renovación aceptada máximo 2 veces.
- Renovación 3ª vez → “Límite de renovaciones alcanzado”.
- Respuesta inmediata del GC (< 100 ms).
- Actualización visible en GA y CSV.

### Métricas recolectadas:
- Latencia promedio GC→Actor→GA.
- Throughput de mensajes/s.
- % de errores o pérdidas.
