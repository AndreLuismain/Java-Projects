package br.com.minirpg.api.service;

import br.com.minirpg.api.model.Personagem;
import br.com.minirpg.api.repository.PersonagemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita a "mágica" do Mockito
class PersonagemServiceTest {

    @InjectMocks
    private PersonagemService service; // O Cozinheiro de verdade que vamos testar

    @Mock
    private PersonagemRepository repository; // A Despensa "de mentira" (Mock)

    @Test
    void deveAtacarComSucessoEDiminuirVidaDoAlvo() {
        // 1. Cenário (Arrange): Criando nossos "cobaias" de laboratório
        Personagem guerreiro = new Personagem();
        guerreiro.setId(1L);
        guerreiro.setNome("Guerreiro");
        guerreiro.setNivel(10); // Dano base = 10 * 2 = 20
        guerreiro.setAgilidade(5); // Dano total = 20 + 5 = 25

        Personagem mago = new Personagem();
        mago.setId(2L);
        mago.setNome("Mago");
        mago.setPontosVida(100);

        // Ensinando o banco de dados "fake" a responder quando o Service procurar os IDs
        when(repository.findById(1L)).thenReturn(Optional.of(guerreiro));
        when(repository.findById(2L)).thenReturn(Optional.of(mago));

        // 2. Ação (Act): O guerreiro ataca o mago
        String resultado = service.atacar(1L, 2L);

        // 3. Verificação (Assert): O que deve ter acontecido?
        // Vida original (100) - Dano (25) = 75
        assertEquals(75, mago.getPontosVida(), "A vida do alvo deveria ser 75");

        // Verifica se o Service realmente mandou o banco salvar a nova vida do Mago
        verify(repository, times(1)).save(mago);
    }
}