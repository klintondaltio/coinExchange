import { Moon, Sun } from "lucide-react";
import { useEffect, useState } from "react";
import { toggleDarkMode } from "../theme";

const ThemeToggle = () => {
    const [isDark, setIsDark] = useState(false);

    useEffect(() => {
        const savedTheme = localStorage.getItem("theme");
        setIsDark(
            savedTheme === "dark" ||
            (!savedTheme && window.matchMedia("(prefers-color-scheme: dark)").matches)
        );
    }, []);

    const toggle = () => {
        toggleDarkMode();
        setIsDark(!isDark);
    };

    return (
        <button
            onClick={toggle}
            className="ml-2 p-2 rounded hover:bg-gray-200 dark:hover:bg-gray-700"
        >
            {isDark ? <Sun className="w-5 h-5 text-yellow-400" /> : <Moon className="w-5 h-5 text-gray-700" />}
        </button>
    );
};

export default ThemeToggle;