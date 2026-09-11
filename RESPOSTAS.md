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