import ProfileCard from "../components/ProfileCard.jsx";
import {useAuth} from "../context/AuthContext.jsx";

export default function MyProfile() {
    const {token, user} = useAuth();

    if (!token) {
        return <p>Du måste vara inloggad för att se din profil.</p>;
    }

    return (
        <div>
            <ProfileCard/>
        </div>
    )
}