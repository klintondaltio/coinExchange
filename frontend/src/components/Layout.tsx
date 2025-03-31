import { NavLink, Outlet } from "react-router-dom";
import MachineStatusIndicator from "./MachineStatusIndicator";
import ThemeToggle from "./ThemeToggle";
import logo from "../assets/adpbrazillabs_logo.jpg"; // adjust if needed

const Layout = () => {
    return (
        <div className="min-h-screen bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 flex flex-col">
            <header className="bg-white dark:bg-gray-800 shadow p-4 flex items-center justify-between">
                <div className="flex items-center gap-4">
                    <img src={logo} alt="ADP Logo" className="h-16" />
                    <h1 className="text-2xl font-bold">Coin Exchange</h1>
                </div>

                <div className="flex items-center gap-4">
                    <MachineStatusIndicator />
                    <ThemeToggle />
                </div>
            </header>

            <nav className="bg-gray-200 dark:bg-gray-700 text-sm p-3 flex justify-center gap-6 shadow">
                {["dashboard", "exchange", "inventory", "history", "bills", "status"].map((route) => (
                    <NavLink
                        key={route}
                        to={`/${route}`}
                        className={({ isActive }) =>
                            `hover:underline capitalize ${
                                isActive ? "text-blue-600 border-b-2 border-blue-600 pb-1" : ""
                            }`
                        }
                    >
                        {route}
                    </NavLink>
                ))}
            </nav>

            <main className="flex-grow p-6">
                <Outlet />
            </main>
        </div>
    );
};

export default Layout;
