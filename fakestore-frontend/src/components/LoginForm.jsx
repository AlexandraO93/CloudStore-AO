import {useEffect, useState} from "react";
import {useAuth} from "../context/AuthContext.jsx";
import "./LoginForm.css";
import {useNavigate} from "react-router-dom";

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
