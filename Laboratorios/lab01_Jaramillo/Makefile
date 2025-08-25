# Compilador que se usará
GCC = gcc

# Flags generales
CFLAGS = -lm

# Opciones para compilar con OpenMP (paralelismo con hilos)
FOPENMP = -fopenmp -O3

# Librería POSIX de hilos
POSIX = -lpthread

# Archivo fuente adicional (no usado directamente en este makefile)
modulo = inter.c

# Nombre de los programas a compilar
PROGRAMAS = mmClasicaOpenMP 

# Compila todos los programas listados
ALL: $(PROGRAMAS)


# Compilación de mmClasicaOpenMP
mmClasicaOpenMP:
	$(GCC) $(CLFAGS) $@.c -o $@ $(FOPENMP)

# Elimina los ejecutables generados
clean:
	$(RM) $(PROGRAMAS)

