import {AuthContext} from "../AuthContext";
import {useContext, useEffect, useState} from "react";
import {SpotifyAuthContext} from "../SpotifyAuthContext";
import "./CurrentlyPlaying.css"

export function CurrentlyPlaying() {
    const {user} = useContext(AuthContext)
    const {fetchCurrentlyPlaying, spotifyToken} = useContext(SpotifyAuthContext)
    const [currentlyPlaying, setCurrentlyPlaying] = useState(null)

    useEffect(() => {
        const interval = setInterval(() => {
            if (spotifyToken) {
                async function fetchCurrent() {
                    try {
                        const current = await fetchCurrentlyPlaying();
                        setCurrentlyPlaying(current.item)
                    } catch (error) {
                        console.log(error, "No track playing")
                    }
                }

                fetchCurrent()
            }
        }, 3000)
    })

    return (
        <div className="currently-playing-container">
            {spotifyToken && currentlyPlaying && (
                <div className="player">
                    <img className = "player-img"
                         src = {currentlyPlaying.album.images[2].url}
                         alt = "placeholder.png"/>

                    <div className="player-info">
                        <h3 className="player-track">{currentlyPlaying.name}</h3>
                        <div className="player-artist">
                            {currentlyPlaying.artists.map((artist, index, array) => (
                                <span key={artist.id}>
                                    <h3>{artist.name}</h3>
                                    {index < array.length - 1 && (
                                        <span>{index === array.length - 2 ? " & " : ", "}</span>
                                    )}
                                </span>
                            ))}
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}