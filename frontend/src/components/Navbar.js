import {Link} from "react-router-dom"
import {useNavigate, useLocation} from "react-router-dom";
import React, {useContext, useRef, useState} from "react";
import {FaSearch} from "react-icons/fa";
import "./Navbar.css"
import {SearchResult} from "./SearchResult";
import {AuthContext} from "../AuthContext";

export function Navbar() {
    const {user, logout} = useContext(AuthContext)
    const location = useLocation();
    const navigate = useNavigate();
    const [input, setInput] = useState("");
    const debounceTimeout = useRef(null); // store timeout ID
    const [results, setResults] = useState([]);
    const [searchType, setSearchType] =
        useState(
            location.pathname.includes("/artist")
                ? "artists"
                : location.pathname.includes("/album")
                    ? "releases"
                    : location.pathname.includes("/track")
                        ? "tracks"
                        : ""
        );
    const fetchData = (value) => {
        if (!value || value.trim() === "") {
            setResults([]); //set this to recently searched later!
            return;
        }

        fetch(`http://localhost:8081/${searchType}/${value}`)
            .then((response) => response.json())
            .then((json) => {
                if (json.artists) {
                    setResults(json.artists);
                } else if (json.releaseGroups) {
                    setResults(json.releaseGroups);
                } else if (json.tracks) {
                    setResults(json.tracks);
                } else {
                    setResults([]);
                }
            })
            .catch(() => setResults([]));
    };

    const handleChange = (value) => {
        setInput(value);

        // Clear any previous timeout before setting a new one
        if (debounceTimeout.current) {
            clearTimeout(debounceTimeout.current);
        }

        // Wait 200ms after typing stops before calling fetch
        debounceTimeout.current = setTimeout(() => {
            fetchData(value);
        }, 400);
    };

    const [buttonsVisible, setButtonsVisible] = useState(false);
    const [profileHoverVisible, setProfileHoverVisible] = useState(false);

    const handleSearchTypeChange = (type) => {
        setInput("")
        setSearchType(type)
        setResults([])
    }

    return (
        <div className="container">
            <div className="fullNavBar">
                <nav className="navbarLink">
                    <ul className="nav--list">
                        <Link to="/" className={`item ${location.pathname === "/" ? "active" : ""}`}
                        >Home</Link>
                        <Link to="/music" className={`item ${location.pathname.includes("/music") ? "active" : ""}`}
                        >Music</Link>
                        <Link to="/movies" className={`item ${location.pathname.includes("/movies") ? "active" : ""}`}
                        >Movies</Link>
                        <Link to="/tv" className={`item ${location.pathname.includes("/tv") ? "active" : ""}`}
                        >TV</Link>
                    </ul>
                    <div className="search-wrapper">
                        <FaSearch
                            id="search-icon"
                            onClick={() =>
                                setButtonsVisible(!buttonsVisible)
                            }
                        />
                        <div className="input-wrapper">
                            <input
                                placeholder="Type to search..."
                                value={input}
                                onChange={(e) => handleChange(e.target.value)}
                                onClick={() =>
                                    setButtonsVisible(true)}
                            />
                        </div>

                        {buttonsVisible && (
                            <div className="search-buttons">
                                <button className={`artist-button ${searchType === "artists" ? "active" : ""}`}
                                        onClick={() => {
                                            handleSearchTypeChange("artists")
                                        }}
                                >
                                    Artists
                                </button>
                                <button className={`release-button ${searchType === "releases" ? "active" : ""}`}
                                        onClick={() => {
                                            handleSearchTypeChange("releases")
                                        }}
                                >
                                    Releases
                                </button>
                                <button className={`track-button ${searchType === "tracks" ? "active" : ""}`}
                                        onClick={() => {
                                            handleSearchTypeChange("tracks")
                                        }}
                                >
                                    Tracks
                                </button>
                            </div>
                        )}
                        <div className="results-list">
                            {
                                results.map((result, id) => {
                                    return <SearchResult result={result} searchType={searchType} key={id}/>
                                })}
                        </div>
                    </div>

                </nav>
            </div>
            <div className="profile-nav">
                <img className="pfp"
                     src="https://i.pinimg.com/1200x/83/bc/8b/83bc8b88cf6bc4b4e04d153a418cde62.jpg"
                     alt="placeholder.png"
                     onClick={() => {
                         user == null ? navigate(`/login`) : navigate(`/user/${user.username}`
                         )
                     }}
                     onMouseEnter={() => setProfileHoverVisible(true)}
                     onMouseLeave={() => setProfileHoverVisible(false)}
                />
                <h5 className="pfp-username"
                    onClick={() => {
                        user == null ? navigate(`/login`) : navigate(`/user/${user.username}`
                        )
                    }}
                    onMouseEnter={() => setProfileHoverVisible(true)}

                >{user == null ? `Log in` : `${user.username}`}</h5>
                {profileHoverVisible && user && (
                    <div className="pfp-hover"
                         onMouseLeave={() => setProfileHoverVisible(false)}
                    >
                        <div className="logout"
                             onClick={() => {
                                 logout()
                             }}>
                            Logout
                        </div>
                    </div>
                )}
            </div>
        </div>

    )
}