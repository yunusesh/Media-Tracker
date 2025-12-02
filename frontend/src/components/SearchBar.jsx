import React, {useState, useRef} from "react";
import {FaSearch} from "react-icons/fa";
import "./SearchBar.css"
import {useNavigate} from "react-router-dom";

export default function SearchBar({searchTypeProp, buttonsEnabled, onClickFunction}) {
    const [input, setInput] = useState("");
    const debounceTimeout = useRef(null);
    const [results, setResults] = useState([]);
    const [searchType, setSearchType] = useState(searchTypeProp || "artists");
    const [buttonsVisible, setButtonsVisible] = useState(false);
    const navigate = useNavigate();

    const fetchData = (value) => {
        if (!value || value.trim() === "") {
            setResults([]);
            return;
        }

        fetch(`http://localhost:8081/${searchType}/${value}`)
            .then((response) => response.json())
            .then((json) => {
                if (json.artists) setResults(json.artists);
                else if (json.releaseGroups) setResults(json.releaseGroups);
                else if (json.tracks) setResults(json.tracks);
                else setResults([]);
            })
            .catch(() => setResults([]));
    };

    const handleChange = (value) => {
        setInput(value);
        if (debounceTimeout.current) clearTimeout(debounceTimeout.current);
        debounceTimeout.current = setTimeout(() => fetchData(value), 400);
    };

    const handleSearchTypeChange = (type) => {
        setInput("");
        setSearchType(type);
        setResults([]);
    };

    return (
        <div className="search-wrapper">
            <FaSearch
                id="search-icon"
                onClick={() => {
                    buttonsEnabled ?
                        setButtonsVisible(true) : setButtonsVisible(false)
                }
                }
            />
            <div className="input-wrapper">
                <input
                    placeholder="Type to search..."
                    value={input}
                    onChange={(e) => handleChange(e.target.value)}
                    onClick={() => {
                        buttonsEnabled ?
                            setButtonsVisible(true) : setButtonsVisible(false)
                    }
                    }
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
                    results.map((result, index) => (
                        <div className="search-result" key ={index}
                             onClick={() => {
                                 if (searchType === "artists") {
                                     navigate(`/artist/${result.id}`)
                                 } else if (searchType === "releases") {
                                     {
                                         buttonsEnabled ? navigate(`/album/${result.id}`) :
                                             onClickFunction(result)
                                     }
                                 } else if (searchType === "tracks") {
                                     navigate(`/track/${result.id}`)
                                 } else alert('Invalid search type')
                             }
                             }
                        >
                            {
                                searchType === "artists" ? (
                                    <h4>{result.name}</h4>
                                ) : searchType === "releases" || searchType === "tracks" ? (
                                    <div className="result-row">
                                        <img className="result-img"
                                             src={`https://coverartarchive.org/release-group/${result.id}/front`}
                                             alt="placeholder.png"/>
                                        <div className="result-info">
                                            <h5>{result.title} </h5>
                                            <h5>
                                                {result["artist-credit"]?.map((artist, index, array) => (
                                                    <span
                                                        key={artist.id}

                                                    >{artist.name}
                                                        {index < array.length - 1 &&
                                                            (index === array.length - 2 ? " & " : ", ")}
                                            </span>
                                                ))}
                                            </h5>
                                        </div>
                                    </div>
                                ) : (
                                    "Invalid search type"
                                )
                            }
                        </div>
                    ))}
            </div>
        </div>
    );
}
