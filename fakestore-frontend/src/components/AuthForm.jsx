import {useEffect, useState} from "react";
import {useAuth} from "../context/AuthContext.jsx";
import "./LoginForm.css";
import {useNavigate} from "react-router-dom";
import {AUTH_API_URL} from "../config/api.js";

const AuthForm = () => {
    const {login, token} = useAuth();
    const navigate = useNavigate();

    const [isRegister, setIsRegister] = useState(false);

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [address, setAddress] = useState("");
    const [phone, setPhone] = useState("");

    useEffect(() => {
        if (token) {
            navigate("/login");
        }
    }, [token, navigate]);

    const handleSubmit = async () => {
        if (isRegister) {
            const payload = {
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password,
                address: address,
                phone: phone
            };

            const res = await fetch(`${AUTH_API_URL}/users/register`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload)
            });

            const contentType = res.headers.get("content-type");
            if (res.ok && contentType && contentType.includes("application/json")) {
                const data = await res.json();
                alert("Konto skapat! Logga in.");
                setIsRegister(false);
            } else {
                const errorText = await res.text();
                alert("Fel: " + errorText);
            }
            return;
        }
        try {
            await login(email, password);
        } catch {
            alert("Inloggning misslyckades! Fel användarnamn eller lösenord");
        }
    };

    return (
        <form className="login-form" onSubmit={(e) => {
            e.preventDefault();
            handleSubmit();
        }}>
            <h2 className="register-form">{isRegister ? "Skapa konto" : "Logga in"}</h2>

            <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                required
                autoComplete="username"
            />

            {isRegister && (
                <>
                    <input
                        type="text"
                        placeholder="Förnamn"
                        value={firstName}
                        onChange={e => setFirstName(e.target.value)}
                        required
                    />
                    <input
                        type="text"
                        placeholder="Efternamn"
                        value={lastName}
                        onChange={e => setLastName(e.target.value)}
                        required
                    />
                    <input
                        type="text"
                        placeholder="Adress"
                        value={address}
                        onChange={e => setAddress(e.target.value)}
                        required
                    />
                    <input
                        type="tel"
                        placeholder="Telefonnummer"
                        value={phone}
                        onChange={e => setPhone(e.target.value)}
                        required
                    />
                </>
            )}

            <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                required
                autoComplete={isRegister ? "new-password" : "current-password"}
            />

            <button className="submit-button" type="submit">
                {isRegister ? "Registrera" : "Logga in"}
            </button>

            <p>
                {isRegister ? (
                    <>
                        Har du redan konto?{" "}
                        <span className="submit-button" onClick={() => setIsRegister(false)}>Logga in</span>
                    </>
                ) : (
                    <>
                        Har du inget konto?{" "}
                        <span className="submit-button" onClick={() => setIsRegister(true)}>Registrera dig</span>
                    </>
                )}
            </p>
        </form>
    );
};

export default AuthForm;
