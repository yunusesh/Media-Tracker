import {Link} from "react-router-dom"
import {useNavigate, useLocation} from "react-router-dom";
import React, {useContext, useRef, useState} from "react";
import "./Navbar.css"
import SearchBar from "./SearchBar";
import {AuthContext} from "../AuthContext";

export function Navbar() {
    const {user, logout} = useContext(AuthContext)
    const location = useLocation();
    const navigate = useNavigate();
    useState(
        location.pathname.includes("/artist")
            ? "artists"
            : location.pathname.includes("/album")
                ? "releases"
                : location.pathname.includes("/track")
                    ? "tracks"
                    : ""
    );

    const [profileHoverVisible, setProfileHoverVisible] = useState(false);

    return (
        <div className="container">
            <div className="fullNavBar">
                <nav className="navbarLink">
                    <ul className="nav--list">
                        <Link to="/" className={`item ${location.pathname === "/" ? "active" : ""}`}
                        >Home</Link>
                        <Link to= {user ? `/user/${user.username}` : "/login"} className={`item ${location.pathname.includes("/user") ? "active" : ""}`}
                        >Profile</Link>
                    </ul>
                    <SearchBar buttonsEnabled={true}/>
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
                        {user && (
                            <div className = "settings"
                                 onClick={() => {
                                     navigate(`/settings`)
                                 }}>
                                Settings
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>

    )
}