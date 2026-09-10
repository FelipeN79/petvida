package br.edu.iftm.petvida.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.edu.iftm.petvida.model.Animal;
import br.edu.iftm.petvida.model.Tutor;

@Repository
public class AnimalRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public void salvar(Animal animal) {
        String sql = "INSERT INTO animal (id_animal, nome, especie, idade, tutor_id_tutor) VALUES (?, ?, ?, ?, ?)";
        jdbc.update(sql, animal.getId(), animal.getNome(), animal.getEspecie(),
                animal.getIdade(), animal.getTutor().getId());
    }

    public Animal buscarPorId(int id) {
        String sql = "SELECT a.id_animal, a.nome, a.especie, a.idade, " +
                      "t.id_tutor, t.nome AS nome_tutor, t.telefone " +
                      "FROM animal a JOIN tutor t ON a.tutor_id_tutor = t.id_tutor " +
                      "WHERE a.id_animal = ?";
        return jdbc.queryForObject(sql, (rs, rowNum) -> {
            Tutor tutor = new Tutor(
                    rs.getInt("id_tutor"),
                    rs.getString("nome_tutor"),
                    rs.getString("telefone")
            );
            return new Animal(
                    rs.getInt("id_animal"),
                    rs.getString("nome"),
                    rs.getString("especie"),
                    rs.getInt("idade"),
                    tutor
            );
        }, id);
    }

    public int contarAnimais() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM animal", Integer.class);
    }

    public double mediaIdade() {
        return jdbc.queryForObject("SELECT AVG(idade * 1.0) FROM animal", Double.class);
    }

    public String animalMaisVelho() {
        String sql = "SELECT nome FROM animal ORDER BY idade DESC LIMIT 1";
        return jdbc.queryForObject(sql, String.class);
    }
}