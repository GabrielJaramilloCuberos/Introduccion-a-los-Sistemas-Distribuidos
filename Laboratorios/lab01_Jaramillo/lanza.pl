#!/usr/bin/perl
#**************************************************************
# Pontificia Universidad Javeriana
# Autor: Gabriel Jaramillo Cuberos
# Fecha: Agosto 2025
# Materia: Intro Sist. Distribuidos
# Tema: Taller de Evaluación de Rendimiento
# Fichero: script automatización ejecución por lotes
#**************************************************************

# Obtiene la ruta donde se ejecuta script
$Path = `pwd`;
chomp($Path);

# Nombre del ejecutable que se quiere correr varias veces
$Nombre_Ejecutable = "mmClasicaOpenMP";

# Diferentes tamaños de matrices
@Size_Matriz = ("200","300","400","500","600","700","800","900","1000","1100","1200","1300");

# Número de hilos que se usarán
@Num_Hilos = (1,2,4,8,12,16,20);

# Número de repeticiones para cada matriz para cada hilo
$Repeticiones = 30;

# Recorre cada tamaño de matriz
foreach $size (@Size_Matriz){
    # Para cada tamaño, recorre la lista de hilos
    foreach $hilo (@Num_Hilos) {

        # Crea el nombre del archivo donde se guardarán los resultados
        $file = "$Path/$Nombre_Ejecutable-".$size."-Hilos-".$hilo.".dat";

        # Ejecuta el programa varias veces con la combinación actual
        for ($i=0; $i<$Repeticiones; $i++) {
            # Corre el ejecutable con parámetros (tamaño, hilos)
            # y guarda la salida en el archivo correspondiente
            system("$Path/$Nombre_Ejecutable $size $hilo  >> $file");

            # Línea alternativa para solo mostrar el comando (debug)
            # printf("$Path/$Nombre_Ejecutable $size $hilo \n");
        }

        # Cierra archivo
        close($file);

        $p=$p+1;
    }
}
