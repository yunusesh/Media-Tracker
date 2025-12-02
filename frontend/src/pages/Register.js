import "./Register.css"
import axios from "axios";
import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {LuEyeClosed} from "react-icons/lu";
import {FaEye} from "react-icons/fa";

export function Register() {
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const [viewPassword, setViewPassword] = useState(false)

    const handleSubmit = async () => {
        try {
            await axios.post("http://localhost:8081/auth/signup", {
                username: username,
                password: password,
                email: email
            })
            navigate('/login')
        } catch (error) {
            console.log("Error registering user", error)
        }
    }

    return (
        <div className="register-page">
            <div className="register">
                <h1 className="signup-header">
                    Create an account
                </h1>
                <div className="email-wrapper">
                    <input
                        className="email"
                        placeholder="Enter an email"
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </div>
                <div className="username-wrapper">
                    <input
                        className="username"
                        placeholder="Enter a username"
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>
                <div className="password-wrapper">
                    <input
                        type={viewPassword ? null : "password"}
                        className="password"
                        placeholder="Enter a password"
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    {viewPassword ? <FaEye className="passShow" onClick={() => {
                            setViewPassword(false)
                        }}/> :
                        <LuEyeClosed className="passHide" onClick={() => {
                            setViewPassword(true)
                        }}/>}

                </div>
                <button
                    className="register-button"
                    onClick={handleSubmit}
                >
                    Register
                </button>
            </div>
        </div>
    )
}