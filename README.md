# 💰 Coin Exchange Machine (Backend)

API REST desenvolvida para simular uma máquina de troca de cédulas por moedas. O sistema controla o inventário de moedas, registra o histórico de transações, permite diferentes estratégias de troco (mínima ou máxima) e acompanha o estado da máquina.

---

## 🧪 Tecnologias

- Java 17  
- Spring Boot 3.4  
- Spring Data JPA (H2 em memória)  
- Spring Security (HTTP Basic)  
- MapStruct (opcional, foi substituído por mapeamento manual)  
- JUnit 5, Mockito, MockMvc  
- Maven

---

## 🚀 Como Executar

Via terminal:

```bash
./mvnw spring-boot:run
