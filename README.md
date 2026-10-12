# Actividad 01 – Procesamiento secuencial y paralelo de textos con Java Threads


1. Número total de palabras.
2. Apariciones de la palabra `quijote`.
3. Palabras que contienen la secuencia `ción`.

La búsqueda no distingue mayúsculas de minúsculas. Se compara la versión secuencial con
una versión paralela que usa directamente la clase `Thread` (`start()` / `join()`),
con 1, 2 y 4 hilos, sobre el texto original y una versión ampliada (x100).

## Estructura

```
src/
  main.java             Carga el archivo, mide los tiempos y muestra los resultados
  AnalizadorTexto.java  Conteo, división en bloques sin cortar palabras y versión paralela
  HiloAnalizador.java   Subclase de Thread que procesa un bloque
  Resultado.java        Conteos parciales y su combinación
data/
  quijote.txt           

```

## Requisitos

- JDK 17 o superior (`java -version`).

## Cómo ejecutar

 Compila y ejecuta desde la carpeta del proyecto:

   ```bash
   javac src/*.java -d bin
   java -cp bin main
   ```

## Ejemplo de salida

```
===== CONTEOS DEL TEXTO ORIGINAL =====
Total de palabras: 214739
Apariciones de Quijote: 1079
Palabras con cion: 564

Verificacion con 2 y 4 hilos: correcta

===== RESULTADOS DE RENDIMIENTO =====

Conjunto de datos: Original
Hilos           Tiempo promedio (ms)   Aceleracion
1 (secuencial)  8,6718                 1,00
2               4,8047                 1,80
4               2,4500                 3,54

Conjunto de datos: Ampliado (100 veces)
Hilos           Tiempo promedio (ms)   Aceleracion
1 (secuencial)  779,1719               1,00
2               412,3644               1,89
4               209,0201               3,73
```


## Qué hace el programa

- Comprueba que las versiones con 2 y 4 hilos den los mismos conteos que la secuencial.
- Hace 3 rondas de calentamiento de la JVM (no se miden).
- Mide con `System.nanoTime()` solo el procesamiento, 5 repeticiones por configuración, y promedia.
- Muestra los conteos y la tabla de tiempos y aceleración (S = Ts / Tp).

## Cómo se divide el texto

El texto se parte en N bloques de tamaño parecido. Si un corte cae en medio de una palabra,
se mueve hasta el siguiente separador, así ninguna palabra queda partida entre dos hilos.
Cada hilo cuenta en su propio `Resultado` y el hilo principal los suma después de `join()`.
