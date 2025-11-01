import "./Track.css"
import {useNavigate, useParams} from "react-router-dom";
import {useQuery} from "react-query";
import {FaRegEdit, FaStar} from "react-icons/fa";
import {useContext, useState} from "react";
import {AuthContext} from "../../AuthContext";
import axios from "axios";

export function Track() {
    const {id} = useParams()
    const {user} = useContext(AuthContext)
    const navigate = useNavigate()
    const [rating, setRating] = useState()
    const [isEditing, setIsEditing] = useState(false)

    async function fetchTrack() {
        const response = await fetch(`http://localhost:8081/track/${id}`)
        return response.json()
    }

    const {data, status} = useQuery({
        queryKey: [`track`, id],
        queryFn: () => fetchTrack(id),
        enabled: !!id
    })

    async function fetchTrackFromDB() {
        if (user && data) {
            const response = await axios.post(`http://localhost:8081/api/track/getOrCreate`, {
                trackMbid: id,
                trackTitle: data.title,
                releaseDate: data["first-release-date"],
                releaseMbid: data.releases[0].id,
                releaseTitle: data.releases[0].title,
                format: data.releases[0]["primary-type"],
                artistMbid: data["artist-credit"]?.[0]?.id,
                artistName: data["artist-credit"]?.[0]?.name
            })
            return response.data
        }
    }

    const {data: trackDB} = useQuery({
        queryKey: ['trackDB', id],
        queryFn: () => fetchTrackFromDB(),
        enabled: !!id
    })

    const handleSubmit = async () => {
        console.log("wip")
    }

    const handleLog = async () => {
        if (user && data && trackDB) {
                axios.post(`http://localhost:8081/api/scrobble`, {
                    userId: user.id,
                    trackId: trackDB.id
                })
        }
    }
    if (status === `loading`) {
        <p>Loading...</p>
    }

    if (status === `error`) {
        <p>Error!</p>
    }

    return (
        <div className="track-page">
            <div className="track-info">
                <img className="img"
                     src={`https://coverartarchive.org/release-group/${data?.releases[0].id}/front`}
                     alt='placeholder.jpg'
                />
                <div className="title-above-release">
                    <div className="title-date-artist">
                        <h1 className="title">{data?.title}</h1>
                        <h3 className="date">{data?.["first-release-date"]?.substring(0, 4)} • Track by </h3>
                        <h2 className="artist"
                            onClick={() => navigate(`/music/artist/${data?.releases[0]["artist-credit"]?.[0]?.artist.id}`)}
                        >{data?.releases[0]["artist-credit"]?.[0]?.name}</h2>
                    </div>
                    <h3 className="releases">Appears on</h3>
                    {data?.releases?.map(releaseGroup =>
                        <div className="release-format">
                            <h2 className="release" key={releaseGroup?.id}
                                onClick={() => navigate(`/music/album/${releaseGroup?.id}`)}
                            >{releaseGroup?.title} </h2>
                            <h4 className="format"> {releaseGroup["first-release-date"].substring(0, 4)} • {releaseGroup["primary-type"]} </h4>
                        </div>
                    )}
                </div>
            </div>
            <div className={isEditing ? "stats-editing" : "stats"}>
                Your Stats
                <FaRegEdit className="edit" onClick={() => {
                    user ? setIsEditing(!isEditing) : setIsEditing(false)
                }}/>
                <div className="activity">
                    <h4>500 Total Listens</h4>
                    <h4>400 Listens In 2025</h4>
                    <h4>200 Listens This Month</h4>
                    <h4>100 Listens Today</h4>
                </div>
                <div className="rating">
                    {!isEditing ? (
                            <h3 className={
                                !rating ? "rating-absent" :
                                    rating === 10 ? "rating-ten" :
                                        rating >= 8 && rating <= 9 ? "rating-high" :
                                            rating >= 6 && rating <= 7 ? "rating-med" :
                                                rating >= 4 && rating <= 5 ? "rating-medlow" :
                                                    rating >= 1 && rating <= 3 ? "rating-low" :
                                                        "rating-zero"

                            }>

                                <FaStar className="star"/>
                                {rating ? `${rating}/10` : "1-10"}</h3>
                        ) :
                        <div className="edit-rating">
                            <FaStar className="star"/>
                            <input
                                placeholder="0-10"
                                type="number"
                                value={rating}
                                onChange={(e) => setRating(e.target.value)}
                            />
                            <button className="manual-log"
                                    onClick={handleLog}>
                                Log Track
                            </button>
                            <div className="rating-buttons">
                                <button className="rate-button" onClick={handleSubmit}>
                                    UPDATE
                                </button>
                                <button className="cancel-rate-button" onClick={() => {
                                    setIsEditing(false)
                                }}>
                                    CANCEL
                                </button>
                            </div>
                        </div>


                    }
                </div>
            </div>
        </div>

    )
}