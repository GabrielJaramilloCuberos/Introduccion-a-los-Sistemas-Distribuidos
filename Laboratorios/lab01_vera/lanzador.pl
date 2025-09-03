#!/usr/bin/perl
#**************************************************************
#         		Pontificia Universidad Javeriana
#     Autor: Juan Esteban Vera Garzon
#     Fecha: 15/08/2025
#     Materia: Sistemas Operativos
#     Tema: Taller de EvaluaciÃ³n de Rendimiento
#     Fichero: script automatizaciÃ³n ejecuciÃ³n por lotes 
#****************************************************************/

$Path = `pwd`;
chomp($Path);

$Nombre_Ejecutable = "mmClasicaOpenMP";
@Size_Matriz = ("200","300","400","500","1000","2000","10000","15000","18000","9000","19000","20000");
@Num_Hilos = (1,2,4,8,16,20);
$Repeticiones = 30;

foreach $size (@Size_Matriz){
	foreach $hilo (@Num_Hilos) {
		$file = "$Path/$Nombre_Ejecutable-".$size."-Hilos-".$hilo.".dat";
		for ($i=0; $i<$Repeticiones; $i++) {
		system("$Path/$Nombre_Ejecutable $size $hilo  >> $file");
			#printf("$Path/$Nombre_Ejecutable $size $hilo \n");
		}
		close($file);
	$p=$p+1;
	}
}
