import {useNavigate, useParams} from "react-router-dom";
import {useQuery} from "react-query";
import React, {useEffect, useState} from "react";
import {FaStar} from "react-icons/fa";
import "./UserRatings.css"

export function UserRatings() {
    const {username} = useParams()
    const navigate = useNavigate()
    const [ratings, setRatings] = useState([])

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
            setRatings(userRatings.sort((a, b) => new Date(b.ratedAt) - new Date(a.ratedAt)))
        }
    }, [userTrackRatings, userReleaseRatings])
    return (
        <div className="user-ratings-page">
            <h1 className="category">All Ratings</h1>
            <div className="user-ratings">
                {ratings.map(rating => (
                    <div className="ratings-page-item" key={rating.mbid}>
                        <img className="ratings-item-img"
                             src={`https://coverartarchive.org/release-group/${rating.releaseMbid}/front`}
                             alt="placeholder.png"
                             onClick={() => {
                                 navigate(
                                     rating.title ?
                                         `/music/album/${rating.releaseMbid}` :
                                         `/music/track/${rating.trackMbid}`
                                 )
                             }}
                        />
                        <div className="ratings-release-info">
                            {rating.title ?
                                <h5 className="profile-item-title"
                                    key={rating.title}
                                    onClick={() => {
                                        navigate(`/music/album/${rating.releaseMbid}`)
                                    }}
                                >{rating.title} </h5>
                                :
                                <h5 className="profile-item-title"
                                    key={rating.trackTitle}
                                    onClick={() => {
                                        navigate(`/music/track/${rating.trackMbid}`)
                                    }}
                                >{rating.trackTitle} </h5>

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
                            <div className = "profile-item-container">
                                {rating.artists.map((artist, index, array) => (
                                    <span key={artist.id}>
                                    <h5 className="profile-item-artist"
                                        onClick={() => navigate(`/music/artist/${artist.mbid}`)}
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
                        <div className="rating-value">
                            <h5 className={
                                rating.rating == 10 ? "rating-value-ten" :
                                    rating.rating >= 8 && rating.rating <= 9 ? "rating-value-high" :
                                        rating.rating >= 6 && rating.rating <= 7 ? "rating-value-med" :
                                            rating.rating >= 4 && rating.rating <= 5 ? "rating-value-medlow" :
                                                rating.rating >= 1 && rating.rating <= 3 ? "rating-value-low" :
                                                    "rating-value-zero"

                            } key={rating.id}>
                                <FaStar className="star-profile"/>
                                {rating.rating}/10
                            </h5>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}