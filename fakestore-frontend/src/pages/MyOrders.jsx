import {useEffect, useState} from "react";
import {useAuth} from "../context/AuthContext.jsx";
import {AUTH_API_URL} from "../config/api.js";
import "./MyOrders.css";
import ShoppingBag from "../assets/shopping-bag.svg"


export default function MyOrders() {
    const {token, customerId} = useAuth();
    const [orderData, setOrderData] = useState(null);
    const [expandedOrderId, setExpandedOrderId] = useState(null); //
    const [localProducts, setLocalProducts] = useState([]);

    useEffect(() => {
        const savedProducts = JSON.parse(localStorage.getItem("all_products") || "[]");
        setLocalProducts(savedProducts);

        const fetchOrders = async () => {
            try {
                const res = await fetch(`${AUTH_API_URL}/users/${customerId}/orders-by-user`, {
                    headers: {"Authorization": `Bearer ${token}`}
                });
                if (res.ok) {
                    const result = await res.json();
                    setOrderData(result);
                }
            } catch (err) {
                console.error("Fel vid hämtning av ordrar:", err);
            }
        };
        if (token && customerId) fetchOrders();
    }, [token, customerId]);

    const formatOrderDate = (dateString) => {
        if (!dateString) return "";
        return dateString.substring(0, 16).replace("T", " ");
    };

    const getProductInfo = (productId) => {
        return localProducts.find(p => p.id === productId) || {title: `Produkt ${productId}`, image: null};
    };

    if (!orderData) return <p>Laddar dina beställningar...</p>;

    return (
        <div className="orders-container">
            <h2>Beställningar för {orderData.user.firstName} {orderData.user.lastName}</h2>

            {orderData.orders.length === 0 ? (
                <p>Du har inte gjort några beställningar än.</p>
            ) : (
                <div className="orders-content">
                    <div className="orders-list">
                        {orderData.orders.map(order => {
                            const isExpanded = expandedOrderId === order.orderId;

                            return (
                                <div
                                    key={order.orderId}
                                    className={`order-card ${isExpanded ? "expanded" : ""}`}
                                    onClick={() => setExpandedOrderId(isExpanded ? null : order.orderId)}
                                >
                                    <div className="order-summary">
                                        <img className="order-image" src={ShoppingBag} alt="Bag"/>
                                        <div className="summary-text">
                                            <h3>Order #{order.orderId}</h3>
                                            <p>{formatOrderDate(order.orderDate)}</p>
                                        </div>
                                        <div className="summary-info">
                                            <p>{order.items.length} produkter</p>
                                            <p className="total-price">
                                                {(order.totalAmount * 10).toFixed(2)} kr
                                            </p>
                                        </div>
                                        <span className={`status-badge ${order.status.toLowerCase()}`}>
                                    {order.status}
                                </span>
                                    </div>

                                    {isExpanded && (
                                        <div className="order-details-expanded">
                                            <hr/>
                                            <h4>Produktspecifikation</h4>
                                            <div className="expanded-items-grid">
                                                {order.items.map(item => {
                                                    const info = getProductInfo(item.productId);
                                                    return (
                                                        <div key={item.productId} className="detail-item">
                                                            {info.image && <img src={info.image} alt={info.title}
                                                                                className="mini-prod-img"/>}
                                                            <div className="item-text">
                                                                <p className="item-title">{info.title}</p>
                                                                <p className="item-meta">{item.quantity} st
                                                                    à {(item.price * 10).toFixed(2)} kr</p>
                                                            </div>
                                                        </div>
                                                    );
                                                })}
                                            </div>
                                        </div>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}
        </div>
    );
}