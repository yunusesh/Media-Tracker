import {useContext} from "react";
import {SpotifyAuthContext} from "../SpotifyAuthContext";
import {AuthContext} from "../AuthContext";
export function Settings() {
    const { redirectUserToSpotify, fetchProfile, spotifyToken, fetchCurrentlyPlaying } = useContext(SpotifyAuthContext);
    const { user } = useContext(AuthContext)

    return (
        <div className="settings-page">
            {!spotifyToken && user && (
                <button onClick={redirectUserToSpotify}>
                    Connect to Spotify
                </button>
            )}

            {spotifyToken && user && (
                <button onClick={async () => {
                    const profile = await fetchProfile();
                    console.log("profile", profile);
                }}>
                    Fetch Profile
                </button>
            )}
        </div>
    );
}
