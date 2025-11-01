import {useEffect, useState} from "react";
import {useQuery} from "react-query";
import {useNavigate, useParams} from "react-router-dom";
import "./UserActivity.css"

export function UserActivity() {
    const {username} = useParams()
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

    useEffect(() => {
        if (userListens) {
            setListened(userListens.sort((a, b) => new Date(b.firstListenedAt) - new Date(a.firstListenedAt)))
        }
    }, [userListens])

    function handleDate(sqlDate) {
        const timestamp = new Date(sqlDate).toLocaleString()

        return [timestamp.substring(0, timestamp.indexOf(",")), timestamp.substring(timestamp.indexOf(",") + 1)]
    }

    return (
        <div className="user-ratings-page">
            <div className="user-ratings">
                {listened.map(track => (
                    <div className="ratings-page-item" key={track.trackMbid}>
                        <img className="ratings-item-img"
                             src={`https://coverartarchive.org/release-group/${track.releaseMbid}/front`}
                             alt="placeholder.png"
                             onClick={() => {
                                 navigate(`/music/track/${track.trackMbid}`)
                             }}
                        />
                        <div className="ratings-release-info">
                            <h5 className="profile-item-title"
                                key={track.trackTitle}
                                onClick={() => {
                                    navigate(`/music/track/${track.trackMbid}`)
                                }}
                            >{track.trackTitle} </h5>
                            <h5 className="format">
                                Track by
                            </h5>
                            <h5 className="profile-item-artist"
                                key={track.artistName}
                                onClick={() => {
                                    navigate(`/music/artist/${track.artistMbid}`)
                                }}
                            >{track.artistName}</h5>
                        </div>
                        <div className="listen-timestamp">
                            <h5>{handleDate(track.firstListenedAt)[1]}</h5>
                            <h5>{handleDate(track.firstListenedAt)[0]}</h5>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}