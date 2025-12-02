import {createContext, useEffect, useState} from "react";

export const AuthContext = createContext()

export const AuthProvider = ({children}) => {
    const [user, setUser] = useState(null)

    const login = async (token) => {
        try{
        localStorage.setItem("token", token)

        const response = await fetch("http://localhost:8081/user/me", {
            headers: {Authorization: `Bearer ${token}`},
        })

        const userData = await response.json()
        setUser(userData)
        }
        catch(error){
            console.log("Token not found")
        }
    }


    const logout = () => {
        localStorage.removeItem("token")
        setUser(null)
    }

    useEffect(() => {
        const token = localStorage.getItem("token")
        if (token){
            login(token)
        }
    }, [])

    return (
        <AuthContext.Provider value={{user, login, logout}}>
            {children}
        </AuthContext.Provider>
    )
}