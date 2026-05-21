import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class central {
    public static void main(String[] args) {
        Scanner ler = new Scanner(System.in);
        LeitorCsv leitor = new LeitorCsv();
        List<BaseDeDados> DadosLidos = new ArrayList<>(); // Cria a lista vazia primeiro
        boolean arquivoLidoComSucesso = false;

        do {//Usuario escolhe qual arquivo vai ler
            System.out.print("Digite o caminho do arquivo CSV (ex: src/234.csv): ");
            String caminhoDoCsv = ler.nextLine();

            try {
                // Tenta ler e já guarda o resultado na lista que criamos lá em cima!
                DadosLidos = leitor.processarArquivo(caminhoDoCsv, central::converterLinha);

                // Se chegou nesta linha, não deu erro! Podemos sair do laço.
                arquivoLidoComSucesso = true;
                System.out.println("Arquivo lido com sucesso!\n");

            } catch (ArquivoInvalidoException e) {
                // Se o arquivo não existir, cai aqui, dá a bronca e o laço repete
                System.out.println("ERRO: " + e.getMessage());
                System.out.println("Por favor, tente novamente.\n");
            }
        } while (!arquivoLidoComSucesso);

        // --- A partir daqui o arquivo já tá garantido na memória ---
        System.out.println("--- Dados processados ---");

        //Lista que guarda quais comandos foram feitos do menu
        LinkedList<Integer> historicoComandos = new LinkedList<>();
        int opcao = 0;

        while (opcao != 5) {
            System.out.println("\n--------- Menu ---------");
            System.out.println("""
                    Opção 1: Listar Potência por ano
                    Opção 2: Buscar por Usina
                    Opção 3: Filtrar por tipo de geração
                    Opção 4: Maior quantidade de usinas
                    Opção 5: Sair do Menu\s
                    """);

            System.out.print("Escolha uma opção: ");
            opcao = ler.nextInt();
            ler.nextLine();

            historicoComandos.addFirst(opcao);
            if (historicoComandos.size() > 5) {
                historicoComandos.removeLast();
            }

            switch (opcao) {
                case 1:
                    System.out.println("\n--- Potência Total por Ano ---");
                    // Abre o fluxo de dados (Stream) da nossa lista
                    TreeMap<Integer, BigDecimal> potenciaPorAno = DadosLidos.stream()
                            // Agrupa os elementos lidos
                            .collect(Collectors.groupingBy(
                                    // Usa o ano de referência como chave (critério de agrupamento)
                                    BaseDeDados::anoReferencia,
                                    // Instancia um TreeMap para garantir que os anos fiquem ordenados do menor para o maior
                                    TreeMap::new,
                                    // Soma as potências de cada usina desse ano, começando do valor ZERO
                                    Collectors.reducing(BigDecimal.ZERO, BaseDeDados::potenciaInstaladaKw, BigDecimal::add)
                            ));

                    // Percorre (itera) o mapa gerado, passando por cada par de Ano e Potência
                    for (Map.Entry<Integer, BigDecimal> entrada : potenciaPorAno.entrySet()) {
                        // Imprime a chave (ano) e o valor (potência total calculada)
                        System.out.println("Ano: " + entrada.getKey() + " | Potência Total: " + entrada.getValue() + " kW");
                    }
                    // Encerra a execução da opção 1
                    break;

                case 2:
                    System.out.println("Digite a sigla da Usina (Ex: UFV):");
                    // Captura a sigla digitada pelo usuário
                    String siglaUsina = ler.nextLine();

                    // Busca uma usina no fluxo de dados (Retorna Optional para evitar erro se não achar nada)
                    Optional<BaseDeDados> usinaEncontrada = DadosLidos.stream()
                            // Filtra comparando a sigla ignorando maiúsculas/minúsculas
                            .filter(usina -> usina.tipoGeracao().sigla().equalsIgnoreCase(siglaUsina))
                            // Pega a primeira usina que passar no filtro acima
                            .findFirst();

                    // Verifica se o Optional não está vazio (ou seja, se achou a usina)
                    if (usinaEncontrada.isPresent()) {
                        // Extrai a usina de dentro da caixa do Optional
                        BaseDeDados usina = usinaEncontrada.get();

                        // Switch do Java 21 (Pattern Matching) que avalia a CLASSE do objeto
                        switch (usina.tipoGeracao()) {
                            // Se for da classe Renovável, salva na variável 'r' e imprime a mensagem
                            case FonteRenovavel r ->
                                    System.out.println("Esta usina usa energia limpa (Renovável)! Sigla extraída: " + r.sigla());
                            // Se for da classe Não Renovável, salva na variável 'n' e imprime a mensagem
                            case FonteNaoRenovavel n ->
                                    System.out.println("Esta usina usa fontes Não Renováveis! Sigla extraída: " + n.sigla());
                            // Trava de segurança exigida pelo Java caso apareça um tipo que não conhecemos
                            default -> throw new IllegalStateException("Tipo de geração desconhecido ou nulo!");
                        }
                    } else {
                        // Cai aqui caso o .isPresent() seja falso
                        System.out.println("Usina não encontrada!");
                    }
                    // Encerra a execução da opção 2
                    break;

                case 3:
                    System.out.println("Digite o tipo de geração para somar a potência (Ex: UFV):");
                    // Captura a sigla que servirá de filtro para a soma
                    String tipoSoma = ler.nextLine();

                    // Calcula a soma rodando um fluxo de dados na lista
                    BigDecimal somaPotencia = DadosLidos.stream()
                            // Separa apenas as usinas que tenham a mesma sigla digitada
                            .filter(usina -> usina.tipoGeracao().sigla().equalsIgnoreCase(tipoSoma))
                            // "Pega" apenas a coluna da potência (descarta as outras colunas da usina)
                            .map(BaseDeDados::potenciaInstaladaKw)
                            // Acumula/Soma todas essas potências, partindo do ZERO
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Imprime a soma final calculada
                    System.out.println("Soma total de potência para " + tipoSoma + ": " + somaPotencia + " kW");
                    // Encerra a execução da opção 3
                    break;

                case 4:
                    // Busca o maior elemento da lista (Retorna Optional caso a lista esteja vazia)
                    Optional<BaseDeDados> maiorQuantidade = DadosLidos.stream()
                            // Encontra o maior usando como critério a coluna de quantidade de usinas
                            .max(Comparator.comparingInt(BaseDeDados::qtdUsinas));

                    // Verifica se conseguiu achar algum vencedor
                    if (maiorQuantidade.isPresent()) {
                        System.out.println("Registro com maior quantidade de usinas:");
                        // Imprime todos os dados da usina vencedora (chama o método toString() do Record)
                        System.out.println(maiorQuantidade.get());
                    } else {
                        // Mensagem se a lista inteira de dados estiver vazia
                        System.out.println("Nenhum dado encontrado.");
                    }
                    // Encerra a execução da opção 4
                    break;

                case 5:
                    // Mensagem de despedida para fechar o laço do while do menu
                    System.out.println("Saindo do sistema...");
                    break;

                default:
                    // Cai aqui caso o usuário digite 6, 7, 0, etc.
                    System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    //MÉTODOS QUE VIERAM DO LEITOR CSV (AGORA SÃO STATIC)
    private static Optional<BaseDeDados> converterLinha(String linha) {
        List<String> colunas = separarColunas(linha);

        try {
            // 1. DISPARA A EXCEÇÃO UNCHECKED se vier faltando coluna
            var dado = getBaseDeDados(colunas);
            return Optional.of(dado);

        } catch (LinhaCorrompidaException e) {
            // 2. CAPTURA A SUA EXCEÇÃO E EXIBE O AVISO
            System.err.println("Aviso - linha ignorada: " + e.getMessage());
            return Optional.empty(); // Retorna vazio para o LeitorCsv ignorar e continuar lendo o resto

        } catch (Exception e) {
            // Captura qualquer outro erro de conversão (ex: letra onde deveria ter número)
            System.err.println("Aviso - linha ignorada por erro de formato: " + e.getMessage());
            return Optional.empty();
        }
    }
    //Separa as colunas e o que vai ter em cada uma e procura inconsistências
    private static BaseDeDados getBaseDeDados(List<String> colunas) {
        // Verifica se a linha tem as 7 colunas esperadas pelo arquivo da ANEEL
        if (colunas.size() < 7) {
            // Se faltar dado, dispara a nossa Unchecked Exception (Fail-fast) para o método que chamou
            throw new LinhaCorrompidaException("A linha possui apenas " + colunas.size() + " colunas.");
        }

        // Pega a primeira coluna (índice 0), remove espaços em branco (trim) e converte para Inteiro
        int id = Integer.parseInt(colunas.get(0).trim());

        // Pega a segunda coluna, limpa os espaços e converte para o formato de Data do Java
        LocalDate data = LocalDate.parse(colunas.get(1).trim());

        // Pega a terceira coluna (que é a sigla em formato de texto) e limpa os espaços
        String siglaOriginal = colunas.get(2).trim();

        // Categoriza se a energia é renovável ou não usando o Switch Expression do Java
        // Ele retorna o objeto instanciado direto para a variável 'categoria'
        FonteEnergia categoria = switch (siglaOriginal) {
            // Se a sigla for EOL, UFV ou CGH, cria uma instância do record Renovável
            case "EOL", "UFV", "CGH" -> new FonteRenovavel(siglaOriginal);
            // Qualquer outra sigla cai no default e vira uma instância do record Não Renovável
            default -> new FonteNaoRenovavel(siglaOriginal);
        };

        // Pega a quarta coluna, limpa espaços e converte para Inteiro (quantidade de usinas)
        int qtdUsinas = Integer.parseInt(colunas.get(3).trim());

        // Tratamento da quinta coluna (Potência): tira possíveis aspas duplas, troca a vírgula brasileira por ponto americano e tira os espaços
        String potenciaStr = colunas.get(4).replace("\"", "").replace(",", ".").trim();

        // Verifica se o texto da potência ficou vazio. Se sim, usa valor ZERO. Se não, converte para BigDecimal (alta precisão)
        BigDecimal potenciaInstaladaKw = potenciaStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(potenciaStr);

        // Pega a sexta coluna, limpa e converte para Inteiro (Mês)
        int mesReferencia = Integer.parseInt(colunas.get(5).trim());

        // Pega a sétima coluna, limpa e converte para Inteiro (Ano)
        int anoReferencia = Integer.parseInt(colunas.get(6).trim());

        // Monta o objeto final chamando o construtor do record e devolve para quem pediu
        return new BaseDeDados(id, data, categoria, qtdUsinas, potenciaInstaladaKw, mesReferencia, anoReferencia);
    }

    private static List<String> separarColunas(String linha) {
        // Cria a lista vazia que vai guardar cada texto separado
        List<String> colunas = new ArrayList<>();

        // Cria um construtor de textos (mais eficiente que concatenar Strings com '+') para guardar as letras da coluna atual
        StringBuilder valorAtual = new StringBuilder();

        // Bandeira (flag) para avisar se o laço está lendo um texto dentro de aspas ou não
        boolean dentroDeAspas = false;

        // Laço for que passa por cada letra (caractere) da linha inteira
        for (int i = 0; i < linha.length(); i++) {
            // Pega o caractere exato da posição 'i'
            char c = linha.charAt(i);

            // Verifica se o caractere atual é uma aspa dupla
            if (c == '"') {
                // Inverte a bandeira (se estava false vira true, se estava true vira false)
                dentroDeAspas = !dentroDeAspas;

                // Se não for aspa, verifica se é uma VÍRGULA e se NÃO ESTAMOS dentro de aspas
            } else if (c == ',' && !dentroDeAspas) {
                // Achamos um separador válido! Salva tudo o que juntamos até agora na lista de colunas
                colunas.add(valorAtual.toString());

                // Zera o construtor de textos para começar a montar a próxima coluna
                valorAtual.setLength(0);

                // Se for uma letra normal (ou uma vírgula que está protegida dentro das aspas)
            } else {
                // Adiciona o caractere na palavra que estamos montando
                valorAtual.append(c);
            }
        }

        // Quando o laço termina, a última palavra montada não tem vírgula depois dela, então adicionamos manualmente
        colunas.add(valorAtual.toString());

        // Devolve a lista completa e separada
        return colunas;
    }
}

