import {useState} from "react";
import {AuthContext} from "./AuthContext";
import {API_BASE_URL} from "../config/api";

/*
 * AuthProvider
 *
 * Wrapper-komponent som omsluter hela applikationen.
 * Ansvarar för autentiseringslogik:
 * - håller state för token och customerId
 * - lagrar token och customerId i localStorage
 * - återställer auth-state vid siduppdatering
 * - tillhandahåller login- och logout-funktioner
 *   via AuthContext.Provider
 */

export const AuthProvider = ({children}) => {
    // Initiera state från localStorage så auth överlever refresh
    const [token, setToken] = useState(localStorage.getItem("token") || null);
    const [customerId, setCustomerId] = useState(localStorage.getItem("customerId") || null);
    const [user, setUser] = useState(null);

    const login = async (email, password) => {
        const res = await fetch(`${API_BASE_URL}/request-token`, {
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

        const userRes = await fetch(`${API_BASE_URL}/users/${data.customerId}`, {
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
