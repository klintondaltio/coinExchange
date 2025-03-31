# Frontend - Coin Exchange

Este diretório contém o **frontend** da aplicação **Coin Exchange**, construído com **React**, **TypeScript**, **Vite** e **Tailwind CSS**.

---

## Tecnologias Utilizadas

- [React](https://react.dev/)
- [TypeScript](https://www.typescriptlang.org/)
- [Vite](https://vitejs.dev/)
- [Tailwind CSS](https://tailwindcss.com/)
- [Axios](https://axios-http.com/)
- [Lucide React](https://lucide.dev/) (ícones)
- [shadcn/ui](https://ui.shadcn.com/) (componentes)

---

## Como Rodar Localmente

```bash
cd frontend
npm install
npm run dev
```

Acesse: [http://localhost:5173](http://localhost:5173)

---

## Scripts Disponíveis

| Comando            | Descrição                                |
|--------------------|--------------------------------------------|
| `npm run dev`      | Sobe o servidor de desenvolvimento         |
| `npm run build`    | Gera os arquivos de produção (em `/dist`)  |
| `npm run preview`  | Visualiza a build localmente               |

---

## Estrutura de Pastas

```
frontend/
├── public/                # Arquivos públicos
├── src/
│   ├── assets/            # Imagens e logo
│   ├── components/        # Componentes reutilizáveis (header, toggle, etc.)
│   ├── pages/             # Páginas da aplicação
│   ├── theme.ts           # Lógica do modo claro/escuro
│   └── App.tsx            # Roteador principal
├── index.html             # HTML principal
├── tailwind.config.js     # Configuração do Tailwind
└── vite.config.ts         # Configuração do Vite
```

---

## Tema Claro/Escuro

O sistema possui suporte ao modo escuro com persistência em `localStorage`, controlado via botão no header.

---

## Funcionalidades

- Troca de moedas por cédulas com estratégia mínima/máxima
- Visualização e adição ao inventário
- Histórico com filtros por data, valor e estratégia
- Status da máquina (operacional ou fora de operação)
- Dashboard geral
- Suporte ao tema escuro

---

> Desenvolvido como parte do desafio técnico da **ADP Brasil Labs** por [@klintondaltio](https://github.com/klintondaltio)
