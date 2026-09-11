package br.edu.iftm.petvida.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.edu.iftm.petvida.repository.AnimalRepository;

@Controller // <-- adicionado
public class ConsultaController {

    @Autowired
    AnimalRepository repository;

    @GetMapping("consulta")
    public String consulta(Model model) {
        model.addAttribute("animal", repository.buscarPorId(2));
        return "consulta.html";
    }
}