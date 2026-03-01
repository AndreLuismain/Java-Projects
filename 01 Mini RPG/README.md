#  Sistema Mini RPG - Desafio 01

Um simulador simples de ataques de RPG baseado em turnos e classes, desenvolvido inteiramente em **Java**. Este projeto tem como objetivo principal demonstrar a aplicação prática de conceitos fundamentais de **Programação Orientada a Objetos (POO)**.

##  Funcionalidades

- Criação de personagens com atributos únicos (Nome, Vida, Força, Magia, Mana).
- Sistema de classes específicas (`Guerreiro` e `Mago`) derivadas de uma classe base.
- Ataques personalizados e dinâmicos dependendo da classe do personagem.
- Sistema de verificação de Mana para os Magos (o ataque só ocorre se houver mana suficiente).
- Rastreador global de personagens instanciados.

##  Conceitos Técnicos Aplicados

Este repositório serve como uma demonstração prática dos seguintes conceitos do Java:

* **Herança (`extends`):** As subclasses `Guerreiro` e `Mago` herdam atributos e métodos da superclasse `Personagem`, evitando repetição de código.
* **Polimorfismo (`@Override`):** O método `atacar()` foi sobrescrito nas subclasses para garantir que Guerreiros e Magos tenham comportamentos de ataque distintos.
* **Encapsulamento:** Uso de modificadores de acesso (`protected` e `private`) para proteger os dados (como a variável `totalPersonagens`).
* **Membros Estáticos (`static`):** Implementação de um contador global que pertence à classe `Personagem` como um todo, e não a instâncias individuais.
* **Coleções e Iterações:** Utilização de `ArrayList` para agrupar os personagens em uma equipe e laço `for-each` para executar as ações de forma limpa e iterativa.
