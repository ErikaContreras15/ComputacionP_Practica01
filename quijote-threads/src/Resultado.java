
public class Resultado {
    public long totalPalabras;
    public long aparicionesPalabra;
    public long palabrasConSecuencia;

    public void combinar(Resultado otro) {
        totalPalabras += otro.totalPalabras;
        aparicionesPalabra += otro.aparicionesPalabra;
        palabrasConSecuencia += otro.palabrasConSecuencia;
    }

    public boolean igualA(Resultado o) {
        return totalPalabras == o.totalPalabras
            && aparicionesPalabra == o.aparicionesPalabra
            && palabrasConSecuencia == o.palabrasConSecuencia;
    }

    @Override
    public String toString() {
        return "total=" + totalPalabras
             + ", palabra=" + aparicionesPalabra
             + ", secuencia=" + palabrasConSecuencia;
    }
}
