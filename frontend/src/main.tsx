import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import { applyInitialTheme } from "./theme";

applyInitialTheme(); // Aplica o tema salvo (claro/escuro) antes de renderizar o app

ReactDOM.createRoot(document.getElementById("root")!).render(
    <React.StrictMode>
        <App />
    </React.StrictMode>
);
