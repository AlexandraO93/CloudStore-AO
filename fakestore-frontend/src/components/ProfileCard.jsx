import {useAuth} from "../context/AuthContext.jsx";
import {AUTH_API_URL} from "../config/api.js";
import {useEffect, useState} from "react";
import "./ProfileCard.css";
import {useNavigate} from "react-router-dom";

export default function ProfileCard() {
    const {token, customerId} = useAuth();
    const navigate = useNavigate();
    const [isEditingProfile, setIsEditingProfile] = useState(false);
    const [isEditingEmail, setIsEditingEmail] = useState(false);
    const [isEditingPassword, setIsEditingPassword] = useState(false);


    const [userData, setUserData] = useState({
        firstName: "",
        lastName: "",
        email: "",
        address: "",
        phone: "",
        password: "",
        currentPassword: ""
    })

    useEffect(() => {
        const fetchUser = async () => {
            if (!token || !customerId) return;

            try {
                const res = await fetch(`${AUTH_API_URL}/users/${customerId}`, {
                    headers: {"Authorization": `Bearer ${token}`}
                });
                if (res.ok) {
                    const data = await res.json();
                    setUserData(data);
                }
            } catch (err) {
                console.error("Kunde inte hämta användardata: ", err);
            }
        };
        fetchUser();
    }, [token, customerId]);

    const saveProfile = async () => {
        try {
            const res = await fetch(`${AUTH_API_URL}/users/${customerId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(userData)
            });

            if (isEditingPassword && userData.password !== userData.confirmPassword) {
                alert("De nya lösenorden matchar inte!");
                return;
            }

            if (res.ok) {
                setIsEditingProfile(false);
                setIsEditingEmail(false);
                setIsEditingPassword(false);
                alert("Profilen uppdaterad!");

                setUserData(prev => ({
                    ...prev,
                    currentPassword: "",
                    password: ""
                }));
            } else {
                const errorData = await res.json();
                alert(errorData.message || "Ett fel uppstod vid uppdatering.");
            }
        } catch (err) {
            console.error("Gick inte att spara:", err);
        }
    };

    return (
        <div className="profile-card-container">
            <h2 className="product-card-title">Min Profil</h2>

            <div className="profile-card-content">
                <h4 className="product-card-subtitle">Personuppgifter</h4>
                <div className="profile-name-section">
                    <div className="name-group">
                        <p className="profile-title">Förnamn: </p>
                        <p className="profile-input">{userData.firstName}</p>
                    </div>
                    <div className="name-group">
                        <p className="profile-title">Efternamn: </p>
                        <p className="profile-input">{userData.lastName}</p>
                    </div>
                </div>
                <div className="address-group">
                    <p className="profile-title">Adress: </p>
                    <div className="profile-input">
                        {userData.address ? (
                            userData.address.split(',').map((part, index) => (
                                <span key={index}>
                                {part.trim()}<br/>
                            </span>
                            ))
                        ) : (
                            <span>Ingen adress sparad</span>
                        )}
                    </div>
                </div>
                <div className="phone-group">
                    <p className="profile-title">Mobilnummer: </p>
                    <p className="profile-input">{userData.phone}</p>
                </div>
                <button className="edit-profile-button" onClick={() => setIsEditingProfile(true)}>Uppdatera
                    information
                </button>
            </div>

            <div className="profile-card-content">
                <h4 className="product-card-subtitle">Kontaktuppgifter</h4>
                <div className="email-group">
                    <p className="profile-title">Email: </p>
                    <p className="profile-input">{userData.email}</p>
                </div>
                <button className="edit-profile-button" onClick={() => setIsEditingEmail(true)}>Ändra e-postadress
                </button>

                <div className="password-group">
                    <p className="profile-title">Lösenord: </p>
                    <p className="profile-input">*********</p>
                </div>
                <button className="edit-profile-button" onClick={() => setIsEditingPassword(true)}>Ändra lösenord
                </button>

            </div>

            <button className="my-orders-button" onClick={() => navigate(`/users/${customerId}/orders-by-user`)}>Mina
                beställningar
            </button>

            {isEditingProfile && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Redigera personuppgifter</h3>

                        <input
                            type="text"
                            placeholder="Förnamn"
                            value={userData.firstName}
                            onChange={(e) => setUserData({...userData, firstName: e.target.value})}
                        />
                        <input
                            type="text"
                            placeholder="Efternamn"
                            value={userData.lastName}
                            onChange={(e) => setUserData({...userData, lastName: e.target.value})}
                        />
                        <input
                            type="text"
                            placeholder="Adress"
                            value={userData.address}
                            onChange={(e) => setUserData({...userData, address: e.target.value})}
                        />
                        <input
                            type="text"
                            placeholder="Telefon"
                            value={userData.phone}
                            onChange={(e) => setUserData({...userData, phone: e.target.value})}
                        />

                        <div className="modal-actions">
                            <button onClick={saveProfile}>Spara</button>
                            <button onClick={() => setIsEditingProfile(false)}>Avbryt</button>
                        </div>
                    </div>
                </div>
            )}
            {isEditingEmail && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Ändra e-postadress</h3>

                        <input
                            type="email"
                            placeholder="Email"
                            value={userData.email}
                            onChange={(e) => setUserData({...userData, email: e.target.value})}
                        />

                        <div className="modal-actions">
                            <button onClick={saveProfile}>Spara</button>
                            <button onClick={() => setIsEditingEmail(false)}>Avbryt</button>
                        </div>
                    </div>
                </div>
            )}
            {isEditingPassword && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Ändra lösenord</h3>

                        <input
                            type="password"
                            placeholder="Nuvarande lösenord"
                            value={userData.currentPassword}
                            onChange={(e) => setUserData({...userData, currentPassword: e.target.value})}
                        />

                        <input
                            type="password"
                            placeholder="Nytt lösenord"
                            value={userData.password}
                            onChange={(e) => setUserData({...userData, password: e.target.value})}
                        />

                        <input
                            type="password"
                            placeholder="Bekräfta nytt lösenord"
                            value={userData.confirmPassword}
                            onChange={(e) => setUserData({...userData, confirmPassword: e.target.value})}
                        />

                        <div className="modal-actions">
                            <button onClick={saveProfile}>Spara</button>
                            <button onClick={() => setIsEditingPassword(false)}>Avbryt</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}