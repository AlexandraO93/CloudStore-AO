import './App.css'
import {Navigate, Route, Routes} from "react-router-dom"; // Fixad import
import AuthForm from "./components/AuthForm";
import ProductList from "./components/ProductList";
import {useAuth} from "./context/useAuth.js";
import LoginForm from "./components/LoginForm.jsx";
import ProductDetail from "./components/ProductDetail.jsx";
import Footer from "./components/Footer.jsx";
import CloudStoreNavBar from "./components/Navbar.jsx"

const StartPage = () => {
    const {token} = useAuth();
    return token ? <Navigate to="/products"/> : <AuthForm/>;
};

const ProtectedRoute = ({children}) => {
    const {token} = useAuth();
    return token ? children : <Navigate to="/register"/>;
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


                    {/* Skyddad produktsida */}
                    <Route
                        path="/products"
                        element={
                            <ProtectedRoute>
                                <ProductList/>
                            </ProtectedRoute>
                        }
                    />
                    <Route path="/products/:id" element={<ProductDetail/>}/>
                    {/* Catch-all: Om man skriver fel URL, skickas man till start */}
                    <Route path="*" element={<Navigate to="/"/>}/>
                </Routes>
            </main>
            <Footer/>
        </div>
    );
}

export default App;