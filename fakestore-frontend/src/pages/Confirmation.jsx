import {AUTH_API_URL} from "../config/api.js";
import {useAuth} from "../context/AuthContext.jsx";
import {useEffect, useState} from "react";
import {useLocation} from "react-router-dom";
import "./Confirmation.css";

export default function Confirmation() {
    const {token} = useAuth();
    const [orderData, setOrderData] = useState(null);
    const [localProducts, setLocalProducts] = useState([]);
    const location = useLocation();
    const orderId = location.state?.orderId;

    useEffect(() => {
        const savedProducts = JSON.parse(localStorage.getItem("all_products") || "[]");
        setLocalProducts(savedProducts);

        if (!orderId || !token) return;

        const fetchOrders = async () => {
            try {
                const res = await fetch(`${AUTH_API_URL}/orders/${orderId}`, {
                    headers: {"Authorization": `Bearer ${token}`}
                });
                if (res.ok) {
                    const result = await res.json();
                    setOrderData(result);
                }
            } catch (err) {
                console.error("Fel vid hämtning av orderbekräftelse:", err);
            }
        };
        if (token && orderId) fetchOrders();
    }, [token, orderId]);

    const formatOrderDate = (dateString) => {
        if (!dateString) return "";
        return dateString.substring(0, 16).replace("T", " ");
    };

    const getProductInfo = (productId) => {
        return localProducts.find(p => p.id === productId) || {title: `Produkt ${productId}`, image: null};
    };

    if (!orderId) {
        return (
            <div className="confirmation-page-container">
                <p>Ingen order hittades. Gå tillbaka till butiken och försök igen.</p>
            </div>
        );
    }

    if (!orderData) return <p>Laddar din bekräftelse...</p>;

    return (
        <div className="confirmation-page-container">
            <h2 className="confirmation-headline">Orderbekräftelse</h2>

            <div className="confirmation-content">
                <div className="thank-you-section">
                    <h3>Tack för din beställning!</h3>
                    <p>Här nedan ser du information om din leverans.</p>
                </div>

                <div className="order-summary-box">
                    <h4>Orderdetaljer</h4>
                    <p><strong>Ordernummer:</strong> #{orderData.orderId}</p>
                    <p><strong>Totalt belopp:</strong> {(orderData.totalAmount * 10).toFixed(2)} kr</p>
                </div>

                <div className="ordered-items">
                    <h4>Beställda varor</h4>
                    <div className="confirmation-items-list">
                        {orderData.items.map(item => {
                            const info = getProductInfo(item.productId);
                            return (
                                <div key={item.productId} className="conf-item">
                                    {info.image && <img src={info.image} alt={info.title} className="conf-img"/>}
                                    <div className="conf-item-info">
                                        <p className="conf-item-title">{info.title}</p>
                                        <p>{item.quantity} st à <strong>{(item.price * 10).toFixed(2)} kr</strong></p>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>
        </div>
    );
}