import {useNavigate, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {useQuery} from "react-query";
import "./User.css"
import {FaStar} from "react-icons/fa";

export function User() {
    const navigate = useNavigate()
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear();
    const {username} = useParams()
    const [ratings, setRatings] = useState([])
    const [listened, setListened] = useState([])
    const [topOfYear, setTopOfYear] = useState([])

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


    if (status === 'loading') {
        return <p>Loading...</p>
    }

    if (status === 'error') {
        return <p>Error!</p>
    }

    return (
        <div className="user-page">
            <div className="profile">
                <div className="profile-categories">
                    <h1 className="category">Top 5</h1>
                    <div className="category-releases">

                    </div>
                </div>
                <div className="profile-categories">
                    <h1 className="category">Top of {currentYear}</h1>
                    <div className="category-releases">

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
                            <div className="releaseGroup-items" key={track.trackMbid}>
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
                                />
                                <div className="release-info">
                                    <h4 className="profile-item-title"
                                        key={track.trackId}
                                        onClick={() => {
                                            navigate(`/music/track/${track.trackMbid}`)
                                        }}> {track.trackTitle}</h4>
                                    <h5 className="format">
                                        Track by
                                    </h5>
                                    <h4 className="profile-item-artist"
                                        key={track.artistName}
                                        onClick={() => {
                                            navigate(`/music/artist/${track.artistMbid}`)
                                        }}
                                    >{track.artistName}</h4>
                                </div>
                                <div className="listen-timestamp">
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
                                         navigate(`/music/album/${rating.releaseMbid}`)
                                     }}
                                />
                                <div className="release-info">
                                    {rating.title ?
                                        <h4 className="profile-item-title"
                                            key={rating.title}
                                            onClick={() => {
                                                navigate(`/music/album/${rating.releaseMbid}`)
                                            }}
                                        >{rating.title} </h4>
                                        :
                                        <h4 className="profile-item-title"
                                            key={rating.trackTitle}
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
                                <div className="rating-value">
                                    <h4 className={
                                        rating.rating == 10 ? "rating-value-ten" :
                                            rating.rating >= 8 && rating.rating <= 9 ? "rating-value-high" :
                                                rating.rating >= 6 && rating.rating <= 7 ? "rating-value-med" :
                                                    rating.rating >= 4 && rating.rating <= 5 ? "rating-value-medlow" :
                                                        rating.rating >= 1 && rating.rating <= 3 ? "rating-value-low" :
                                                            "rating-value-zero"

                                    } key={rating.id}>
                                        <FaStar className="star-profile"/>
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