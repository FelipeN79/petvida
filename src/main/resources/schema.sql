CREATE TABLE tutor (
    id_tutor INT PRIMARY KEY,
    nome VARCHAR(45),
    telefone VARCHAR(45)
);

CREATE TABLE animal (
    id_animal INT PRIMARY KEY,
    nome VARCHAR(45),
    especie VARCHAR(45),
    idade INT,
    tutor_id_tutor INT,
    FOREIGN KEY (tutor_id_tutor) REFERENCES tutor(id_tutor)
);