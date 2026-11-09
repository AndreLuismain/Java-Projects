package br.com.andreluismain.editaisic.integration.gemini;

import br.com.andreluismain.editaisic.domain.model.Opportunity;
import br.com.andreluismain.editaisic.domain.model.Profile;
import br.com.andreluismain.editaisic.dto.response.GeminiAnalysisDto;

/**
 * Contrato de integração com provedores de Inteligência Artificial para análise semântica.
 *
 * <p>Permite a substituição do Gemini por outros provedores (OpenAI, Anthropic, mocks locais)
 * mantendo o desacoplamento do caso de uso central.</p>
 */
public interface LlmGateway {

    /**
     * Executa a análise semântica e classificação de aderência entre um perfil e uma oportunidade.
     *
     * @param profile perfil acadêmico do usuário
     * @param opportunity oportunidade/edital coletado
     * @return {@link GeminiAnalysisDto} estruturado conforme o contrato JSON estrito
     */
    GeminiAnalysisDto analyze(Profile profile, Opportunity opportunity);

    /**
     * Retorna o nome do modelo atualmente em uso (ex: "gemini-1.5-flash").
     */
    String getModelName();

    /**
     * Retorna a versão do template de prompt configurado.
     */
    String getPromptVersion();
}
