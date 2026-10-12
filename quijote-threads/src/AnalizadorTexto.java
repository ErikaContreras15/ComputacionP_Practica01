import java.util.Locale;

public final class AnalizadorTexto {

    private AnalizadorTexto() {}

    public static boolean esCaracterDePalabra(char c) {
        return Character.isLetterOrDigit(c);
    }

   
    public static Resultado analizar(String texto, int inicio, int fin,
                                     String palabra, String secuencia) {
        Resultado r = new Resultado();
        int i = inicio;
        while (i < fin) {
            // saltar separadores
            while (i < fin && !esCaracterDePalabra(texto.charAt(i))) i++;
            if (i >= fin) break;
            int ini = i;
            while (i < fin && esCaracterDePalabra(texto.charAt(i))) i++;

            String w = texto.substring(ini, i).toLowerCase(Locale.ROOT);
            r.totalPalabras++;
            if (w.equals(palabra)) r.aparicionesPalabra++;
            if (w.contains(secuencia)) r.palabrasConSecuencia++;
        }
        return r;
    }

    /** Version secuencial: un unico flujo de ejecucion sobre todo el texto. */
    public static Resultado secuencial(String texto, String palabra, String secuencia) {
        return analizar(texto, 0, texto.length(), palabra, secuencia);
    }


    public static int[] limites(String texto, int nHilos) {
        int n = texto.length();
        int[] lim = new int[nHilos + 1];
        lim[0] = 0;
        lim[nHilos] = n;
        for (int k = 1; k < nHilos; k++) {
            int corte = Math.max((int) ((long) n * k / nHilos), lim[k - 1]);
            while (corte < n && corte > 0
                   && esCaracterDePalabra(texto.charAt(corte))
                   && esCaracterDePalabra(texto.charAt(corte - 1))) {
                corte++;
            }
            lim[k] = corte;
        }
        return lim;
    }

    /** Version paralela: un Thread por bloque, start() + join(), y combinacion. */
    public static Resultado paralelo(String texto, String palabra, String secuencia,
                                     int nHilos) throws InterruptedException {
        int[] lim = limites(texto, nHilos);
        HiloAnalizador[] hilos = new HiloAnalizador[nHilos];

        for (int k = 0; k < nHilos; k++) {
            hilos[k] = new HiloAnalizador(texto, lim[k], lim[k + 1], palabra, secuencia);
            hilos[k].start();
        }

        Resultado total = new Resultado();
        for (HiloAnalizador h : hilos) {
            h.join();                       // esperar a que termine
            total.combinar(h.getResultado()); // sumar su resultado parcial
        }
        return total;
    }
}
