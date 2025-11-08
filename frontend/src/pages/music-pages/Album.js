import './Album.css'
import {useNavigate, useParams} from "react-router-dom";
import {useQuery} from "react-query"
import {useContext, useEffect, useState} from "react";
import {FaStar} from "react-icons/fa";
import {FaRegEdit} from "react-icons/fa";
import {AuthContext} from "../../AuthContext";
import axios from "axios";
import {Bounce, toast, ToastContainer} from "react-toastify";

export function Album() {
    const {id} = useParams();
    const {user} = useContext(AuthContext);
    // grab state of releaseGroupId from search query b/c it is has the most general album cover (not specific to release)
    const navigate = useNavigate();
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear()
    const currentMonth = currentDate.getMonth()
    const currentDay = currentDate.getDay()
    const [visible, setVisible] = useState(false);
    const [albumImage, setAlbumImage] = useState("")
    const [albumTitle, setAlbumTitle] = useState("")
    const [albumDate, setAlbumDate] = useState("")
    const [albumReissue, setAlbumReissue] = useState("")
    const [reissueIds, setReissueIds] = useState([])
    const [reissuesList, setReissuesList] = useState([])
    const [rating, setRating] = useState()
    const [isEditing, setIsEditing] = useState(false)
    const [tracklistDB, setTracklistDB] = useState([])
    const [genres, setGenres] = useState([])
    const [totalScrobbles, setTotalScrobbles] = useState([])
    const [yearScrobbles, setYearScrobbles] = useState([])
    const [monthScrobbles, setMonthScrobbles] = useState([])
    const [dayScrobbles, setDayScrobbles] = useState([])

    async function fetchAlbum() {
        const response = await fetch(`http://localhost:8081/album/${id}`)
        return response.json()
    }

    const {data, status} = useQuery({
        queryKey: ['album', id],
        queryFn: () => fetchAlbum(id),
        enabled: !!id //refetch and update page when key (id) changes (we are now on a new album page)
    })

    //this only exists to dynamically update page contents based on what reissue is selected, default to releaseGroup
    useEffect(() => {
        if (data) {
            setAlbumImage(`https://coverartarchive.org/release-group/${data.id}/front`)
            setAlbumTitle(data.title)
            setAlbumDate(data["first-release-date"].substring(0, 4))
            setAlbumReissue(data)
            const ids = data.releases.map(release => release.id)
            setReissueIds(ids)
            setGenres(data.genres.map(genre => genre.name))
        }
    }, [data])

    async function fetchReleaseFromDB() {
        if (data) {
            const response = await axios.post(`http://localhost:8081/api/release/getOrCreate`, {
                releaseMbid: id,
                title: data.title,
                releaseDate: data["first-release-date"],
                format: data["primary-type"],
                artistMbid: data["artist-credit"]?.[0]?.id,
                artistName: data["artist-credit"]?.[0]?.name,
                genres: data.genres?.map(genre => ({
                    mbid: genre.id,
                    genreName: genre.name
                })) || [] //genre object naming on mbid is different from the db so we have to map to correct name

            })
            return response.data
        }
    }

    const {data: releaseDB} = useQuery({
        queryKey: ['releaseDB', id],
        queryFn: () => fetchReleaseFromDB(),
        enabled: !!id
    })

    async function fetchRating() {
        if (user && releaseDB) {
            const response = await fetch(`http://localhost:8081/api/release-rating/user/${user.id}/release/${releaseDB.id}`)
            return response.json()
        }
    }

    const {data: userRating} = useQuery({
        queryKey: ['userRating', user?.id, releaseDB?.id],
        queryFn: () => fetchRating(),
        enabled: !!user && !!releaseDB
    })

    useEffect(() => {
        if (userRating) {
            setRating(userRating.rating)
        }
    }, [userRating])

    async function fetchUserScrobbles() {
        if (user && releaseDB) {
            const response = await fetch(`http://localhost:8081/api/scrobble/user/${user.id}/release/${releaseDB.id}`)
            return response.json()
        }
    }

    const {data: userScrobbles} = useQuery({
        queryKey: ['userScrobbles', user?.id, releaseDB?.id],
        queryFn: () => fetchUserScrobbles(),
        enabled: !!user && !!releaseDB
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
        if (user && releaseDB) {
            await axios.post('http://localhost:8081/api/release-rating', {
                userId: user.id,
                releaseId: releaseDB.id,
                rating: rating
            })
            setIsEditing(false)
        }
    }

    useEffect(() => {
        const fetchReissues = async () => {
            try {
                const responses = await Promise.all(reissueIds.map(reissueId => fetch(`http://localhost:8081/reissue/${reissueId}`)))
                const reissueData = await Promise.all(responses.map(response => response.json()))
                setReissuesList(reissueData)
            } catch (error) {
                console.error("Failed to fetch releases", error)
            }
        }
        fetchReissues()
    }, [reissueIds])


    useEffect(() => {
        const fetchTrackListFromDB = async () => {
            try {
                const responses = await Promise.all(albumReissue?.tracklist.map(track => axios.post('http://localhost:8081/api/track/getOrCreate', {
                    trackMbid: track.recording.id,
                    trackTitle: track.title,
                    releaseDate: track.recording["first-release-date"],
                    releaseMbid: id,
                    releaseTitle: albumReissue.title,
                    format: data["primary-type"],
                    artistMbid: data["artist-credit"]?.[0]?.id,
                    artistName: data["artist-credit"]?.[0]?.name
                })))
                const tracklistData = await Promise.all(responses.map(response => response.data))
                setTracklistDB(tracklistData)
            } catch (error) {
                console.error("Failed to fetch tracklist", error)
            }
        }
        if (albumReissue && data) {
            fetchTrackListFromDB()
        }
    }, [id, albumReissue, data])

    const logSuccess = () => toast.success('Release Logged', {
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
        if (user && releaseDB) {
            tracklistDB.map(track => {
                    axios.post(`http://localhost:8081/api/scrobble`, {
                        userId: user.id,
                        trackId: track.id,
                        releaseId: releaseDB.id
                    })
                }
            )
            logSuccess()
        }
    }

    if (status === 'loading') {
        return <p>Loading...</p>
    }

    if (status === 'error') {
        return <p>Error!</p>
    }

    return (
        <div className="album-page">
            <div className="album-info">
                <div className="links-under-img">
                    <img className="img" src={albumImage} alt="placeholder.jpg"/>
                    <div className="album-links">
                        <h5>www.youtube.com/album</h5>
                        <h5>www.spotify.com/album</h5>
                        <h5>www.applemusic.com/album</h5>
                    </div>
                    <div className="genres">
                        {genres.join(", ") + " // genres"}
                    </div>
                </div>
                <div className="list-under-title">
                    <div className="title-date-artist">
                        <div className="title-box">
                            <h1 className="title">{albumTitle}</h1>
                        </div>
                        <div className="date-artist">
                            <h3 className="date-format"> {albumDate} • {data["primary-type"]} by </h3>
                            {/*(0,4) grabs start of date to end of date i.e. 2014-10-12 becomes 2012) */}
                            <h2 className="artist" onClick={() => {
                                navigate(`/music/artist/${data["artist-credit"]?.[0]?.id}`)
                            }}
                                //0 grabs first release/artist
                            >{data["artist-credit"]?.[0]?.name}</h2>
                        </div>
                        <div className="reissues-container">
                            {visible && (
                                <div className="reissues-list">
                                    {reissuesList.map(reissue => (
                                        <div className="reissues-result"
                                             key={reissue.id}
                                             onClick={() => {
                                                 //if a new reissue then do all the changes else go back to defaults
                                                 if (reissue.disambiguation !== "") {
                                                     setAlbumImage(`https://coverartarchive.org/release/${reissue.id}/front`)
                                                     setAlbumTitle(`${reissue.title} (${reissue.disambiguation})`)
                                                     setAlbumReissue(reissue)
                                                     if (reissue.date !== "") {
                                                         setAlbumDate(reissue.date.substring(0, 4))
                                                     }
                                                 } else {
                                                     setAlbumImage(`https://coverartarchive.org/release-group/${data.id}/front`)
                                                     setAlbumTitle(data.title)
                                                     setAlbumDate(data["first-release-date"].substring(0, 4))
                                                     setAlbumReissue(data)
                                                 }
                                             }}>
                                            {reissue.title}{reissue.disambiguation !== "" ?
                                            ` (${reissue.disambiguation.charAt(0).toUpperCase() + reissue.disambiguation.slice(1)})` :
                                            ""}
                                        </div>
                                    ))}

                                </div>
                            )}
                            <button className="reissues" onClick={() => {
                                setVisible(!visible);

                            }}> Reissues ({data.releases?.length})
                            </button>

                        </div>
                    </div>
                    <div className="tracklist">
                        {albumReissue.tracklist?.map(track => (
                            <h3 className="tracklist-items"
                                key={track.id}
                                onClick={() => {
                                    navigate(`/music/track/${track?.recording?.id}`)
                                }}

                            >{track.title}</h3>
                        ))}
                    </div>
                </div>
            </div>
            <div className="user-album">
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
                                    Log Release
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