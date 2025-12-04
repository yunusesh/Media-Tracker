import {createContext, useState, useEffect, useContext} from "react";
import {AuthContext} from "./AuthContext";
import axios from "axios";
import {useQuery} from '@tanstack/react-query';

export const SpotifyAuthContext = createContext();

export const SpotifyAuthProvider = ({children}) => {
    const {user} = useContext(AuthContext)
    const [spotifyToken, setSpotifyToken] = useState(null);
    const clientId = "861232ceb8814a5aac18d04aa73a0ca3";
    const redirectUri = "https://inconsolably-blearier-catharine.ngrok-free.dev/callback";
    const params = new URLSearchParams(window.location.search);
    const code = params.get("code");

    async function fetchUserSpotify() {
        if (!user) return;
        const response = await fetch(`http://localhost:8081/api/spotify/user/${user.id}`)

        return response.json()
    }

    const {data: userData} = useQuery({
        queryKey: ["userData", user],
        queryFn: () => fetchUserSpotify(),
        enabled: !!user
    })

    useEffect(() => {
        const saved = localStorage.getItem("spotifyToken")
        if (saved) {
            setSpotifyToken(saved);
        } else if (userData && userData.accessToken) {
            localStorage.setItem("spotifyToken", userData.accessToken)
            setSpotifyToken(userData.accessToken)
        }
    }, []);

    useEffect(() => {
        if (!code || !user || spotifyToken) return;

        let duplicate = false;

        async function getAccessToken() {
            if (duplicate) return
            duplicate = true //avoids reruns

            const response = await fetch(`http://localhost:8081/callback?code=${code}`)
            if (response.ok && user) {
                const data = await response.json()

                await axios.post('http://localhost:8081/api/spotify/user', {
                    userId: user.id,
                    accessToken: data["access_token"],
                    accessTokenExpiry: data["expires_in"],
                    refreshToken: data["refresh_token"]
                })

                localStorage.setItem("spotifyToken", data["access_token"])
                setSpotifyToken(data["access_token"])

                window.history.replaceState({}, "", "/settings")
            }
        }

        getAccessToken()
    }, [code, user, spotifyToken]);

    async function refreshSpotifyToken() {
        if (!userData) return;
        const response = await fetch(`http://localhost:8081/api/spotify/token?refresh=${userData.refreshToken}`)
        const token = await response.json()
        if (response.status === 401) {
            localStorage.removeItem("spotifyToken")
            setSpotifyToken(null)
        } else {
            localStorage.setItem("spotifyToken", token["access_token"])
            setSpotifyToken(token["access_token"])
            await axios.put(`http://localhost:8081/api/spotify/user/${userData.userId}`, {
                accessToken: token["access_token"]
            })
        }
    }

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
        try {
            const res = await fetch("https://api.spotify.com/v1/me/player/currently-playing", {
                headers: {Authorization: `Bearer ${spotifyToken}`}
            });

            if (res.status === 401) {
                await refreshSpotifyToken()
            } else {
                return res.json();
            }

        } catch (error) {
            console.log(error, "Invalid token")
        }
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
            refreshSpotifyToken
        }}>
            {children}
        </SpotifyAuthContext.Provider>
    );
};
