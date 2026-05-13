import React, {createContext, useContext, useEffect, useState} from 'react';

const CartContext = createContext();

export const CartProvider = ({children}) => {
    const [cartItems, setCartItems] = useState(() => {
        const savedCart = localStorage.getItem("addedProducts");
        return savedCart ? JSON.parse(savedCart) : [];
    });

    useEffect(() => {
        localStorage.setItem("addedProducts", JSON.stringify(cartItems));
    }, [cartItems]);

    const addToCart = (productToAdd) => {
        const incomingQty = (productToAdd.quantity) || 1;

        setCartItems((prevItems) => {
            const existingItem = prevItems.find(item => item.id === productToAdd.id);

            if (existingItem) {
                return prevItems.map(item =>
                    item.id === productToAdd.id
                        ? {...item, quantity: (item.quantity) + incomingQty}
                        : item
                );
            } else {
                return [...prevItems, {...productToAdd, quantity: incomingQty}];
            }
        });
    };

    const updateQuantity = (productId, newQuantity) => {
        setCartItems((prevItems) =>
            prevItems.map((item) =>
                item.id === productId
                    ? {...item, quantity: Number(newQuantity)}
                    : item
            )
        );
    }

    const removeFromCart = (productId) => {
        setCartItems((prevItems) =>
            prevItems.filter(item => item.id !== productId)
        );
    };

    const clearCart = () => {
        setCartItems([]);
    };

    return (
        <CartContext.Provider value={{cartItems, addToCart, updateQuantity, removeFromCart, clearCart}}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => {
    const context = useContext(CartContext);
    if (!context) {
        throw new Error("useCart måste användas inom en CartProvider");
    }
    return context;
};