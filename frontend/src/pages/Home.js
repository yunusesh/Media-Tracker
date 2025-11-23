import './Home.css'
import {AuthContext} from "../AuthContext";
import React, {useContext, useEffect, useState} from "react";
import {useQuery} from "react-query";
import {useNavigate} from "react-router-dom";
import {GrPowerCycle} from "react-icons/gr";

export function Home() {
    const {user} = useContext(AuthContext);
    const navigate = useNavigate();
    const [user3x3Tracks, setUser3x3Tracks] = useState([]);
    const [user3x3Releases, setUser3x3Releases] = useState([]);
    const filters = ["day", "week", "month", "year", "overall"]
    const [trackFilter, setTrackFilter] = useState(filters[1]);
    const [nextTrackFilter, setNextTrackFilter] = useState(2);
    const [releaseFilter, setReleaseFilter] = useState(filters[1]);
    const [nextReleaseFilter, setNextReleaseFilter] = useState(2);
    const now = Date.now();
    const oneDay = 24 * 60 * 60 * 1000;
    const oneWeek = 7 * oneDay;
    const oneMonth = 30 * oneDay;
    const oneYear = 365 * oneDay;


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
        if (user) {
            const response = await fetch(`http://localhost:8081/api/scrobble/user/${user.id}`)
            return response.json()
        }
    }

    const {data: userListens} = useQuery({
        queryKey: ["tracks", user],
        queryFn: () => fetchUserListens(),
        enabled: !!user
    })

    const windows = {
        day: oneDay,
        week: oneWeek,
        month: oneMonth,
        year: oneYear
    };

    function getUniqueScrobbles(releases, window, idType) {
        const filtered = releases.filter(r =>
            new Date(r.firstListenedAt).getTime() >= now - window
        );

        const seen = new Set();
        const unique = filtered.filter(r => {
            if (seen.has(r[idType])) return false;
            seen.add(r[idType]);
            return true;
        });

        return unique.slice(0, 9);
    }


    useEffect(() => {
        if (userListens) {
            const sortedTracks = [...userListens].sort((a, b) => b.trackScrobbles - a.trackScrobbles);
            if(trackFilter in windows)
                setUser3x3Tracks(
                    getUniqueScrobbles(sortedTracks, windows[trackFilter], "trackId")
                );
            else if (trackFilter === "overall"){
                const seen = new Set();
                setUser3x3Tracks(sortedTracks.filter(track => {
                    if (seen.has(track.trackId)) return false;
                    seen.add(track.trackId);
                    return true;
                }).slice(0, 9))

            }
        }
    }, [user, userListens, trackFilter])

    useEffect(() => {
        if (userListens) {
            const sortedReleases = [...userListens].sort((a, b) => b.releaseScrobbles - a.releaseScrobbles);
            if(releaseFilter in windows)
            setUser3x3Releases(
                getUniqueScrobbles(sortedReleases, windows[releaseFilter], "releaseId")
            );
            else if (releaseFilter === "overall"){
                const seen = new Set();
                setUser3x3Releases(sortedReleases.filter(release => {
                    if (seen.has(release.releaseId)) return false;
                    seen.add(release.releaseId);
                    return true;
                }).slice(0, 9))

            }
        }
    }, [user, userListens, releaseFilter]);

    return (
        <div className="home-page">
            {user && userListens && (
                <section className="charts">
                    <div className="charts-wrapper">
                        <div className="tracks-wrapper">
                            <div className="tracks-header">
                                <div className="category-wrapper">
                                    <h1 className="category">Top Tracks</h1>
                                </div>
                                <div className="switch-wrapper">
                                    <GrPowerCycle
                                        className="switch"
                                        onClick={() => {
                                            setTrackFilter(filters[nextTrackFilter]);
                                            if (nextTrackFilter + 1 >= filters.length) {
                                                setNextTrackFilter(0);
                                            } else {
                                                setNextTrackFilter(nextTrackFilter + 1);
                                            }
                                        }}
                                    />
                                    <h4 onClick={() => {
                                        setTrackFilter(filters[nextTrackFilter]);
                                        if (nextTrackFilter + 1 >= filters.length) {
                                            setNextTrackFilter(0);
                                        } else {
                                            setNextTrackFilter(nextTrackFilter + 1);
                                        }
                                    }}>{trackFilter}</h4>
                                </div>
                            </div>
                            <div className="top-tracks">
                                {user3x3Tracks.map((track, index) => (
                                    <div className="track" key={index}>
                                        <img className="chart-img"
                                             src={track.releaseMbid ?
                                                 `https://coverartarchive.org/release-group/${track.releaseMbid}/front` :
                                                 `https://coverartarchive.org/release-group/${track.altReleaseMbid}/front`
                                             }
                                             alt="placeholder.png"
                                             onClick={() => {
                                                 navigate(`/track/${track.trackMbid}`)
                                             }}
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

                        <div className="releases-wrapper">
                            <div className="releases-header">
                                <div className="category-wrapper">
                                    <h1 className="category">Top Releases</h1>
                                </div>
                                <div className="switch-wrapper">
                                    <GrPowerCycle
                                        className="switch"
                                        onClick={() => {
                                            setReleaseFilter(filters[nextReleaseFilter]);
                                            if (nextReleaseFilter + 1 >= filters.length) {
                                                setNextReleaseFilter(0);
                                            } else {
                                                setNextReleaseFilter(nextReleaseFilter + 1);
                                            }
                                        }}/>
                                    <h4 onClick={() => {
                                        setReleaseFilter(filters[nextReleaseFilter]);
                                        if (nextReleaseFilter + 1 >= filters.length) {
                                            setNextReleaseFilter(0);
                                        } else {
                                            setNextReleaseFilter(nextReleaseFilter + 1);
                                        }
                                    }}>{releaseFilter}</h4>
                                </div>
                            </div>
                            <div className="top-releases">
                                {user3x3Releases.map((release, index) => (
                                    <div className="release" key={index}>
                                        <img className="chart-img"
                                             src={`https://coverartarchive.org/release-group/${release.releaseMbid}/front`}
                                             alt="placeholder.png"
                                             onClick={() => {
                                                 navigate(`/album/${release.releaseMbid}`)
                                             }}
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