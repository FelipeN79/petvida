package br.edu.iftm.petvida.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.edu.iftm.petvida.model.Animal;
import br.edu.iftm.petvida.model.Tutor;
import br.edu.iftm.petvida.repository.AnimalRepository;
import br.edu.iftm.petvida.repository.TutorRepository;

@Controller
public class PetVidaController {

    private static final int MEU_ID = 116;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @GetMapping("/ficha_16")
    public String ficha(Model model) {
        Animal animal = animalRepository.buscarPorId(MEU_ID);
        model.addAttribute("nomeAnimal", animal.getNome());
        model.addAttribute("especieAnimal", animal.getEspecie());
        model.addAttribute("idadeAnimal", animal.getIdade());
        model.addAttribute("nomeTutor", animal.getTutor().getNome());
        model.addAttribute("telefoneTutor", animal.getTutor().getTelefone());
        return "ficha";
    }

    @GetMapping("/tutor_16")
    public String tutor(Model model) {
        Tutor tutor = tutorRepository.buscarPorId(MEU_ID);
        int qtdAnimais = tutorRepository.contarAnimaisDoTutor(MEU_ID);
        model.addAttribute("nomeTutor", tutor.getNome());
        model.addAttribute("telefoneTutor", tutor.getTelefone());
        model.addAttribute("qtdAnimais", qtdAnimais);
        return "tutor";
    }

    @GetMapping("/resumo_16")
    public String resumo(Model model) {
        int totalAnimais = animalRepository.contarAnimais();
        double media = animalRepository.mediaIdade();
        String mediaFormatada = String.format("%.2f", media);
        String maisVelho = animalRepository.animalMaisVelho();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dataHora = LocalDateTime.now().format(formatter);

        model.addAttribute("totalAnimais", totalAnimais);
        model.addAttribute("mediaIdade", mediaFormatada);
        model.addAttribute("animalMaisVelho", maisVelho);
        model.addAttribute("dataHora", dataHora);
        return "resumo";
    }
}