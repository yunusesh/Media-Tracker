import {useContext} from "react";
import {SpotifyAuthContext} from "../SpotifyAuthContext";
export function Settings() {
    const { redirectUserToSpotify, fetchProfile, spotifyToken, fetchCurrentlyPlaying } = useContext(SpotifyAuthContext);

    return (
        <div className="settings-page">
            {!spotifyToken && (
                <button onClick={redirectUserToSpotify}>
                    Connect to Spotify
                </button>
            )}

            {spotifyToken && (
                <button onClick={async () => {
                    const profile = await fetchCurrentlyPlaying();
                    console.log("profile", profile);
                }}>
                    Fetch Profile
                </button>
            )}
        </div>
    );
}
