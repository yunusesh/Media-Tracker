import { Navbar } from "./components/Navbar"
import { Outlet } from "react-router-dom"
import { CurrentlyPlaying } from "./components/CurrentlyPlaying"

export function Layout(){ // makes the nav bar a constant through all pages
    return(
        <> 
            <Navbar>
                <main>
                    <Outlet/>
                </main>
            </Navbar>
            <footer>
                <CurrentlyPlaying/>
            </footer>

        </>
    )
}