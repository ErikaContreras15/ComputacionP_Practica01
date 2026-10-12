import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;

public class main {

    static String palabra = "quijote";
    static String secuencia = "ci\u00f3n"; // "ción"
    static int[] hilos = {1, 2, 4};
    static int repeticiones = 5;

    public static void main(String[] args) throws Exception {
        // Leer el libro y dejarlo en memoria antes de medir
        String texto = Files.readString(Path.of("data/quijote.txt"));
        texto = Normalizer.normalize(texto, Normalizer.Form.NFC);
        texto = quitarCabecera(texto);

        // Texto ampliado: el original repetido 100 veces
        String ampliado = (texto + "\n").repeat(100);

        // Conteos del texto original
        Resultado ref = AnalizadorTexto.secuencial(texto, palabra, secuencia);
        System.out.println("===== CONTEOS DEL TEXTO ORIGINAL =====");
        System.out.println("Total de palabras: " + ref.totalPalabras);
        System.out.println("Apariciones de Quijote: " + ref.aparicionesPalabra);
        System.out.println("Palabras con cion: " + ref.palabrasConSecuencia);
        System.out.println();

        // Comprobar que la version paralela da lo mismo
        for (int h = 2; h <= 4; h += 2) {
            Resultado r = AnalizadorTexto.paralelo(texto, palabra, secuencia, h);
            if (!r.igualA(ref)) {
                System.out.println("Error: resultado distinto con " + h + " hilos");
                return;
            }
        }
        System.out.println("Verificacion con 2 y 4 hilos: correcta");
        System.out.println();

        System.out.println("===== RESULTADOS DE RENDIMIENTO =====");
        medir("Original", texto);
        medir("Ampliado (100 veces)", ampliado);
    }

    // Mide el tiempo promedio con 1, 2 y 4 hilos y muestra la tabla
    static void medir(String nombre, String texto) throws Exception {
        // Calentamiento de la JVM (no se mide)
        for (int i = 0; i < 3; i++) {
            for (int h : hilos) ejecutar(texto, h);
        }

        System.out.println();
        System.out.println("Conjunto de datos: " + nombre);
        System.out.printf("%-15s %-22s %-15s%n", "Hilos", "Tiempo promedio (ms)", "Aceleracion");

        double tiempoSecuencial = 0;
        for (int h : hilos) {
            long suma = 0;
            for (int i = 0; i < repeticiones; i++) {
                long inicio = System.nanoTime();
                ejecutar(texto, h);
                suma += System.nanoTime() - inicio;
            }
            double ms = suma / (double) repeticiones / 1_000_000.0;
            if (h == 1) tiempoSecuencial = ms;

            String etiqueta = (h == 1) ? "1 (secuencial)" : String.valueOf(h);
            System.out.printf("%-15s %-22.4f %-15.2f%n", etiqueta, ms, tiempoSecuencial / ms);
        }
    }

    // 1 hilo = secuencial, 2 o 4 = paralelo con Thread
    static Resultado ejecutar(String texto, int h) throws InterruptedException {
        if (h == 1) return AnalizadorTexto.secuencial(texto, palabra, secuencia);
        return AnalizadorTexto.paralelo(texto, palabra, secuencia, h);
    }

    // Quita el encabezado y pie de Project Gutenberg
    static String quitarCabecera(String t) {
        int ini = t.indexOf("*** START OF");
        int fin = t.indexOf("*** END OF");
        ini = (ini >= 0) ? t.indexOf('\n', ini) + 1 : 0;
        if (fin < 0) fin = t.length();
        return t.substring(ini, fin);
    }
}