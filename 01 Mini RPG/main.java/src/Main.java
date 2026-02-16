//Rpg com personagens e ataques desafio 01
//Bibliotecas
import java.util.ArrayList;

//Classe Personagem/atributos
class Personagem{
    protected String nome;
    protected int vida;
    //contador
    private static int totalPersonagens = 0;
    //Construtor
    public Personagem(String nome, int vida){
        this.nome = nome;
        this.vida = vida;
        totalPersonagens++;
    }
    public void atacar() {
        System.out.println(nome + " atacou um personagem! ");
    }
    //contador
    public static int getTotalPersonagens() {
        return totalPersonagens;
    }
}
//subclasse Guerreiro
class Guerreiro extends Personagem{
    int forca;
    public Guerreiro(String nome, int vida, int forca){
        super(nome, vida);
        this.forca = forca;
    }
    @Override
    public void atacar(){
        System.out.println("o guerreiro atacou! Causando " + forca + " de dano!! ");
    }
}
//subclasse mago
class Mago extends Personagem{
    int magia;
    int mana;
    public Mago(String nome, int vida, int magia, int mana){
        super(nome, vida);
        this.magia = magia;
        this.mana = mana;
    }
    @Override
    public void atacar(){
        if (mana >= magia) {
            vida = magia - vida;
            this.mana -= magia;
            System.out.println("-"+ magia +"!!! Guerreiro Ferido!!!");
            System.out.println("O mago " + nome + " atacou causando "+ magia +" de dano! ");
        } else {
            System.out.println("O mago " + nome + " tentou atacar mas estava sem mana!");
        }
    }
}

//main
public class Main {
     public static void main(String[] args) {
        ArrayList<Personagem> equipe = new ArrayList<>();
        equipe.add(new Guerreiro("Guerreiro", 10, 10));
        equipe.add(new Mago("Merlin", 80, 30, 30));
        equipe.add(new Mago("Gandalf", 80, 40, 5));

        //for-each
        for (Personagem nome : equipe){
            nome.atacar();
        }
        //Chamar contador
        Personagem.getTotalPersonagens();
        System.out.println("Personagens Totais: "+Personagem.getTotalPersonagens());
    }
}

