package br.edu.iftm.petvida;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.edu.iftm.petvida.model.Animal;
import br.edu.iftm.petvida.model.Tutor;
import br.edu.iftm.petvida.repository.AnimalRepository;
import br.edu.iftm.petvida.repository.TutorRepository;

@SpringBootApplication
public class PetvidaApplication implements CommandLineRunner {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AnimalRepository animalRepository;

    public static void main(String[] args) {
        SpringApplication.run(PetvidaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Tutores da Seção 2 (fixos)
        Tutor marina = new Tutor(1, "Marina Alves", "34 99101-0001");
        Tutor carlos = new Tutor(2, "Carlos Prado", "34 99101-0002");

        tutorRepository.salvar(marina);
        tutorRepository.salvar(carlos);

        // Seu tutor (semente NN=16)
        Tutor meuTutor = new Tutor(116, "Felipe", "34 91616-1616");
        tutorRepository.salvar(meuTutor);

        // Animais da Seção 2 (fixos)
        animalRepository.salvar(new Animal(2, "Mimi", "gato", 3, marina));
        animalRepository.salvar(new Animal(3, "Thor", "cao", 1, carlos));
        animalRepository.salvar(new Animal(4, "Lila", "gato", 11, carlos));

        // Seu animal (semente NN=16)
        animalRepository.salvar(new Animal(116, "Pet_16", "cao", 16, meuTutor));
    }
}