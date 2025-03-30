import { Link, Outlet } from "react-router-dom";
import MachineStatusIndicator from "./MachineStatusIndicator";
import ThemeToggle from "./ThemeToggle";
import logo from "../assets/adpbrazillabs_logo.jpg"; // ajuste se estiver em outro lugar

const Layout = () => {
    return (
        <div className="min-h-screen bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 flex flex-col">
            <header className="bg-white dark:bg-gray-800 shadow p-4 flex items-center justify-between">
                <div className="flex items-center gap-4">
                    <img src={logo} alt="Logo ADP" className="h-16" /> {/* aumentamos o tamanho da logo aqui */}
                    <h1 className="text-2xl font-bold">Coin Exchange</h1>
                </div>

                <div className="flex items-center gap-4">
                    <MachineStatusIndicator />
                    <ThemeToggle />
                </div>
            </header>

            <nav className="bg-gray-200 dark:bg-gray-700 text-sm p-3 flex justify-center gap-6 shadow">
                <Link to="/dashboard" className="hover:underline">Dashboard</Link>
                <Link to="/exchange" className="hover:underline">Trocar</Link>
                <Link to="/inventory" className="hover:underline">Inventário</Link>
                <Link to="/history" className="hover:underline">Histórico</Link>
                <Link to="/bills" className="hover:underline">Cédulas</Link>
                <Link to="/status" className="hover:underline">Status</Link>
            </nav>

            <main className="flex-grow p-6">
                <Outlet />
            </main>
        </div>
    );
};

export default Layout;
