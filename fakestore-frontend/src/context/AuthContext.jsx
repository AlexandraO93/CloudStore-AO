import {createContext, useContext, useState} from "react";
import {AUTH_API_URL} from "../config/api.js";

const AuthContext = createContext(null);

export const AuthProvider = ({children}) => {
    const [token, setToken] = useState(localStorage.getItem("token") || null);
    const [customerId, setCustomerId] = useState(localStorage.getItem("customerId") || null);
    const [user, setUser] = useState(() => {
        const savedEmail = localStorage.getItem("userEmail");
        return savedEmail ? {email: savedEmail} : null;
    });

    const login = async (email, password) => {
        const res = await fetch(`${AUTH_API_URL}/request-token`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({email, password}),
        });

        if (!res.ok) {
            throw new Error("Login failed");
        }

        const data = await res.json();

        setToken(data.token);
        setCustomerId(data.customerId);
        setUser({email: email});

        // Persistera auth-data
        localStorage.setItem("token", data.token);
        localStorage.setItem("customerId", data.customerId);
        localStorage.setItem("userEmail", email);

        const userRes = await fetch(`${AUTH_API_URL}/users/${data.customerId}`, {
            headers: {
                Authorization: `Bearer ${data.token}`,
            }
        });

        const userData = await userRes.json();
        setUser(userData);
    };

    const logout = () => {
        setToken(null);
        setCustomerId(null);
        setUser(null);

        localStorage.removeItem("token");
        localStorage.removeItem("customerId");
        localStorage.removeItem("userEmail");
    };

    return (
        <AuthContext.Provider value={{token, customerId, user, setUser, login, logout}}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);