import React, {useState} from 'react';
import {useNavigate} from "react-router-dom";
import "./ProductCard.css";
import {useAuth} from "../context/AuthContext.jsx";
import {useCart} from "../context/CartContext.jsx";
import {PRODUCT_API_URL} from "../config/api.js";

export default function ProductCard({
                                        product,
                                        onStatusChange,
                                        showQuantityControls = false,
                                        showAddToCart = true,
                                        showReloadCart = false,
                                        showDeleteFromCart = false,
                                        showCartInfo = false
                                    }) {
    const navigate = useNavigate();
    const {token, user} = useAuth();
    const [isLiked, setIsLiked] = useState(product.likedByEmails?.includes(user?.email));
    const [quantity, setQuantity] = useState(product.quantity || 1);
    const {addToCart, updateQuantity, removeFromCart} = useCart();

    const handleLikeProduct = async (e) => {
        e.preventDefault();

        if (!token) return;

        try {
            const res = await fetch(`${PRODUCT_API_URL}/products/${product.id}/like`, {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (res.ok) {
                const newLikedState = !isLiked;
                setIsLiked(newLikedState);

                if (onStatusChange) {
                    onStatusChange(product.id, newLikedState);
                }
            }
        } catch (error) {
            console.error("Kunde inte gilla inlägget:", error);
        }
    };

    const handlePlusQuantity = (e) => {
        e.stopPropagation();
        setQuantity(prev => prev + 1);
    };

    const handleMinusQuantity = (e) => {
        e.stopPropagation();
        setQuantity(prev => (prev > 1 ? prev - 1 : 1));
    };

    const handleAddToCart = (e) => {
        e.stopPropagation();
        addToCart({...product, quantity: quantity});
        alert(`${product.title} tillagd i varukorgen!`);
    };

    const handleUpdateCart = (e) => {
        e.stopPropagation();
        updateQuantity(product.id, quantity);
        alert(`Antalet för ${product.title} har uppdaterats till ${quantity}!`);
    };

    const handleDeleteFromCart = (e) => {
        e.stopPropagation();
        removeFromCart(product.id);
        alert(`${product.title} borttagen ur varukorgen`);
    };

    return (
        <article className="product-card">
            <div className="product-card-content" onClick={() => navigate(`/products/${product.id}`)}>
                <img className="product-image" src={product.image} alt={product.title}/>
                <h3 className="product-title">{product.title}</h3>
                <small className="product-category">{product.category} </small>
                <p className="product-price"><strong>Pris: </strong>{(product.price * 10).toFixed(2)} kr</p>

                {showCartInfo && (
                    <div className="cart-item-details">
                        <p>Antal: {product.quantity} st</p>
                        <p>Delsumma: {(product.price * 10 * product.quantity).toFixed(2)} kr</p>
                    </div>
                )}
            </div>

            <div className="product-buttons">
                <div>
                    <button className="read-more-button" onClick={() => navigate(`/products/${product.id}`)}>Läs mer
                    </button>
                    <button className="like-button" onClick={handleLikeProduct}>
                        {isLiked ? '♥' : '♡'}
                    </button>
                </div>
                <div className="add-to-cart-buttons">
                    {showQuantityControls && (
                        <div className="quantity-controls">
                            <button className="minus-button" onClick={handleMinusQuantity}>-</button>
                            <span className="number-in-cart-button">{quantity}</span>
                            <button className="plus-button" onClick={handlePlusQuantity}>+</button>
                        </div>
                    )}
                    {showAddToCart && (
                        <button className="buy-button" onClick={handleAddToCart}>Lägg i varukorgen</button>
                    )}
                    {showReloadCart && (
                        <button className="reload-cart" onClick={handleUpdateCart}></button>
                    )}
                    {showDeleteFromCart && (
                        <button className="delete-from-cart-button" onClick={handleDeleteFromCart}></button>
                    )}
                </div>
            </div>
        </article>
    );
}

