# 07 · Microsserviço de Agendamento de Laboratórios e Salas

API REST em **Java 21** e **Spring Boot 3** para gestão de espaços acadêmicos (laboratórios e salas de estudo), consulta de janelas de disponibilidade, criação/cancelamento de reservas com **controle de concorrência pessimista no banco de dados** e exportação de relatórios semanais de ocupação em **CSV**.

> 📄 Especificação técnica detalhada disponível em **[`TECH_SPEC.md`](./TECH_SPEC.md)**.

---

## 1. Arquitetura e Destaques de Engenharia

* **Prevenção de *Double-Booking* sob Concorrência (`BookingService` & `ResourceRepository`):** Aplicação de bloqueio pessimista (`@Lock(LockModeType.PESSIMISTIC_WRITE)` / `SELECT ... FOR UPDATE`) sobre o recurso acadêmico antes da verificação de interseção temporal (`startTime < existing.endTime AND endTime > existing.startTime`), garantindo exclusão mútua entre transações simultâneas.
* **Regras de Domínio Estritas:** Validação de capacidade máxima do espaço (`attendeesCount <= capacity`), duração mínima/máxima da reserva e verificação de recurso ativo.
* **Cálculo de Janelas Livres (`AvailabilityService`):** Algoritmo que subtrai intervalos reservados da janela operacional diária para retornar os slots disponíveis exatos.
* **Exportação Analítica em CSV (`CsvExporter` & `WeeklyOccupancyReportService`):** Geração de relatório semanal com total de reservas, minutos ocupados e taxa percentual de ocupação por sala/laboratório.

---

## 2. Principais Endpoints REST

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/resources` | Cadastra um novo laboratório ou sala de estudo. |
| `GET` | `/api/v1/resources` | Lista os recursos acadêmicos cadastrados. |
| `GET` | `/api/v1/resources/{id}/availability?date=YYYY-MM-DD` | Calcula os horários livres e ocupados do recurso na data informada. |
| `POST` | `/api/v1/bookings` | Cria uma reserva com validação transacional e bloqueio pessimista contra conflitos de horário (`409 Conflict`). |
| `DELETE` | `/api/v1/bookings/{id}` | Cancela uma reserva ativa registrando o motivo do cancelamento. |
| `GET` | `/api/v1/reports/weekly-occupancy?weekStart=YYYY-MM-DD` | Exporta o relatório semanal de ocupação em formato `text/csv`. |

---

## 3. Como Compilar, Testar e Executar

```powershell
cd 07-agendamento-laboratorios-salas
mvn clean test
mvn spring-boot:run
```
* **API Base URL:** `http://localhost:8080`
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
