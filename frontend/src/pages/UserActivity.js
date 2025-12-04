import React, {useEffect, useMemo, useState} from "react";
import {useQuery} from '@tanstack/react-query';
import {useNavigate, useParams} from "react-router-dom";
import "./UserActivity.css"

export function UserActivity() {
    const {username, page} = useParams()
    const navigate = useNavigate()
    const [listened, setListened] = useState([])

    async function fetchUser() {
        const response = await fetch(`http://localhost:8081/user/${username}`)
        return response.json()
    }

    const {data: userData} = useQuery({
        queryKey: ["user", username],
        queryFn: () => fetchUser(),
        enabled: !!username,
    })

    async function fetchUserListens() {
        const response = await fetch(`http://localhost:8081/api/scrobble/user/${userData?.id}`)
        return response.json()
    }

    const {data: userListens} = useQuery({
        queryKey: ["userListens", userData?.id],
        queryFn: () => fetchUserListens(),
        enabled: !!userData?.id
    })


    const sortedListens = useMemo(() => {
        if (!userListens) return [];

        return [...userListens].sort(
            (a, b) => new Date(b.firstListenedAt) - new Date(a.firstListenedAt)
        );
    }, [userListens]);

    useEffect(() => {
        setListened(sortedListens.slice(60 * (page - 1), 60 * page));
    }, [sortedListens, page]);


    function handleDate(sqlDate) {
        const timestamp = new Date(sqlDate).toLocaleString()

        return [timestamp.substring(0, timestamp.indexOf(",")), timestamp.substring(timestamp.indexOf(",") + 1)]
    }

    return (
        <div className="user-ratings-page">
            <h1 className="category" id = "top">All Activity</h1>
            <header className="pages">
                {Array.from({length: Math.ceil(userListens?.length / 60)}).map((_, i) => (
                    <button className= {i + 1 == page ? "current-page-button" : "page-button"}
                            key={i}
                            onClick={() => {
                                navigate(`/user/${username}/activity/${i + 1}`)
                            }}>
                        {i + 1}
                    </button>
                ))}
            </header>
            <div className="user-ratings">
                {listened.map((track, i) => (
                    <div className="ratings-page-item" key={i}>
                        <img className="ratings-item-img"
                             src={
                                 track.releaseMbid ?
                                     `https://coverartarchive.org/release-group/${track.releaseMbid}/front` :
                                     `https://coverartarchive.org/release-group/${track.altReleaseMbid}/front`
                             }
                             alt="placeholder.png"
                             onClick={() => {
                                 navigate(`/track/${track.trackMbid}`)
                             }}
                        />
                        <div className="ratings-release-info">
                            <h5 className="profile-item-title"
                                onClick={() => {
                                    navigate(`/track/${track.trackMbid}`)
                                }}
                            >{track.trackTitle} </h5>
                            <h5 className="format">
                                Track by
                            </h5>
                            <div className="profile-item-container">
                                {track.artists.map((artist, index, array) => (
                                    <span key={index}>
                                    <h5 className="profile-item-artist"
                                        onClick={() => navigate(`/artist/${artist.mbid}`)}
                                    >
                                    {artist.artistName}
                                    </h5>
                                        {index < array.length - 1 && (
                                            <span>{index === array.length - 2 ? " & " : ", "}</span>
                                        )}
                                </span>
                                ))}
                            </div>
                        </div>
                        <div className="listen-timestamp">
                            <h5>{handleDate(track.firstListenedAt)[1]}</h5>
                            <h5>{handleDate(track.firstListenedAt)[0]}</h5>
                        </div>
                    </div>
                ))}
            </div>
            <footer className="pages">
                {Array.from({length: Math.ceil(userListens?.length / 60)}).map((_, i) => (
                    <button className= {i + 1 == page ? "current-page-button" : "page-button"}
                            key={i}
                            onClick={() => {
                                navigate(`/user/${username}/activity/${i + 1}`)
                                document.getElementById("top")
                                    .scrollIntoView({block: "end", behavior: "smooth"})
                            }}>
                        {i + 1}
                    </button>
                ))}
            </footer>
        </div>
    )
}