import {useContext, useEffect} from "react";
import {SpotifyAuthContext} from "../SpotifyAuthContext";
import {AuthContext} from "../AuthContext";
import {useQuery} from '@tanstack/react-query';
import {FaSpotify} from "react-icons/fa";
import "./Settings.css"

export function Settings() {
    const {redirectUserToSpotify, fetchProfile, spotifyToken} = useContext(SpotifyAuthContext);
    const {user} = useContext(AuthContext)

    const { data: userSpotify } = useQuery({
        queryKey: ["userSpotify", user],
        queryFn: () => fetchProfile(),
        enabled: !!user
    })

    useEffect(() => {
        console.log(userSpotify)
    }, [userSpotify]);

    return (
        <div className="settings-page">
            <div className="connections">
                <h2>Connections</h2>
                {!spotifyToken && user && (
                    <button className = "connect-spotify" onClick={redirectUserToSpotify}>
                        <FaSpotify className = "spotify-logo"/>
                        <h3>Connect to Spotify</h3>
                    </button>
                )}

                {spotifyToken && user && userSpotify && (
                    <div className = "connected-spotify">
                        <FaSpotify className = "spotify-logo"/>
                        <h3>{userSpotify["display_name"]}</h3>
                    </div>
                )}
            </div>
        </div>
    );
}
