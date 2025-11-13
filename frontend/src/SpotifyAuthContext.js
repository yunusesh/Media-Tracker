import {createContext, useState, useEffect, useContext} from "react";
import {AuthContext} from "./AuthContext";

export const SpotifyAuthContext = createContext();

export const SpotifyAuthProvider = ({children}) => {
    const {user} = useContext(AuthContext)
    const [spotifyToken, setSpotifyToken] = useState(null);
    const clientId = "861232ceb8814a5aac18d04aa73a0ca3";
    const redirectUri = "https://inconsolably-blearier-catharine.ngrok-free.dev/callback";

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);

        async function getAccessToken() {
            const code = params.get("code");
            if (!code || spotifyToken) return;
            const response = await fetch(`http://localhost:8081/callback?code=${code}`)
            if (response.ok) {
                const data = await response.json()
                localStorage.setItem("spotifyToken", data["access_token"])
                setSpotifyToken(data["access_token"])

                window.history.replaceState({}, document.title, `/settings`)
            }

        }

        getAccessToken()
    }, []);

    async function redirectUserToSpotify() {
        const params = new URLSearchParams({
            client_id: clientId,
            response_type: "code",
            redirect_uri: redirectUri,
            scope: "user-read-recently-played user-read-currently-playing",
        });

        window.location = `https://accounts.spotify.com/authorize?${params.toString()}`;
    }

    async function fetchProfile() {
        if (!spotifyToken) return;
        const res = await fetch("https://api.spotify.com/v1/me", {
            headers: {Authorization: `Bearer ${spotifyToken}`},
        });
        return res.json();
    }

    async function fetchCurrentlyPlaying() {
        if (!spotifyToken) return;
        const res = await fetch("https://api.spotify.com/v1/me/player/currently-playing", {
            headers: {Authorization: `Bearer ${spotifyToken}`}
        });
        return res.json();
    }

    async function fetchRecentlyPlayed() {
        if (!spotifyToken) return;
        const res = await fetch("https://api.spotify.com/v1/me/player/recently-played", {
            headers: {Authorization: `Bearer ${spotifyToken}`}
        });
        return res.json();
    }

    return (
        <SpotifyAuthContext.Provider value={{
            spotifyToken,
            redirectUserToSpotify,
            fetchProfile,
            fetchCurrentlyPlaying,
            fetchRecentlyPlayed,
        }}>
            {children}
        </SpotifyAuthContext.Provider>
    );
};
