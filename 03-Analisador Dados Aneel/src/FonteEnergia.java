public sealed interface FonteEnergia permits FonteRenovavel, FonteNaoRenovavel {
    String sigla();
}

record FonteRenovavel(String sigla) implements FonteEnergia {}

record FonteNaoRenovavel(String sigla) implements FonteEnergia {}