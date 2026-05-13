import './App.css'
import {Navigate, Outlet, Route, Routes} from "react-router-dom"; // Fixad import
import AuthForm from "./components/AuthForm";
import ProductList from "./pages/ProductList.jsx";
import {useAuth} from "./context/AuthContext.jsx";
import LoginForm from "./components/LoginForm.jsx";
import ProductDetail from "./pages/ProductDetail.jsx";
import Footer from "./components/Footer.jsx";
import CloudStoreNavBar from "./components/Navbar.jsx"
import LikedProducts from "./pages/LikedProducts.jsx";
import ShoppingCart from "./pages/ShoppingCart.jsx";
import Checkout from "./pages/Checkout.jsx";
import MyProfile from "./pages/MyProfile.jsx";
import MyOrders from "./pages/MyOrders.jsx";
import Confirmation from "./pages/Confirmation.jsx";

const StartPage = () => {
    const {token} = useAuth();
    return token ? <Navigate to="/products"/> : <AuthForm/>;
};

const ProtectedRoute = ({children}) => {
    const {token} = useAuth();
    return token ? <Outlet/> : <Navigate to="/register"/>;
};

function App() {
    return (
        <div className="app-wrapper">
            <CloudStoreNavBar/>
            <main className="main-content">
                <Routes>
                    <Route path="/" element={<StartPage/>}/>

                    <Route path="/login" element={<LoginForm/>}/>
                    <Route path="/register" element={<AuthForm/>}/>


                    <Route element={<ProtectedRoute/>}>
                        {/* Skyddad produktsida */}
                        <Route path="/products" element={<ProductList/>}/>
                        <Route path="/products/:id" element={<ProductDetail/>}/>
                        <Route path="/products/liked" element={<LikedProducts/>}/>
                        <Route path="/shopping-cart" element={<ShoppingCart/>}/>
                        <Route path="/checkout" element={<Checkout/>}/>
                        <Route path="/users/:id" element={<MyProfile/>}/>
                        <Route path="/users/:id/orders-by-user" element={<MyOrders/>}/>
                        <Route path="/confirmation" element={<Confirmation/>}/>
                    </Route>

                    {/* Catch-all: Om man skriver fel URL, skickas man till start */}
                    <Route path="*" element={<Navigate to="/"/>}/>
                </Routes>
            </main>
            <Footer/>
        </div>
    );
}

export default App;