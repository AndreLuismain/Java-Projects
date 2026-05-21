import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class LeitorCsv {

    // Adicionamos o <T> para avisar o Java que é genérico, e a Function como parâmetro
    public <T> List<T> processarArquivo(String caminhoArquivo, Function<String, Optional<T>> conversor) throws ArquivoInvalidoException {
        List<T> listaDados = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                if (linha.startsWith("\"") && linha.endsWith("\"")) {
                    linha = linha.substring(1, linha.length() - 1);
                }

                // 3. Usa a função injetada para converter a linha
                Optional<T> dadoValido = conversor.apply(linha);

                // 4. Se a conversão deu certo, adiciona na lista genérica
                dadoValido.ifPresent(dado -> listaDados.add(dado));
            }
        } catch (IOException e) {
            throw new ArquivoInvalidoException("Arquivo não encontrado no caminho: " + caminhoArquivo);
        }
        return listaDados;
    }
}