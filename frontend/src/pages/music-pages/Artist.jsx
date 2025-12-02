import './Artist.css'
import {useNavigate, useParams} from "react-router-dom";
import {useQuery} from "react-query";
import {useContext, useEffect, useState} from "react";
import {FaRegEdit} from "react-icons/fa";
import axios from "axios";
import {AuthContext} from "../../AuthContext";

export function Artist() {
    const {id} = useParams()
    const {user} = useContext(AuthContext);
    const navigate = useNavigate()
    const [genres, setGenres] = useState([])
    const now = Date.now();
    const oneDay = 24 * 60 * 60 * 1000;
    const oneWeek = 7 * oneDay;
    const oneMonth = 30 * oneDay;
    const oneYear = 365 * oneDay;
    const [totalScrobbles, setTotalScrobbles] = useState([])
    const [yearScrobbles, setYearScrobbles] = useState([])
    const [monthScrobbles, setMonthScrobbles] = useState([])
    const [dayScrobbles, setDayScrobbles] = useState([])


    async function fetchArtist() {
        const response = await fetch(`http://localhost:8081/artist/${id}`);
        return response.json()
    }

    const {data, status} = useQuery({
        queryKey: ['artist', id],
        queryFn: () => fetchArtist(id),
        enabled: !!id,
    })

    const [artistImage, setArtistImage] = useState(null);

    useEffect(() => {
        if (data) {
            setGenres(data.genres.map(genre => genre.name))
            if (`${data.url}` == null) {
                setArtistImage(`https://coverartarchive.org/release-group/${data["release-groups"]?.[0]?.id}/front`)
            } else setArtistImage(`${data.url}`);
        }
    }, [data])

    async function fetchArtistFromDB() {
        if (data) {
            const response = await axios.post(`http://localhost:8081/api/artist/getOrCreate`, {
                mbid: data.id,
                artistName: data.name,
                imageUrl: data.url,
                genres: data.genres?.map(genre => ({
                    mbid: genre.id,
                    genreName: genre.name
                })) || []
            })
            return response.data
        }
    }

    const {data: artistDB} = useQuery({
        queryKey: ['artistDB', id],
        queryFn: () => fetchArtistFromDB(),
        enabled: !!id && !!data
    })

    async function fetchUserScrobbles() {
        if (user && artistDB) {
            const response = await fetch(`http://localhost:8081/api/scrobble/user/${user.id}/artist/${artistDB.id}`)
            return response.json()
        }
    }

    const {data: userScrobbles} = useQuery({
        queryKey: ['userScrobbles', user?.id, artistDB?.id],
        queryFn: () => fetchUserScrobbles(),
        enabled: !!user && !!artistDB
    })

    useEffect(() => {
        if (userScrobbles) {
            setTotalScrobbles(userScrobbles.length)
            setYearScrobbles(userScrobbles.filter(date => new Date(date) >= now - oneYear).length)
            setMonthScrobbles(userScrobbles.filter(date => new Date(date) >= now - oneMonth).length)
            setDayScrobbles(userScrobbles.filter(date => new Date(date) >= now - oneDay).length)
        }
    }, [userScrobbles]);

    if (status === 'loading') {
        return <p>Loading...</p>
    }

    if (status === 'error') {
        return <p>Error!</p>
    }

    const releaseGroupsByFormat = {
        albums: [],
        mixtapes: [],
        eps: [],
        singles: [],
        live: [],
        compilations: [],
        others: [],
    };

    data["release-groups"]?.forEach(releaseGroup => {
        const format =
            releaseGroup["secondary-types"]?.includes("Mixtape/Street") ? "mixtapes" :
                releaseGroup["secondary-types"]?.includes("Compilation") ? "compilations" :
                    releaseGroup["secondary-types"]?.includes("Live") ? "live" :

                        ["Broadcast", "Spokenword",
                            "Interview", "Audiobook", "Audio drama",
                            "Remix", "DJ-mix", "Demo", "Field recording"]
                            .some(type => releaseGroup["secondary-types"]?.includes(type))
                        && releaseGroup["primary-type"]?.includes("Album") ? "others" :

                            releaseGroup["primary-type"]?.includes("Album") ? "albums" :
                                releaseGroup["primary-type"]?.includes("EP") ? "eps" :
                                    releaseGroup["primary-type"]?.includes("Single") ? "singles" :
                                        "others";


        releaseGroupsByFormat[format].push(releaseGroup);
    });

    return (
        <div className="artist-page">
            <div className="artist-page-left">
                <div className="name-over-img">
                    <h1 className="artist-name">{data.name}</h1>
                    <img className="artist-img"
                         src={artistImage}
                         onError={() =>
                             setArtistImage(
                                 `https://coverartarchive.org/release-group/${data["release-groups"]?.[0]?.id}/front`
                             )}
                         alt="placeholder.png"/>
                    <div className="genres">
                        {genres.join(", ") + " // genres"}
                    </div>

                </div>
                <div className="stats">
                    Your Stats
                    <div className="activity">
                        <h4>{totalScrobbles} Total Listens</h4>
                        <h4>{yearScrobbles} Listens In 2025</h4>
                        <h4>{monthScrobbles} Listens This Month</h4>
                        <h4>{dayScrobbles} Listens Today</h4>
                    </div>
                </div>
            </div>

            <div className="releases">
                {Object.entries(releaseGroupsByFormat).map(([format, groups]) => (
                    <div className="categories" key={format}>
                        {/* i.e. type albums becomes header Albums */}
                        <h3 className="category">{format.charAt(0).toUpperCase() + format.slice(1)}</h3>
                        <div className="category-releases">
                            {groups.filter(release => release["first-release-date"] != "").sort((a,b) => new Date(b["first-release-date"]) - new Date(a["first-release-date"])).map(releaseGroup => (
                                <div className="releaseGroup-items" key={releaseGroup.id}
                                     onClick={() => navigate(`/album/${releaseGroup.id}`)}>
                                    <img className="releaseGroup-img"
                                         src={`https://coverartarchive.org/release-group/${releaseGroup.id}/front`}
                                         alt="placeholder.jpg"/>
                                    <h4 className="releaseGroup-title">{releaseGroup.title}</h4>
                                    {/* substring(0,4) 2012-10-15 -> 2012 */}
                                    <h5 className="releaseGroup-date"> {releaseGroup["first-release-date"]?.substring(0, 4)}</h5>
                                </div>
                            ))}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}