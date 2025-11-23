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
    const oneMonth = 30 * oneDay; // or calculate real months if needed
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

            if (trackFilter === "day") {
                setUser3x3Tracks(uniqueTracks
                    .filter(t => new Date(t.firstListenedAt) >= now - oneDay)
                    .slice(0, 9)
                );
            }
            else if (trackFilter === "week") {
                setUser3x3Tracks(uniqueTracks
                    .filter(t => new Date(t.firstListenedAt) >= now - oneWeek)
                    .slice(0, 9)
                );
            }
            else if (trackFilter === "month") {
                setUser3x3Tracks(uniqueTracks
                    .filter(t => new Date(t.firstListenedAt) >= now - oneMonth)
                    .slice(0, 9)
                );
            }
            else if (trackFilter === "year") {
                setUser3x3Tracks(uniqueTracks
                    .filter(t => new Date(t.firstListenedAt) >= now - oneYear)
                    .slice(0, 9)
                );
            }

            else if (trackFilter === "overall") {
                setUser3x3Tracks(uniqueTracks.slice(0, 9));
            }

            if (releaseFilter === "day") {
                setUser3x3Releases(uniqueReleases
                    .filter(r => new Date(r.firstListenedAt) >= now - oneDay)
                    .slice(0, 9)
                );
            }
            else if (releaseFilter === "week") {
                setUser3x3Releases(uniqueReleases
                    .filter(r => new Date(r.firstListenedAt) >= now - oneWeek)
                    .slice(0, 9)
                );
            }
            else if (releaseFilter === "month") {
                setUser3x3Releases(uniqueReleases
                    .filter(r => new Date(r.firstListenedAt) >= now - oneMonth)
                    .slice(0, 9)
                );
            }
            else if (releaseFilter === "year") {
                setUser3x3Releases(uniqueReleases
                    .filter(r => new Date(r.firstListenedAt) >= now - oneYear)
                    .slice(0, 9)
                );
            }
            else if (releaseFilter === "overall") {
                setUser3x3Releases(uniqueReleases.slice(0, 9));
            }
        }
    }, [userListens, trackFilter, releaseFilter]);

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
                                <div
                                    className="switch-wrapper"
                                    onClick={() => {
                                        setTrackFilter(filters[nextTrackFilter]);
                                        if (nextTrackFilter + 1 >= filters.length) {
                                            setNextTrackFilter(0);
                                        } else {
                                            setNextTrackFilter(nextTrackFilter + 1);
                                        }
                                    }}
                                >
                                    <GrPowerCycle
                                        className="switch"/>
                                    <h4>{trackFilter}</h4>
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
                                <div className="switch-wrapper"
                                     onClick={() => {
                                         setReleaseFilter(filters[nextReleaseFilter]);
                                         if (nextReleaseFilter + 1 >= filters.length) {
                                             setNextReleaseFilter(0);
                                         } else {
                                             setNextReleaseFilter(nextReleaseFilter + 1);
                                         }
                                     }}
                                >
                                    <GrPowerCycle className="switch"/>
                                    <h4>{releaseFilter}</h4>
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