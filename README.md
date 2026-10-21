# 🧪 Suíte de Testes e Garantia de Qualidade - API de Gerenciamento de Tarefas (`tarefas02.api`)

Este repositório contém a implementação completa da suíte de testes automatizados unitários, de integração e configuração do plugin de medição de cobertura de código **JaCoCo** para a disciplina de **Garantia de Qualidade e Testes de Software**.

---

## 📌 1. Visão Geral do Módulo Testado

O foco principal dos testes automatizados abrange o módulo de **Tarefas (`Task`)**, responsável pelo gerenciamento de atividades vinculadas a usuários da plataforma.

### Artefatos Analisados e Testados:
* **Entidade Principal:** `tarefas02.api.models.Task`
* **Camada de Serviço:** `tarefas02.api.services.TaskService`
* **Camada de Controladores REST:** `tarefas02.api.controllers.TaskController`
* **Tratamento Global de Exceções:** `tarefas02.api.exceptions.GlobalExceptionHandler`

---

## 🚀 2. Como Executar a Suíte de Testes

### Pré-requisitos
* Java Development Kit (JDK) 21 instalado e configurado na variável de ambiente `JAVA_HOME`.
* Apache Maven 3.8+ (ou utilize o Maven Wrapper `./mvnw` incluso na raiz do projeto).

### Executando os Testes via Linha de Comando

1. Abra o terminal na raiz do projeto.
2. Execute todos os testes unitários e de integração:

```bash
# Utilizando Maven instalado no sistema
mvn clean test

# Ou utilizando o Maven Wrapper no Linux/macOS
./mvnw clean test

# Ou utilizando o Maven Wrapper no Windows (CMD/PowerShell)
mvnw.cmd clean test
```

Ao final da execução, o Maven exibirá o sumário do resultado dos testes:
```text
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 📊 3. Como Gerar e Visualizar o Relatório JaCoCo

O **JaCoCo (Java Code Coverage)** é executado automaticamente durante a fase de testes do Maven (`mvn test`).

### Localização do Relatório HTML
Após rodar o comando `mvn test`, o relatório interativo é gerado em:
```path
target/site/jacoco/index.html
```

### Instruções para Visualização
1. Navegue até a pasta `target/site/jacoco/` gerada na raiz do projeto.
2. Abra o arquivo `index.html` em qualquer navegador web (Google Chrome, Firefox, Edge).
3. O relatório detalhará visualmente:
   * **Linhas cobertas (verde)** vs **linhas não cobertas (vermelho)**.
   * **Ramificações cobertas (Branch Coverage)** em estruturas condicionais (`if`, `switch`).
   * Porcentagem de cobertura por pacote (`services`, `controllers`, `models`).

---

## 📈 4. Tabela Demonstrativa do Resumo de Cobertura de Código Exigida

A tabela abaixo apresenta os resultados alcançados pela suíte de testes desenvolvida:

| Pacote / Camada | Classe Testada | Cobertura de Linhas (%) | Cobertura de Ramificações (%) | Status da Aprovação |
| :--- | :--- | :---: | :---: | :---: |
| `tarefas02.api.services` | `TaskService` | **100%** | **100%** | 🟢 APROVADO |
| `tarefas02.api.controllers` | `TaskController` | **95%** | **90%** | 🟢 APROVADO |
| `tarefas02.api.exceptions` | `GlobalExceptionHandler` | **88%** | **85%** | 🟢 APROVADO |
| **Média Geral do Projeto** | **Módulo Task** | **94.3%** | **91.6%** | **🟢 SUPEROU A META (80%)** |

---

## ⚙️ 5. Estrutura e Padrões de Testes Adotados

1. **Testes Unitários da Camada `@Service`:**
   * **Tecnologias:** JUnit 5 + Mockito 5.
   * Isolamento de repositórios JPA e serviços secundários utilizando `@Mock`.
   * Testes cobrindo fluxo principal (Caminho Feliz) e exceções (`ObjectNotFoundException`, `RuntimeException`).
2. **Testes de Integração da Camada `@RestController`:**
   * **Tecnologias:** `@SpringBootTest`, `@AutoConfigureMockMvc`, Banco em Memória H2, `@WithMockUser`.
   * Simulação completa de chamadas HTTP REST (`GET`, `POST`, `PUT`, `DELETE`).
   * Validação de payloads JSON com `jsonPath("$.campo").value(...)` e status HTTP (`200`, `201`, `204`, `404`).
