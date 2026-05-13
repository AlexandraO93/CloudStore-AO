import {useCart} from "../context/CartContext.jsx";
import ProductCard from "../components/ProductCard.jsx";
import "./ShoppingCart.css";
import {useNavigate} from "react-router-dom";

const ShoppingCart = () => {
    const navigate = useNavigate();
    const {cartItems, clearCart} = useCart();
    const totalPrice = cartItems.reduce((sum, item) => sum + (item.price * 10 * item.quantity), 0);

    return (
        <div className="shopping-cart-container">
            <h2 className="shopping-cart-title">Din Varukorg</h2>

            {cartItems.length === 0 ? (
                <p>Din varukorg är tom.</p>
            ) : (
                <>
                    <div className="product-grid">
                        {cartItems.map(item => (
                            <div key={item.id} className="cart-item-wrapper">
                                <ProductCard
                                    product={item}
                                    showQuantityControls={true}
                                    showAddToCart={false}
                                    showReloadCart={true}
                                    showDeleteFromCart={true}
                                    showCartInfo={true}
                                />
                            </div>
                        ))}
                    </div>


                    <div className="cart-summary">
                        <h3>Totalbelopp: {totalPrice.toFixed(2)} kr</h3>
                        <button className="empty-button" onClick={clearCart}>Töm varukorg</button>
                        <button className="checkout-button" onClick={() => navigate("/checkout")}>Fortsätt till
                            kassan
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export default ShoppingCart;