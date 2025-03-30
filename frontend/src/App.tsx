// App.tsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Layout from "./components/Layout";
import ExchangePage from "./pages/ExchangePage";
import HistoryPage from "./pages/HistoryPage";
import InventoryPage from "./pages/InventoryPage";
import BillsPage from "./pages/BillsPage";
import StatusPage from "./pages/StatusPage";
import DashboardPage from "./pages/DashboardPage";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Layout />}>
                    <Route index element={<Navigate to="/dashboard" />} />
                    <Route path="exchange" element={<ExchangePage />} />
                    <Route path="history" element={<HistoryPage />} />
                    <Route path="inventory" element={<InventoryPage />} />
                    <Route path="bills" element={<BillsPage />} />
                    <Route path="status" element={<StatusPage />} />
                    <Route path="dashboard" element={<DashboardPage />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
