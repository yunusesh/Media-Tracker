import "./Track.css"
import {useNavigate, useParams} from "react-router-dom";
import {useQuery} from "react-query";
import {FaRegEdit, FaStar} from "react-icons/fa";
import {useContext, useEffect, useState} from "react";
import {AuthContext} from "../../AuthContext";
import axios from "axios";
import {ToastContainer, toast, Bounce} from 'react-toastify';


export function Track() {
    const {id} = useParams()
    const {user} = useContext(AuthContext)
    const navigate = useNavigate()
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear()
    const currentMonth = currentDate.getMonth()
    const currentDay = currentDate.getDay()
    const [rating, setRating] = useState()
    const [isEditing, setIsEditing] = useState(false)
    const [genres, setGenres] = useState([])
    const [totalScrobbles, setTotalScrobbles] = useState([])
    const [yearScrobbles, setYearScrobbles] = useState([])
    const [monthScrobbles, setMonthScrobbles] = useState([])
    const [dayScrobbles, setDayScrobbles] = useState([])

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
        if (data) {
            const response = await axios.post(`http://localhost:8081/api/track/getOrCreate`, {
                trackMbid: id,
                trackTitle: data.title,
                releaseDate: data["first-release-date"],
                releaseMbid: data.releases[0].id,
                releaseTitle: data.releases[0].title,
                format: data.releases[0]["primary-type"],
                artistMbid: data["artist-credit"]?.[0]?.id,
                artistName: data["artist-credit"]?.[0]?.name,
                genres: data.genres?.map(genre => ({
                    mbid: genre.id,
                    genreName: genre.name
                })) || []
            })
            return response.data
        }
    }

    useEffect(() => {
        if (data) {
            setGenres(data.genres.map(genre => genre.name))
        }
    }, [data]);

    const {data: trackDB} = useQuery({
        queryKey: ['trackDB', id],
        queryFn: () => fetchTrackFromDB(),
        enabled: !!id
    })

    async function fetchRating() {
        if (user && trackDB) {
            const response = await fetch(`http://localhost:8081/api/track-rating/user/${user.id}/track/${trackDB.id}`)
            return response.json()
        }
    }

    const {data: userRating} = useQuery({
        queryKey: ['userRating', user?.id, trackDB?.id],
        queryFn: () => fetchRating(),
        enabled: !!user && !!trackDB
    })

    useEffect(() => {
        if (userRating) {
            setRating(userRating.rating)
        }
    }, [userRating])


    async function fetchUserScrobbles() {
        if (user && trackDB) {
            const response = await fetch(`http://localhost:8081/api/scrobble/user/${user.id}/track/${trackDB.id}`)
            return response.json()
        }
    }

    const {data: userScrobbles} = useQuery({
        queryKey: ['userScrobbles', user?.id, trackDB?.id],
        queryFn: () => fetchUserScrobbles(),
        enabled: !!user && !!trackDB
    })

    useEffect(() => {
        if (userScrobbles) {
            setTotalScrobbles(userScrobbles.length)
            setYearScrobbles(userScrobbles.filter(date => new Date(date).getFullYear() === currentYear).length)
            setMonthScrobbles(userScrobbles.filter(date => new Date(date).getMonth() === currentMonth).length)
            setDayScrobbles(userScrobbles.filter(date => new Date(date).getDay() === currentDay).length)
        }
    }, [userScrobbles]);

    const handleSubmit = async () => {
        if (user && trackDB) {
            await axios.post('http://localhost:8081/api/track-rating', {
                userId: user.id,
                trackId: trackDB.id,
                rating: rating
            })
            setIsEditing(false)
        }
    }

    const logSuccess = () => toast.success('Track Logged', {
        position: "bottom-center",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: false,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
        transition: Bounce,
    });

    const handleLog = async () => {
        if (user && data && trackDB) {
            axios.post(`http://localhost:8081/api/scrobble`, {
                userId: user.id,
                trackId: trackDB.id,
            })
            logSuccess()
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
                <div className="links-under-img">
                    <img className="img"
                         src={`https://coverartarchive.org/release-group/${data?.releases[0].id}/front`}
                         alt="placeholder.jpg"/>
                    <div className="album-links">
                        <h5>www.youtube.com/album</h5>
                        <h5>www.spotify.com/album</h5>
                        <h5>www.applemusic.com/album</h5>
                    </div>
                    <div className="genres">
                        {genres.join(", ") + " // genres"}
                    </div>
                </div>
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
            <div className="user-track">
                <div className={isEditing ? "stats-editing" : "stats"}>
                    Your Stats
                    <FaRegEdit className="edit" onClick={() => {
                        user ? setIsEditing(!isEditing) : setIsEditing(false)
                    }}/>
                    <div className="activity">
                        <h4>{totalScrobbles} Total Listens</h4>
                        <h4>{yearScrobbles} Listens In 2025</h4>
                        <h4>{monthScrobbles} Listens This Month</h4>
                        <h4>{dayScrobbles} Listens Today</h4>
                    </div>
                    <div className="rating">
                        {!isEditing ? (
                                <h3 className={
                                    !rating ? "rating-absent" :
                                        rating == 10 ? "rating-ten" :
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
                                <ToastContainer className="success-alert"
                                                position="bottom-center"
                                                autoClose={5000}
                                                hideProgressBar={false}
                                                newestOnTop
                                                closeOnClick={false}
                                                rtl={false}
                                                pauseOnFocusLoss
                                                draggable
                                                pauseOnHover
                                                theme="colored"
                                                transition={Bounce}
                                />
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
        </div>

    )
}