import {useNavigate, useParams} from "react-router-dom";
import React, {useContext, useEffect, useState} from "react";
import {useQuery} from "react-query";
import "./User.css"
import {FaRegEdit, FaStar} from "react-icons/fa";
import {AuthContext} from "../AuthContext";
import {IoCloseSharp} from "react-icons/io5";
import {closestCenter, DndContext} from "@dnd-kit/core";
import {SortableContext, useSortable, verticalListSortingStrategy} from "@dnd-kit/sortable";
import {CSS} from '@dnd-kit/utilities';
import axios from "axios";
import {Bounce, toast, ToastContainer} from "react-toastify";
import SearchBar from "../components/SearchBar";

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
    const [searchBarVisible, setSearchBarVisible] = useState(false)

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
            setRatings([...userRatings].sort((a, b) => new Date(b.ratedAt) - new Date(a.ratedAt)).slice(0, 12))
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
                [...userListens].sort((a, b) => new Date(b.firstListenedAt) - new Date(a.firstListenedAt)).slice(0, 6)
            );
        }
    }, [userListens])

    async function fetchUserTop5() {
        const response = await fetch(`http://localhost:8081/api/user/${userData?.id}/top/releases`)
        return response.json()
    }

    const {data: userTopReleases, refetch: refetchTop5} = useQuery({
        queryKey: ["userTopReleases", userData?.id],
        queryFn: () => fetchUserTop5(),
        enabled: !!userData?.id
    })

    useEffect(() => {
        if (userTopReleases) {
            setTop5(userTopReleases)
        }
    }, [userTopReleases])

    //uses drag and drop kit to let user sort their top 5
    function SortableItem({release}) {
        const {attributes, listeners, setNodeRef, transform, transition} =
            useSortable({id: release.releaseMbid});

        const style = {
            transform: CSS.Transform.toString(transform),
            transition,
        }

        return (
            <div ref={setNodeRef} style={style} className="top5-row" {...attributes} {...listeners}>
                <img className="top5-img"
                     src={`https://coverartarchive.org/release-group/${release.releaseMbid}/front`}
                     alt={release.releaseTitle}
                />
                <div className="top5-info">
                    <h5>{release.releaseTitle}</h5>
                    <h5>
                        {release.artists.map((artist, index, array) => (
                            <span
                                key={index}
                            >{artist.artistName}
                                {index < array.length - 1 &&
                                    (index === array.length - 2 ? " & " : ", ")}
                                            </span>
                        ))}
                    </h5>
                </div>
            </div>
        );
    }

    function handleDragEnd(event) {
        const {active, over} = event;
        if (active.id !== over.id) {
            setTop5(prev => {
                const oldIndex = prev.findIndex(item => item.releaseMbid === active.id);
                const newIndex = prev.findIndex(item => item.releaseMbid === over.id);

                const updated = [...prev];
                const [movedItem] = updated.splice(oldIndex, 1);
                updated.splice(newIndex, 0, movedItem);
                return updated;
            });
        }
    }

    async function handleTop5Save() {
        console.log(top5)
        await Promise.all(Array.from({length: 5}).map((_, index) => {
            if (index + 1 > top5.length){
                axios.delete(`http://localhost:8081/api/user/${user.id}/top/releases/${index + 1}`)
            }
            else{
                axios.put(`http://localhost:8081/api/user/${user.id}/top/releases`, {
                    userId: user.id,
                    tier: index + 1, //tiers are 1-5, index is 1-4
                    releaseId: top5[index].releaseId
                })
            }

        }))

        saveSuccess()
    }

    function removeTop5Item(index) {
        const tempTop5 = top5.filter((_, i) => i + 1 !== index);
        setTop5(tempTop5)
    }

    async function addTop5Item(release) {
        const response = await axios.post('http://localhost:8081/api/release/getOrCreate', {
            releaseMbid: release.releaseGroupId,
            title: release.title,
            releaseDate: release["first-release-date"],
            format: release["primary-type"],
            artists: release["artist-credit"]
                ?.map(c => c.artist)
                ?.filter(a => a?.id)
                ?.map(a => ({
                    mbid: a.id,
                    artistName: a.name
                })) || [],
        })

        const newItem  = {
            tier: top5.length + 1,
            userId: user.id,
            releaseId: response.data.id,
            releaseMbid: release.releaseGroupId,
            releaseTitle: release.title,
            format: release["primary-type"],
            artists: response.data.artists
        }

        const tempTop5 = [...top5]
        tempTop5.push(newItem)
        setTop5(tempTop5)
    }

    const saveSuccess = () => toast.success('Saved Changes', {
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

    if (status === 'loading') {
        return <p>Loading...</p>
    }

    if (status === 'error') {
        return <p>Error!</p>
    }

    return (
        <div className="user-page">
            <section className="profile-user">
                <img
                    className="profile-pfp"
                    src="https://i.pinimg.com/1200x/83/bc/8b/83bc8b88cf6bc4b4e04d153a418cde62.jpg"
                    alt="placeholder.png"/>
                <h1 className="profile-username">
                    {username}
                </h1>
            </section>
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
            {isEditing ? (
                <section className="top5-popup">
                    {searchBarVisible ? (
                        <SearchBar className="profile-search"
                                   searchTypeProp="releases"
                                   buttonsEnabled={false}
                                   onClickFunction={(release) => {
                                       addTop5Item(release)
                                       setSearchBarVisible(false)
                                   }}/>
                    ) : null}
                    <div className="edit-top5">
                        <IoCloseSharp className="top5-exit" onClick={() => {
                            setIsEditing(false)
                        }}/>
                        <button className="save-changes" onClick={() => {
                            setIsEditing(false)
                            handleTop5Save()
                        }}>
                            Save
                        </button>
                        <DndContext collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
                            <SortableContext
                                items={top5.map(r => r.releaseMbid)}
                                strategy={verticalListSortingStrategy}
                            >
                                {Array.from({length: 5}, (_, index) => (
                                    <div className="top5-category" key = {index}>
                                        <div className="top5-pre">{index + 1}</div>
                                        {top5[index] ?
                                            <>
                                                <SortableItem key={top5[index].releaseMbid} release={top5[index]}/>
                                                <button className="top5-remove"
                                                        onClick={() =>
                                                            removeTop5Item(index + 1)}> Remove
                                                </button>
                                            </> :
                                            <>
                                                <div className="top5-row"></div>
                                                <button className="top5-add" key={index + 1}
                                                        onClick={() => {
                                                            setSearchBarVisible(true)
                                                        }}> Add
                                                </button>
                                            </>

                                        }
                                    </div>
                                ))}
                            </SortableContext>
                        </DndContext>

                    </div>
                </section>
            ) : null}

            <section className="profile">
                <div className="profile-categories">
                    <div className="top5-header">
                        <h1 className="category">Top 5</h1>
                        {user && user.username === username ?
                            <FaRegEdit className="edit" onClick={() => {
                                setIsEditing(!isEditing)
                            }}/> :
                            null
                        }
                    </div>
                    <div className="profile-category-releases">
                        {top5.map((release, index) => (
                            <div className="releaseGroup-items">
                                <img className="profile-item-img"
                                     src={`https://coverartarchive.org/release-group/${release.releaseMbid}/front`}
                                     alt="placeholder.png"
                                     onClick={() => {
                                         navigate(
                                             release.releaseTitle ?
                                                 `/album/${release.releaseMbid}` :
                                                 `/track/${release.trackMbid}`
                                         )
                                     }}
                                     key={index}
                                />
                                <div className="release-info">
                                    {release.releaseTitle ?
                                        <h4 className="profile-item-title"
                                            onClick={() => {
                                                navigate(`/album/${release.releaseMbid}`)
                                            }}
                                        >{release.releaseTitle} </h4>
                                        :
                                        <h4 className="profile-item-title"
                                            onClick={() => {
                                                navigate(`/track/${release.trackMbid}`)
                                            }}
                                        >{release.trackTitle} </h4>
                                    }
                                    {release.format ?
                                        <h5 className="format">
                                            {release.format.charAt(0).toUpperCase() + release.format.slice(1)} by
                                        </h5>
                                        :
                                        <h5 className="format">
                                            Track by
                                        </h5>
                                    }
                                    <div className="profile-item-container">
                                        {release.artists.map((artist, index, array) => (
                                            <span key={index}>
                                    <h4 className="profile-item-artist"
                                        onClick={() => navigate(`/artist/${artist.mbid}`)}
                                    >
                                    {artist.artistName}
                                    </h4>{index < array.length - 1 && (
                                        <span>
                                            {index === array.length - 2 ? " & " : ", "}
                                        </span>
                                                )}
                                </span>
                                        ))}
                                    </div>
                                </div>
                                <div className="top5-rank">
                                    {index + 1}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
                <div className="profile-categories">
                    <div className="category-header">
                        <h1 className="category">Top of 2025</h1>
                        <h3 className="see-more" onClick={() => {
                            navigate(`/user/${username}/ratings/1`)
                        }}
                        >SEE MORE</h3>
                    </div>                    <div className="profile-category-releases">
                        {topOfYear.map((rating, i) => (
                            <div className="releaseGroup-items" key={i}>
                                <img className="profile-item-img"
                                     src={`https://coverartarchive.org/release-group/${rating.releaseMbid}/front`}
                                     alt="placeholder.png"
                                     onClick={() => {
                                         navigate(
                                             rating.title ?
                                                 `/album/${rating.releaseMbid}` :
                                                 `/track/${rating.trackMbid}`
                                         )
                                     }}
                                />
                                <div className="release-info">
                                    {rating.title ?
                                        <h4 className="profile-item-title"
                                            onClick={() => {
                                                navigate(`/album/${rating.releaseMbid}`)
                                            }}
                                        >{rating.title} </h4>
                                        :
                                        <h4 className="profile-item-title"
                                            onClick={() => {
                                                navigate(`/track/${rating.trackMbid}`)
                                            }}
                                        >{rating.trackTitle} </h4>
                                    }
                                    {rating.format ?
                                        <h5 className="format">
                                            {rating.format.charAt(0).toUpperCase() + rating.format.slice(1)} by
                                        </h5>
                                        :
                                        <h5 className="format">
                                            Track by
                                        </h5>
                                    }
                                    <div className="profile-item-container">
                                        {rating.artists.map((artist, index, array) => (
                                            <span key={index}>
                                    <h4 className="profile-item-artist"
                                        onClick={() => navigate(`/artist/${artist.mbid}`)}
                                    >
                                    {artist.artistName}
                                    </h4>
                                                {index < array.length - 1 && (
                                                    <span>{index === array.length - 2 ? " & " : ", "}</span>
                                                )}
                                </span>
                                        ))}
                                    </div>
                                </div>
                                <div className="rating-value">
                                    <h4 className={
                                        rating.rating == 10 ? "rating-value-ten" :
                                            rating.rating >= 8 && rating.rating <= 9 ? "rating-value-high" :
                                                rating.rating >= 6 && rating.rating <= 7 ? "rating-value-med" :
                                                    rating.rating >= 4 && rating.rating <= 5 ? "rating-value-medlow" :
                                                        rating.rating >= 1 && rating.rating <= 3 ? "rating-value-low" :
                                                            "rating-value-zero"

                                    }>
                                        <FaStar className="star-profile"/>
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
                            navigate(`/user/${username}/activity/1`)
                        }}
                        >SEE MORE</h3>
                    </div>
                    <div className="profile-category-releases">
                        {listened.map((track, i) => (
                            <div className="releaseGroup-items" key={i}>
                                <img className="profile-item-img"
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
                                <div className="release-info">
                                    <h4 className="profile-item-title"
                                        onClick={() => {
                                            navigate(`/track/${track.trackMbid}`)
                                        }}> {track.trackTitle}</h4>
                                    <h5 className="format">
                                        Track by
                                    </h5>

                                    <div className="profile-item-container">
                                        {track.artists.map((artist, index, array) => (
                                            <span key={index}>
                                    <h4 className="profile-item-artist"
                                        onClick={() => navigate(`/artist/${artist.mbid}`)}
                                    >
                                    {artist.artistName}
                                    </h4>
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
                </div>
                <div className="profile-categories">
                    <div className="category-header">
                        <h1 className="category">Recently Rated</h1>
                        <h3 className="see-more" onClick={() => {
                            navigate(`/user/${username}/ratings/1`)
                        }}
                        >SEE MORE</h3>
                    </div>
                    <div className="profile-category-releases">
                        {ratings.map((rating, i) => (
                            <div className="releaseGroup-items" key={i}>
                                <img className="profile-item-img"
                                     src={`https://coverartarchive.org/release-group/${rating.releaseMbid}/front`}
                                     alt="placeholder.png"
                                     onClick={() => {
                                         navigate(
                                             rating.title ?
                                                 `/album/${rating.releaseMbid}` :
                                                 `/track/${rating.trackMbid}`
                                         )
                                     }}
                                />
                                <div className="release-info">
                                    {rating.title ?
                                        <h4 className="profile-item-title"
                                            onClick={() => {
                                                navigate(`/album/${rating.releaseMbid}`)
                                            }}
                                        >{rating.title} </h4>
                                        :
                                        <h4 className="profile-item-title"
                                            onClick={() => {
                                                navigate(`/track/${rating.trackMbid}`)
                                            }}
                                        >{rating.trackTitle} </h4>
                                    }
                                    {rating.format ?
                                        <h5 className="format">
                                            {rating.format.charAt(0).toUpperCase() + rating.format.slice(1)} by
                                        </h5>
                                        :
                                        <h5 className="format">
                                            Track by
                                        </h5>
                                    }
                                    <div className="profile-item-container">
                                        {rating.artists.map((artist, index, array) => (
                                            <span key={index}>
                                    <h4 className="profile-item-artist"
                                        onClick={() => navigate(`/artist/${artist.mbid}`)}
                                    >
                                    {artist.artistName}
                                    </h4>
                                                {index < array.length - 1 && (
                                                    <span>{index === array.length - 2 ? " & " : ", "}</span>
                                                )}
                                </span>
                                        ))}
                                    </div>
                                </div>
                                <div className="rating-value">
                                    <h4 className={
                                        rating.rating == 10 ? "rating-value-ten" :
                                            rating.rating >= 8 && rating.rating <= 9.9 ? "rating-value-high" :
                                                rating.rating >= 6 && rating.rating <= 7.9 ? "rating-value-med" :
                                                    rating.rating >= 4 && rating.rating <= 5.9 ? "rating-value-medlow" :
                                                        rating.rating >= 1 && rating.rating <= 3.9 ? "rating-value-low" :
                                                            "rating-value-zero"

                                    }>
                                        <FaStar className="star-profile"/>
                                        {rating.rating}/10
                                    </h4>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </section>
        </div>
    )
}