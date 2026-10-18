# 01 · Sistema Mini RPG (Fundamentos de POO em Java)

Simulador de combate em turnos baseado em classes de personagens, desenvolvido em **Java** para demonstrar a aplicação prática dos pilares de **Programação Orientada a Objetos (POO)**.

---

## 1. Objetivo do Código

Modelar uma hierarquia de personagens de RPG (`Personagem`, `Guerreiro` e `Mago`) onde cada subclasse especializa o comportamento de ataque e gerencia seus próprios atributos de combate (força física vs. magia e consumo de mana), mantendo a contagem global de instâncias criadas em memória.

---

## 2. Conceitos de Engenharia e POO Aplicados

* **Herança (`extends`):** As subclasses `Guerreiro` e `Mago` estendem a superclasse `Personagem`, reutilizando estado comum (`nome`, `vida`) e construtores (`super(...)`).
* **Polimorfismo (`@Override`):** Sobrescrita do método `atacar()` em cada subclasse, permitindo que uma coleção heterogênea (`ArrayList<Personagem>`) execute comportamentos distintos em tempo de execução (Dynamic Dispatch).
* **Encapsulamento:** Controle de visibilidade de atributos com modificadores `protected` (acesso na hierarquia) e `private` (isolamento do contador estático `totalPersonagens`).
* **Membros de Classe (`static`):** Rastreamento do total de objetos instanciados via atributo e método estático `Personagem.getTotalPersonagens()`.
* **Coleções Genéricas:** Uso de `ArrayList<Personagem>` e iteração `for-each` para processamento polimórfico da equipe.

---

## 3. Estrutura do Código

```text
01 Mini RPG/
├── README.md
└── main.java/
    └── src/
        └── Main.java   # Implementação de Personagem, Guerreiro, Mago e ponto de entrada
```

---

## 4. Como Compilar e Executar

Certifique-se de possuir o **JDK 17+** instalado no ambiente. No terminal:

```powershell
cd "01 Mini RPG/main.java/src"
javac Main.java
java Main
```

### Saída Esperada
```text
o guerreiro atacou! Causando 10 de dano!! 
-30!!! Guerreiro Ferido!!!
O mago Merlin atacou causando 30 de dano! 
O mago Gandalf tentou atacar mas estava sem mana!
Personagens Totais: 3
```
