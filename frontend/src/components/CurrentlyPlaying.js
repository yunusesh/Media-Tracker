import {AuthContext} from "../AuthContext";
import {useContext, useEffect, useRef, useState} from "react";
import {SpotifyAuthContext} from "../SpotifyAuthContext";
import "./CurrentlyPlaying.css"
import {TiPinOutline} from "react-icons/ti";

export function CurrentlyPlaying() {
    const {user} = useContext(AuthContext)
    const {fetchCurrentlyPlaying, spotifyToken, refreshSpotifyToken} = useContext(SpotifyAuthContext)
    const [currentlyPlaying, setCurrentlyPlaying] = useState(null)
    const [pinned, setPinned] = useState(false)
    const containerRef = useRef(null);
    const textRef = useRef(null);
    const [scrollDistance, setScrollDistance] = useState(0);
    const [isPlaying, setIsPlaying] = useState(false);

    useEffect(() => {
        if (!spotifyToken || !user) return;
        let interval = setInterval(() => {
            async function fetchCurrent() {
                try {
                    const response = await fetchCurrentlyPlaying()

                    if (response.status === 429) {
                        console.warn("Rate limit exceeded -- wait 10 seconds")
                        clearInterval(interval)
                        interval = setInterval(fetchCurrent, 10000)
                    }
                    setCurrentlyPlaying(response.item)
                    setIsPlaying(response["is_playing"])

                    if (containerRef.current && textRef.current) {
                        const containerWidth = containerRef.current.offsetWidth;
                        const textWidth = textRef.current.scrollWidth;

                        if (textWidth > containerWidth) {
                            setScrollDistance(textWidth - containerWidth);
                        } else {
                            setScrollDistance(0);
                        }
                    }
                } catch (error) {
                    console.log(error, "No track playing")
                }
            }

            fetchCurrent()

        }, 3000)

        return () => clearInterval(interval);
    })

    if (!spotifyToken || !currentlyPlaying || !user) {
        return null
    }

    return (
        <div className={pinned ? "currently-playing-container-pinned" : "currently-playing-container"}>
            <div className="player">
                <TiPinOutline className={pinned ? "pin-active" : "pin"}
                              onClick={() => {
                                  setPinned(!pinned)
                              }}/>
                <div className="player-left">
                    <img className="player-img"
                         src={currentlyPlaying.album.images[2].url}
                         alt="placeholder.png"/>
                    <div className="player-info" ref={containerRef}>
                        <h3 className="player-track"
                            ref={textRef}
                            style={{
                                animation: scrollDistance
                                    ? `scrollText ${scrollDistance / 30 + 3}s linear infinite alternate`
                                    : "none",
                            }}
                        >{currentlyPlaying.name}</h3>
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
                <div className="equalizer-container">
                    <div className={isPlaying ? "bar-1-active" : "bar-1"}></div>
                    <div className={isPlaying ? "bar-2-active" : "bar-2"}></div>
                    <div className={isPlaying ? "bar-3-active" : null}></div>

                </div>
            </div>
        </div>
    )
}