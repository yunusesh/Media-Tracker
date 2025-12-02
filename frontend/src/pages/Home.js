import './Home.css'
import {AuthContext} from "../AuthContext";
import React, {useContext, useEffect, useState} from "react";
import {useQuery} from "react-query";
import {useNavigate} from "react-router-dom";
import {GrPowerCycle} from "react-icons/gr";
import {forEach} from "lodash";

export function Home() {
    const {user} = useContext(AuthContext);
    const navigate = useNavigate();

    const filters = ["day", "week", "month", "year", "overall"]
    const [trackFilter, setTrackFilter] = useState(filters[1]);
    const [nextTrackFilter, setNextTrackFilter] = useState(2);
    const [releaseFilter, setReleaseFilter] = useState(filters[1]);
    const [nextReleaseFilter, setNextReleaseFilter] = useState(2);
    const [artistFilter, setArtistFilter] = useState(filters[1])
    const [nextArtistFilter, setNextArtistFilter] = useState(2)

    const [trackTopster, setTrackTopster] = useState([]);
    const [releaseTopster, setReleaseTopster] = useState([]);
    const [artistTopster, setArtistTopster] = useState([])

    const topsterSizes = [9, 16, 25, 36, 100]
    const [releaseTopsterSize, setReleaseTopsterSize] = useState(topsterSizes[0]);
    const [trackTopsterSize, setTrackTopsterSize] = useState(topsterSizes[0]);
    const [artistTopsterSize, setArtistTopsterSize] = useState(topsterSizes[0])
    const [nextTrackTopsterSize, setNextTrackTopsterSize] = useState(1);
    const [nextReleaseTopsterSize, setNextReleaseTopsterSize] = useState(1);
    const [nextArtistTopsterSize, setNextArtistTopsterSize] = useState(1);

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

        if(idType == "artist"){
            return filtered.filter(r => {
                let max = 0;
                let finalArtist = 0;
                r.artistScrobbles.forEach(artist => {
                    if (artist.scrobbles > max){
                        finalArtist = artist.artistId;
                    }
                    max = Math.max(max, artist.scrobbles);
                })

                if (seen.has(finalArtist)) return false;
                seen.add(finalArtist);
                return true;
            })
        }

        return filtered.filter(r => {
            if (seen.has(r[idType])) return false;
            seen.add(r[idType]);
            return true;
        });
    }

    useEffect(() => {
        if (userListens){
            const sortedArtists = [...userListens].sort((a,b) => {
                let bMax = 0
                b.artistScrobbles.forEach(artist => {
                    bMax = Math.max(artist.scrobbles, bMax)
                })
                let aMax = 0
                a.artistScrobbles.forEach(artist => {
                    aMax = Math.max(artist.scrobbles, aMax)
                })

                return bMax - aMax;
            })

            if(artistFilter in windows){
                setArtistTopster(
                    getUniqueScrobbles(sortedArtists, windows[artistFilter], "artist").slice(0, artistTopsterSize)
                );
            }
        }
    }, [user, userListens, artistFilter, artistTopsterSize]);

    useEffect(() => {
        if (userListens) {
            const sortedTracks = [...userListens].sort((a, b) => b.trackScrobbles - a.trackScrobbles);
            if (trackFilter in windows) {
                setTrackTopster(
                    getUniqueScrobbles(sortedTracks, windows[trackFilter], "trackId").slice(0, trackTopsterSize)
                );
            }
            else if (trackFilter === "overall") {
                const seen = new Set();
                setTrackTopster(sortedTracks.filter(track => {
                    if (seen.has(track.trackId)) return false;
                    seen.add(track.trackId);
                    return true;
                }).slice(0, trackTopsterSize))

            }
        }
    }, [user, userListens, trackFilter, trackTopsterSize])

    useEffect(() => {
        if (userListens) {
            const sortedReleases = [...userListens].sort((a, b) => b.releaseScrobbles - a.releaseScrobbles);
            if (releaseFilter in windows)
                setReleaseTopster(
                    getUniqueScrobbles(sortedReleases, windows[releaseFilter], "releaseId").slice(0, releaseTopsterSize)
                );
            else if (releaseFilter === "overall") {
                const seen = new Set();
                setReleaseTopster(sortedReleases.filter(release => {
                    if (seen.has(release.releaseId)) return false;
                    seen.add(release.releaseId);
                    return true;
                }).slice(0, releaseTopsterSize))

            }
        }
    }, [user, userListens, releaseFilter, releaseTopsterSize]);

    return (
        <div className="home-page">
            {user && userListens && (
                <section className="charts">
                    <div className="charts-wrapper">
                        <div className="tracks-wrapper">
                            <div className="tracks-header">
                                <div className="size-switch-wrapper">
                                    <h4 onClick={() => {
                                        setTrackTopsterSize(topsterSizes[nextTrackTopsterSize]);
                                        if (nextTrackTopsterSize + 1 >= topsterSizes.length) {
                                            setNextTrackTopsterSize(0);
                                        } else {
                                            setNextTrackTopsterSize(nextTrackTopsterSize + 1);
                                        }
                                    }}>{Math.sqrt(trackTopsterSize)}x{Math.sqrt(trackTopsterSize)}</h4>
                                    <GrPowerCycle
                                        className="switch"
                                        onClick={() => {
                                            setTrackTopsterSize(topsterSizes[nextTrackTopsterSize]);
                                            if (nextTrackTopsterSize + 1 >= topsterSizes.length) {
                                                setNextTrackTopsterSize(0);
                                            } else {
                                                setNextTrackTopsterSize(nextTrackTopsterSize + 1);
                                            }
                                        }}
                                    />
                                </div>
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
                            <div
                                className="top-tracks"
                                style={{
                                    gridTemplateColumns: `repeat(${Math.sqrt(trackTopsterSize)}, 1fr)`,
                                    gridTemplateRows: `repeat(${Math.sqrt(trackTopsterSize)}, 1fr)`,
                                }}
                            >
                                {trackTopster.map((track, index) => (
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
                                        <div className={trackTopsterSize == 100 ? "chart-track-info-small" : "chart-track-info"}>
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
                                <div className="size-switch-wrapper">
                                    <h4 onClick={() => {
                                        setReleaseTopsterSize(topsterSizes[nextReleaseTopsterSize]);
                                        if (nextReleaseTopsterSize + 1 >= topsterSizes.length) {
                                            setNextReleaseTopsterSize(0);
                                        } else {
                                            setNextReleaseTopsterSize(nextReleaseTopsterSize + 1);
                                        }
                                    }}>{Math.sqrt(releaseTopsterSize)}x{Math.sqrt(releaseTopsterSize)}</h4>
                                    <GrPowerCycle
                                        className="switch"
                                        onClick={() => {
                                            setReleaseTopsterSize(topsterSizes[nextReleaseTopsterSize]);
                                            if (nextReleaseTopsterSize + 1 >= topsterSizes.length) {
                                                setNextReleaseTopsterSize(0);
                                            } else {
                                                setNextReleaseTopsterSize(nextReleaseTopsterSize + 1);
                                            }
                                        }}
                                    />
                                </div>
                                <div className="category-wrapper">
                                    <h1 className="category">Top Releases</h1>
                                </div>
                                <div className="switch-wrapper">
                                    <GrPowerCycle
                                        className="switch"
                                        onClick={() => {
                                            setArtistFilter(filters[nextReleaseFilter]);
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
                            <div className="top-releases"
                                 style={{
                                     gridTemplateColumns: `repeat(${Math.sqrt(releaseTopsterSize)}, 1fr)`,
                                     gridTemplateRows: `repeat(${Math.sqrt(releaseTopsterSize)}, 1fr)`
                                 }}>
                                {releaseTopster.map((release, index) => (
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
                        <div className="artists-wrapper">
                            <div className="artists-header">
                                <div className="size-switch-wrapper">
                                    <h4 onClick={() => {
                                        setArtistTopsterSize(topsterSizes[nextArtistTopsterSize]);
                                        if (nextArtistTopsterSize + 1 >= topsterSizes.length) {
                                            setNextArtistTopsterSize(0);
                                        } else {
                                            setNextArtistTopsterSize(nextArtistTopsterSize + 1);
                                        }
                                    }}>{Math.sqrt(artistTopsterSize)}x{Math.sqrt(artistTopsterSize)}</h4>
                                    <GrPowerCycle
                                        className="switch"
                                        onClick={() => {
                                            setArtistTopsterSize(topsterSizes[nextArtistTopsterSize]);
                                            if (nextArtistTopsterSize + 1 >= topsterSizes.length) {
                                                setNextArtistTopsterSize(0);
                                            } else {
                                                setNextArtistTopsterSize(nextArtistTopsterSize + 1);
                                            }
                                        }}
                                    />
                                </div>
                                <div className="category-wrapper">
                                    <h1 className="category">Top Artists</h1>
                                </div>
                                <div className="switch-wrapper">
                                    <GrPowerCycle
                                        className="switch"
                                        onClick={() => {
                                            setArtistFilter(filters[nextArtistFilter]);
                                            if (nextArtistFilter + 1 >= filters.length) {
                                                setNextArtistFilter(0);
                                            } else {
                                                setNextArtistFilter(nextArtistFilter + 1);
                                            }
                                        }}/>
                                    <h4
                                        onClick={() => {
                                        setArtistFilter(filters[nextArtistFilter]);
                                        if (nextArtistFilter + 1 >= filters.length) {
                                            setNextArtistFilter(0);
                                        } else {
                                            setNextArtistFilter(nextArtistFilter + 1);
                                        }
                                    }}>{artistFilter}</h4>
                                </div>
                            </div>
                            <div className="top-artists"
                                 style={{
                                     gridTemplateColumns: `repeat(${Math.sqrt(artistTopsterSize)}, 1fr)`,
                                     gridTemplateRows: `repeat(${Math.sqrt(artistTopsterSize)}, 1fr)`
                                 }}>
                                {artistTopster.map((scrobble, index) => {
                                    console.log(scrobble)
                                    let max = 0;
                                    let finalArtistIndex = 0
                                    {scrobble.artistScrobbles.forEach((artist, i) => {
                                        if (artist.scrobbles > max){
                                            finalArtistIndex = i;
                                        }
                                        max = Math.max(max, artist.scrobbles);
                                    })}
                                    return (
                                        <div className = "artist" key = {scrobble.id}>
                                            <img
                                                className = "chart-img"
                                                src = {scrobble.artists[finalArtistIndex].imageUrl ?
                                                    scrobble.artists[finalArtistIndex].imageUrl :
                                                    `https://coverartarchive.org/release-group/${scrobble.releaseMbid}/front`
                                            }
                                                alt = "placeholder.png"
                                                onClick={() => {
                                                    navigate(`/artist/${scrobble.artists[finalArtistIndex].mbid}`)
                                                }}
                                            />
                                            <div className={artistTopsterSize == 100 ? "chart-track-info-small" : "chart-track-info"}>
                                                <h5>
                                                    {scrobble.artists[finalArtistIndex].artistName}
                                                </h5>
                                            </div>

                                        </div>
                                    )
                                })}
                            </div>
                        </div>
                    </div>
                </section>
            )}
        </div>

    )
}