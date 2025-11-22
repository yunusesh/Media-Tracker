import './Home.css'
import {AuthContext} from "../AuthContext";
import React, {useContext, useEffect, useState} from "react";
import {useQuery} from "react-query";
import {useNavigate} from "react-router-dom";

export function Home() {
    const {user} = useContext(AuthContext);
    const navigate = useNavigate();
    const [user3x3Tracks, setUser3x3Tracks] = useState([]);
    const [user3x3Releases, setUser3x3Releases] = useState([]);


    async function fetchUser() {
        const response = await fetch(`http://localhost:8081/user/${user.username}`)
        return response.json()
    }

    const {data: userData} = useQuery({
        queryKey: ["user", user],
        queryFn: () => fetchUser(),
        enabled: !!user
    })

    async function fetchUserListens() {
        const response = await fetch(`http://localhost:8081/api/scrobble/user/${user.id}`)
        return response.json()
    }

    const {data: userListens} = useQuery({
        queryKey: ["tracks", user],
        queryFn: () => fetchUserListens(),
        enabled: !!user
    })

    useEffect(() => {
        if (userListens) {
            const seen = new Set();

            const sortedTracks = [...userListens].sort((a, b) => b.trackScrobbles - a.trackScrobbles);
            const uniqueTracks = sortedTracks.filter(track => {
                if (seen.has(track.trackId)) return false;
                seen.add(track.trackId);
                return true;
            });
            const sortedReleases = [...userListens].sort((a, b) => b.releaseScrobbles - a.releaseScrobbles);
            const uniqueReleases = sortedReleases.filter(release => {
                if (seen.has(release.releaseId)) return false;
                seen.add(release.releaseId);
                return true;
            });

            console.log(sortedReleases)


            console.log(sortedTracks)
            setUser3x3Tracks(uniqueTracks.slice(0, 9))
            setUser3x3Releases(uniqueReleases.slice(0, 9))
        }
    }, [userListens]);

    return (
        <div className="home-page">
            {user && userListens && (
                <section className="charts">
                    <div className="charts-wrapper">

                        <div className="tracks-header">
                            <h1 className="category">Top Tracks</h1>
                            <div className="top-tracks">
                                {user3x3Tracks.map((track, index) => (
                                    <div className="track" key={index}>
                                        <img className="chart-img"
                                             src={
                                                 `https://coverartarchive.org/release-group/${track.releaseMbid}/front` ||
                                                 `https://coverartarchive.org/release-group/${track.altReleaseMbid}/front`
                                             }
                                             alt="placeholder.png"
                                             onClick={() => {navigate(`/track/${track.trackMbid}`)}}
                                        />
                                        <div className="chart-track-info">
                                            <h5>{track.trackTitle}</h5>
                                            <h5>{track.artists.map((artist, index, array) => (
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
                                ))}
                            </div>
                        </div>

                        <div className="releases-header">
                            <h1 className="category">Top Releases</h1>
                            <div className="top-releases">
                                {user3x3Releases.map((release, index) => (
                                    <div className="release" key={index}>
                                        <img className="chart-img"
                                             src={`https://coverartarchive.org/release-group/${release.releaseMbid}/front`}
                                             alt="placeholder.png"
                                             onClick={() => {navigate(`/album/${release.releaseMbid}`)}}
                                        />
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </section>
            )}
        </div>

    )
}