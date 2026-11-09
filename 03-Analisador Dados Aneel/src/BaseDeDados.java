import java.math.BigDecimal;
import java.time.LocalDate;

public record BaseDeDados(int id,
                          LocalDate data,
                          FonteEnergia tipoGeracao,
                          int qtdUsinas,
                          BigDecimal potenciaInstaladaKw,
                          int mesReferencia,
                          int anoReferencia
) {
    //tratamento dos erros do arquivo
    public BaseDeDados {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data não pode ser nula!");
        }
    }
}
