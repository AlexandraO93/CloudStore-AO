import {useEffect, useState} from "react";
import {useAuth} from "../context/useAuth";
import "./LoginForm.css";
import {useNavigate} from "react-router-dom";
/*
 * LoginForm
 *
 * Denna komponent ansvarar för inloggning av användaren.
 * Den innehåller formulärfält för användarnamn och lösenord
 * samt logik för att initiera inloggning via AuthProvider.
 *
 * Komponenten:
 * - använder useAuth() för att anropa login-funktionen
 *   och för att läsa aktuell autentiseringsstatus (token)
 * - använder useNavigate() för att omdirigera användaren
 *   efter lyckad inloggning
 * - använder useState för att hantera formulärdata
 *
 * Flöde:
 * 1. Användaren skriver in användarnamn och lösenord
 * 2. Klick på "Logga in" anropar login(username, password)
 * 3. När token sätts i AuthProvider uppdateras context
 * 4. useEffect reagerar på att token ändras
 * 5. Användaren omdirigeras automatiskt till /feed via navigate()
 */


const LoginForm = () => {
    const {login, token} = useAuth();
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    useEffect(() => {
        if (token) {
            navigate("/products");
        }
    }, [token, navigate]);

    const handleLogin = async () => {
        try {
            await login(email, password);
        } catch (err) {
            alert("Inloggning misslyckades! Kontrollera dina uppgifter.");
        }
    };

    return (
        <div className="login-form">
            <input
                type="text"
                aria-label="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <input
                type="password"
                aria-label="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button onClick={handleLogin}>Logga in</button>
            {token && <p>Token: {token}</p>}
        </div>
    );
};

export default LoginForm;
