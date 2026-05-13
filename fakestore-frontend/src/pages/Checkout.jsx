import {useAuth} from "../context/AuthContext.jsx";
import {useEffect, useState} from "react";
import {AUTH_API_URL} from "../config/api.js";
import "./Checkout.css";
import ProductCard from "../components/ProductCard.jsx";
import {useCart} from "../context/CartContext.jsx";
import {useNavigate} from "react-router-dom";

export default function Checkout() {
    const {token, user, customerId} = useAuth();
    const {cartItems, clearCart} = useCart();
    const navigate = useNavigate();
    const [isEditingProfile, setIsEditingProfile] = useState(false);
    const [isEditingNameOrEmail, setIsEditingNameOrEmail] = useState(false);
    const [isProcessing, setIsProcessing] = useState(false);

    const conversionRate = 10;

    const totalPrice = cartItems.reduce((acc, item) => {
        const price = Number(item.price) || 0;
        const qty = Number(item.quantity) || 1;
        return acc + (price * qty * conversionRate);
    }, 0);

    const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        email: user?.email || "",
        address: "",
        phone: ""
    })

    const handlePlaceOrder = async () => {
        setIsProcessing(true);

        setTimeout(async () => {
            const orderData = {
                customerId: customerId,
                items: cartItems.map(item => ({
                    productId: item.id,
                    quantity: item.quantity || 1
                }))
            };

            try {
                const res = await fetch(`${AUTH_API_URL}/users/${customerId}/orders`, { // Eller den port du valt
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },
                    body: JSON.stringify(orderData)
                });

                if (res.ok) {
                    const result = await res.json();
                    alert("Tack för ditt köp!");
                    clearCart();
                    navigate("/confirmation", {state: {orderId: result.orderId}});
                } else {
                    alert("Något gick fel, beställningen kunde inte slutföras.");
                }
            } catch (err) {
                console.error("Orderfel:", err);
            } finally {
                setIsProcessing(false);
            }
        }, 2000);
    };

    useEffect(() => {
        const fetchUser = async () => {
            if (!token || !customerId) return;

            try {
                const res = await fetch(`${AUTH_API_URL}/users/${customerId}`, {
                    headers: {"Authorization": `Bearer ${token}`}
                });
                if (res.ok) {
                    const data = await res.json();
                    setFormData({
                        firstName: data.firstName || "",
                        lastName: data.lastName || "",
                        email: data.email || "",
                        address: data.address || "",
                        phone: data.phone || ""
                    });
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
                body: JSON.stringify(formData)
            });

            if (res.ok) {
                setIsEditingProfile(false);
                setIsEditingNameOrEmail(false);
                alert("Profilen uppdaterad!");

            } else {
                const errorData = await res.json();
                alert(errorData.message || "Ett fel uppstod vid uppdatering.");
            }
        } catch (err) {
            console.error("Gick inte att spara:", err);
        }
    };

    return (

        <div className="checkout-form-container">
            <div className="checkout-form-layout">
                <h2 className="checkout-form-headline">KASSA</h2>

                <div className="checkout-address-form">
                    <h3 className="checkout-information">MIN INFORMATION</h3>
                    <p className="checkout-input">{formData.firstName} {formData.lastName}</p>
                    <p className="checkout-input">{formData.email}</p>
                    <button className="edit-button" onClick={() => setIsEditingNameOrEmail(true)}>REDIGERA</button>


                    <h3 className="checkout-address">LEVERANS</h3>
                    <p className="checkout-input">{formData.firstName} {formData.lastName}</p>
                    <div className="checkout-input">
                        {formData.address ? (
                            formData.address.split(',').map((part, index) => (
                                <span key={index}>
                                {part.trim()}<br/>
                            </span>
                            ))
                        ) : (
                            <span>Ingen adress sparad</span>
                        )}
                    </div>

                    <p className="checkout-input">{formData.phone}</p>
                    <button className="edit-button" onClick={() => setIsEditingProfile(true)}>REDIGERA</button>
                </div>

                <div className="shopping-cart-products">
                    <h3 className="checkout-information">DIN BESTÄLLNING</h3>
                    <div className="product-grid">
                        {cartItems.length > 0 ? (
                            cartItems.map((item) => (
                                <ProductCard
                                    key={item.id}
                                    product={item}
                                    showDeleteFromCart={true}
                                    showAddToCart={false}
                                    showQuantityControls={true}
                                    showReloadCart={true}
                                />
                            ))
                        ) : (
                            <p>Din vagn är tom</p>
                        )}
                    </div>
                    <div className="order-summary-section">
                        <hr/>
                        <h3>Totalt att betala: {totalPrice.toFixed(2)} kr</h3>
                        <button
                            className="abort-buy-button"
                            onClick={() => navigate("/products")}
                        >
                            Fortsätt handla
                        </button>
                        <button
                            className="place-order-button"
                            onClick={handlePlaceOrder}
                            disabled={cartItems.length === 0 || isProcessing}
                        >
                            {isProcessing ? "BEARBETAR..." : "BETALA OCH SLUTFÖR KÖP"}
                        </button>

                    </div>
                </div>
            </div>

            {isEditingNameOrEmail && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Redigera kontaktuppgifter</h3>

                        <input
                            type="text"
                            placeholder="Förnamn"
                            value={formData.firstName}
                            onChange={(e) => setFormData({...formData, firstName: e.target.value})}
                        />
                        <input
                            type="text"
                            placeholder="Efternamn"
                            value={formData.lastName}
                            onChange={(e) => setFormData({...formData, lastName: e.target.value})}
                        />
                        <input
                            type="email"
                            placeholder="Email"
                            value={formData.email}
                            onChange={(e) => setFormData({...formData, email: e.target.value})}
                        />
                        <div className="modal-actions">
                            <button onClick={saveProfile}>Spara</button>
                            <button onClick={() => setIsEditingNameOrEmail(false)}>Avbryt</button>
                        </div>
                    </div>
                </div>
            )}

            {isEditingProfile && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Redigera leveransuppgifter</h3>

                        <input
                            type="text"
                            placeholder="Förnamn"
                            value={formData.firstName}
                            onChange={(e) => setFormData({...formData, firstName: e.target.value})}
                        />
                        <input
                            type="text"
                            placeholder="Efternamn"
                            value={formData.lastName}
                            onChange={(e) => setFormData({...formData, lastName: e.target.value})}
                        />
                        <input
                            type="text"
                            placeholder="Adress"
                            value={formData.address}
                            onChange={(e) => setFormData({...formData, address: e.target.value})}
                        />
                        <input
                            type="text"
                            placeholder="Telefon"
                            value={formData.phone}
                            onChange={(e) => setFormData({...formData, phone: e.target.value})}
                        />

                        <div className="modal-actions">
                            <button onClick={saveProfile}>Spara</button>
                            <button onClick={() => setIsEditingProfile(false)}>Avbryt</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}