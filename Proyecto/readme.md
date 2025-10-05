# 📘 Sistema Distribuido de Préstamo, Renovación y Devolución de Libros

Autores: Gabriel Jaramillo Cuberos, Roberth Méndez Rivera, Mariana Osorio Vásquez, Juan Esteban Vera Garzón 

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

## Diagrama de componentes 
``` mermaid
graph LR
  subgraph Cliente
    PS[Proceso Solicitante]
  end

  subgraph Sede_1
    GC1[Gestor de Carga]
    A1R[Actor Renovación]
    A1D[Actor Devolución]
    GA1[Gestor de Almacenamiento]
  end

  subgraph Sede_2
    GC2[Gestor de Carga]
    A2R[Actor Renovación]
    A2D[Actor Devolución]
    GA2[Gestor de Almacenamiento]
  end

  PS --> GC1
  PS --> GC2
  GC1 --> A1R
  GC1 --> A1D
  A1R --> GA1
  A1D --> GA1
  GC2 --> A2R
  GC2 --> A2D
  A2R --> GA2
  A2D --> GA2
  GA1 <-. Sincronización .-> GA2
```
## Interacción
### Devolución 
``` mermaid
sequenceDiagram 

participant PS 

participant GC 

participant Broker as ZeroMQ PUB/SUB 

participant ActorD as Actor Devolución 

participant GA as Gestor Almacenamiento 

 

PS->>GC: POST /devolucion {libroId, sede, fecha} 

GC-->>PS: 202 OK (aceptada) 

GC->>Broker: PUB "devolucion" {libroId, sede, fecha} 

Broker-->>ActorD: entrega msg "devolucion" 

ActorD->>GA: updateLibroDevolucion(libroId, fecha) 

GA-->>ActorD: OK 
```
### Renovación
```mermaid
sequenceDiagram 

participant PS 

participant GC 

participant Broker as ZeroMQ PUB/SUB 

participant ActorR as Actor Renovación 

participant GA as Gestor Almacenamiento 

 

PS->>GC: POST /renovacion {libroId, sede, fechaActual} 

GC-->>PS: 202 OK nuevaFecha = +7d* 

GC->>Broker: PUB "renovacion" {libroId, fechaActual, nuevaFecha} 

Broker-->>ActorR: entrega msg "renovacion" 

ActorR->>GA: updateLibroRenovacion libroId, nuevaFecha máx. 2 renov. 

GA-->>ActorR: OK/ERROR límite 
```
## 🖥️ Despliegue
### Diagrama de despliegue
```mermaid
graph LR 

subgraph PC_A Máquina A - Sede 1 

GC1 

A1D 

A1R 

end 

subgraph PC_B Máquina B - Sede 2 

GC2 

A2D 

A2R 

end 

subgraph PC_C Máquina C - Clientes 

PSx 

end 

 

PSx --- GC1 

PSx --- GC2 

GC1 --- A1D 

GC1 --- A1R 

GC2 --- A2D 

GC2 --- A2R 
```

### Requisitos:

Java 17 o superior
Librería JeroMQ
Dos o más máquinas en red local (LAN)
Archivos CSV y de carga en la carpeta data/

### Estructura de carpetas:
```
Lab3/
│── src/
│   ├── Gestor_Almacenamiento/
|        ├──BaseDatos.java
|        ├──Ejemplar.java
|        ├──GestorAlmacenamiento.java
|        ├──GestorAlmacenamientompl.java
|        ├──Libro.java
|        ├──ServidorGA.java
|   ├── Gestor_Carga/
|        ├──ActorClient.java
|        ├──BibliotecaGC.java
|        ├──BibliotecaGClmpl.java
|        ├──Message.java
|        ├──ServidorGC.java
│   ├── ClienteBatch.java
│   ├── libros.txt
│   ├── peticiones.txt
│── README.md
```
## Diagrama de fallos 
```mermaid
graph TD
  subgraph Sede_1
    GC1[GestorCarga 1]
    GA1[GestorAlmacenamiento 1]
  end

  subgraph Sede_2
    GC2[GestorCarga 2]
    GA2[GestorAlmacenamiento 2]
  end

  GC1 -- Heartbeat --> GC2
  GC2 -- Heartbeat --> GC1

  GA1 -- Replicación periódica --> GA2
  GA2 -- Replicación periódica --> GA1

  GC1 --> AL1[Registro de alertas y logs]
  GC2 --> AL2[Registro de alertas y logs]
```
## Modelo de seguridad 
``` mermaid
graph LR
  PS[Proceso Solicitante PS]
  GC[Gestor de Carga GC]
  A[Actores Renovación / Devolucion]
  GA[Gestor de Almacenamiento GA]

  PS -- Comunicación segura TLS/SSL --> GC
  GC -- Canal cifrado PUB/SUB --> A
  A -- Autenticación y validación --> GA
  GA -- Logs cifrados --> PS
```

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
