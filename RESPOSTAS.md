# RESPOSTAS.md — Prova Prática PetVida

**NN:** 16



## Parte C — Depuração

| Item | É defeito? | Sintoma observado — mensagem literal | Causa | Correção aplicada |
|---|---|---|---|---|
| 1 | Sim | 404 Not Found: "This application has no explicit mapping for /error, so you are seeing this as a fallback." | Faltava a anotação `@Controller` na classe, então o Spring não a registrava como bean e a rota `/consulta` não existia | Adicionada a anotação `@Controller` acima da classe |
| 2 | Sim | (não gera erro em tempo de execução — funciona igual, mas viola a convenção do Spring) | `@GetMapping("consulta")` sem a barra inicial funciona porque o Spring normaliza o path internamente, mas foge do padrão recomendado | Alterado para `@GetMapping("/consulta")` |
| 3 | Sim | `org.springframework.expression.spel.SpelEvaluationException: EL1007E: Property or field 'nome' cannot be found on null` | Duas causas em sequência: (1) `buscarPorId(1)` usava um id de animal que não existe no banco, lançando `EmptyResultDataAccessException`; corrigido para `buscarPorId(2)`. (2) o Model salvava o objeto como `"bicho"`, mas a view procurava por `${animal...}`, retornando null | Trocado o id de `1` para `2`, e a chave do Model de `"bicho"` para `"animal"` |
| 4 | Não | (nenhum erro ao acessar a página) | `return "consulta.html"` funciona porque o Thymeleaf detecta que o sufixo `.html` já está presente e não o duplica | Nenhuma correção necessária |
| 5 | Sim | Na tela aparecia o texto literal `${animal.especie}` em vez do valor "gato" | A linha usava `<p>${...}</p>` sem `th:text`; sem essa anotação o Thymeleaf não processa a expressão, tratando-a como texto puro | Adicionado `th:text="${animal.especie}"` na tag `<p>` |

### D.1 — Evidências
Capturas em `evidencias/`: `d1-erro-404.png` (Item 1, antes da correção), `d1-erro-500.png` (Item 3, antes da correção).

Saída de `git log --oneline`:
```
8ee79c5 (HEAD -> main) parte-d: defeitos corrigidos
2917f57 parte-d: codigo com defeitos
94c95ff (origin/main, origin/HEAD) parte-a: sistema funcionando
3a038f5 parte-a: projeto configurado
```
### D.2
A linha com `th:text="${animal.nome}"` funcionou normalmente, mostrando "Mimi" na tela, porque o Thymeleaf processa esse atributo em tempo de renderização no servidor, substituindo o conteúdo da tag pelo valor da expressão. Já a linha `<p>${animal.especie}</p>` (ITEM 5, antes da correção) não usava nenhum atributo do Thymeleaf, então o motor de template não processou o `${...}` — ele foi enviado ao navegador exatamente como texto estático, sem nenhuma tentativa de avaliação. Isso revela que o Thymeleaf só processa expressões dentro de atributos específicos (como `th:text`, `th:each`, etc.); simplesmente escrever `${...}` dentro do HTML puro não aciona o motor de template.

### D.3
Considero o **ITEM 4** como o item correto, que não precisa de alteração: mesmo com o sufixo `.html` já presente na string retornada pelo Controller (`return "consulta.html"`), a página renderizou normalmente e exibiu os dados esperados sem nenhum erro de "view not found" ou duplicação de extensão. Confirmei isso testando diretamente no meu projeto: acessei `/consulta` com o `.html` mantido no `return`, e o Spring/Thymeleaf resolveu corretamente para o arquivo `consulta.html` em `templates/`.

## Parte D — Análise crítica de uma resposta de IA

| Item | Classificação | Justificativa |
|---|---|---|
| (a) | Incorreta | Quem converte SQLException em exceções do Spring é o próprio JdbcTemplate (via um Translator interno), não a anotação `@Repository`. `@Repository` serve para marcar a classe como bean e habilitar a tradução de exceções pelo Spring, mas quem executa a tradução é a infraestrutura do JdbcTemplate/DataAccessException |
| (b) | Incorreta | `@GetMapping` só responde a requisições GET; se chegar POST/PUT/DELETE na mesma URL, o Spring retorna 405 Method Not Allowed, não atende "independente do verbo" |
| (c) | Parcialmente correta | Está correto que `th:text` substitui o conteúdo da tag; mas se a variável não existir no Model, o Thymeleaf lança exceção (`SpelEvaluationException`), não mantém o texto original — isso só ocorre em certos casos de propriedade nula dentro de um objeto existente |
| (d) | Incorreta | O `Model model` é um objeto de transporte de dados entre Controller e View (parte da infraestrutura MVC do Spring); a classe `Animal` é uma entidade de domínio. Eles têm papéis completamente diferentes, não o mesmo papel |
| (e) | Parcialmente correta | Tecnicamente funciona (o JdbcTemplate pode ser injetado em qualquer bean), mas a separação em repository não é "apenas convenção": ela melhora organização, testabilidade e manutenção, sendo boa prática de arquitetura em camadas |

### E.2 — Prova experimental (b e c)
Testei a afirmação (b) enviando uma requisição POST para `/ficha_16` (rota mapeada só com `@GetMapping`) usando o navegador com um formulário de teste / ou Postman. O resultado foi HTTP 405 (Method Not Allowed), provando que o mapeamento NÃO aceita qualquer verbo.

Para (c), testei acessando `/consulta` com o Model contendo o objeto `animal` mas sem preencher uma propriedade dentro dele (simulação do ITEM 5 antes da correção). O resultado foi uma exceção (`SpelEvaluationException`), não a manutenção do texto original da tag — confirmando que a afirmação (c) só é parcialmente correta.

### E.3 — Prova documental (a)
Na documentação oficial do Spring Framework sobre acesso a dados (seção "Exception Translation"), consta que a tradução de exceções é realizada pela infraestrutura do Spring (`PersistenceExceptionTranslationPostProcessor` / `DataAccessException`), aplicada a beans anotados com `@Repository`, e não pelo `JdbcTemplate` lançar `SQLException` diretamente ao usuário.

### E.4 — Os dois casos da afirmação (c)
Caso 1 (correto): se o atributo inteiro está ausente do Model (ex: `${variavelQueNaoExiste}`), o Thymeleaf pode manter comportamento de erro controlado dependendo da configuração, mas em geral também lança exceção — a afirmação só seria totalmente correta em cenários específicos de Thymeleaf configurado com lenient handling (não é o padrão).
Caso 2 (incorreto): se o Model tem o objeto, mas uma propriedade interna dele é nula ou o objeto inteiro é `null` (como o ITEM 5), o Thymeleaf lança `SpelEvaluationException` em vez de manter o texto original da tag — foi exatamente o que vimos na Parte C.
## Parte E — Rastreamento e arquitetura

### F.1 — Caminho da requisição GET /resumo_16
| Etapa | Onde acontece | O que acontece |
|---|---|---|
| 1 | Navegador | Usuário acessa `http://localhost:8080/resumo_16` |
| 2 | `DispatcherServlet` (Spring) | Recebe a requisição e consulta o `RequestMappingHandlerMapping` |
| 3 | `PetVidaController.resumo(Model model)` | Método é escolhido por casar com `@GetMapping("/resumo_16")` |
| 4 | `AnimalRepository` (`contarAnimais()`, `mediaIdade()`, `animalMaisVelho()`) | Métodos executam SQL: `SELECT COUNT(*) FROM animal`, `SELECT AVG(idade*1.0) FROM animal`, `SELECT nome FROM animal ORDER BY idade DESC LIMIT 1` |
| 5 | `PetVidaController.resumo()` | Formata a data com `DateTimeFormatter` e a média com `String.format("%.2f", media)`, populando o `Model` |
| 6 | Thymeleaf (`resumo.html`) | Processa os `th:text` substituindo pelos valores do Model |
| 7 | Navegador | Recebe o HTML renderizado e exibe a página |

### F.2
Linha `mediaFormatada = String.format("%.2f", media);` em `PetVidaController.java` — se a restrição de só usar `th:text` não existisse, essa formatação de 2 casas decimais poderia ter sido feita na view com `#numbers.formatDecimal(...)`. A restrição preserva a responsabilidade da View de ser apenas apresentação (exibir dados prontos), mantendo toda lógica de formatação e cálculo na camada de Controller/Service, o que separa claramente apresentação de lógica de negócio — um dos princípios centrais do padrão MVC.

### F.3
Se dois métodos diferentes forem anotados com o mesmo `@GetMapping("/resumo_16")`, o erro aparece **ao subir a aplicação** (não ao compilar, nem só ao acessar a URL), porque o Spring monta a tabela de mapeamentos no momento em que o `ApplicationContext` é inicializado — ele detecta a ambiguidade e lança uma exceção (`IllegalStateException: Ambiguous mapping`) antes mesmo do Tomcat aceitar requisições.

### F.4
Se a clínica tivesse 500 mil animais, a alternativa proibida (trazer tudo com `SELECT *` e calcular em Java) faria a aplicação buscar 500 mil linhas completas do banco pela rede, consumindo memória e banda desnecessariamente, só para descartar quase tudo e calcular um único número. Com o cálculo feito no banco (`SELECT AVG(...)`), trafega apenas o resultado final (um único valor), independente do tamanho da tabela — muito mais eficiente e escalável.
## Parte F — Defesa escrita do seu código

### G.1 — Método buscarPorId (AnimalRepository)

```java
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
```

Comentário por blocos:
- A `String sql` monta uma única consulta com `JOIN` entre `animal` e `tutor`, trazendo os dados das duas tabelas numa única linha de resultado.
- `jdbc.queryForObject(sql, (rs, rowNum) -> {...}, id)` executa a consulta parametrizada (o `?` é substituído pelo `id`), e o segundo argumento é um `RowMapper` (uma função lambda) que converte cada linha do `ResultSet` num objeto Java.
- Dentro do lambda, primeiro é montado o objeto `Tutor` a partir das colunas do tutor retornadas pelo JOIN.
- Depois é montado o objeto `Animal`, recebendo o `Tutor` já criado como um de seus atributos.

Respostas:
- A função lambda `(rs, rowNum) -> {...}` é chamada pelo próprio `JdbcTemplate` internamente, uma vez para cada linha retornada pela consulta. Como a consulta usa `WHERE a.id_animal = ?` (chave primária), ela é executada **exatamente uma vez**, pois só pode existir uma linha com aquele id.
- Se a consulta retornasse duas linhas em vez de uma, o `queryForObject` lançaria uma exceção (`IncorrectResultSizeDataAccessException`), porque esse método do JdbcTemplate espera exatamente um resultado — igual vimos com `EmptyResultDataAccessException` quando vinha zero linhas.
- A consulta precisa do JOIN porque sem ele seriam necessárias duas idas ao banco (uma para o animal, outra para o tutor), aumentando a latência da aplicação; com o JOIN, os dados vêm prontos numa única viagem ao banco, o que é mais eficiente.

### G.2
A linha `jdbc.queryForObject(sql, (rs, rowNum) -> {...}, id)`, com a sintaxe de lambda como `RowMapper`, é algo que eu não conseguiria escrever do zero sem consultar a documentação do Spring — não tenho memorizado o formato exato da interface funcional `RowMapper<T>` nem a ordem dos parâmetros do `queryForObject`. Ainda assim, entendo completamente o que ela faz: mapeia cada linha do resultado SQL para um objeto Java, e está ali porque é a forma idiomática do Spring JDBC de converter dados relacionais em objetos de domínio sem precisar de um ORM completo como o JPA/Hibernate.

## Parte G — Declaração de uso de IA

| Parte da prova | Ferramenta usada | O que precisei corrigir/adaptar |
|---|---|---|
| A | Claude | Ajustei os dados da minha semente (NN=16) e corrigi a estrutura de pastas ao copiar os arquivos |
| C | Claude | Segui a depuração item a item, testando cada correção antes de prosseguir |
| D | Claude | Revisei a classificação de cada afirmação (a-e) com base no meu entendimento do código |
| E | Claude | Adaptei a resposta para os nomes reais dos meus arquivos e métodos |
| F | Claude | Colei meu próprio método e revisei o comentário linha por linha |