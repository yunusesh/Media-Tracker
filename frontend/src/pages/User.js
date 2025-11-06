import {useNavigate, useParams} from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import {useQuery} from "react-query";
import "./User.css"
import {FaRegEdit, FaStar} from "react-icons/fa";
import {AuthContext} from "../AuthContext";

export function User() {
    const navigate = useNavigate()
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear();
    const {username} = useParams()
    const {user} = useContext(AuthContext)
    const [ratings, setRatings] = useState([])
    const [listened, setListened] = useState([])
    const [topOfYear, setTopOfYear] = useState([])
    const [isEditing, setIsEditing] = useState(false)
    const [top5, setTop5] = useState([])

    function handleDate(sqlDate) {
        const timestamp = new Date(sqlDate).toLocaleString()

        return [timestamp.substring(0, timestamp.indexOf(",")), timestamp.substring(timestamp.indexOf(",") + 1)]
    }

    async function fetchUser() {
        const response = await fetch(`http://localhost:8081/user/${username}`)
        return response.json()
    }

    const {data: userData} = useQuery({
        queryKey: ["user", username],
        queryFn: () => fetchUser(),
        enabled: !!username,
    })

    async function fetchUserReleaseRatings() {
        const response = await fetch(`http://localhost:8081/api/release-rating/user/${userData?.id}`)
        return response.json()
    }

    const {data: userReleaseRatings, status} = useQuery({
        queryKey: ["userRatings", userData?.id],
        queryFn: () => fetchUserReleaseRatings(),
        enabled: !!userData?.id,
    })

    async function fetchUserTrackRatings() {
        const response = await fetch(`http://localhost:8081/api/track-rating/user/${userData?.id}`)
        return response.json()
    }

    const {data: userTrackRatings} = useQuery({
        queryKey: ["userTrackRatings", userData?.id],
        queryFn: () => fetchUserTrackRatings(),
        enabled: !!userData?.id,
    })

    useEffect(() => {
        if (userTrackRatings && userReleaseRatings) {
            const userRatings = userTrackRatings.concat(userReleaseRatings)
            setRatings(userRatings.sort((a, b) => new Date(b.ratedAt) - new Date(a.ratedAt)).slice(0, 12))
            setTopOfYear(userRatings.filter(rating => rating.releaseDate != null && rating.releaseDate.includes(currentYear)))
        }
    }, [userTrackRatings, userReleaseRatings])

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
            setListened(
                userListens.sort((a, b) => new Date(b.firstListenedAt) - new Date(a.firstListenedAt)).slice(0, 6)
            );
        }
    }, [userListens])

    async function fetchUserTop5(){
        const response = await fetch(`http://localhost:8081/api/user/${userData?.id}/top/releases`)
        return response.json()
    }

    const {data: userTopReleases} = useQuery({
        queryKey: ["userTopReleases", userData?.id],
        queryFn: () => fetchUserTop5(),
        enabled: !!userData?.id
    })

    useEffect(() => {
        if(userTopReleases){
            setTop5(userTopReleases)
        }
    }, [userTopReleases])

    if (status === 'loading') {
        return <p>Loading...</p>
    }

    if (status === 'error') {
        return <p>Error!</p>
    }

    return (
        <div className="user-page">
            {isEditing ? (
                <div className ="edit-top5">
                    <div className = "top5-category">
                        <div className = "top5-pre">
                            1
                        </div>
                    </div>
                    <div className = "top5-category">
                        <div className = "top5-pre">
                            2
                        </div>

                    </div>
                    <div className = "top5-category">
                        <div className = "top5-pre">
                            3
                        </div>

                    </div>
                    <div className = "top5-category">
                        <div className = "top5-pre">
                            4
                        </div>

                    </div>
                    <div className = "top5-category">
                        <div className = "top5-pre">
                            5
                        </div>

                    </div>

                </div>
            ) : null}
            <div className="profile">
                <div className="profile-categories">
                    <div className="top5-header">
                        <h1 className="category">Top 5</h1>
                        {user && user.username === username ?
                            <FaRegEdit className = "edit" onClick={() => {setIsEditing(!isEditing)}}/> :
                            null
                        }
                    </div>
                    <div className="category-releases">
                        {top5.map(release => (
                            <div className="releaseGroup-items" key={release.mbid}>
                                <img className="profile-item-img"
                                     src={`https://coverartarchive.org/release-group/${release.releaseMbid}/front`}
                                     alt="placeholder.png"
                                     onClick={() => {
                                         navigate(
                                             release.releaseTitle ?
                                                 `/music/album/${release.releaseMbid}` :
                                                 `/music/track/${release.trackMbid}`
                                         )
                                     }}
                                     key={release.releaseMbid + "image"}
                                />
                                <div className="release-info" key={release.releaseMbid + "release-info"}>
                                    {release.releaseTitle ?
                                        <h4 className="profile-item-title"
                                            key={release.releaseTitle + "release"}
                                            onClick={() => {
                                                navigate(`/music/album/${release.releaseMbid}`)
                                            }}
                                        >{release.releaseTitle} </h4>
                                        :
                                        <h4 className="profile-item-title"
                                            key={release.trackTitle + "track"}
                                            onClick={() => {
                                                navigate(`/music/track/${release.trackMbid}`)
                                            }}
                                        >{release.trackTitle} </h4>
                                    }
                                    {release.format ?
                                        <h5 className="format" key={release.format}>
                                            {release.format.charAt(0).toUpperCase() + release.format.slice(1)} by
                                        </h5>
                                        :
                                        <h5 className="format" key="track">
                                            Track by
                                        </h5>
                                    }
                                    <h4 className="profile-item-artist"
                                        key={release.artistName}
                                        onClick={() => {
                                            navigate(`/music/artist/${release.artistMbid}`)
                                        }}
                                    >{release.artistName}</h4>
                                </div>
                                <div className = "top5-rank">
                                    {release.tier}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
                <div className="profile-categories">
                    <h1 className="category">Top of {currentYear}</h1>
                    <div className="category-releases">
                        {topOfYear.map(rating => (
                            <div className="releaseGroup-items" key={rating.mbid}>
                                <img className="profile-item-img"
                                     src={`https://coverartarchive.org/release-group/${rating.releaseMbid}/front`}
                                     alt="placeholder.png"
                                     onClick={() => {
                                         navigate(
                                             rating.title ?
                                                 `/music/album/${rating.releaseMbid}` :
                                                 `/music/track/${rating.trackMbid}`
                                         )
                                     }}
                                     key={rating.releaseMbid + "image"}
                                />
                                <div className="release-info" key={rating.mbid + "release-info"}>
                                    {rating.title ?
                                        <h4 className="profile-item-title"
                                            key={rating.title + "release"}
                                            onClick={() => {
                                                navigate(`/music/album/${rating.releaseMbid}`)
                                            }}
                                        >{rating.title} </h4>
                                        :
                                        <h4 className="profile-item-title"
                                            key={rating.trackTitle + "track"}
                                            onClick={() => {
                                                navigate(`/music/track/${rating.trackMbid}`)
                                            }}
                                        >{rating.trackTitle} </h4>
                                    }
                                    {rating.format ?
                                        <h5 className="format" key={rating.format}>
                                            {rating.format.charAt(0).toUpperCase() + rating.format.slice(1)} by
                                        </h5>
                                        :
                                        <h5 className="format" key="track">
                                            Track by
                                        </h5>
                                    }
                                    <h4 className="profile-item-artist"
                                        key={rating.artistName}
                                        onClick={() => {
                                            navigate(`/music/artist/${rating.artistMbid}`)
                                        }}
                                    >{rating.artistName}</h4>
                                </div>
                                <div className="rating-value" key={rating.mbid + rating.rating}>
                                    <h4 className={
                                        rating.rating == 10 ? "rating-value-ten" :
                                            rating.rating >= 8 && rating.rating <= 9 ? "rating-value-high" :
                                                rating.rating >= 6 && rating.rating <= 7 ? "rating-value-med" :
                                                    rating.rating >= 4 && rating.rating <= 5 ? "rating-value-medlow" :
                                                        rating.rating >= 1 && rating.rating <= 3 ? "rating-value-low" :
                                                            "rating-value-zero"

                                    } key={rating.id}>
                                        <FaStar className="star-profile" key={rating.id + "star"}/>
                                        {rating.rating}/10
                                    </h4>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
                <div className="profile-categories">
                    <div className="category-header">
                        <h1 className="category">Recently Listened</h1>
                        <h3 className="see-more" onClick={() => {
                            navigate(`/user/${username}/music/activity`)
                        }}
                        >SEE MORE</h3>
                    </div>
                    <div className="category-releases">
                        {listened.map(track => (
                            <div className="releaseGroup-items" key={track.trackId + "track-items"}>
                                <img className="profile-item-img"
                                     src={
                                         track.releaseMbid ?
                                             `https://coverartarchive.org/release-group/${track.releaseMbid}/front` :
                                             `https://coverartarchive.org/release-group/${track.altReleaseMbid}/front`
                                     }
                                     alt="placeholder.png"
                                     onClick={() => {
                                         navigate(`/music/track/${track.trackMbid}`)
                                     }}
                                     key={track.releaseMbid + "track-img"}
                                />
                                <div className="release-info" key={track.trackId + "track-info"}>
                                    <h4 className="profile-item-title"
                                        key={track.trackId}
                                        onClick={() => {
                                            navigate(`/music/track/${track.trackMbid}`)
                                        }}> {track.trackTitle}</h4>
                                    <h5 className="format" key={track.trackId + "format"}>
                                        Track by
                                    </h5>
                                    <h4 className="profile-item-artist"
                                        key={track.artistName}
                                        onClick={() => {
                                            navigate(`/music/artist/${track.artistMbid}`)
                                        }}
                                    >{track.artistName}</h4>
                                </div>
                                <div className="listen-timestamp" key={track.timestamp}>
                                    <h5>{handleDate(track.firstListenedAt)[1]}</h5>
                                    <h5>{handleDate(track.firstListenedAt)[0]}</h5>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
                <div className="profile-categories">
                    <div className="category-header">
                        <h1 className="category">Recently Rated</h1>
                        <h3 className="see-more" onClick={() => {
                            navigate(`/user/${username}/music/ratings`)
                        }}
                        >SEE MORE</h3>
                    </div>
                    <div className="category-releases">
                        {ratings.map(rating => (
                            <div className="releaseGroup-items" key={rating.mbid}>
                                <img className="profile-item-img"
                                     src={`https://coverartarchive.org/release-group/${rating.releaseMbid}/front`}
                                     alt="placeholder.png"
                                     onClick={() => {
                                         navigate(
                                             rating.title ?
                                                 `/music/album/${rating.releaseMbid}` :
                                                 `/music/track/${rating.trackMbid}`
                                         )
                                     }}
                                     key={rating.releaseMbid + "image"}
                                />
                                <div className="release-info" key={rating.mbid + "release-info"}>
                                    {rating.title ?
                                        <h4 className="profile-item-title"
                                            key={rating.title + "release"}
                                            onClick={() => {
                                                navigate(`/music/album/${rating.releaseMbid}`)
                                            }}
                                        >{rating.title} </h4>
                                        :
                                        <h4 className="profile-item-title"
                                            key={rating.trackTitle + "track"}
                                            onClick={() => {
                                                navigate(`/music/track/${rating.trackMbid}`)
                                            }}
                                        >{rating.trackTitle} </h4>
                                    }
                                    {rating.format ?
                                        <h5 className="format" key={rating.format}>
                                            {rating.format.charAt(0).toUpperCase() + rating.format.slice(1)} by
                                        </h5>
                                        :
                                        <h5 className="format" key="track">
                                            Track by
                                        </h5>
                                    }
                                    <h4 className="profile-item-artist"
                                        key={rating.artistName}
                                        onClick={() => {
                                            navigate(`/music/artist/${rating.artistMbid}`)
                                        }}
                                    >{rating.artistName}</h4>
                                </div>
                                <div className="rating-value" key={rating.mbid + rating.rating}>
                                    <h4 className={
                                        rating.rating == 10 ? "rating-value-ten" :
                                            rating.rating >= 8 && rating.rating <= 9 ? "rating-value-high" :
                                                rating.rating >= 6 && rating.rating <= 7 ? "rating-value-med" :
                                                    rating.rating >= 4 && rating.rating <= 5 ? "rating-value-medlow" :
                                                        rating.rating >= 1 && rating.rating <= 3 ? "rating-value-low" :
                                                            "rating-value-zero"

                                    } key={rating.id}>
                                        <FaStar className="star-profile" key={rating.id + "star"}/>
                                        {rating.rating}/10
                                    </h4>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    )
}