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
    const now = Date.now();
    const oneDay = 24 * 60 * 60 * 1000;
    const oneWeek = 7 * oneDay;
    const oneMonth = 30 * oneDay;
    const oneYear = 365 * oneDay;
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
                isrc: null,
                trackTitle: data.title,
                releaseDate: data["first-release-date"],
                releaseMbid: data.releases[0].id,
                releaseSpotifyId: null,
                releaseTitle: data.releases[0].title,
                format: data.releases[0]["primary-type"],
                artists: data["artist-credit"]?.map(artist => ({
                    mbid: artist.id,
                    spotifyId: null,
                    artistName: artist.name
                })) || [],
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
        enabled: !!id && !!data
    })

    async function fetchRating() {
        const response = await fetch(`http://localhost:8081/api/track-rating/user/${user.id}/track/${trackDB.id}`)
        return response.json()
    }

    const {data: userRating} = useQuery({
        queryKey: ['userRating', user?.id, trackDB?.id],
        queryFn: () => fetchRating(),
        enabled: !!user?.id && !!trackDB?.id
    })

    useEffect(() => {
        if (userRating) {
            setRating(userRating.rating)
        }
    }, [userRating])


    async function fetchUserScrobbles() {
        const response = await fetch(`http://localhost:8081/api/scrobble/user/${user.id}/track/${trackDB.id}`)
        return response.json()

    }

    const {data: userScrobbles} = useQuery({
        queryKey: ['userScrobbles', user?.id, trackDB?.id],
        queryFn: () => fetchUserScrobbles(),
        enabled: !!user?.id && !!trackDB?.id
    })

    useEffect(() => {
        if (userScrobbles) {
            setTotalScrobbles(userScrobbles.length)
            setYearScrobbles(userScrobbles.filter(date => new Date(date) >= now - oneYear).length)
            setMonthScrobbles(userScrobbles.filter(date => new Date(date) >= now - oneMonth).length)
            setDayScrobbles(userScrobbles.filter(date => new Date(date) >= now - oneDay).length)
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
                        {data?.["artist-credit"]?.map((artist, index, array) => (
                            <span key={artist.id}>
                                    <h2 className="artist"
                                        onClick={() => navigate(`/artist/${artist.id}`)}
                                    >
                                    {artist.name}
                                    </h2>
                                {index < array.length - 1 && (
                                    <span>{index === array.length - 2 ? " & " : ", "}</span>
                                )}
                                </span>
                        ))}
                    </div>
                    <h3 className="appears-on">Appears on</h3>
                    {data?.releases?.map(releaseGroup =>
                        <div className="release-format">
                            <h2 className="release" key={releaseGroup?.id}
                                onClick={() => navigate(`/album/${releaseGroup?.id}`)}
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
                                            rating >= 8 && rating <= 9.9 ? "rating-high" :
                                                rating >= 6 && rating <= 7.9 ? "rating-med" :
                                                    rating >= 4 && rating <= 5.9 ? "rating-medlow" :
                                                        rating >= 1 && rating <= 3.9 ? "rating-low" :
                                                            "rating-zero"

                                }>

                                    <FaStar className="star"/>
                                    {rating ? `${rating}/10` : "1-10"}</h3>
                            ) :
                            <div className="edit-rating">
                                <FaStar className="star"/>
                                <input
                                    placeholder="0-10"
                                    value={rating}
                                    onChange={(e) => {
                                        let val = e.target.value;
                                        if (val === "") {
                                            setRating(val);
                                            return;
                                        }

                                        // Only allow digits, optional dot, max 1 decimal
                                        if (!/^\d{0,2}(\.\d?)?$/.test(val)) return;

                                        let num = parseFloat(val);
                                        if (num > 10) val = val.substring(0,1);
                                        if (num < 0) val = "0";

                                        // Round to 1 decimal and remove infinite zeros
                                        if (val.includes(".")) {
                                            const parts = val.split(".");
                                            // Keep only 1 decimal place
                                            parts[1] = parts[1].substring(0, 1);
                                            val = parts.join(".");
                                        }

                                        if (val.length > 3) val = val.substring(0, 3);

                                        setRating(val);
                                    }
                                    }
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