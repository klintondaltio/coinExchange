# Bills to coins exchange challenge

Given an amount in Bills that can be $1, $2, $5, $10, $20, $50 and $100, exchange it to coins that can be 1c,
5c, 10c or 25 cents. The machine needs to assume there is a finite number of coins.
You have 72 hours to finish this challenge. Please upload your code to your personal github account and
share the link with your contact for this interview.

---

## ✅ Requirements:

- ✅ **Start with 100 coins of each type;**
  > Atendido no backend: o inventário de moedas é inicializado com 100 unidades de cada valor no `CoinProperties`.

  > Visível no frontend: **Dashboard**, **Inventory**

- ✅ **BONUS: allow for number of coins to be configurable and easily changed if needed;**
  > Atendido via propriedade `initialQuantity` no `application.properties` do backend

- ✅ **Prompt User for an amount they want to get change for;**
  > Atendido na tela **Exchange**

- ✅ **BONUS: implement REST service inputs as an alternative as well;**
  > Atendido — API REST criada no backend com endpoints como `/api/exchange`

- ✅ **Catch bad input and display error messages or catch exceptions if applicable;**
  > Backend lança exceções com mensagens claras  
  > Frontend exibe erros em vermelho na tela **Exchange** e **Inventory**

- ✅ **Machine should return the least quantity of coins possible;**
  > Atendido como estratégia padrão (`minimal = true`)  
  > Visível no resultado da troca na tela **Exchange**

- ✅ **BONUS: allow to also return the most quantity of coins;**
  > Atendido com opção de estratégia `MAXIMAL` na tela **Exchange**

- ✅ **Machine should display a message if it does not have enough coins;**
  > Atendido — `InsufficientCoinsException` lançada e tratada no frontend com mensagem de erro

- ✅ **It should maintain the state of coins and bills left throughout all the transaction till it runs out of the coin then it should exit;**
  > Atendido — o estado é mantido em memória e refletido nas telas:
  > - **Dashboard** (moedas e cédulas)
  > - **Inventory** (estado detalhado de moedas)
  > - **Bills** (estado detalhado de cédulas)

- ✅ **Deliver the code with unit test cases;**
  > Atendido — diversos testes criados no arquivo `ExchangeServiceTest.java` cobrindo todos os casos relevantes

---

## Example

Inicialmente a máquina tem 100 moedas de cada tipo.

### User input:
* Amount: $30

Expected message:

> Exchanged $30 successfully!

Expected change:

> - 100 coins (25 cents) = $25
> - 50 coins (10 cents) = $5

Expected state:
- Bills amount: $30
- Coins amount: $11
  - 50 coins (10 cents)
  - 100 coins (5 cents)
  - 100 coins (1 cent)

---

## Additional Requirements:

- ✅ **Create a README file with steps to run the application;**
  > Atendido — veja `README.md` no repositório

- ✅ **Come to interview prepared to walk through code;**
  > Código limpo, modularizado e comentado, com README e prints ilustrativos

---

## For Backend challenge

- ✅ **Write it in Java using Spring Boot and implement a REST API;**
  > Atendido — Projeto Spring Boot, API REST implementada

- ✅ **The data storage can be in memory, local file or database;**
  > Atendido — persistência em memória + JPA (com suporte a banco se necessário)

- ✅ **The UI should be command line (CLI) or REST client (Postman)**
  > CLI disponível via chamada à API. Porém, como o desafio é Fullstack, o frontend cobre esse ponto.

---

## For Fullstack challenge

### Backend:
- ✅ Spring Boot com REST API e autenticação básica
- ✅ In-memory data storage (padrão), facilmente adaptável

### Frontend:
- ✅ **Write it in Typescript**
- ✅ **React + Vite + Tailwind CSS**
- ✅ Tema claro/escuro
- ✅ Navegação SPA com React Router
- ✅ Paginação e filtros no histórico

---

> Desenvolvido por [klintondaltio](https://github.com/klintondaltio)
